'''

@author:   "Norman Seeliger"
@date:     "23.06.2015"
@Version:  "1.1"
@license:  "GLP 3.0"

@title:    "Computational semantics / machine reading lecture : Negationsskopus"

'''
    
import sys, os              # e.g. for parsing arguments of the input
import copy                 # avoid side effects of using lists
from com.hp.hpl.jena.rdf.model import ModelFactory  # APACHE functions for RDF models
from decimal import *       # convert numbers and ease calculations
from itertools import cycle

# Defining global variables 
main_model  = None          # the RDF model taking all information within the RDF file into account
parts       = list()        # the sentences given by the CoNLL format
negated_sen = list()        # list of sentence numbers indicating which sentence is negated 
profiles    = list()        # a profile consists of all possible values (OBJ, SUBJ, ..) for a negated sentence after calculation

class Modelcreator():
    """Create the models out of the files provided"""
        
    def __init__(self, CoNLLfile, Textfile, position_of_negation, rdf_mode = "TURTLE"):
        """Parse the tokens of the CoNLL format file
        @CoNLLfile            :     file containing the CoNLL data
        @Textfile             :     file containing the RDF data
        @position_of_negation :     position of the negation as column within the CoNLL format
        @rdf_mode             :     mode of the RDF file being TURTLE, XML etc. (Default: TURTLE) """
        
        self.ConLLfile = CoNLLfile
        self.RDFfile   = Textfile 
        self.rdf_mode  = rdf_mode
        self.position_of_negation = int(position_of_negation)
        self.readRDF()
        self.readCoNLL()
        
    def readRDF(self):
        """Create models from reading the RDF statements with a specific mode"""
        global main_model       
        main_model = ModelFactory.createDefaultModel();    
        main_model.read(self.RDFfile, self.rdf_mode)
    
    def getModel(self):
        """Returns the model created out of the RDF file"""
        return self.__class__.main_model
        
    def readCoNLL(self):
        """Parse the information of a CoNLL file into different lists and models"""
        # setting defaults
        bufferstring    = ""                   # contains the normal sentence at the end
        fragments       = list()               # single parts of a line with all columns 
        current_position_in_sentence = 0       # current position within a sentence
        sentence_parts  = list(  )             # list containing the parsed column information 
        number_of_sentences          = 1       # incremented counter for sentences

        filebuffer = open(self.ConLLfile,"r+")

        # extract the information of the CoNLLfile to lists
        for line in filebuffer.readlines() :
            # for every line in the file
            fragments = line.split("\t")
            if (len(fragments) > 10) :                       
                # no empty lines, blank lines or entries with few information
                current_position_in_sentence = int(fragments[0].strip("\""))
                if (current_position_in_sentence == 1 and len(sentence_parts) > 0):    
                    # start of a new sentence
                    if (len(sentence_parts) > 0): parts.append(sentence_parts)
                    if bufferstring != ""       : parts[-1].append(bufferstring)
                    if len(fragments) > 1 :                 
                        # initialize new sentence
                        bufferstring = fragments[1]   + " "       
                    else :
                        bufferstring = "" 
                    current_position_in_sentence = 0
                    parts[-1].append(number_of_sentences)
                    # store number of this sentence and increment afterwards
                    number_of_sentences = number_of_sentences + 1                    
                    sentence_parts = [[fragments[1],fragments[2],
                                                   " ".join(x.strip("\n").strip("\t") 
                                                            for x in fragments[self.position_of_negation:]),0]]
                    
                else :    
                    bufferstring += fragments[1] + " "         
                    # add to new sentence
                    if len(fragments[1]) > 1 and len(fragments)>10:                  
                        # no , ; ! ? etc.
                        if fragments[self.position_of_negation].strip() not in ["0","-","O"] :
                            if "A" in fragments[self.position_of_negation].strip() and not ("AM-" in
                                fragments[self.position_of_negation].strip()) :
                                # if object but not modi as "AM-TMP" or "AM-LOC"
                                    sentence_parts.append([fragments[1],fragments[2],
                                                   " ".join(x.strip("\n").strip("\t") 
                                                            for x in fragments[self.position_of_negation:])+"    OBJ",0])
                            else :
                                if "ADJ" in fragments[5] or "ADV" in fragments[8] :
                                    # explicetly flag adjectives
                                    sentence_parts.append([fragments[1],fragments[2],"ADJ",0])
                                else :
                                    sentence_parts.append([fragments[1],fragments[2],
                                                   " ".join(x.strip("\n").strip("\t") 
                                                            for x in fragments[self.position_of_negation:]),0])

                        elif "ADJ" in fragments[5] or "ADV" in fragments[8] :
                            sentence_parts.append([fragments[1],fragments[2],"ADJ",0])
                        else :
                            sentence_parts.append([fragments[1],fragments[2],"0",0])
                           
                                
        # add last entry for the loop stopping at the last without adding it
        if (len(sentence_parts) > 0): parts.append(sentence_parts)
        if bufferstring != "":        parts[-1].append(bufferstring)
        parts[-1].append(number_of_sentences)
        
        #check for negation within the single sentences
        self.checkForNegation()

        filebuffer.close()
        
    def checkForNegation(self):
        """Find out which sentence is negated and add prior information"""
        for number, liste in enumerate(parts) :         # for every sentence
            for entry in liste[:-2] :                   # except for the last entries (being the whole sentence)
                if entry[2].strip() not in ["0",0,"O"] and "NEG" in entry[2] :          # if a part is negated
                    negated_sen.append(number)
                    for pos,new_entry in enumerate(liste[:-2]) :       # reiterate over all entries of a sentence-list
                        parts[number][pos][-1] = round(1/(float(len(liste))-2.0),3) 
                        # add prior information about negation (minus 2 positions for complete sentence and number)
                    break
                
class NegationDissolver:
    """Try to dissvole the ambiguties within a sentence"""
    
    def __init__(self, sentence) :
        """Initialize parameters
        
        @sentence        :     summarized information about the sentence as [position, token, whole_sentence]"""
        
        self.general_position = int(sentence[-1])-1 # last position is general position
        self.sentence         = sentence
        self.main_sentence    = copy.deepcopy(self.sentence)
        
        profiles.append([self.general_position]) 
        # list containing all models for a given sentence
        
        self.solvedDisam(0)          # first check within the sentence itself for multiple occurences 
        

            
    def solvedDisam(self, Position):
        """Check within the sentence for possible explanations, adapt the sequences for the individual foci
        
        @position        :     position in the whole text with the current sentence on 0 (zero)"""
        
        # perform analysis, sum up results and refresh the original information stored in main_sentence
        for old_entry in self.sentence[:-2]:
            # simplify annotation
            if "B-AM-TMP" in old_entry[-2]:
                old_entry[-2] = "B-AM-TMP"
            elif "S-AM-TMP" in old_entry[-2] :
                old_entry[-2] = "S-AM-TMP"
        # check for time being negated
        self.clearTime(Position)      
        # get the highest valued token after computation   
        profiles[-1].append(["Time",self.getMax("AM-TMP")[-1],self.getMax("AM-TMP")])
        # refresh sentence with original sentence
        self.sentence = copy.deepcopy(self.main_sentence)
        
        self.clearPlace(Position)         
        profiles[-1].append(["Place",self.getMax("AM-LOC")[-1],self.getMax("AM-LOC")])
        self.sentence = copy.deepcopy(self.main_sentence)        

        self.clearVerbalPhrase(Position)
        profiles[-1].append(["Verb",self.getMax("-V")[-1],self.getMax("-V")])
        self.sentence = copy.deepcopy(self.main_sentence)
        
        self.clearAdjAdv()
        profiles[-1].append(["AdjAdv",self.getMax("ADJ")[-1],self.getMax("ADJ")])
        self.sentence = copy.deepcopy(self.main_sentence)
        
        self.clearObject()
        profiles[-1].append(["Object",self.getMax("OBJ")[-1],self.getMax("OBJ")]) 

        # get the highest scoring model
        self.summarize()
    
    def summarize(self):
        """Comparing the models and choose the best one, create output as RDF"""
        best_model = [0]
        best_mode  = ""
        statements = main_model.listStatements(None,None,None)  

        if len(profiles[-1]) < 2 : return

        # find the highest value of all models
        for entry in profiles[-1][1:] :
            if entry[1] != "None" and entry[1] > best_model[-1] :
                best_model = entry[-1]
                best_mode = entry[0]  

        # new_annotations are added to the original RDF data at the end of the computation
        
        if best_mode == "Verb" :
            for statement in statements :
                if str(statement.getPredicate()).endswith(best_model[0]):
                    old_predicate = str(statement.getPredicate())
                    new_predicate = old_predicate[:len(old_predicate)-len(best_model[0])] + "not_"+best_model[0]
                    new_annotations.append(str(statement.getSubject())+"\t"+new_predicate+"\t"+str(statement.getObject()))
                    return
        elif best_mode == "Place" :
            for statement in statements :
                if str(statement.getObject()).endswith(best_model[0]) :
                    old_predicate = str(statement.getPredicate())
                    new_predicate = ":not_at"
                    new_annotations.append(str(statement.getSubject())+"\t"+new_predicate+"\t"+str(statement.getObject()))
                    return
        elif best_mode == "Time" :
            for statement in statements :
                if str(statement.getObject()).endswith(best_model[0]) :
                    old_predicate = str(statement.getPredicate())
                    new_predicate = ":not_in"
                    new_annotations.append(str(statement.getSubject())+"\t"+new_predicate+"\t"+str(statement.getObject()))
                    return 
        elif best_mode == "AdjAdv" :
            for statement in statements :
                if str(statement.getObject()).endswith(best_model[0]) :
                    new_predicate = ":not_be"
                    new_annotations.append(str(statement.getSubject())+"\t"+new_predicate+"\t"+":"+best_model[0]+"t")
                    return 
        else : # objects and subjects
            for statement in statements :
                if str(statement.getObject()).endswith(best_model[0]) :
                    old_predicate = str(statement.getPredicate())
                    new_predicate = "not_"+old_predicate
                    new_annotations.append(":_"+"\t"+new_predicate+"\t"+str(statement.getObject()))
                    return

    def wrapExpressions(self,modus,fragments):
        """Wrap expressions belonging together"""
        # not implemented yet
            
    def getMax(self, modus):
        """Return the fragment having the highest value
        
        @modus        :         being time, location, verb etc."""
        
        most_significant_fragment = ["None","None"]
        highest_value_found = 0
        for entry in self.sentence[:-2] :
            if entry[-1] > highest_value_found and modus in entry[-2]:
                most_significant_fragment = entry
                highest_value_found = entry[-1]
        return most_significant_fragment
    
    def clearObject(self):
        """Identify if the subject has been negated"""
        multiple_negated_obj = list()
        multiple_negated_obj_simpl = list()
        
        # get all fragments for objects
        obj_fragments = [x for x in self.sentence[:-2] if "OBJ" in self.sentence[-2]]         

        for position in range(1, min(self.general_position, 3), 1):      
            # take the context into account, differentiate between negated and not negated context
            if (self.general_position + position) not in negated_sen :
                obj_fragments.extend([x for x in parts[self.general_position + position][:-2] if "OBJ" in x[-2]])
            else:
                multiple_negated_obj.extend([x for x in parts[self.general_position + position][:-2] if "OBJ" in x[-2]])
            if (self.general_position - position) not in negated_sen :
                obj_fragments.extend([x for x in parts[self.general_position - position][:-2] if "OBJ" in x[-2]])
            else:
                multiple_negated_obj.extend([x for x in parts[self.general_position - position][:-2] if "OBJ" in x[-2]])
            if len(obj_fragments) == 1:  
                # if there is no other information about time in the context 
                # set starting values for position 0 if there's just one fragment
                closest_fragment = self.getClosestFragment("OBJ") 
                self.adaptValues(closest_fragment[1], closest_fragment[0])                    
                return
            else :  
                # if there is additional information about time in the context
                multiple_negated_obj_simpl = [y[1] for y in multiple_negated_obj]
                obj_fragments.extend(multiple_negated_obj)
                doubled = self.doubledExpressions("OBJ", obj_fragments)
                if doubled == [] :
                    del obj_fragments[:]
                    obj_fragments = [x for x in self.sentence[:-2] if "OBJ" in self.sentence[-2]]                     
                    closest_fragment = self.getClosestFragment("OBJ") 
                    self.adaptValues(closest_fragment[1], closest_fragment[0])
                    # downsize the effect of context according to the distance in sentences
                    # nearest sentence are taken as the sentence itself (pos - 1 for pos > 0)
                    return
                else :
                    # if an expression appears multiple times in sentences which are
                    # not all having a negation, it is less likely that is is negated
                    for entry in self.sentence[:-2] :
                        for entry_2 in doubled :
                            if entry[1] == entry_2[1] and entry[1] not in multiple_negated_obj_simpl:
                                entry[-1] = 0
                                return
                                # multiple times but the negated one is not one of them -> not negated
                            elif  entry[1] == entry_2[1] and entry[1] in multiple_negated_obj_simpl:
                                entry[-1] = entry[-1] + 1
                                # multiple times and negated again -> indication for negation    
                    # if the loop above fails, reset the fragments and look for distance as last step
                    del obj_fragments[:]
                    obj_fragments = [x for x in self.sentence[:-2] if "OBJ" in self.sentence[-2]]                     
                    closest_fragment = self.getClosestFragment("OBJ") 
                    self.adaptValues(closest_fragment[1], closest_fragment[0])
           
    def clearAdjAdv(self):
        """Try to solve negated adjectives and adverbs"""
        statements = main_model.listStatements(None,None,None)  

        # storing the objects linked to adjectives
        acteurs = list()
        multiple_negated_adjadv = list()
        multiple_negated_adjadv_simpl = list()
        
        # get the parts of sentence having adjectives or adverbs
        # e.g. ['nice', 'ADJ', 0.059] as x results in storing it as ADJ/ADV fragment 
        adjadv_fragments = [x for x in self.sentence[:-2] if x[-2].endswith("ADJ")] 
        
        if len(adjadv_fragments) == 0:
            for entry in self.sentence[:-2] :
                entry[-1] = 0
            return
                
        # if there's just one, try to solve it within the sentence (for adjectives being annotaed in RDF already)
        # if that does not help, take the context into account
        
        for fragment in adjadv_fragments:        # iterate over all different fragments if there is more than one
            already_used = False 
            # marker if the object of a statement has already been used
            suffix = "n"+str(self.sentence[-1]-1)+"_"+str(self.sentence.index(fragment)+1).zfill(3)+"_"+fragment[0]
            # that is e.g. "n49_07_September" as data
            # zfill fills up to 3 positions with leading zeros
            for statement in statements :      # for all statements 
                if str(statement.getObject()).endswith(fragment[1]) :   # get the statement having the fragment
                    if str(statement.getPredicate()).endswith("not-be") :
                        for entry in self.sentence[:-2] :
                            if entry == fragment:
                                entry[-1] += 2
                        return
                    if not already_used : 
                        if (statement.getSubject()) not in acteurs :    
                        # get the subject if it was not used before for this adjective
                            acteurs.append(statement.getSubject()) 
                            already_used = True
                        else :
                        # if the same acteur has been found with different a temporal modi within the same sentence, 
                        # it is likely that the time has been negated
                            for position,entry in enumerate(self.sentence[:-2]) :
                                closest_fragment = self.getClosestFragment("ADJ")
                                if entry == closest_fragment[1] :
                                # closest fragment gets its value increased by 1
                                    entry[-1] = entry[-1]+1
                                    return 
                                elif entry[-2].endswith("ADJ") :
                                # other time fragments get their value softly increased based on the distance
                                    entry[-1] = entry[-1]+1/(abs(position-self.pos_of_neg))
                                else :
                                # other values stay as they are
                                    entry[-1] = entry[-1]
                            
        # if we had more than one kind of this expression in the sentence itself but it lead not to
        # solving negation, take the context into account as well            
            for position in range(1, min(self.general_position, 3), 1):      
                # if the context sentences are not negated in itself, add the verbs
                # otherwise, take them for negation indicators     
                if (self.general_position + position) not in negated_sen :
                    adjadv_fragments.extend([x for x in parts[self.general_position + position][:-2] if x[-2].endswith("ADJ")])
                else:
                    multiple_negated_adjadv.extend([x for x in parts[self.general_position + position][:-2] if x[-2].endswith("ADJ")])
                if (self.general_position - position) not in negated_sen :
                    adjadv_fragments.extend([x for x in parts[self.general_position - position][:-2] if x[-2].endswith("ADJ")])
                else:
                    multiple_negated_adjadv.extend([x for x in parts[self.general_position - position][:-2] if x[-2].endswith("ADJ")])
                # taking context into account
                if len(adjadv_fragments) == 1:  
                    # if there is no other information about time in the context 
                    # set starting values for position 0 if there's just one fragment
                    closest_fragment = self.getClosestFragment("ADJ") 
                    self.adaptValues(closest_fragment[1], closest_fragment[0])                    
                    return
                else :  # if there is additional information about time in the context
                    # if there are doubled expressions within the sentences
                    multiple_negated_adjadv_simpl = [y[1] for y in multiple_negated_adjadv]
                    adjadv_fragments.extend(multiple_negated_adjadv)
                    doubled = self.doubledExpressions("ADJ", adjadv_fragments)
                    if doubled == [] :
                        closest_fragment = self.getClosestFragment("ADJ") 
                        self.adaptValues(closest_fragment[1], closest_fragment[0])
                        # downsize the effect of context according to the distance in sentences
                        # nearest sentence are taken as the sentence itself (pos - 1 for pos > 0)
                        return
                    else :
                        # if an expression appears multiple times in sentences which are
                        # not all having a negation, it is less likely that is is negated
                        for entry in self.sentence[:-2] :
                            for entry_2 in doubled :
                                if entry[1] == entry_2[1] and entry[1] not in multiple_negated_adjadv_simpl:
                                    entry[-1] = entry[-1] * 1 / len(adjadv_fragments)
                                    # multiple times but the negated one is not one of them -> possibly not negated
                                elif  entry[1] == entry_2[1] and entry[1] in multiple_negated_adjadv_simpl:
                                    entry[-1] = entry[-1] + 1
                                    # multiple times (in negation) indicates negation
    
    def clearVerbalPhrase(self,Position):
        """Try to detect different verbs for disambiguation"""
        multiple_negated_verbs = list()
        multiple_negated_verbs_simpl = list()
        
        verb_fragments = [x for x in self.sentence[:-2] if "-V" in x[-2]]

        for position in range(1,min(self.general_position,2),1):      
            # if the context sentences are not negated in itself, add the verbs
            # otherwise, take them for negation indicators     
            if (self.general_position + position) not in negated_sen :
                verb_fragments.extend([x for x in parts[self.general_position+position][:-2] 
                                       if "-V" in x[-2]])
            else:
                multiple_negated_verbs.extend([x for x in parts[self.general_position+position][:-2] 
                                       if "-V" in x[-2]])
            if (self.general_position - position) not in negated_sen :
                verb_fragments.extend([x for x in parts[self.general_position-position][:-2] 
                                       if "-V" in x[-2]])
            else:
                multiple_negated_verbs.extend([x for x in parts[self.general_position-position][:-2] 
                                       if "-V" in x[-2]])    
        if len(multiple_negated_verbs) > 0 :
            # if there are negated verbs nearby, get the basic forms of the verbs
            multiple_negated_verbs_simpl = [y[1] for y in multiple_negated_verbs] 
            verb_fragments.extend(multiple_negated_verbs)
  
        doubled = self.doubledExpressions("-V", verb_fragments)
        if doubled == [] :
            # if there are no doubled verbs in the context
                # set starting values for position 0 if there's just one fragment
            del verb_fragments[:]
            verb_fragments = [x for x in self.sentence[:-2] if "-V" in x[-2]]
            closest_fragment = self.getClosestFragment("-V") 
            self.adaptValues(closest_fragment[1],closest_fragment[0])
        else : # if there is additional information in the context
            # look if there are doubled expressions within the sentences
            if doubled  != [] :
                # if an expression appears multiple times in sentences which are
                # not all having a negation, it is less likely that is is negated
                for entry in self.sentence[:-2] :
                    for entry_2 in doubled :
                        if entry[1] == entry_2[1] and entry[1] not in multiple_negated_verbs_simpl :
                            entry[-1] = 0
                            # appearance of the same verb in the not negated context heavily refuses negation
                        elif entry[1] == entry_2[1] and entry[1] in multiple_negated_verbs_simpl :
                            entry[-1] += 1
                            return
                            # appearance of the same verb in multiple negated sentences indicates negation 
            # if it could not found that one verb is definitely negated, ask for the closest one
            del verb_fragments[:]
            verb_fragments = [x for x in self.sentence[:-2] if "-V" in x[-2]]
            closest_fragment = self.getClosestFragment("-V") 
            self.adaptValues(closest_fragment[1],closest_fragment[0])
            
                
        
    def clearTime(self,Position):
        """Try to take different times into account for disambiguation"""
        statements = main_model.listStatements(None,None,None)  
        # get all statements from the model
        
        # storing the objects linked to temporal modi
        acteurs = list()
        multiple_negated_times = list()
        multiple_negated_times_simpl = list()
        
        # get the parts of sentence having temporal modi
        # e.g. ['September', 'E-AM-TMP"', 0.059] as x results in storing it as time fragment 
        time_fragments = [x for x in self.sentence[:-2] if x[-2].endswith("B-AM-TMP")] 
        time_fragments.extend([x for x in self.sentence[:-2] if x[-2].endswith("S-AM-TMP")])
        
        if len(time_fragments) == 0:
            for entry in self.sentence[:-2] :
                entry[-1] = 0
            return
                
        # if there's just one, look for context first and then for close distance
        if len(time_fragments) == 1:
            for position in range(1,min(self.general_position,3),1):      
                # if the context sentences are not negated in itself, add the verbs
                # otherwise, take them for negation indicators     
                if (self.general_position + position) not in negated_sen :
                    time_fragments.extend([x for x in parts[self.general_position+position][:-2] 
                                       if (x[-2].endswith("S-AM-TMP") or x[-2].endswith("B-AM-TMP"))])
                else:
                    multiple_negated_times.extend([x for x in parts[self.general_position+position][:-2] 
                                       if (x[-2].endswith("S-AM-TMP") or x[-2].endswith("B-AM-TMP"))])
                if (self.general_position - position) not in negated_sen :
                    time_fragments.extend([x for x in parts[self.general_position-position][:-2] 
                                       if (x[-2].endswith("S-AM-TMP") or x[-2].endswith("B-AM-TMP"))])
                else:
                    multiple_negated_times.extend([x for x in parts[self.general_position-position][:-2] 
                                       if (x[-2].endswith("S-AM-TMP") or x[-2].endswith("B-AM-TMP"))])     
                # taking context into account
                if len(time_fragments) == 1:  
                    # if there is no other information about time in the context 
                    # set starting values for position 0 if there's just one fragment
                    closest_fragment = self.getClosestFragment("AM-TMP") 
                    self.adaptValues(closest_fragment[1],closest_fragment[0])                    
                    return
                else : # if there is additional information about time in the context
                    # if there are doubled expressions within the sentences
                    multiple_negated_times_simpl = [y[1] for y in multiple_negated_times]
                    time_fragments.extend(multiple_negated_times)
                    doubled = self.doubledExpressions("AM-TMP", time_fragments)
                    if doubled  == [] :
                        for entry in self.sentence[:-2]:
                            if "AM-TMP" in entry[-2] :
                                entry[-1] = entry[1]+1
                            # only one time modus in the sentence but other times in context indicates negation
                        return
                    else :
                        # if an expression appears multiple times in sentences which are
                        # not all having a negation, it is less likely that is is negated
                        for entry in self.sentence[:-2] :
                            for entry_2 in doubled :
                                if entry[1] == entry_2[1] and entry[1] not in multiple_negated_times_simpl:
                                    entry[-1] = 0
                                    # multiple times but the negated one is not one of them -> possibly negated
                                elif  entry[1] == entry_2[1] and entry[1] in multiple_negated_times_simpl:
                                    entry[-1] =  entry[-1] + 1
                                    # multiple times (in negation) and the 
        
        for fragment in time_fragments:        # iterate over all different fragments if there is more than one
            for statement in statements :      # for all statements with all time fragments
                if str(statement.getObject()).endswith(fragment[0]) :   # get the statement having the fragment
                    if (statement.getSubject()) not in acteurs :    
                        # get the subject/object if it was not used before
                        acteurs.append(statement.getSubject()) 
                    else :
                        # if the same acteur has been found with different a temporal modi within the same sentence, 
                        # it is likely that the time has been negated
                        for position,entry in enumerate(self.sentence[:-2]) :
                            closest_fragment = self.getClosestFragment("AM-TMP")
                            if entry == closest_fragment[1] :
                                # closest fragment gets its value increased by 1
                                entry[-1] = entry[-1]+1
                            elif entry[-2].endswith("AM-TMP") :
                                # other time fragments get their value softly increased based on the distance
                                entry[-1] = entry[-1]+1/(abs(position-self.pos_of_neg))
                            else :
                                # other values stay as they are
                                entry[-1] = entry[-1]
                        return 
        # if we had more than one kind of this expression in the sentence itself but it lead not to
        # solving negation, take the context into account as well            
        for position in range(1,min(self.general_position,3),1):      
            # if the context sentences are not negated in itself, add the verbs
            # otherwise, take them for negation indicators     
            if (self.general_position + position) not in negated_sen :
                time_fragments.extend([x for x in parts[self.general_position+position][:-2] 
                                       if (x[-2].endswith("S-AM-TMP") or x[-2].endswith("B-AM-TMP"))])
            else:
                multiple_negated_times.extend([x for x in parts[self.general_position+position][:-2] 
                                       if (x[-2].endswith("S-AM-TMP") or x[-2].endswith("B-AM-TMP"))])
            if (self.general_position - position) not in negated_sen :
                    time_fragments.extend([x for x in parts[self.general_position-position][:-2] 
                                       if (x[-2].endswith("S-AM-TMP") or x[-2].endswith("B-AM-TMP"))])
            else:
                    multiple_negated_times.extend([x for x in parts[self.general_position-position][:-2] 
                                       if (x[-2].endswith("S-AM-TMP") or x[-2].endswith("B-AM-TMP"))])     
            # taking context into account
            if len(time_fragments) == 1:  
                # no further information
                return
            else : # if there is additional information about time in the context
                # if there are doubled expressions within the sentences
                multiple_negated_times_simpl = [y[1] for y in multiple_negated_times]
                time_fragments.extend(multiple_negated_times)
                doubled = self.doubledExpressions("AM-TMP", time_fragments)
                if doubled  == [] :
                    closest_fragment = self.getClosestFragment("AM-TMP") 
                    self.adaptValues(closest_fragment[1],closest_fragment[0])
                    # downsize the effect of context according to the distance in sentences
                    # nearest sentence are taken as the sentence itself (pos - 1 for pos > 0)
                    return
                else :
                    # if an expression appears multiple times in sentences which are
                    # not all having a negation, it is less likely that is is negated
                    for entry in self.sentence[:-2] :
                        for entry_2 in doubled :
                            if entry[1] == entry_2[1] and entry[1] not in multiple_negated_times_simpl:
                                entry[-1] = entry[-1] * 1/len(self.time_fragments)
                                # multiple times but the negated one is not one of them -> possibly not negated
                            elif  entry[1] == entry_2[1] and entry[1] in multiple_negated_times_simpl:
                                entry[-1] =  entry[-1] + 1
                                # multiple times (in negation), higher probability of being negated

    def clearPlace(self,Position):
        """Try to take different times into account for disambiguation"""
        statements = main_model.listStatements(None,None,None)  
        # get all statements from the model
        
        acteurs = list()
        multiple_negated_places = list()
        multiple_negated_places_simpl = list()
        
        # get the parts of sentence having locations
        # e.g. ['Berlin', 'E-AM-LOC"', 0.059] as x results in storing it as locatio fragment 
        place_fragments = [x for x in self.sentence[:-2] if "B-AM-LOC" in x[-2] or "S-AM-LOC" in x[-2]] 
        
        if len(place_fragments) == 0:
            for entry in self.sentence[:-2] :
                entry[-1] = 0
            return
                
        # if there's just one, look for context first and then for close distance
        if len(place_fragments) == 1:
            for position in range(1,min(self.general_position,3),1):      
                # if the context sentences are not negated in itself, add the verbs
                # otherwise, take them for negation indicators     
                if (self.general_position + position) not in negated_sen :
                    place_fragments.extend([x for x in parts[self.general_position+position][:-2] 
                                       if "B-AM-LOC" in x[-2] or "S-AM-LOC" in x[-2]])
                else:
                    multiple_negated_places.extend([x for x in parts[self.general_position+position][:-2] 
                                       if "B-AM-LOC" in x[-2] or "S-AM-LOC" in x[-2]])
                if (self.general_position - position) not in negated_sen :
                    place_fragments.extend([x for x in parts[self.general_position-position][:-2] 
                                       if "B-AM-LOC" in x[-2] or "S-AM-LOC" in x[-2]])
                else:
                    multiple_negated_places.extend([x for x in parts[self.general_position-position][:-2] 
                                       if "B-AM-LOC" in x[-2] or "S-AM-LOC" in x[-2]])   
                # taking context into account
                if len(place_fragments) == 1:  
                    # if there is no other information about time in the context 
                    # set starting values for position 0 if there's just one fragment
                    closest_fragment = self.getClosestFragment("AM-LOC") 
                    self.adaptValues(closest_fragment[1],closest_fragment[0])                    
                    return
                else : # if there is additional information about time in the context
                    # if there are doubled expressions within the sentences
                    multiple_negated_places_simpl = [y[1] for y in multiple_negated_places]
                    place_fragments.extend(multiple_negated_places)
                    doubled = self.doubledExpressions("AM-LOC", place_fragments)
                    if doubled  == [] :
                        for entry in self.sentence[:-2]:
                            if "AM-LOC" in entry[-2] :
                                entry[-1] = entry[1]+1
                            # only one time modus in the sentence but other times in context indicates negation
                        return
                    else :
                        # if an expression appears multiple times in sentences which are
                        # not all having a negation, it is less likely that is is negated
                        for entry in self.sentence[:-2] :
                            for entry_2 in doubled :
                                if entry[1] == entry_2[1] and entry[1] not in multiple_negated_places_simpl:
                                    entry[-1] = 0
                                    # multiple times but the negated one is not one of them -> possibly negated
                                elif  entry[1] == entry_2[1] and entry[1] in multiple_negated_places_simpl:
                                    entry[-1] =  entry[-1] + 1
                                    # multiple times (in negation) and the 
        
        for fragment in place_fragments:        # iterate over all different fragments if there is more than one
            suffix = "n"+str(self.sentence[-1]-1)+"_"+str(self.sentence.index(fragment)+1).zfill(3)+"_"+fragment[0]
            # that is e.g. "n1_014_September" as data
            # zfill fills up to 3 positions with leading zeros
            for statement in statements :      # for all statements with all time fragments
                if str(statement.getObject()).endswith(fragment[0]) :   # get the statement having the fragment
                    if (statement.getSubject()) not in acteurs :    
                        # get the subject/object if it was not used before
                        acteurs.append(statement.getSubject()) 
                    else :
                        # if the same acteur has been found with different a temporal modi within the same sentence, 
                        # it is likely that the time has been negated
                        for position,entry in enumerate(self.sentence[:-2]) :
                            closest_fragment = self.getClosestFragment("AM-LOC")
                            if entry == closest_fragment[1] :
                                # closest fragment gets its value increased by 1
                                entry[-1] = entry[-1]+1
                            elif entry[-2].endswith("AM-LOC") :
                                # other time fragments get their value softly increased based on the distance
                                entry[-1] = entry[-1]+1/(abs(position-self.pos_of_neg))
                            else :
                                # other values stay as they are
                                entry[-1] = entry[-1]
                        return 
        # if we had more than one kind of this expression in the sentence itself but it lead not to
        # solving negation, take the context into account as well            
        for position in range(1,min(self.general_position,3),1):      
            # if the context sentences are not negated in itself, add the verbs
            # otherwise, take them for negation indicators     
            if (self.general_position + position) not in negated_sen :
                place_fragments.extend([x for x in parts[self.general_position+position][:-2] 
                                         if "B-AM-LOC" in x[-2] or "S-AM-LOC" in x[-2]])
            else:
                multiple_negated_places.extend([x for x in parts[self.general_position+position][:-2] 
                                        if "B-AM-LOC" in x[-2] or "S-AM-LOC" in x[-2]])
            if (self.general_position - position) not in negated_sen :
                    place_fragments.extend([x for x in parts[self.general_position-position][:-2] 
                                        if "B-AM-LOC" in x[-2] or "S-AM-LOC" in x[-2]])
            else:
                    multiple_negated_places.extend([x for x in parts[self.general_position-position][:-2] 
                                       if "B-AM-LOC" in x[-2] or "S-AM-LOC" in x[-2]])     
            # taking context into account
            if len(place_fragments) == 1:  
                # no further information
                return
            else : # if there is additional information about time in the context
                # if there are doubled expressions within the sentences
                multiple_negated_places_simpl = [y[1] for y in multiple_negated_places]
                place_fragments.extend(multiple_negated_places)
                doubled = self.doubledExpressions("AM-LOC", place_fragments)
                if doubled  == [] :
                    closest_fragment = self.getClosestFragment("AM-LOC") 
                    self.adaptValues(closest_fragment[1],closest_fragment[0])
                    # downsize the effect of context according to the distance in sentences
                    # nearest sentence are taken as the sentence itself (pos - 1 for pos > 0)
                    return
                else :
                    # if an expression appears multiple times in sentences which are
                    # not all having a negation, it is less likely that is is negated
                    for entry in self.sentence[:-2] :
                        for entry_2 in doubled :
                            if entry[1] == entry_2[1] and entry[1] not in multiple_negated_places_simpl:
                                entry[-1] = entry[-1] * 1/len(self.time_fragments)
                                # multiple times but the negated one is not one of them -> possibly negated
                            elif  entry[1] == entry_2[1] and entry[1] in multiple_negated_places_simpl:
                                entry[-1] =  entry[-1] + 1
                                # multiple times (in negation), higher probability of being negated


    def doubledExpressions(self,modus,input):
        """Find out if expressions appear more than once within a list 
           containing special fragments or the whole sentence"""
        expressions_found = list()
        expressions_found_first = list()
        
        simplified_input = [x[1] for x in input]
        # that is, ['competitor', 'people','Peter'] for [['competitors', 'competitor', 'O', 0.036], ... ]
        for entry in simplified_input: 
            try : 
                if simplified_input.count(entry) > 1 and entry not in expressions_found_first : 
                    # if an entry appears more than once and has not been stored for that yet, add it
                    expressions_found_first.append(entry)
            except ValueError :
                pass
        for entry in input :
            for doubled_entry in expressions_found_first :
                if entry[1] == doubled_entry and entry not in expressions_found:
                    # add the original fragments corresponding to the simplified parts found to appear more than once
                    expressions_found.append(entry)
        return expressions_found
        
    def adaptValues(self,singleton, distance):
        """Change the probabilities for an entry if it is a singleton"""
        getcontext().prec = 4
        # set the precision to 4
        new_value_singleton = round((1.000-(1.000/(len(self.sentence)-3)))*(1.000/distance),4)
        new_value_others = round((1.000-new_value_singleton)/(len(self.sentence)-3),4)
        for fragment in self.sentence[:-2] :
            if fragment == singleton:
                # change the one significant fragment becoming the highest value
                fragment[-1] = round(fragment[-1]+new_value_singleton,4)
            else:
                # downsize all other fragments
                fragment[-1] = round(fragment[-1]+new_value_others,4)
                    
    def getClosestFragment(self, modi):
        """Given multiple fragments for a specific mode, return the one which is closest to negation"""
        positions = []  # positions of relevant fragments
        modus = modi        # AM-TMP, AM-LOC etc.
        
        self.pos_of_neg = 0      # position of negation 
        
        # get position of negation
        for pos,fragment in enumerate(self.sentence[:-2]) :
            if "NEG" in fragment[-2] :
                self.pos_of_neg = pos
                break;
        # get positions of modi
        for pos,fragment in enumerate(self.sentence[self.pos_of_neg:-2]) :
            if modus in fragment[-2] :
                positions.append((pos,fragment))   
                # e.g. having negation of position 4 and modi at 1,3,6, whe've [3,1,2] as relative positions
                # and tuples like [(3,x),(1,y),(2,z)] 
        if len(positions) == 0 :
            # if no appearance has been found after the negation, assign a high value for the nearest before
            for pos,fragment in enumerate(self.sentence[0:-2]) :
                if modus in fragment[-2]:
                    positions.append((max(abs(self.pos_of_neg-pos),len(self.sentence)/2),fragment))    

        # return smallest distance
        minimal = len(self.sentence); pos_for_minimal = 0
        for current,entry in enumerate(positions):
            if entry[0] < minimal : # if absolute position relative to negation is smaller than the one found before
                pos_for_minimal = current
        
        return positions[pos_for_minimal] # e.g. return (3,[September,'E-AM-TMP',0.06])
        
    def returnList(self):
        """Returns the altered list"""
        return self.sentence

def mainProcedure():
    """Performing the analysis from a general place"""
    
    # the annotations created by evaluating the models at the end and adding information about negation
    global new_annotations 
    new_annotations = list()

    Modelcreator(CoNLLfile,RDFfile, position_of_negation, format)
    if len(negated_sen) > 0:
        for negated_sentence in negated_sen :
            # iterate through all negated sentences and create the models for them checking for negation
            NegationDissolver(parts[int(negated_sentence)]) # update entry

    if (path_for_outfile != None) :
        # write the new RDF information back to file
        global global_rdf_outfile
        global_rdf_outfile = open(path_for_outfile+CoNLLfile.split(".")[0]+"_Out.txt","w+")
        statements = main_model.listStatements(None,None,None)  
        main_model.write(global_rdf_outfile,format)
        for sentence in new_annotations :
            global_rdf_outfile.write(sentence+"\t")

    if (path_for_outfile != None):
        # create user output 
        outfile = open(path_for_outfile+CoNLLfile.split(".")[0]+".txt","w+")
        if profiles == [] :
            outfile.write("Keine Negation gefunden!")
            print("Keine Negation gefunden!")
            global_rdf_outfile.close()
            outfile.close()
        for profile in profiles:
            outfile.write("Satz Nr."+str(profile[0])+": "+str(parts[profile[0]][-2]+"\n"))
            outfile.write("Wert Zeitmodus: \t"+str(profile[1][1])+" - "+profile[1][2][1]+"\n")
            outfile.write("Wert Ort: \t"+str(profile[2][1])+" - "+profile[2][2][1]+"\n")        
            outfile.write("Wert Verb: \t"+str(profile[3][1])+" - " +profile[3][2][1]+"\n")
            outfile.write("Wert Adjective: \t"+str(profile[4][1])+" - " +profile[4][2][1]+"\n")       
            outfile.write("Wert Object: \t "+str(profile[5][1])+" - " +profile[5][2][1]+"\n")
            outfile.write("")
        outfile.close()
        global_rdf_outfile.close()

    
    for profile in profiles:
        print("Satz Nr."+str(profile[0])+": "+str(parts[profile[0]][-2]))
        print("Wert Zeitmodus: \t"+str(profile[1][1])+" - "+profile[1][2][1])
        print("Wert Ort: \t"+str(profile[2][1])+" - "+profile[2][2][1])        
        print("Wert Verb: \t"+str(profile[3][1])+" - " +profile[3][2][1])
        print("Wert Adjective: \t"+str(profile[4][1])+" - " +profile[4][2][1])       
        print("Wert Object: \t"+str(profile[5][1])+" - " +profile[5][2][1]) 
        print("")

if __name__ == "__main__" :
    # defaults
    CoNLLfile = ""
    RDFfile   = ""
    global format
    format  = "TURTLE"
    position_of_negation = 0
    global path_for_outfile
    path_for_outfile = None
    current_directory = os.getcwd()
    
    
    # parsing of arguments
    Language_type   = 'DE'
    for position, fragment in enumerate(sys.argv) :
        if fragment == "-conll" :
            CoNLLfile = sys.argv[position+1]
        if fragment == "-rdf" :
            RDFfile = sys.argv[position+1]
        if fragment == "-format" :
            format = sys.argv[position+1]
        if fragment == "-wd":
            current_directory =sys.argv[position+1]
        if fragment == "-neg" :
            position_of_negation = sys.argv[position+1]
        if fragment == "-out" :
            path_for_outfile = sys.argv[position+1]
        
    # change the working directory
    os.chdir(current_directory)

    #print(os.listdir(current_directory))
    if (CoNLLfile == "") :
        print("No CoNLL file given. Aborting...")
    elif (RDFfile == "") :
        print("No RDF file given. Aborting...")#
    else :
        mainProcedure()

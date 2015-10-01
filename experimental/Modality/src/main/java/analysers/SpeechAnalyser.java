package analysers;

import java.util.ArrayList;
import java.util.Date;

import com.sun.org.apache.xpath.internal.operations.Mod;
import enums.Column;
import enums.Modus;
import enums.Rede;
import helpers.ConsolePrint;
import language_elements.*;


public class SpeechAnalyser extends Analyser{
	private static int sentencenr_last = -1; //nr des satzes in dem das letzte nicht geschlossene anf�hrungszeichen vorkam. -1 wenn geschlossen wurde.
	private static boolean ended_with_colon = false;
	private static ArrayList<String> pp_strings;
	private static ArrayList<String> directSpeechWords;
	private static ArrayList<String> indirectSpeechWords;
	private static final String DIR_SPEECH_CONTENT = Rede.direkte.getString();
	private static final String IND_SPEECH = Rede.indirekte.getString();

	@Override
	public void analyse(Sentence sentence){

		Long start = new Date().getTime();
		findDirectSpeech(sentence);
		findIndirectSpeech(sentence);

		time_needed+=(new Date().getTime()-start);
	}
	public SpeechAnalyser(){
		buildPersonalPronounsArray();
		buildDirectSpeechWordsArray();
		buildIndirectSpeechKeyWordsArray();
		stats.put(DIR_SPEECH_CONTENT, 0);
		stats.put(IND_SPEECH, 0);
	}
	
	private void findDirectSpeech(Sentence sentence){
		//http://www.duden.de/sprachwissen/rechtschreibregeln/anfuehrungszeichen

		//aktuell finden durch :
        //  - ob schlüsselwörter, wie "sagen", "erklären" usw. irgendwo im kompletten Satz vorkommen
        //  - ob ausgewählte personalpronomen innerhalb von Anführungszeichen vorkommen
        //  - nur ein Anführungszeichen im Satz -> entweder beginnt oder endet hier direkte Rede
        //  - ob das letzte Token innerhalb von Anführungszeichen ein Satzzeichen ist (?!.)
        //  - ob der vorhergehende Satz mit Doppelpunkt aufgehört hat in Kombination mit Anführungszeichen im aktuellen Satz.
		// 	- ob zwischen den beiden anführungszeichen mehr als 10 wörter vorkommen -> eher kein titel sondern direkte rede


		//durch speechwords und ended_with_colon restriktion fallen 50% der s�tze weg;
        //wird später nochmal verfeinert indem der inhalt zwischen den anführungszeichen auf keyowrds gecheckt wird.
		Boolean hasKeyword = (sentence.containsLemma2String(directSpeechWords) || ended_with_colon);
		//check for 1./2. person personal pronouns - wahrscheinlich keine gute idee, da direkte rede nur mit anf�hrunsgzeichen

			//check for quotation marks
			ArrayList<Token> qMarks = sentence.findTokensByTokenString("\"");
			
			//genau ein anf�hrungszeichen wenn direkte rede �ber mehrere S�tze geht. gute erkennung

			if(qMarks.size()%2 != 0){ //ungerade anzahl an anführungszeichen.
			// Entweder direkte Rede hat schon begonnen oder geht �ber diesen Satz

				if(sentencenr_last >=0){ //direkte Rede hat zuvor schon begonnen -> diese beenden. Restliche Anzahl muss gerade sein
				// -> Endet auch in diesem Satz.
					//taggen bis zu first
					int first = qMarks.remove(0).getNumber()-1;
					sentence.tagIOBES(null,first, Column.REDE, DIR_SPEECH_CONTENT);
					sentencenr_last = -1;
					stats.put(DIR_SPEECH_CONTENT,stats.get(DIR_SPEECH_CONTENT)+1);

					tagCompleteSpeechParts(qMarks, sentence, hasKeyword);
					//satz�bergreifende rede beginnt hier mit unbekannter anzahl an abgeschlossenen reden dazwischen.
				}else{
					while(qMarks.size()>1){ //solange zwischen Anf�hrungszeichen taggen bis nur noch eins �brug
                        int start = qMarks.remove(0).getNumber()-1;
                        int end = qMarks.remove(0).getNumber()-1;
                        if(hasKeyword || (sentence.containsTokenString(pp_strings, start, end))){
                            sentence.tagIOBES(start,end, Column.REDE, DIR_SPEECH_CONTENT);
							stats.put(DIR_SPEECH_CONTENT,stats.get(DIR_SPEECH_CONTENT)+1);
                        }

					}
					// letztes token muss satz�bergreifende Rede beginnen.
					int first = qMarks.remove(0).getNumber()-1;
					sentencenr_last = sentence.getId();
					sentence.tagIOBES(first,null, Column.REDE, DIR_SPEECH_CONTENT);
				}
				//gerade anzahl -> entweder gar keine anf�hrungszeichen oder alle im satz abgeschlossen.
			}else {
				if (qMarks.size() == 0 && sentencenr_last >= 0) {
					sentence.tagIOBES(null, null, Column.REDE, DIR_SPEECH_CONTENT);
				} else if (qMarks.size() > 0) {
					tagCompleteSpeechParts(qMarks, sentence, hasKeyword);
				}
			}

		//wichtiges zeichen, ob der n�chste Satz vll eine direkte Rede ist.
		ended_with_colon = sentence.getLastToken().getTokenString().equals(":");
	}




	
	private void findIndirectSpeech(Sentence sentence){
		ArrayList<Token> conj_tokens = sentence.findTokensByMorphology("subj");
		Token root = sentence.getRoot();
		if(!root.getModus().equals(Modus.conjunctive.getString())){
			for(String pattern2: directSpeechWords){
				if(root.getLemma2().equals(pattern2+"_")){

					if(conj_tokens.size()>0){
						stats.put(IND_SPEECH,stats.get(IND_SPEECH)+1);
						sentence.tagIOBES(0,sentence.getLastIndex(), Column.REDE, Rede.indirekte.getString());
					}
				}
			}
			if(conj_tokens.size()>0 && sentence.containsLemma2String(directSpeechWords)){
				sentence.tagIOBES(0,sentence.getLastIndex(), Column.REDE, Rede.indirekte.getString());
				stats.put(IND_SPEECH, stats.get(IND_SPEECH) + 1);
			}
		}else{

			//ConsolePrint.printPraedikat(sentence.getRoot(), sentence.findPraedikat());
			//ConsolePrint.printSentence(sentence);
			//
		}
	}


	private void tagCompleteSpeechParts(ArrayList<Token> qMarks, Sentence sentence, Boolean hasKeyword){
		while(!qMarks.isEmpty()){ //solange zwischen Anf�hrungszeichen taggen bis keine mehr �brig
			int start = qMarks.remove(0).getNumber()-1;
			int end = qMarks.remove(0).getNumber()-1;
			hasKeyword = hasKeyword || (sentence.containsTokenString(pp_strings, start, end)) || sentence.getTokens().get(end-1).getPOS_EXT().equals("$.") || end-start > 10;
			if(hasKeyword){
				sentence.tagIOBES(start, end, Column.REDE, DIR_SPEECH_CONTENT);
				stats.put(DIR_SPEECH_CONTENT,stats.get(DIR_SPEECH_CONTENT)+1);
			}

		}
	}
	
	private void buildPersonalPronounsArray(){
		if(SpeechAnalyser.pp_strings == null){
			SpeechAnalyser.pp_strings = new ArrayList<>();
			SpeechAnalyser.pp_strings.add("ich");
			SpeechAnalyser.pp_strings.add("mich");
			SpeechAnalyser.pp_strings.add("mir");
			SpeechAnalyser.pp_strings.add("du");
			SpeechAnalyser.pp_strings.add("mein");
			SpeechAnalyser.pp_strings.add("meiner");
			SpeechAnalyser.pp_strings.add("meines");
			SpeechAnalyser.pp_strings.add("meine");
			SpeechAnalyser.pp_strings.add("dein");
			SpeechAnalyser.pp_strings.add("deiner");
			SpeechAnalyser.pp_strings.add("deine");
			SpeechAnalyser.pp_strings.add("deines");
			SpeechAnalyser.pp_strings.add("dir");
			SpeechAnalyser.pp_strings.add("dich");
			SpeechAnalyser.pp_strings.add("unser");
			SpeechAnalyser.pp_strings.add("unseres");
		}
	}

	private void buildIndirectSpeechKeyWordsArray(){
		indirectSpeechWords = new ArrayList<>();
		indirectSpeechWords.add(" er habe ");
		indirectSpeechWords.add(" sie habe ");
		indirectSpeechWords.add(" es habe ");
		indirectSpeechWords.add(" habe er ");
		indirectSpeechWords.add(" habe sie ");
		indirectSpeechWords.add(" habe es ");
	}
	
	private void buildDirectSpeechWordsArray(){
		if(directSpeechWords == null){
			directSpeechWords = new ArrayList<>();
			directSpeechWords.add("lauten");
			directSpeechWords.add("reden");
			directSpeechWords.add("sagen");
			directSpeechWords.add("kommentieren");
			directSpeechWords.add("glauben");
			directSpeechWords.add("halten");
			directSpeechWords.add("finden");
			directSpeechWords.add("schreiben");
			directSpeechWords.add("unterstreichen");
			directSpeechWords.add("berichten");
			directSpeechWords.add("begrüßen");
			directSpeechWords.add("sprechen");
			directSpeechWords.add("erzählen");
			directSpeechWords.add("behaupten");
			directSpeechWords.add("einwenden");
			directSpeechWords.add("äußern");
			directSpeechWords.add("meinen");
			directSpeechWords.add("schreien");
			directSpeechWords.add("schimpfen");
			directSpeechWords.add("nörgeln");
			directSpeechWords.add("bezeichnen");
			directSpeechWords.add("warnen");
			//directSpeechWords.add("so_");
			
		}
	}
}

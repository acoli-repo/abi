package analysers;

import com.sun.org.apache.xpath.internal.operations.Mod;
import enums.Column;
import enums.FiniteVerbTags;
import enums.InfinitivVerbTags;
import enums.Modus;
import helpers.ConsolePrint;
import language_elements.Sentence;
import language_elements.Token;

import java.util.ArrayList;
import java.util.Date;

public class ModusAnalyser extends Analyser {
    private static final String CONJUNCTIVE = Modus.conjunctive.getString();
    private static final String IMPERATIVE = Modus.imperative.getString();
    private static final String INDICATIVE = Modus.indicative.getString();
    public ModusAnalyser(){
        stats.put(CONJUNCTIVE, 0);
        stats.put(IMPERATIVE, 0);
        stats.put(INDICATIVE, 0);
    }
    @Override
    public void analyse(Sentence sentence){
        Long start = new Date().getTime();
        if(!findImperative(sentence) && !findIndicative(sentence)){
            findConjunctive(sentence);
        }


        time_needed+=(new Date().getTime()-start);
    }

    private boolean findImperative(Sentence s){
        Token root = s.getRoot();
        if(root.getMorphology().contains("imp")){
            root.setModus(IMPERATIVE);
            stats.put(IMPERATIVE, stats.get(IMPERATIVE) + 1);

            return true;
        }
        return false;
    }

    private boolean findIndicative(Sentence s){
        Token root = s.getRoot();
        if(root.getMorphology().contains("ind")){
            root.setModus(INDICATIVE);
            stats.put(INDICATIVE, stats.get(INDICATIVE)+1);
            ArrayList<Token> pr = s.findPraedikat();
            for(Token t : pr){
                t.setModus(INDICATIVE);
            }
            return true;
        }
        return false;
    }

    private void findConjunctive(Sentence s){
        Token root = s.getRoot();
        String pos = root.getPOS_EXT();
        if(root.isFiniteVerb()){
            root.setModus(CONJUNCTIVE);
            s.tagPraedikat(Column.MODUS, Modus.conjunctive);
            stats.put(CONJUNCTIVE, stats.get(CONJUNCTIVE)+1);
        }


        for(InfinitivVerbTags itag: InfinitivVerbTags.values()){
            if(pos.equals(itag.name())){
                //hauptsatzwertige infitivephrase
            }
        }
    }
}

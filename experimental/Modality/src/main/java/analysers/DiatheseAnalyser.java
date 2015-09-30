package analysers;

import enums.Column;
import enums.Diathese;
import language_elements.Sentence;
import language_elements.Token;

import java.util.Date;


public class DiatheseAnalyser extends Analyser{
    private static final String PASSIVE = Diathese.passive.getString();
    private static final String ACTIVE = Diathese.active.getString();

    public DiatheseAnalyser(){
        stats.put(PASSIVE, 0);
        stats.put(ACTIVE,0);
    }
    @Override
    public void analyse(Sentence sentence){
        Long start = new Date().getTime();
        findDiathese(sentence);
        time_needed+=(new Date().getTime()-start);
    }

    //wenn root hilfsverb f�r passiv ist -> passive, sonst -> active
    private void findDiathese(Sentence sentence){
        Token root = sentence.getRoot();
        if(root.getLemma2().contains("passiv")){
            stats.put(PASSIVE, stats.get(PASSIVE)+1);
            root.setDiathese(PASSIVE);
            sentence.tagPraedikat(Column.DIATHESE, Diathese.passive);
        }else if(root.isFiniteVerb()|| root.isInfinitivVerb()){
            stats.put(ACTIVE, stats.get(ACTIVE)+1);
            root.setDiathese(ACTIVE);
            sentence.tagPraedikat(Column.DIATHESE, Diathese.active);
        }

    }

}

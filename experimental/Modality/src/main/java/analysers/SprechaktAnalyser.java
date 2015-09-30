package analysers;


import enums.Sprechakt;
import enums.Tempus;
import helpers.ConsolePrint;
import language_elements.Sentence;
import language_elements.Token;

public class SprechaktAnalyser extends Analyser {


    @Override
    public void analyse(Sentence sentence) {
        Token root = sentence.getRoot();
        findSprechakt(root,sentence);
    }
    public SprechaktAnalyser(){
        for(Sprechakt e: Sprechakt.values()){
            stats.put(e.getString(), 0);
        }
    }

    private void findSprechakt(Token root, Sentence s){
        String tkstring= root.getTokenString();
        Token last = s.getLastToken();
        String lemma = root.getLemma2();
        if(s.containsTokenString("jemals") && root.getSatzArt().contains("int")){
            //ConsolePrint.printSentence(s);
        }

        if(s.containsTokenString("frage ich mich")){
            //ConsolePrint.printSentence(s);

        }
        if(s.containsLemma2String("können")){
           // ConsolePrint.printSentence(s);
        }

        if(root.getLemma2().contains("können")){
            ConsolePrint.printSentence(s);
        }
    }
}

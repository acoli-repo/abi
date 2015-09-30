package helpers;
import enums.*;
import language_elements.Sentence;
import language_elements.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ManualAnnotation {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    int ds_last = -1; //helper variable for direct speech recognition
    public ManualAnnotation(){

    }
    public void annotatePraedikat(Sentence sentence) {

        System.out.println(sentence.toHumanReadableString());
        Token root = sentence.getRoot();
        ArrayList<Token> praedikat = sentence.findPraedikat();
        System.out.print(root.getTokenString());
        for(Token t : praedikat){
            System.out.print(" "+t.getTokenString());
        }
        System.out.println("");
        praedikat.add(root);

        startTempusDialog(br, praedikat);
        startDiatheseDialog(br, praedikat);
        startAspectDialog(br, praedikat);
        startModalityDialog(br, praedikat);
        startModusDialog(br, praedikat);

    }

    public void annotateSentence(Sentence sentence){
        startRedeDialog(sentence);
        startSatzartDialog(sentence);
        startIndirekteRedeDialog(sentence);
    }


    private void startSatzartDialog(Sentence sentence){
        ConsolePrint.printSentence(sentence);
        System.out.println("declarative: 1  -  imperative: 2  -  interrogative: 3");
        String s = null;
        try {
            s = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch(s){
            case "1":
                sentence.tagIOBES(0, sentence.getLastIndex(), Column.SATZART, Satzart.declarative.getString());
                break;
            case "2":
                sentence.tagIOBES(0, sentence.getLastIndex(), Column.SATZART, Satzart.imperative.getString());
                break;
            case "3":
                sentence.tagIOBES(0, sentence.getLastIndex(), Column.SATZART, Satzart.interrogative.getString());
                break;
        }
    }

    private void startIndirekteRedeDialog(Sentence sentence){
        System.out.println("indirect speech: 1");
        String s = null;
        try {
            s = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch(s){
            case "1":
                sentence.tagIOBES(0, sentence.getLastIndex(), Column.REDE, Rede.indirekte.getString());
                break;
        }

    }

    private void startRedeDialog(Sentence sentence){
        //check for quotation marks
        int firstIndex;
        int lastIndex;
        ArrayList<Token> qMarks = sentence.findTokensByTokenString("\"");

        if(qMarks.size()>= 2){
            ConsolePrint.printSentence(sentence);
            while(qMarks.size()>1){
                firstIndex = qMarks.remove(0).getNumber()-1;
                lastIndex = qMarks.remove(0).getNumber()-1;
                processSubSentence(sentence, firstIndex, lastIndex);
            }

            if(qMarks.size()>0){
                //process start of direct speech
                firstIndex = qMarks.remove(0).getNumber()-1;
                processSubSentence(sentence,firstIndex, null);
            }
        }else if(qMarks.size()>0){
            if(ds_last>0){
                //direct speech ends here
                firstIndex = qMarks.remove(0).getNumber()-1;
                processSubSentence(sentence,null, firstIndex);
            }else{
                //direct speech begins here
                firstIndex = qMarks.remove(0).getNumber()-1;
                processSubSentence(sentence,firstIndex, null);
            }
        }else if(qMarks.size()==0 && ds_last >=0){
            processSubSentence(sentence, null, null);
        }
    }

    private void processSubSentence(Sentence sentence, Integer first, Integer last){
        Integer mLast = last == null ? sentence.getLastIndex() : last;
        Integer mFirst = first == null ? 0 : first;
        String s = null;
        ArrayList<Token> tokens = sentence.getTokens();
        List<Token> sub_sentence = tokens.subList(mFirst, mLast+1);
        String sub_string="";
        for(Token t: sub_sentence){
            sub_string = sub_string.concat(t.getTokenString()+" ");
        }
        System.out.println(sub_string);
        System.out.println("direct speech: 1");
        try {
            s = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch(s){
            case "1":
                if(first==null)ds_last = -1;
                if(last == null)ds_last = sentence.getId();
                sentence.tagIOBES(first, last, Column.REDE, Rede.direkte.getString());
                break;
        }
    }

    private void startModusDialog(BufferedReader br, ArrayList<Token> p){
        System.out.println("indicative: 1  -  conjunctive: 2  -  imperative: 3");
        String s = null;
        try {
            s = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch(s){
            case "1":
                for(Token t: p){
                    t.setModus(Modus.indicative.getString());
                }
                break;
            case "2":
                for(Token t: p){
                    t.setDiathese(Modus.conjunctive.getString());
                }
                break;
            case "3":
                for(Token t: p){
                    t.setDiathese(Modus.imperative.getString());
                }
                break;
        }
    }

    private void startDiatheseDialog(BufferedReader br,ArrayList<Token> p){
        System.out.println("active: 1 - passive: 2");
        String s = null;
        try {
            s = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch(s){
            case "1":
                for(Token t: p){
                    t.setDiathese(Diathese.active.getString());
                }
                break;
            case "2":
                for(Token t: p){
                    t.setDiathese(Diathese.passive.getString());
                }
                break;
        }
    }

    private void startAspectDialog(BufferedReader br,ArrayList<Token> p){
        System.out.println("imperfect: 1 - perfect: 2");
        String s = null;
        try {
            s = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch(s){
            case "1":
                for(Token t: p){
                    t.setAspekt(Aspekt.imperfect.getString());
                }
                break;
            case "2":
                for(Token t: p){
                    t.setAspekt(Aspekt.perfect.getString());
                }
                break;
        }
    }
    private void startModalityDialog(BufferedReader br,ArrayList<Token> p){
        System.out.println("epistemic: 1 - nonepistemic: 2");
        String s = null;
        try {
            s = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch(s){
            case "1":
                for(Token t: p){
                    t.setModality(Modality.epistemic.getString());
                }
                break;
            case "2":
                for(Token t: p){
                    t.setModality(Modality.nonepistemic.getString());
                }
                break;
        }
    }

    private void startTempusDialog(BufferedReader br ,ArrayList<Token> p){
        System.out.println("past 1 - present 2 - futur 3");
        String s = null;
        try {
            s = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch(s){
            case "1":
                for(Token t: p){
                    t.setTempus(Tempus.past.getString());
                }
                break;
            case "2":
                for(Token t: p){
                    t.setTempus(Tempus.present.getString());
                }
                break;
            case "3":
                for(Token t: p){
                    t.setTempus(Tempus.future.getString());
                }
                break;
        }
    }
}



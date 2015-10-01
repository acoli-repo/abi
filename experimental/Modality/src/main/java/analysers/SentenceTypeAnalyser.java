package analysers;

import java.util.Date;

import enums.Column;
import enums.Satzart;
import helpers.ConsolePrint;
import language_elements.Sentence;
import language_elements.Token;

public class SentenceTypeAnalyser extends Analyser{
	private static final String IMPERATIVE = Satzart.imperative.getString();
	private static final String INTERROGATIVE = Satzart.interrogative.getString();
	private static final String DECLARATIVE = Satzart.declarative.getString();

	public SentenceTypeAnalyser(){
		stats.put(IMPERATIVE, 0);
		stats.put(INTERROGATIVE, 0);
		stats.put(DECLARATIVE,0);
	}
	@Override
	public void analyse(Sentence sentence){
		Long start = new Date().getTime();
		if(!findImperative(sentence) && !findInterrogative(sentence)){
			findDeclarative(sentence);
		}


		time_needed+=(new Date().getTime()-start);
	}	
	
	private boolean findImperative(Sentence sentence){
		Token root = sentence.getRoot();
		Token last = sentence.getLastToken();
		if((root.containsMorphology("imp") || last.getTokenString().equals("!"))&&root.containsMorphology("2")){
			//funktioniert ganz gut
			stats.put(IMPERATIVE, stats.get(IMPERATIVE)+1);
			sentence.tagIOBES(0, sentence.getLastIndex(), Column.SATZART, IMPERATIVE);
			return true;
		}

		return false;
	}
	
	private boolean findInterrogative(Sentence sentence){
		boolean endWithQMark = sentence.toHumanReadableString().endsWith("?");
		boolean containsColon = sentence.toHumanReadableString().contains(":");
		if(endWithQMark && !containsColon){
			sentence.tagIOBES(0, sentence.getLastIndex(), Column.SATZART, INTERROGATIVE);
			stats.put(INTERROGATIVE, stats.get(INTERROGATIVE)+1);
			return true;


		}else{
			sentence.tagIOBES(0, sentence.getLastIndex(), Column.SATZART, DECLARATIVE);
		}
		return false;
	}

	private void findDeclarative(Sentence sentence){
		Token root = sentence.getRoot();
		if(root.isFiniteVerb()){
			sentence.tagIOBES(0, sentence.getLastIndex(), Column.SATZART, DECLARATIVE);
			stats.put(DECLARATIVE, stats.get(DECLARATIVE)+1);
		}


	}
}

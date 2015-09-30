package analysers;

import java.util.ArrayList;
import java.util.Date;

import enums.Aspekt;
import enums.Column;
import enums.InfinitivVerbTags;
import helpers.ConsolePrint;
import language_elements.Sentence;
import language_elements.Token;

public class AspektAnalyser extends Analyser{
	private final String IMPERFEKT = Aspekt.imperfect.getString();
	private final String PERFEKT = Aspekt.perfect.getString();

	public AspektAnalyser(){
		stats.put(PERFEKT, 0);
		stats.put(IMPERFEKT, 0);
	}


	// http://hypermedia.ids-mannheim.de/call/public/termwb.ansicht?v_app=p&v_id=3174


	private void findImperfect(Sentence sentence){
		Token root = sentence.getRoot();
		String lemma = root.getLemma2();
		//pr�dikate die haben/sein als Hilfsverb beinhalten
		if(!lemma.equals("haben%aux_") && !lemma.equals("sein%aux_") && root.containsMorphology("past") && root.isFiniteVerb()){
			root.setAspekt(IMPERFEKT);
			sentence.tagPraedikat(Column.ASPEKT, Aspekt.imperfect);
			stats.put(IMPERFEKT, stats.get(IMPERFEKT)+1);
		}
	}

	private void findPerfect(Sentence sentence){
		Token root = sentence.getRoot();
		String lemma = root.getLemma2();
		ArrayList<Token> pr = sentence.findPraedikat();
		boolean containsPP = false;
		for(Token t : pr){
			if(t.isPartizipPerfektVerb()){
				containsPP =true;
			}
		}
		//pr�dikate die haben/sein als Hilfsverb beinhalten
		if((lemma.equals("haben%aux_")
				|| lemma.equals("sein%aux_")) && containsPP){
			ArrayList<Token> praedikat = sentence.findPraedikat();
			root.setAspekt(PERFEKT);
			stats.put(PERFEKT, stats.get(PERFEKT) + 1);
			sentence.tagPraedikat(Column.ASPEKT, Aspekt.perfect);
		}
	}

	@Override
	public void analyse(Sentence sentence) {
		Long start = new Date().getTime();
		findPerfect(sentence);
		findImperfect(sentence);
		time_needed+=(new Date().getTime()-start);
	}
}

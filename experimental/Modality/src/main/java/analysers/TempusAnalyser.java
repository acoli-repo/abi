package analysers;

import enums.Column;
import enums.Tempus;
import helpers.ConsolePrint;
import language_elements.*;

import java.util.ArrayList;
import java.util.Date;

public class TempusAnalyser extends Analyser{
	private static String FUTUR = Tempus.future.getString();
	private static String PRESENT = Tempus.present.getString();
	private static String PAST = Tempus.past.getString();
	public TempusAnalyser(){
		for(Tempus e: Tempus.values()){
			stats.put(e.getString(), 0);
		}
	}
	@Override
	public void analyse(Sentence sentence){

		Long start = new Date().getTime();
		Token root = sentence.getRoot();

		if(root != null){
			ArrayList<Token> praedikat = sentence.findPraedikat();
			findPast(root, praedikat);
			findFuture(root, praedikat);
			findPraesens(root, praedikat);
		}
		time_needed+=(new Date().getTime()-start);
	}
	
	
	
	private void findPast(Token token, ArrayList<Token> praedikat){
		String lemma = token.getLemma2();
		String tokenstring = token.getTokenString();
		if(token.containsMorphology("past") && token.isFiniteVerb()) {
			if(lemma.contains("werden") && !tokenstring.contains("ü")){
				token.setTempus(PAST);
				token.getSentence().tagPraedikat(Column.TEMPUS, Tempus.past);
			}else if(!lemma.contains("werden")){
				token.setTempus(PAST);
				token.getSentence().tagPraedikat(Column.TEMPUS, Tempus.past);
			}



		}else if(praedikat.size()>0 && (lemma.equals("haben%aux_")|| lemma.equals("sein%aux_"))){
			//findet praedikate die jeweils morphologisch mit pres annotiert sind aber zusammen past sind ->
			// sind gescheitert, hat gefordert, ist geblieben, haben gebillig
			token.setTempus(PAST);
			praedikat.get(0).setTempus(PAST);
			stats.put(PAST, stats.get(PAST) + 1);
		}
	}
	

	private void findPraesens(Token token, ArrayList<Token> praedikat){
		if(token.containsMorphology("pres") && token.getTempus().equals("_")){
			token.setTempus(PRESENT);

			for(Token t : praedikat){
				t.setTempus(PRESENT);
			}
			stats.put(PRESENT, stats.get(PRESENT) + 1);
		}
	}

	private void findFuture(Token token, ArrayList<Token> praedikat){
		//alle S�tze, die "werden" als Auxiliarverb beinhalten und nicht im Konjungtiv stehen sind Futur. Wenn werden als passiv oder kein Auxiliarverb ist, ist der Satz im Pr�sens.
		if(token.getLemma2().equals("werden%aux_")) {
			if((token.containsMorphology("past") && !token.getTokenString().contains("u"))||token.containsMorphology("pres")){
				for (Token t : praedikat) {
					t.setTempus(FUTUR);
				}
				token.setTempus(FUTUR);
				stats.put(FUTUR,stats.get(FUTUR)+1);
			}

		}
	}
}

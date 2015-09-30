
package analysers;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionService;

import com.sun.org.apache.xpath.internal.operations.Mod;
import enums.Column;
import enums.Modality;
import enums.Modus;
import enums.Tempus;
import helpers.ConsolePrint;
import language_elements.*;

public class ModalityAnalyser extends Analyser{
	private final String EPISTEMIC = Modality.epistemic.getString();
	private final String NONEPISTEMIC = Modality.nonepistemic.getString();

	private final String [] modalparticles= {"ja", "eben", "halt", "auch", "doch", "schon", "denn", "etwa", "nur", "bloß", "aber", "vielleicht", "wohl"};

	public ModalityAnalyser(){
		stats.put(EPISTEMIC, 0);
		stats.put(NONEPISTEMIC, 0);
	}
	@Override
	public void analyse(Sentence sentence){
		Long start = new Date().getTime();
		findModality(sentence);
		time_needed+=(new Date().getTime()-start);
	}
	
	private void findModality(Sentence sentence){

		// http://hypermedia.ids-mannheim.de/call/public/sysgram.ansicht?v_id=1552



		Token root = sentence.getRoot();
		ArrayList<Token> pr = sentence.findPraedikat();
		boolean containsModalVerb = false;
		boolean containsHaben = false;
		for(Token t : pr){
			if(t.getPOS_EXT().contains("VM")){
				containsModalVerb = true;
			}
			if(t.getLemma2().contains("haben")){
				containsHaben = true;
			}
		}
		if(root.getPOS_EXT().equals("VMFIN")){
			if(root.getLemma2().contains("sollen")){
				stats.put(NONEPISTEMIC, stats.get(NONEPISTEMIC) + 1);
				root.setModality(NONEPISTEMIC);
				sentence.tagPraedikat(Column.MODALITY, Modality.nonepistemic);

			}
			if(root.getLemma2().contains("können")){
				stats.put(NONEPISTEMIC, stats.get(NONEPISTEMIC)+1);
				root.setModality(NONEPISTEMIC);
				sentence.tagPraedikat(Column.MODALITY, Modality.nonepistemic);
			}
			if(containsHaben && pr.size()>1){
				//Modalverb + Partizip Perfekt + haben. 
				stats.put(EPISTEMIC, stats.get(EPISTEMIC)+1);
				root.setModality(EPISTEMIC);
				sentence.tagPraedikat(Column.MODALITY, Modality.epistemic);
			}
			if (root.getModus().equals(Modus.conjunctive.getString())){
				if(root.getTempus().equals(Tempus.past.getString())){
					//dürfte präteritum konjunktiv -> epistemisch
					if(root.getLemma2().equals("dürfen%aux_")){
						stats.put(EPISTEMIC, stats.get(EPISTEMIC) + 1);
						root.setModality(EPISTEMIC);
						sentence.tagPraedikat(Column.MODALITY, Modality.epistemic);
					}

				}else if(root.containsMorphology("past")){

				}else if(root.containsMorphology("pres") && root.getTempus().equals(Tempus.future.getString())){
					//kein futur mit vmfin als wurzel möglich
				}else if(root.containsMorphology("pres")){

				}
			}
		}else if (containsModalVerb){

			//entweder perfekt oder futur -> nicht epistemisch
			//z.B. Das Gegenteil wird man nicht beweisen können. -> nichtepistemisch
			// Wir hatten dagegen Herrn KPS daran erinnern dürfen , daß den ...
			stats.put(NONEPISTEMIC, stats.get(NONEPISTEMIC)+1);
			root.setModality(NONEPISTEMIC);
			sentence.tagPraedikat(Column.MODALITY, Modality.nonepistemic);
		}

		

	}
}

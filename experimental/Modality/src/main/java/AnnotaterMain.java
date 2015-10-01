import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

import analysers.*;
import helpers.ConllWriter;
import helpers.Evaluator;
import helpers.ManualAnnotation;
import language_elements.*;

public class AnnotaterMain {

	//run config:
	//never set both on true
	//both false -> automated annotation
	private static final boolean MANUAL_ANNOTATION = false;
	private static final boolean EVALUATION = true;


	private static final String INPUT_FILE = "korpus2.txt";
	private static final String MANUAL_OUT = "korpus2_ref_out";
	private static final String AUTO_OUT = "korpus2_auto_out";



	public static void main(String[] args) {
		Long start = new Date().getTime();
		
		if(INPUT_FILE == null){
			System.out.println("filepath argument needed");
			return;
		}
		startAnalysis(args);
		if(AnnotaterMain.EVALUATION){
			try {
				Evaluator.eval(AUTO_OUT +".txt", MANUAL_OUT+".txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{

		}
		ConllWriter.closeWriter();
		System.out.println("Time elapsed: "+ (new Date().getTime()-start)+ "ms");

	}
	

	private static void startAnalysis(String[] args){
		ManualAnnotation manualAnnotation = new ManualAnnotation();
		ArrayList<Analyser> analysers = new ArrayList();
		analysers.add(new AspektAnalyser());
		analysers.add(new DiatheseAnalyser());
		analysers.add(new ModusAnalyser());
		analysers.add(new SentenceTypeAnalyser());
		analysers.add(new SpeechAnalyser());
		analysers.add(new TempusAnalyser());
		analysers.add(new ModalityAnalyser());
		analysers.add(new SprechaktAnalyser());
		if(MANUAL_ANNOTATION){
			ConllWriter.setOutputFileName(MANUAL_OUT);
		}else{
			ConllWriter.setOutputFileName(AUTO_OUT);
		}
		Path file = Paths.get(INPUT_FILE);
		Sentence sentence = new Sentence();
		try (BufferedReader reader = Files.newBufferedReader(file)) {
		    String line;
		    int count = 0;
		    while ((line = reader.readLine()) != null && count < 100000000) {
		    	count++;
	
		    	Token token = new Token(line);

		    	if(line.isEmpty()){

					if(MANUAL_ANNOTATION){
						manualAnnotation.annotatePraedikat(sentence);
						manualAnnotation.annotateSentence(sentence);
					}else{
						for(Analyser a: analysers){
							a.analyse(sentence);
						}
					}

					ConllWriter.printSentence(sentence);
		    		sentence = new Sentence();
		    	}else{
		    		//ConsolePrint.printTokenInfo(token);
		    		sentence.addToken(token);
		    	}
		    }
			if(!MANUAL_ANNOTATION){
				for(Analyser a: analysers){
					a.getStatistic();
				}
			}


		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
	
	
	
	

}

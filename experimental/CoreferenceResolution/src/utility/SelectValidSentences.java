package utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import openNLP.OpenNLPCoreferenceResolution;
import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.Linker;
import opennlp.tools.coref.LinkerMode;
import stanfordCoreNLP.StanfordCoreferenceResolution;

/**
 * Determine sentences that be processed by Stanford and OpenNLP coreference resolution system and write them to file.
 * @author Sergej Jaschonkow
 *
 */

public class SelectValidSentences {

	/**
	 * Determine sentences that be processed by Stanford and OpenNLP coreference resolution system and write them to file.
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args){
		
		File srcDir = new File(args[0]);
		File destDir = new File(args[1]);
		File[] inputFiles = srcDir.listFiles();

		boolean parseWithCorefResInfos = Boolean.parseBoolean(args[2]);
		boolean includeInputCorefs = Boolean.parseBoolean(args[3]);
		boolean includeSelfReferences = Boolean.parseBoolean(args[4]);

		// line splitter for splitting line contents of input files
		String lineSplitter = "\\s{2,}|\\t+";
		
		// include NER information for coreference resolution
		boolean includeNER = Boolean.parseBoolean(args[5]);
		
		// beta for calculating F-Measure
		double beta = Integer.parseInt(args[6]);
		
		String filePathLinker = args[7];
		LinkerMode linkerMode = LinkerMode.TEST;
		
		// create new stanford coreference resolution instance
		StanfordCoreferenceResolution StanfordCorefResolution = new stanfordCoreNLP.StanfordCoreferenceResolution(new Properties());
		
		// create coreference linker
		Linker corefLinker = OpenNLPCoreferenceResolution.createCorefLinker(filePathLinker, linkerMode);
		
		for (File inputFile : inputFiles) {
			
			// parse input file
			ArrayList<ArrayList<ArrayList<String>>> inputSentences = utility.ParserCoNLL.readCoNLLFile(inputFile, lineSplitter, parseWithCorefResInfos);
			
			// create sentence and tokens annotations
			List<CoreMap> sentences = StanfordCoreferenceResolution.createTokenSentenceAnnotations(inputSentences, includeNER);

			// create stanford annotation object
			Annotation stanfordAnnotation = new Annotation(sentences);

			// call StanfordCoreNLP coreference resolution system
			stanfordAnnotation = StanfordCoreferenceResolution.annotateCorefRes(stanfordAnnotation);

			// create copy of valid sentences of parsed input document
			ArrayList<ArrayList<ArrayList<String>>> inputSentencesStanfordCorefRes = utility.util.copySentences(StanfordCoreferenceResolution.validInputSentences, includeInputCorefs);

			// add stanford coreference annotations to input sentences
			inputSentencesStanfordCorefRes = StanfordCoreferenceResolution.addStanfordCorefs(stanfordAnnotation, includeSelfReferences, inputSentencesStanfordCorefRes);
		
			// create new openNLP coreference resolution instance
			OpenNLPCoreferenceResolution openNLPCorefResolution = new openNLP.OpenNLPCoreferenceResolution();
			
			// get coreferences
			DiscourseEntity[] coreferences = openNLPCorefResolution.getCoreferences(corefLinker, inputSentences, includeNER);
			
			// create copy of parsed input document
			ArrayList<ArrayList<ArrayList<String>>> inputSentencesOpenNLPCorefRes = utility.util.copySentences(openNLPCorefResolution.validInputSentences, includeInputCorefs);
		
			// add openNLP coreference annotations to input sentences
			inputSentencesOpenNLPCorefRes = openNLPCorefResolution.addOpenNLPCorefs(coreferences, includeSelfReferences, inputSentencesOpenNLPCorefRes);
		
			// get common valid sentences of both systems
			ArrayList<ArrayList<ArrayList<String>>> validInputSentences = new ArrayList<ArrayList<ArrayList<String>>>(StanfordCoreferenceResolution.validInputSentences);
			validInputSentences.retainAll(openNLPCorefResolution.validInputSentences);
			
			// write valid input sentences to file
			File destFilePath = new File(destDir, inputFile.getName());
			ParserCoNLL.writeCoNLLFile(destFilePath, validInputSentences, "\t", "\n");
		}
	}
}
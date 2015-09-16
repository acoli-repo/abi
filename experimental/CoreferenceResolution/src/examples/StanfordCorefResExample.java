package examples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.stats.PrecisionRecallStats;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;
import stanfordCoreNLP.StanfordCoreferenceResolution;
import utility.ParserCoNLL;

/**
 * Example class on how to annotate a document with stanford coreference annotations, write them to file and calculate precision, recall and F-Measure.
 * 
 * @author Sergej Jaschonkow
 * 
 */
public class StanfordCorefResExample {

	/**
	 * Annotate input document with stanford coreference annotations, saves document to file and calculate precision, recall and F-Measure.
	 * 
	 * @param args
	 * @throws IOException 
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {

		File srcDir = new File(args[0]); // source folder containing files to be processed
		File destDir = new File(args[1]); // destination folder to save processed files to
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
		
		// create new stanford coreference resolution instance
		StanfordCoreferenceResolution corefResolution = new stanfordCoreNLP.StanfordCoreferenceResolution(new Properties());

		// create new precision instance to aggregate performance metrics of all input files
		PrecisionRecallStats precisionRecallStats = new PrecisionRecallStats();
		
		// create file writer for performance metrics
		FileWriter fw = new FileWriter(new File(destDir, "performance_metrics.txt"));
		
		// write headers to performance metrics file
		fw.append("fileName,");
		fw.append("numTokens,");
		fw.append("numSentences,");
		fw.append("truePositives,");
		fw.append("falsePositives,");
		fw.append("falseNegatives,");
		fw.append("precision,");
		fw.append("recall,");
		fw.append("F(" + beta + ")-Measure\n");
		
		int totalNumSentences = 0;
		int totalNumTokens = 0;
		
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

			// calculate number of sentences and tokens in current document
			int numTokens = utility.util.calculateNumTokens(inputSentencesStanfordCorefRes);
			int numSentences = inputSentencesStanfordCorefRes.size();
			
			// skip document if its contains zero valid sentences
			if(numSentences == 0){
				continue;
			}
			
			// write input sentences with stanford coreference annotations to file
			File destFilePath = new File(destDir, (inputFile.getName() + ".stanf.coref.res.txt"));
			ParserCoNLL.writeCoNLLFile(destFilePath, inputSentencesStanfordCorefRes, "\t", "\n");

			// extract corerefernces from gold standard
			TreeMap<String, ArrayList<IntPair>> goldStandCorefIDs = utility.util.extractCoreferenceIDs(StanfordCoreferenceResolution.validInputSentences);

			// extract stanford coreference annotations
			TreeMap<String, ArrayList<IntPair>> stanfordCorefIDs = utility.util.extractCoreferenceIDs(inputSentencesStanfordCorefRes);

			// calculate true positives (tp), false positves (fp) and false negatives (fn)
			PrecisionRecallStats currentPrecisionRecallStats = utility.util.calculatePrecisionRecallFMeasure(goldStandCorefIDs, stanfordCorefIDs);
			
			// add performance metrics of current document to aggregated performance metrics
			precisionRecallStats.addCounts(currentPrecisionRecallStats);
			
			
			// write performance metrics of current document to file
			fw.append(inputFile.getName() + ",");
			fw.append(numTokens + ",");
			fw.append(numSentences + ",");
			fw.append(currentPrecisionRecallStats.getTP() + ",");
			fw.append(currentPrecisionRecallStats.getFP() + ",");
			fw.append(currentPrecisionRecallStats.getFN() + ",");
			fw.append(currentPrecisionRecallStats.getPrecision() + ",");
			fw.append(currentPrecisionRecallStats.getRecall() + ",");
			fw.append(utility.util.calculateFMeasure(currentPrecisionRecallStats, beta) + "\n");
			
			// update total number of sentences and tokens
			totalNumTokens += numTokens; 
			totalNumSentences += numSentences;
		}
		// write performance metrics of all documents to file
		fw.append("\nAGGREGATED (number of documents: " + inputFiles.length + ")\n");
		fw.append(",");
		fw.append(totalNumTokens + ",");
		fw.append(totalNumSentences + ",");
		fw.append(precisionRecallStats.getTP() + ",");
		fw.append(precisionRecallStats.getFP() + ",");
		fw.append(precisionRecallStats.getFN() + ",");
		fw.append(precisionRecallStats.getPrecision() + ",");
		fw.append(precisionRecallStats.getRecall() + ",");
		fw.append(utility.util.calculateFMeasure(precisionRecallStats, beta) + "\n");
		
		fw.flush();
		fw.close();
	}
}
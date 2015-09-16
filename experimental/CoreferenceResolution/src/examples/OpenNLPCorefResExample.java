package examples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import openNLP.OpenNLPCoreferenceResolution;
import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.Linker;
import opennlp.tools.coref.LinkerMode;
import utility.ParserCoNLL;
import edu.stanford.nlp.stats.PrecisionRecallStats;
import edu.stanford.nlp.util.IntPair;

/**
 * Example of how to annotate a document with OpeNLP coreference annotations, write them to file and calculate precision, recall and F-Measure.
 * 
 * @author Sergej Jaschonkow
 * 
 */
public class OpenNLPCorefResExample {

	public static void main(String[] args) throws IOException{
		
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
		
		String filePathLinker = args[7]; // file path to OpenNLP folder that contains files for coref model, e.g ".../apache-opennlp-1.5.3/models/coref"
		LinkerMode linkerMode = LinkerMode.TEST;
		
		// create coreference linker
		Linker corefLinker = OpenNLPCoreferenceResolution.createCorefLinker(filePathLinker, linkerMode);

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
			
			// create new openNLP coreference resolution instance
			OpenNLPCoreferenceResolution corefResolution = new openNLP.OpenNLPCoreferenceResolution();
			
			// get coreferences
			DiscourseEntity[] coreferences = corefResolution.getCoreferences(corefLinker, inputSentences, includeNER);
			
			// create copy of parsed input document
			ArrayList<ArrayList<ArrayList<String>>> inputSentencesOpenNLPCorefRes = utility.util.copySentences(corefResolution.validInputSentences, includeInputCorefs);
		
			// add openNLP coreference annotations to input sentences
			inputSentencesOpenNLPCorefRes = corefResolution.addOpenNLPCorefs(coreferences, includeSelfReferences, inputSentencesOpenNLPCorefRes);
			
			// calculate number of sentences and tokens in current document
			int numTokens = utility.util.calculateNumTokens(inputSentencesOpenNLPCorefRes);
			int numSentences = inputSentencesOpenNLPCorefRes.size();
			
			// skip document if its contains zero invalid sentences
			if(numSentences != 0){
				// write input sentences with openNLP coreference annotations to file
				File destFilePath = new File(destDir, (inputFile.getName() + ".openNLP.coref.res.txt"));
				ParserCoNLL.writeCoNLLFile(destFilePath, inputSentencesOpenNLPCorefRes, "\t", "\n");
				
				// extract corerefernces from input sentences, i.e. gold standard
				TreeMap<String, ArrayList<IntPair>> goldStandCorefIDs = utility.util.extractCoreferenceIDs(corefResolution.validInputSentences);
				
				// extract openNLP coreference annotations
				TreeMap<String, ArrayList<IntPair>> openNLPCorefIDs = utility.util.extractCoreferenceIDs(inputSentencesOpenNLPCorefRes);
				
				// calculate true positives (tp), false positves (fp) and false negatives (fn)
				PrecisionRecallStats currentPrecisionRecallStats = utility.util.calculatePrecisionRecallFMeasure(goldStandCorefIDs, openNLPCorefIDs);
				
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
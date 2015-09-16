package utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import edu.stanford.nlp.stats.PrecisionRecallStats;
import edu.stanford.nlp.util.CollectionUtils;
import edu.stanford.nlp.util.IntPair;

/**
 * Utility class containing support functions for coference resolution.
 * @author Sergej Jaschonkow
 *
 */
public class util {
	
	/**
	 * Copy a list of sentences in CoNLL format to a new list.
	 * 
	 * @param sentences
	 *            A list of sentences in CoNLL format.
	 * @param includeCorefs
	 *            Boolean value indicating whether containing coreference annotations should be copied as well or not.
	 * @return List of copied sentences in CoNLL format.
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<ArrayList<ArrayList<String>>> copySentences(ArrayList<ArrayList<ArrayList<String>>> sentences, boolean includeCorefs) {

		ArrayList<ArrayList<ArrayList<String>>> copySentences = new ArrayList<ArrayList<ArrayList<String>>>(sentences.size());
		for (ArrayList<ArrayList<String>> sentence : sentences) {
			ArrayList<ArrayList<String>> copySentence = new ArrayList<ArrayList<String>>(sentence.size());
			for (ArrayList<String> tokenInfos : sentence) {
				if (!includeCorefs) {
					for (int tokenIdx = tokenInfos.size() - 1; tokenIdx >= 0; tokenIdx--) {
						String tokenInfo = tokenInfos.get(tokenIdx);

						if (!tokenInfo.contains("<COREF")) {
							List<String> updatedTokenInfos = tokenInfos.subList(0, tokenIdx + 1);

							if (tokenIdx < (tokenInfos.size() - 1)) {
								updatedTokenInfos.add("_"); // place holder for coreference annotaions
							}
							copySentence.add(new ArrayList<String>(updatedTokenInfos));
							break;
						}
					}
				} else {
					copySentence.add((ArrayList<String>) tokenInfos.clone());
				}
			}
			copySentences.add(copySentence);
		}
		return copySentences;
	}
	
	/**
	 * Calculates number of true positive (tp), false positive (fp) and false negative (fn) annotations of a given system compared to gold coreference annotation.
	 * @param goldStandCorefIDs Gold standard coreference annotations.
	 * @param annotatedCorefIDs Coreference annotations that should be compared with gold standard annotations.
	 * @return PrecisionRecallStats for calculating precision, recall and F-Measure.
	 */
	public static PrecisionRecallStats calculatePrecisionRecallFMeasure(TreeMap<String, ArrayList<IntPair>> goldStandCorefIDs, TreeMap<String, ArrayList<IntPair>> annotatedCorefIDs) {

		PrecisionRecallStats precisionRecallStats = new PrecisionRecallStats();
		int totalNumGoldStandCorefs = 0;
		for (Entry<String, ArrayList<IntPair>> goldStandCorefIDsEntry : goldStandCorefIDs.entrySet()) {
			ArrayList<IntPair> goldStandCorefSentenceTokenIndices = goldStandCorefIDsEntry.getValue();
			totalNumGoldStandCorefs += goldStandCorefSentenceTokenIndices.size();

			for (Entry<String, ArrayList<IntPair>> annotatedCorefIDsEntry : annotatedCorefIDs.entrySet()) {
				ArrayList<IntPair> annotatedCorefSentenceTokenIdices = annotatedCorefIDsEntry.getValue();

				// intersection of set of gold standard and annotated coreference sentence and token indices, e.g. identical coreferences
				Set<IntPair> overlapingCorefs = CollectionUtils.intersection(new TreeSet<IntPair>(goldStandCorefSentenceTokenIndices), new TreeSet<IntPair>(annotatedCorefSentenceTokenIdices));
				int numOverlapingCorefs = overlapingCorefs.size();

				// number of annotated coreferences of current coreference ID
				int currentNumAnnotetdCorefs = annotatedCorefSentenceTokenIdices.size();

				// calculate number of true positives(tp) and false negatives (fn) annotated coreferences
				if (numOverlapingCorefs == currentNumAnnotetdCorefs) {
					precisionRecallStats.addTP(currentNumAnnotetdCorefs);
				} else if (numOverlapingCorefs > 0) {
					for (IntPair overlapingCoref : overlapingCorefs) {
						// index of current overlaping annotation in gold standard coreference annotations
						int goldStandCorefIdx = goldStandCorefSentenceTokenIndices.indexOf(overlapingCoref);

						// index of current overlaping annotation in annotated corefernce annotations
						int annotatedCorefIdx = annotatedCorefSentenceTokenIdices.indexOf(overlapingCoref);

						// check if overlapping coference is a referent
						if (goldStandCorefSentenceTokenIndices.indexOf(overlapingCoref) == 0 || annotatedCorefSentenceTokenIdices.indexOf(overlapingCoref) == 0) {
							precisionRecallStats.incrementTP();
						} else {
							IntPair previousGoldStandCoref = goldStandCorefSentenceTokenIndices.get(goldStandCorefIdx - 1);
							IntPair previousAnnotatedCoref = annotatedCorefSentenceTokenIdices.get(annotatedCorefIdx - 1);

							// check if the previous coreference (of the current overlapping coreference annotation) is identical
							if (previousGoldStandCoref.equals(previousAnnotatedCoref)) {
								precisionRecallStats.incrementTP();
							} else {
								precisionRecallStats.incrementFP();
							}
						}
					}
				}
			}
		}
		// calculate number of false negatives(fp) annotated coreferences
		precisionRecallStats.addFN(totalNumGoldStandCorefs - precisionRecallStats.getTP());

		return precisionRecallStats;
	}
	

	/**
	 * Calculates and returns F(beta)-Measure, using the calculation of the modified van Rijsbergen's effectiveness measure used in {@link PrecisionRecallStats}: F(alpha) = 1/(alpha/precision +
	 * (1-alpha)/recall).
	 * 
	 * @param precisionRecallStats
	 *            PrecisionRecallStats instance used for calculating precision and recall.
	 * @param beta
	 *            Weighting of precision vs. recall.
	 * @return F(beta)-Measure.
	 */
	public static double calculateFMeasure(PrecisionRecallStats precisionRecallStats, double beta) {
		double alpha = 1.0 / (1 + (beta * beta));
		return precisionRecallStats.getFMeasure(alpha);
	}

	/**
	 * Extracts coreference annotation information (coreference ID as well as the sentence and token number of the coreference ID) from a list of sentences in CoNLL format.
	 * 
	 * @param sentences
	 *            List of sentences in CoNLL format.
	 * @return coreferenceIDs Coreference IDs and the sentence and token number of the coreference ID.
	 */
	public static TreeMap<String, ArrayList<IntPair>> extractCoreferenceIDs(ArrayList<ArrayList<ArrayList<String>>> sentences) {

		TreeMap<String, ArrayList<IntPair>> coreferenceIDs = new TreeMap<String, ArrayList<IntPair>>();
		int sentenceIdx = 0;
		for (ArrayList<ArrayList<String>> sentence : sentences) {
			try {
				for (ArrayList<String> tokenInfos : sentence) {
					for (int tokenInfosIdx = tokenInfos.size() - 1; tokenInfosIdx >= 0; tokenInfosIdx--) {
						String tokenInfo = tokenInfos.get(tokenInfosIdx);
						if (tokenInfo.contains("<COREF")) {

							// number of tokens in current sentence
							int tokenIdx = Integer.parseInt(tokenInfos.get(0));

							// determine coference ID
							int startIdx = tokenInfo.indexOf("\"");
							int endIdx = tokenInfo.indexOf("\"", startIdx + 1);
							String coreferenceID = tokenInfo.substring(startIdx + 1, endIdx);
							ArrayList<IntPair> storedCoferenceIntPair;

							// check if current coreference ID already stored in extracted coreference IDs
							if (coreferenceIDs.containsKey(coreferenceID)) {
								storedCoferenceIntPair = coreferenceIDs.get(coreferenceID);
							} else {
								storedCoferenceIntPair = new ArrayList<IntPair>();
							}
							// add current coreference intpair to extracted coreferences
							IntPair currentCorefetenceIntPair = new IntPair(sentenceIdx, tokenIdx);
							storedCoferenceIntPair.add(currentCorefetenceIntPair);
							coreferenceIDs.put(coreferenceID, storedCoferenceIntPair);
						} else {
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			sentenceIdx++;
		}
		return coreferenceIDs;
	}
	
	
	/**
	 * Calculate number of tokens in a list of sentences
	 * 
	 * @param sentences
	 *            List of sentences
	 * @return Number of tokens.
	 */
	public static int calculateNumTokens(ArrayList<ArrayList<ArrayList<String>>> sentences) {

		int numTokens = 0;
		for (ArrayList<ArrayList<String>> sentence : sentences) {
			numTokens += sentence.size();
		}
		return numTokens;
	}
}
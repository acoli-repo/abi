package stanfordCoreNLP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;

import org.joda.time.DateTime;

import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.dcoref.Dictionaries.MentionType;
import edu.stanford.nlp.dcoref.Mention;
import edu.stanford.nlp.dcoref.MentionExtractor;
import edu.stanford.nlp.dcoref.RuleBasedCorefMentionFinder;
import edu.stanford.nlp.dcoref.SieveCoreferenceSystem;
import edu.stanford.nlp.ling.CoreAnnotations.BeginIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.EndIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentenceIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokenBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokenEndAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.ValueAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.AlternativeDependenciesAnnotation;
import edu.stanford.nlp.trees.EnglishGrammaticalStructureFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;

/**
 * Class for annotating and extracting Stanford coreference resolution annotations.
 * 
 * @author Sergej Jaschonkow
 * 
 */
public class StanfordCoreferenceResolution {

	public static SieveCoreferenceSystem corefSystem;
	public static MentionExtractor mentionExtractor;
	public static ArrayList<ArrayList<ArrayList<String>>> validInputSentences; // sentences that are in correct format to be processed by stanford coreference resolution system

	/**
	 * Constructor
	 * 
	 * @param properties
	 *            Properties of the coreference system.
	 */
	public StanfordCoreferenceResolution(Properties properties) {
		try {
			StanfordCoreferenceResolution.corefSystem = new SieveCoreferenceSystem(properties);
			StanfordCoreferenceResolution.mentionExtractor = new MentionExtractor(corefSystem.dictionaries(), corefSystem.semantics());
		} catch (Exception e) {
			System.err.println("[ERROR] " + new DateTime() + ": Failed creating DeterministicCorefAnnotator!");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Add stanford coreference annotations to sentences.
	 * 
	 * @param annotation
	 *            Stanford annotation object.
	 * @param includeSelfReferences
	 *            Boolean value indicating whether self referencing coreferences should be added as well or not.
	 * @param sentences
	 *            List of sentences in CoNNL format.
	 * @return List of sentences in CoNNLL format with coreference annotations.
	 */
	public static ArrayList<ArrayList<ArrayList<String>>> addStanfordCorefs(Annotation annotation, boolean includeSelfReferences, ArrayList<ArrayList<ArrayList<String>>> sentences) {

		int corefId = 0;
		Map<Integer, edu.stanford.nlp.dcoref.CorefChain> corefGraph = annotation.get(CorefChainAnnotation.class);
		for (Entry<Integer, edu.stanford.nlp.dcoref.CorefChain> entry : corefGraph.entrySet()) {
			edu.stanford.nlp.dcoref.CorefChain corefChain = entry.getValue();
			CorefMention corefMention = corefChain.getRepresentativeMention();
			Map<IntPair, Set<CorefMention>> mentionMap = corefChain.getMentionMap();

			// exclude self references
			if (!includeSelfReferences) {
				if (mentionMap.size() < 2) {
					continue;
				}
			}

			for (Entry<IntPair, Set<CorefMention>> mention : mentionMap.entrySet()) {
				IntPair intPair = mention.getKey();
				int sentenceIdx = intPair.get(0);
				int tokenIdx = intPair.get(1);
				MentionType mentionType = corefMention.mentionType;

				// get token information
				ArrayList<ArrayList<String>> sentence = sentences.get(sentenceIdx - 1);
				ArrayList<String> tokenInfo = sentence.get(tokenIdx - 1);
				int sizeTokenInfo = tokenInfo.size();

				// remove coreference place holder
				if (tokenInfo.get(sizeTokenInfo - 1).equalsIgnoreCase("_")) {
					tokenInfo.remove(sizeTokenInfo - 1);
				}

				// add coreferences to token information
				tokenInfo.add("<COREF ID=\"" + corefId + "\" TYPE=\"" + mentionType.toString() + "\">");
				sentence.set(tokenIdx - 1, tokenInfo);
				sentences.set(sentenceIdx - 1, sentence);
			}
			corefId++;
		}
		return sentences;
	}

	/**
	 * Create and set stanford token and sentence annotations.
	 * 
	 * @param inputSentences
	 *            List of input sentences in CoNLL format.
	 * @param includeNER
	 *            Boolean value indicating whether NER annotations should be included or not.
	 * @return sentences Stanford sentence annotations.
	 */
	public static List<CoreMap> createTokenSentenceAnnotations(ArrayList<ArrayList<ArrayList<String>>> inputSentences, boolean includeNER) {

		validInputSentences = new ArrayList<ArrayList<ArrayList<String>>>();
		int sentenceIdx = 0;
		int tokenBeginAnnotation = 0;
		Tree stanfordParseTree = null;
		List<CoreMap> sentences = new ArrayList<CoreMap>();
		for (ArrayList<ArrayList<String>> inputSentence : inputSentences) {
			try {
				CoreMap sentence = new CoreLabel();
				List<CoreLabel> tokens = new ArrayList<CoreLabel>();
				String inputParseTree = "";
				for (int tokenIdx = 0; tokenIdx < inputSentence.size(); tokenIdx++) {
					ArrayList<String> parsedLine = inputSentence.get(tokenIdx);
					String word = parsedLine.get(1);
					String lemma = parsedLine.get(2);
					String posTag = parsedLine.get(3);
					String namedEntity = "O";
					if (includeNER) {
						namedEntity = parsedLine.get(4);
					}
					String partOfParseTree = parsedLine.get(6);

					// format brackets
					if (posTag.equals("-LRB-")) { // left round bracket, i.e. "("
						word = "-LRB-";
					} else if (posTag.equals("-RRB-")) { // right round bracket, i.e. ")"
						word = "-RRB-";
					} else if (posTag.equals("-LSB-")) { // left square bracket, i.e. "["
						word = "-LSB-";
					} else if (posTag.equals("-RSB-")) { // right square bracket, i.e. "]"
						word = "-RSB-";
					} else if (posTag.equals("-LCB-")) { // left curly bracket, i.e. "{"
						word = "-LCB-";
					} else if (posTag.equals("-RCB-")) { // right curly bracket, i.e. "}"
						word = "-RCB-";
					}

					// write element to parse tree
					inputParseTree += partOfParseTree.replaceAll("\\*", Matcher.quoteReplacement("(" + posTag + " " + word + ")"));

					// create a token
					CoreLabel token = new CoreLabel();

					// set token information
					token.setOriginalText(word);
					token.setValue(word);
					token.setWord(word);
					token.setLemma(lemma);
					token.setTag(posTag);
					token.setNER(matchStanfordNER(namedEntity));
					token.setIndex(tokenIdx + 1);
					token.setSentIndex(sentenceIdx);
					token.set(TokenBeginAnnotation.class, tokenBeginAnnotation);
					token.set(TokenEndAnnotation.class, tokenBeginAnnotation + 1);
					token.set(BeginIndexAnnotation.class, tokenIdx);
					token.set(EndIndexAnnotation.class, tokenIdx + 1);

					tokens.add(token);
					tokenBeginAnnotation++;
				}
				// replace root element of input parse tree to fit to stanford parse tree format
				inputParseTree = inputParseTree.trim().replace("S1", "ROOT");

				// set tokens annotations and id of sentence
				sentence.set(TokensAnnotation.class, tokens);
				sentence.set(SentenceIndexAnnotation.class, sentenceIdx);

				// create and set parse tree annotations
				stanfordParseTree = Tree.valueOf(inputParseTree);
				sentence.set(TreeAnnotation.class, stanfordParseTree);

				// set sentence value and text annotations
				sentence.set(ValueAnnotation.class, "sentence " + sentenceIdx);
				sentence.set(TextAnnotation.class, "sentence " + sentenceIdx);

				// create and set dependencies
				GrammaticalStructureFactory grammaticalStructureFactory = new EnglishGrammaticalStructureFactory();
				GrammaticalStructure grammaticalStructure = grammaticalStructureFactory.newGrammaticalStructure(stanfordParseTree);
				SemanticGraph alternativeDependencies = new SemanticGraph(grammaticalStructure.typedDependenciesCollapsed());
				sentence.set(AlternativeDependenciesAnnotation.class, alternativeDependencies);

				sentences.add(sentence);
				sentenceIdx++;
				validInputSentences.add(inputSentence);

			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		return sentences;
	}

	/**
	 * Annotates sentence with coreference annotations.
	 * 
	 * @param annotation
	 *            Annotation object.
	 * @param corefSystem
	 *            Coreference system used for coreference resolution.
	 * @param mentionExtractor
	 *            Mention Extractor used for extracting coreference annotation.
	 * @return Annotation object with coreference annotations.
	 */
	public static Annotation annotateCorefRes(Annotation annotation) {

		System.out.println("[INFO] " + new DateTime() + ": Annotating coreferences.");
		try {
			List<Tree> trees = new ArrayList<Tree>();
			List<List<CoreLabel>> sentences = new ArrayList<List<CoreLabel>>();

			// extract trees and sentence words
			if (annotation.containsKey(SentencesAnnotation.class)) {
				for (CoreMap sentence : annotation.get(SentencesAnnotation.class)) {
					List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
					sentences.add(tokens);
					Tree tree = sentence.get(TreeAnnotation.class);
					trees.add(tree);
					MentionExtractor.mergeLabels(tree, tokens);
					MentionExtractor.initializeUtterance(tokens);
				}
			} else {
				System.err.println("[ERROR] " + new DateTime() + ": Coreference resolution system requires SentencesAnnotation!");
				return null;
			}

			// extract all possible mentions
			// don't allow reparsing, otherwise mention finder might cause an NPE (Null Pointer Exception), since no stanford parser is initiated
			boolean allowReparsing = false;
			RuleBasedCorefMentionFinder mentionFinder = new RuleBasedCorefMentionFinder(allowReparsing);
			List<List<Mention>> unOrderedMentions = mentionFinder.extractPredictedMentions(annotation, 0, corefSystem.dictionaries());

			// add the relevant info to mentions and order them for coreference resolution
			edu.stanford.nlp.dcoref.Document document = mentionExtractor.arrange(annotation, sentences, trees, unOrderedMentions);

			// set found coreference annotations to annotation object
			Map<Integer, edu.stanford.nlp.dcoref.CorefChain> result = corefSystem.coref(document);
			annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, result);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return annotation;
	}

	/**
	 * Matches WSJ NER categories to stanford NER categories.
	 * 
	 * @param inputNer
	 *            WSWJ NER category.
	 * @return matchedNer Stanford NER category.
	 */
	public static String matchStanfordNER(String inputNer) {
		String matchedNer = null;
		if (inputNer.contains("LOC")) {
			matchedNer = "LOCATION";
		} else if (inputNer.contains("ORG")) {
			matchedNer = "ORGANIZATION";
		} else if (inputNer.contains("MISC")) {
			matchedNer = "MISC";
		} else if (inputNer.contains("PER")) {
			matchedNer = "PERSON";
		} else {
			matchedNer = inputNer;
		}
		return matchedNer;
	}
}
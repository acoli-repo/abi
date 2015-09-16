package openNLP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;

import opennlp.tools.coref.DefaultLinker;
import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.Linker;
import opennlp.tools.coref.LinkerMode;
import opennlp.tools.coref.mention.DefaultParse;
import opennlp.tools.coref.mention.Mention;
import opennlp.tools.coref.mention.MentionContext;
import opennlp.tools.parser.Parse;
import opennlp.tools.util.Span;
import utility.TokenSpan;

/**
 * Class for annotating and extracting OpenNLP coreference resolution annotations.
 * 
 * @author Sergej Jaschonkow
 * 
 */
public class OpenNLPCoreferenceResolution {

	public ArrayList<Parse> parseTrees;
	public HashMap<TokenSpan, Integer> tokenSpans;
	public ArrayList<ArrayList<ArrayList<String>>> validInputSentences; // sentences that are in correct format to be processed by OpenNLP coreference resolution system

	public OpenNLPCoreferenceResolution() {
		this.parseTrees = new ArrayList<Parse>();
		this.tokenSpans = new HashMap<TokenSpan, Integer>();
		this.validInputSentences = new ArrayList<ArrayList<ArrayList<String>>>();
	}

	/**
	 * Creates and returns coreference linker.
	 * 
	 * @param filePathLinker
	 *            File path of the coreference linker model.
	 * @return Linker Coreference linker.
	 */
	public static Linker createCorefLinker(String filePathLinker, LinkerMode linkerMode) {

		Linker corefLinker = null;
		try {
			corefLinker = new DefaultLinker(filePathLinker, linkerMode);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return corefLinker;
	}

	/**
	 * Create parse tree from token information in CoNLL format.
	 * 
	 * @param sentence
	 *            List of sentences in CoNLL format.
	 * @param sentenceIdx
	 *            Number of current sentence.
	 * @return Parse tree.
	 */
	public Parse createParseTree(ArrayList<ArrayList<String>> sentence, int sentenceIdx, boolean includeNER) {

		int tokenStartSpan = 0;
		int tokenEndSpan = 0;
		int tokenIdx = 1;
		String parseTreeString = "";
		ArrayList<Span> personSpans = new ArrayList<Span>();
		ArrayList<Span> locationSpans = new ArrayList<Span>();
		ArrayList<Span> organizationSpans = new ArrayList<Span>();
		Parse parseTree = null;
		
		try {
			for (ArrayList<String> tokenInfos : sentence) {
				// get information of current token
				String word = tokenInfos.get(1);
				String posTag = tokenInfos.get(3);
				String partOfParseTree = tokenInfos.get(6);
				
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
				
				// update parse tree string
				partOfParseTree = partOfParseTree.replaceAll("\\(", " ("); // add whitespace before brackets, otherwise openNLP has problems recreating parse tree from string
				parseTreeString += partOfParseTree.replaceAll("\\*", Matcher.quoteReplacement(" (" + posTag + " " + word + ")"));
				
				// update current token end span
				tokenEndSpan += word.length();
				
				// set current token spans
				TokenSpan currentTokenSpans = new TokenSpan(sentenceIdx, tokenStartSpan, tokenEndSpan);
				tokenSpans.put(currentTokenSpans, tokenIdx);
				
				// set named entity spans
				if (includeNER) {
					String namedEntityType = tokenInfos.get(4);
					Span span = new Span(tokenIdx - 1, tokenIdx);
					if (namedEntityType.contains("PER")) {
						personSpans.add(span);
					} else if (namedEntityType.contains("LOC")) {
						locationSpans.add(span);
					} else if (namedEntityType.contains("ORG")) {
						organizationSpans.add(span);
					}
				}
				
				tokenStartSpan += word.length() + 1;
				tokenEndSpan++;
				tokenIdx++;
			}
			// format parse tree to OpenNLP format
			parseTreeString = parseTreeString.trim().replace("S1", "TOP");
			
			// create parse tree object from input parse tree string
			parseTree = Parse.parseParse(parseTreeString);
			
			// add named entities to parse
			if(includeNER){
				// add person names
				Span[] tempPersonSpans = new Span[personSpans.size()];
				personSpans.toArray(tempPersonSpans);
				
				Parse[] tagNodes = parseTree.getTagNodes();
				Parse.addNames("person", tempPersonSpans, parseTree.getTagNodes());
				
				// add location names
				Span[] tempLocationSpans = new Span[locationSpans.size()];
				locationSpans.toArray(tempLocationSpans);
				Parse.addNames("location", tempLocationSpans, parseTree.getTagNodes());
				
				// add organization names
				Span[] tempOrganizationSpans = new Span[organizationSpans.size()];
				organizationSpans.toArray(tempOrganizationSpans);
				Parse.addNames("organization", tempOrganizationSpans, parseTree.getTagNodes());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parseTree;
	}

	
	public DiscourseEntity[] getCoreferences(Linker corefLinker, ArrayList<ArrayList<ArrayList<String>>> sentences, boolean includeNER) {

		ArrayList<Mention> mentions = new ArrayList<Mention>();
		int sentenceIdx = 0;
		for(ArrayList<ArrayList<String>> sentence : sentences){
			try {
				// create parse tree
				Parse parseTree = createParseTree(sentence, sentenceIdx, includeNER);
				
				if (parseTree != null) {
					DefaultParse parseWrapper = new DefaultParse(parseTree, sentenceIdx);

					// get coreference mentions in current sentence
					Mention[] currentMentions = corefLinker.getMentionFinder().getMentions(parseWrapper);

					// construct parses for mentions without constituents, i.e. self references
					for (int i = 0, j = currentMentions.length; i < j; i++) {
						if (currentMentions[i].getParse() == null) {
							Parse parse = new Parse(parseTree.getText(), currentMentions[i].getSpan(), "NML", 1.0, 0);
							parseTree.insert(parse);

							// set new parse for current mention
							currentMentions[i].setParse(new DefaultParse(parse, sentenceIdx));
						}
					}
					mentions.addAll(Arrays.asList(currentMentions));
					parseTrees.add(parseTree);
					validInputSentences.add(sentence);
					sentenceIdx++;
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

		if (!mentions.isEmpty()) {
			return corefLinker.getEntities(mentions.toArray(new Mention[mentions.size()]));
		}
		return new DiscourseEntity[0];
	}

	public ArrayList<ArrayList<ArrayList<String>>> addOpenNLPCorefs(DiscourseEntity[] coreferences, boolean includeSelfReferences, ArrayList<ArrayList<ArrayList<String>>> sentences) {

		int corefId = 0;
		for (DiscourseEntity mentions : coreferences) {
			// exclude mentions without constituents, i.e. self references
			if (!includeSelfReferences) {
				if (mentions.getNumMentions() < 2) {
					continue;
				}
			}
			
			Iterator<MentionContext> mentionsItr = mentions.getMentions();
			while (mentionsItr.hasNext()) {
				
				MentionContext mention = mentionsItr.next();
				int headTokenIndex = mention.getHeadTokenIndex();
				
				// get sentence and spans of current mention
				opennlp.tools.coref.mention.Parse[] tokenParses = mention.getTokenParses();
				Span mentionSpan = tokenParses[headTokenIndex].getSpan();
				int mentionStartSpan = mentionSpan.getStart();
				int mentionEndSpan = mentionSpan.getEnd();
				int mentionSentenceIdx = mention.getSentenceNumber();

				// get index of start token of current mention
				Integer startTokenIdx = tokenSpans.get(new TokenSpan(mentionSentenceIdx, mentionStartSpan, mentionEndSpan));

				if (startTokenIdx != null) {

					// get token information
					ArrayList<ArrayList<String>> sentence = sentences.get(mentionSentenceIdx);
					ArrayList<String> tokenInfo = sentence.get(startTokenIdx - 1);
					int sizeTokenInfo = tokenInfo.size();

					// remove coreference place holder
					if (tokenInfo.get(sizeTokenInfo - 1).equalsIgnoreCase("_")) {
						tokenInfo.remove(sizeTokenInfo - 1);
					}

					// add coreferences to token information
					tokenInfo.add("<COREF ID=\"" + corefId + "\" TYPE=\"" + "IDENT" + "\">");
					sentence.set(startTokenIdx - 1, tokenInfo);
					sentences.set(mentionSentenceIdx, sentence);
				}
			}
			corefId++;
		}
		return sentences;
	}
}
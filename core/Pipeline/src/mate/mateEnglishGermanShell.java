package mate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import properties.PropertiesPipeline;
import se.lth.cs.srl.CompletePipeline;
import helper.listFiles;

// TODO: Auto-generated Javadoc
/**
 * The Class mateEnglishGermanShell.
 */
public class mateEnglishGermanShell {

	/**
	 * Mate.
	 *
	 * @param language
	 *            the language
	 * @throws Exception
	 *             the exception
	 */
	public static void mate(String language, String propsfile) throws Exception {
		PropertiesPipeline p = new PropertiesPipeline();
		Properties propspipeline = p.loadProps(propsfile);
		String locationProject = propspipeline.getProperty("locationProject");
		String locationMateModels = propspipeline.getProperty("locationMateModels");
		String locationSRL = locationMateModels;
		String outputPipeline = propspipeline.getProperty("outputPipeline");

		listFiles lf = new listFiles();
		List<File> files = lf.listf(outputPipeline + language
				+ "/sentences/");
		String tagger;
		String parser;
		String srl;
		String lemma;
		List<String> filenames = new ArrayList<String>();
		for (File f1 : files) {
			filenames.add(f1.getAbsolutePath());
		}

	

		//MATE models english
		String _ENGLISH_POS = locationSRL+"models/english/CoNLL2009-ST-English-ALL.anna-3.3.postagger.model";
		String _ENGLISH_LEMMATIZER= locationSRL+"models/english/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model";
		String _ENGLISH_PARSER = locationSRL+"models/english/CoNLL2009-ST-English-ALL.anna-3.3.parser.model";
		String _ENGLISH_SRL = locationSRL+"models/english/CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model";
		//MATE models german
		String _GERMAN_POS = locationSRL+"models/german/tag-ger-3.6.model";
		String _GERMAN_LEMMATIZER = locationSRL+"models/german/lemma-ger-3.6.model";
		String _GERMAN_PARSER = locationSRL+"models/german/parser-ger-3.6.model";
		String _GERMAN_MORPH = locationSRL+"models/german/morphology-ger-3.6.model";
		String _GERMAN_SRL = locationSRL+"models/german/tiger-complete-predsonly-srl-4.11.srl.model";
		String language_org = language;
		
		if (language.equals("en")) {
			language = "eng";
			tagger = _ENGLISH_POS;
			parser = _ENGLISH_PARSER;
			srl = _ENGLISH_SRL;
			lemma = _ENGLISH_LEMMATIZER;
			String[] args = { language, "-tagger", tagger, "-parser", parser,
					"-srl", srl, "-lemma", lemma, "-tokenize", "-test", "-out", };

			CompletePipeline.runCP(args, filenames, outputPipeline, language_org);

		} else {
			language = "ger";
			tagger = _GERMAN_POS;
			parser = _GERMAN_PARSER;
			String morph = _GERMAN_MORPH;
			srl = _GERMAN_SRL;
			lemma = _GERMAN_LEMMATIZER;

			String[] args = { language, "-tagger", tagger, "-parser", parser,
					"-morph", morph, "-srl", srl, "-lemma", lemma, "-tokenize",
					"-test", "-out", };

			CompletePipeline.runCP(args, filenames, outputPipeline, language_org);
		}

	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String[] args) throws Exception {
		mate("de", "/home/kathrin/Dokumente/Pipeline/src/properties/example1.properties");

	}
}
package Mate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import properties.PropertiesPipeline;
import Helper.listFiles;

public class mateEnglishGermanShell {

	public static void mate(String language) {
		PropertiesPipeline props = new PropertiesPipeline();
		Properties propsPipeline = props.loadProps();
		String locationProject = propsPipeline.getProperty("locationProject");
		String extSrc = propsPipeline.getProperty("externalSourceCode");
		String outputPipeline = propsPipeline.getProperty("outputPipeline");
		String s = null;
		listFiles lf = new listFiles();
		List<File> files = lf.listf(locationProject + outputPipeline + language
				+ "/sentences/");
		String outputPath = locationProject + outputPipeline + language
				+ "/mate/"; // path to the output folder
		String langSpecCommand = "";
		Process p1;

		for (File f : files) {
			try {
				if (language.equals("en")) {

					p1 = Runtime.getRuntime().exec(
							locationProject + "/src/Mate/mateEnglish.sh "
									+ extSrc 
									+ " "
									+ f.getAbsolutePath()
									+ " " 
									+ f.getAbsolutePath().split("/sentences/")[1]
								    + " "
								    + outputPath);
				} else {
					p1 = Runtime.getRuntime().exec(
							locationProject + "/src/Mate/mateGerman.sh "
									+ extSrc 
									+ " "
									+ f.getAbsolutePath()
									+ " " 
									+ f.getAbsolutePath().split("/sentences/")[1]
								    + " "
								    + outputPath);
				}
				System.out.println("Output saved to --> " + outputPath
						+ f.getAbsolutePath().split("/sentences/")[1]);

				BufferedReader stdInput = new BufferedReader(
						new InputStreamReader(p1.getInputStream()));

				BufferedReader stdError = new BufferedReader(
						new InputStreamReader(p1.getErrorStream()));

				// read the output from the command
				System.out
						.println("Here is the standard output of the command:\n");
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}

				// read any errors from the attempted command
				System.out
						.println("Here is the standard error of the command (if any):\n");
				while ((s = stdError.readLine()) != null) {
					System.out.println(s);
				}

				// System.exit(0);
			} catch (IOException e) {
				System.out.println("exception happened - here's what I know: ");
				e.printStackTrace();
				System.exit(-1);
			}
		}

	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Mate.mateEnglishGermanShell.mate("de");

	}
}

package helpers;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import language_elements.Sentence;

public class ConllWriter {
	private static PrintWriter writer;
	public static void printSentence(Sentence sentence){
		sentence.printSentence(ConllWriter.writer);
	}
	
	public static void setOutputFileName(String filename){
		try {
			ConllWriter.writer = new PrintWriter(filename+".txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeWriter(){
		ConllWriter.writer.close();
	}
}

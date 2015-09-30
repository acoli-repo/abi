package helpers;
import language_elements.*;

import java.util.ArrayList;

public class ConsolePrint {
	public static void printSentence(Sentence s){
		System.out.println(s.toHumanReadableString());
	}
	
	public static void printSentenceInfo(Sentence sentence){
		try{
			
			//System.out.println(sentence.findTokenByPOS("VVFIN").getTokenLine());
		}catch(NullPointerException e){
			//System.out.println("not found in sentence with id: "+ sentence.getId());
		}
	}
	
	public static void printTokenInfo(Token token){
		System.out.println(token.getTokenLine());
		//System.out.println(token.getMorphology());
	}
	public static void printPraedikat(Token token, ArrayList<Token> praedikat){
		System.out.println(token.getTokenLine());
		praedikat.forEach((t) -> System.out.println(" " + t.getTokenLine()));
		System.out.println("");
	}
	
}

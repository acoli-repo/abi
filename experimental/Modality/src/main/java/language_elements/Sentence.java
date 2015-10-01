package language_elements;

import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import enums.*;


public class Sentence {
	private static int numberOfSentences= 0;

	
	private final int id;
	private Token root; 
	private final ArrayList<Token> tokens;
	
	public Sentence(){
		this.id = ++numberOfSentences;
		tokens = new ArrayList<>();
	}
	public void addToken(Token token){
		tokens.add(token);
		token.setSentence(this);
		
		if(token.getHead().equals(0)){
			this.root = token;
		
		}
	}
	public int getId(){
		return this.id;
	}
	public void tagPraedikat(Column c, AnnotationTag a){
		ArrayList<Token> pr = findPraedikat();
		for(Token t : pr){
			t.setColumn(c, a);
		}
	}
	public static int getNumberOfSentences(){
		return numberOfSentences;
	}
	
	public String toHumanReadableString(){
		String str = "";
		for(Token token : tokens){
			str = str.concat(" "+token.getTokenString());
		}
		return str;
	}
	
	public Token getRoot(){
		return this.root;
	}
	
	public ArrayList<Token> getTokens(){
		return this.tokens;
	}
	
	public ArrayList<Token> findTokensByTokenString(String tokenstring){
		ArrayList<Token> res = new ArrayList<>();
		for (Token token : this.tokens) {
			if (token.getTokenString().equals(tokenstring)) {
				res.add(token);
			}
		}
		return res;
	}
	
	public boolean containsTokenString(String s){
		String cs = toHumanReadableString();
		return cs.contains(s);
	}

	public boolean containsTokenString(ArrayList<String> strings, int start, int end){
		ArrayList<Token> t = this.tokens;
		if(start >= 0 & end >= 0){
			t = new ArrayList<Token>(tokens.subList(start, end));
		}
		for(Token token : t){
			for(String s : strings){
				if(token.getTokenString().equals(s)){
					return true;
				}
			}
			
		}
		return false;
	}

	public boolean containsTokenString(ArrayList<String> strings){
		return containsTokenString(strings, -1, -1);
	}


	public Token getLastToken(){
		return tokens.get(tokens.size() - 1);
	}

	public Integer getLastIndex(){
		return tokens.size()-1;
	}

	public boolean containsLemma2String(String s){
		ArrayList<String> a = new ArrayList<>();
		a.add(s);

		return containsTokenString(a);
	}
	public boolean containsLemma2String(ArrayList<String> strings){
		return containsLemma2String(strings, -1, -1);
	}
	public boolean containsLemma2String(ArrayList<String> strings, int start, int end){
		ArrayList<Token> t = this.tokens;
		if(start >= 0 & end >= 0){
			t = (ArrayList<Token>)tokens.subList(start, end);
		}
		for(Token token : t){
			for(String s : strings){
				//workaround because annotations are like foo_
				if(token.getLemma2().equals(s+"_")){
					return true;
				}
			}

		}
		return false;
	}
	
	
	
	public ArrayList<Token> findTokensByTokenString(ArrayList<String> strings){
		ArrayList<Token> res = new ArrayList<>();
		for(Token token : this.tokens){
			for(String s : strings){
				if(token.getTokenString().equals(s)){
					res.add(token);
				}
			}
			
		}
		return res;
	}

	public ArrayList<Token> findTokensByLemma2(String lemma){
		ArrayList<Token> res = new ArrayList<Token>();
		for (Token token : this.tokens) {
			if (token.getLemma2().equals(lemma)) {
				res.add(token);
			}
		}
		return res;
	}
	
	public Token findTokenByPOS(String pos){
		for(Token token : this.tokens){
			if(token.getPOS().equals(pos)){
				return token;
			}
		}
		return null;
	}
	
	public ArrayList<Token> findTokenByPOS_EXT(String pos_ext){
		ArrayList<Token> res = new ArrayList<>();
		
		for(Token token : this.tokens){
			if(token.getPOS_EXT().equals(pos_ext)){
				res.add(token);
			}
		}
		return res;
	}
	
	public void tagIOBES(Integer start, Integer end, Column column, String content){

		String i_string = "I-";
		String o_string = "O-";
		String b_string = "B-";
		String e_string = "E-";
		String s_string = "S-";

		//start was before current sentence -> set begi to intermediate and single to end
		if(start==null){

			b_string = i_string;
			s_string = e_string;
			start = 0;
		}
		//end is after current sentence -> set end to intermediate
		if(end == null){
			e_string = i_string;
			end = this.getLastIndex();
		}
		if(start < 0){
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(end > tokens.size()-1){
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(int i = start; i < end+1; i++){
			String insert;
			if(end-start < 1){
				insert = s_string+content;
			}else if(i==start){
				insert = b_string+content;
			}else if(i==(end)){
				insert = e_string+content;
			}else{
				insert = i_string+content;
			}
			switch(column){
			case REDE:
				if(!tokens.get(i).getRede().equals("_")){
					//ist schon annotiert
					tokens.get(i).setRede(insert);
				}else{
					tokens.get(i).setRede(insert);
				}

				break;
			case SPRECHAKT:
				tokens.get(i).setSprechakt(insert);
				break;
			case SATZART:
				tokens.get(i).setSatzart(insert);
				break;
			default:
				break;
			}
			
		}
	}
	
	public ArrayList<Token> findTokenNearbyByPOS(Integer index, Integer range, String pos){
		ArrayList<Token> result = new ArrayList<>();
			int end = (index+range>tokens.size()) ? tokens.size() : index+range;
			
			for(int start = (index-range<0) ? 0 : (index-range); start <end ; start++){
				if(tokens.get(start).getPOS().equals(pos)){
					result.add(tokens.get(start));
				}
			}
		return result;
	}
	private ArrayList<Token> findTokensPointingToToken(Integer number){
		ArrayList<Token> res = new ArrayList<>();
		for(Token t: tokens){
			if(t.getHead() == number){
				res.add(t);
			}
		}
		return res;
	}

	private boolean containsOtherThanFiniteVerbs(ArrayList<Token> l){
		for(Token t : l){
			if(t.isInfinitivVerb() || t.isPartizipPerfektVerb()){
				return true;
			}
		}
		return false;
	}

	public ArrayList<Token> findPraedikat(Token r){
		ArrayList<Token> result = new ArrayList<>();
		int rootindex = r.getNumber();
		ArrayList<Token> v1 = findTokensPointingToToken(rootindex);
		Boolean containsOtherThanFin = containsOtherThanFiniteVerbs(v1);
		for(Token t : v1){
			if(r.isInfinitivVerb() && t.isPartizipPerfektVerb()){
				result.add(t);
				result.addAll(findPraedikat(t));
			}else if(r.isFiniteVerb()){
				if(v1.size()>1 && containsOtherThanFin && (t.isPartizipPerfektVerb() || t.isInfinitivVerb())){
					result.add(t);
					result.addAll(findPraedikat(t));
				}else if(v1.size()<2 && (t.isPartizipPerfektVerb() || t.isInfinitivVerb() || t.isFiniteVerb())){
					result.add(t);
					result.addAll(findPraedikat(t));
				}

			}
		}
		return result;
	}

	public ArrayList<Token> findPraedikat(){
		return findPraedikat(this.root);
	}

	public ArrayList<Token> findTokensByAnnotation(Column c, AnnotationTag tag){
		ArrayList<Token> result = new ArrayList<>();
		for(Token token : tokens){
			if (token.getByColumn(c).equals(tag.getString())){
				result.add(token);
			}
		}
		return result;
	}
	public ArrayList<Token> findTokensByMorphology(String s){
		ArrayList<Token> result = new ArrayList<>();
		for(Token token : tokens){
			if(token.containsMorphology(s)){
				result.add(token);
			}
		}
		return result;
	}
	
	public void printSentence(PrintWriter writer){
		for(Token t : tokens){
			t.printToken(writer);
		}
		writer.println("");
	}
	
}

package language_elements;

import java.io.PrintWriter;
import java.util.Arrays;

import enums.*;
import org.apache.commons.lang3.ArrayUtils;

public class Token {
	private static final int COLUMNCOUNT = 23;
	
	private final String [] rows;
	private Token head = this;
	private Sentence sentence;
	
	public Token(String line){
		String [] input = line.split("\t");
		String [] new_args = new String[Token.COLUMNCOUNT-input.length];
		Arrays.fill(new_args, "_");
		rows = ArrayUtils.addAll(input, new_args);
	}
	
	public String[] getRows(){
		return this.rows;
	}
	
	
	public void setTempus(String s){
		rows[Column.TEMPUS.getIndex()] = s;
	}
	
	public void setModus(String s){
		rows[Column.MODUS.getIndex()] = s;
	}
	
	public void setAspekt(String s){
		rows[Column.ASPEKT.getIndex()] = s;
	}
	
	public void setDiathese(String s){
		rows[Column.DIATHESE.getIndex()] = s;
	}

	public void setModality(String s){ rows[Column.MODALITY.getIndex()] = s;}
	
	public void setSatzart(String s){
		rows[Column.SATZART.getIndex()]=s;
	}
	
	public void setSprechakt(String s){
		rows[Column.SPRECHAKT.getIndex()]=s;
	}
	
	public void setRede(String s){
		rows[Column.REDE.getIndex()]=s;
	}
	
	public String getTokenLine(){
		return String.join("\t", rows);
	}
	
	public String getRede(){
		return rows[Column.REDE.getIndex()];
	}
	
	public String getTokenString(){
		return rows[Column.TOK.getIndex()];
	}
	
	public String getLemma(){
		return rows[Column.LEMMA.getIndex()];
	}
	
	public String getLemma2(){
		return rows[Column.LEMMA2.getIndex()];
	}
	public String getByColumn(Column c){
		return rows[c.getIndex()];
	}
	public String getPOS(){
		return rows[Column.POS.getIndex()];
	}
	public String getPOS_EXT(){
		return rows[Column.POS_EXT.getIndex()];
	}
	
	public void setSentence(Sentence s){
		this.sentence = s;
	}

	public String getTempus(){
		return rows[Column.TEMPUS.getIndex()];
	}
	public String getSatzArt(){
		return rows[Column.SATZART.getIndex()];
	}
	public Sentence getSentence(){
		return this.sentence;
	}
	public String getModus(){
		return rows[Column.MODUS.getIndex()];
	}
	public Integer getHead(){
		int head;
		try{
			head = Integer.parseInt(rows[Column.HEAD2.getIndex()]);
		}catch(NumberFormatException e){
			head = -1;
		}
		return head;
	}
	
	public void setHeadToken(Token token){
		this.head = token;
	}

	
	public Token getHeadToken(){
		return this.head;
	}

	public boolean isInfinitivVerb(){
		for(InfinitivVerbTags inftag: InfinitivVerbTags.values()){
			if(this.getPOS_EXT().equals(inftag.name())){
				return true;

			}
		}
		return false;
	}

	public boolean isPartizipPerfektVerb(){
		for(PartizipPerfectVerbTags pptag: PartizipPerfectVerbTags.values()){
			if(this.getPOS_EXT().equals(pptag.name())){
				return true;

			}
		}
		return false;
	}

	public void setColumn(Column c, AnnotationTag a){
		rows[c.getIndex()] = a.getString();
	}
	public boolean isFiniteVerb(){
		for(FiniteVerbTags ftag: FiniteVerbTags.values()){
			if(this.getPOS_EXT().equals(ftag.name())){
				return true;

			}
		}
		return false;
	}
	
	public String getMorphology(){
		return rows[Column.MORPH.getIndex()];
	}
	
	public boolean containsMorphology(String m){
		return this.rows[Column.MORPH.getIndex()].contains("|"+m+"|") || this.rows[Column.MORPH.getIndex()].contains(m+"|") || this.rows[Column.MORPH.getIndex()].contains("|"+m);
	}
	
	public Integer getNumber(){
		return Integer.parseInt(rows[Column.NR.getIndex()]);
	}
	
	
	public void printToken(PrintWriter writer){
		writer.println(getTokenLine());
	}
}

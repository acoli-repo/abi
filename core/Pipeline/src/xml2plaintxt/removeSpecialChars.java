package xml2plaintxt;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import Helper.listFiles;
import Helper.readwriteFiles;

public class removeSpecialChars {
	//the pdf2xml converter has some difficulties with special characters;
	// It also splits the words with "Umlaute" into two. xml2 txt joins theses parts,
	// but returns for example "¨a" instead of "ä". These errors are also corrected
	// by the following code.
	 public void remove(String input_path, String output_path) {
   	  		listFiles lf = new listFiles();
   	  		List<File> files = lf.listf(input_path);
   	  		readwriteFiles rw = new readwriteFiles();
   	  		for (File f : files){
   	  			try{List<String> cont = rw.read(f.getAbsolutePath());
   	  			List<String> modifiedCont = new ArrayList<String>();
   	  			for (String line : cont){
   	  				modifiedCont.add(line//Code from Niko Schenk
   	  				.replace("iﬁ", "ifi")
 	                .replace("ﬂ", "fl")
 	                .replace("ﬁ", "fi")
 	                .replace("ﬀ", "ff")
 	                .replace("ﬀ", "ff")
 	                .replace("ﬃ", "ffi")
 	                //.replace("’", "'") // ?
 	                .replace("˘a", "")
 	                .replace("C ¸ ", "Ç")
 	                .replace("C ¸", "Ç")
 	                .replace("˚ A", "Å")
 	                .replace("´ ı", "í")
 	                .replace("˚ a", "å")
 	                .replace("¨ ı", "ï")
 	                .replace("´ ı", "í")
 	                .replace("˜ a", "ã")
 	                .replace("ˆa", "â")
 	                .replace("˘a", "ă")
 	                .replace("˜ n", "ñ")
 	                .replace("˜ o", "õ")
 	                .replace("´ y", "ý")
 	                .replace("´a", "á")
 	                .replace("´A", "Á")
 	                .replace("´e", "é")
 	                .replace("´E", "É")
 	                .replace("´ı", "í") // PDF.
 	                .replace("´i", "í")
 	                .replace("´I", "Í")
 	                .replace("´o", "ó")
 	                .replace("´O", "Ó")
 	                .replace("´u", "ú")
 	                .replace("´U", "Ú")
 	                .replace("´y", "ý")
 	                .replace("´Y", "Ý")
 	                // DBLP XML specific
 	                .replace("¨a", "ä")
 	                .replace("¨e", "ë")
 	                .replace("¸t", "ţ")
 	                .replace("¸s", "ş")
 	                .replace("¨o", "ö")
 	                .replace("¨i", "&iuml;")
 	                .replace("¨u", "ü")
 	                .replace("¨A", "Ä")
 	                .replace("¨E", "Ë")
 	                .replace("¨I", "&Iuml;")
 	                .replace("¨O", "Ö")
 	                .replace("¨U", "Ü")
 	                .replace("`e", "è")
 	                .replace("`a", "à")
 	                .replace("`i", "ì")
 	                .replace("`o", "ò")
 	                .replace("`u", "ù")
 	                .replace("`A", "À")
 	                .replace("`E", "È")
 	                .replace("`I", "Ì")
 	                .replace("`O", "Ò")
 	                .replace("`U", "Ù")
 	                );
   	  			}
   	  			rw.write(output_path+f.getAbsolutePath().split("/txtPreprocessed/")[1], modifiedCont);
   	  			}catch(Exception ex) {
   		         ex.printStackTrace();
   	  			}	
   	  		}  
 	    }

	 public static void main(String[] args) {	
		}
}

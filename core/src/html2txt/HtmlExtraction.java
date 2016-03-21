package html2txt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import Helper.listFiles;
import Helper.readwriteFiles;

public class HtmlExtraction {

	public String getContent(String url) {
		Document doc;
		String content = "";
		try {
			doc = Jsoup.connect(url).get();
			Elements ps = doc.select("p");
			content = ps.text();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;

	}

	public List<String> parseDocumentFromFile(String htmlfile) {
		File input = new File(htmlfile);
		Document doc;
		String text = "";
		List<String> lines = new ArrayList<String>();
		try {
			doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
		
		// get local html content
		
			org.jsoup.nodes.Element html = doc.select("html").first();
			String id = html.id();
		if(doc.select("article").first().equals(null)){
			org.jsoup.nodes.Element content = doc.select("html").first();
			Elements ptext = content.children();
			content.getElementsByTag("p");
			//org.jsoup.nodes.Element content = doc.getElementById(id);
			//Elements ptext = content.getElementsByTag("p");
			for (org.jsoup.nodes.Element ts : ptext) {
				text =ts.text();
				System.out.println(ts.text());
				lines.add(text);
			}
		}
		else{
			org.jsoup.nodes.Element article = doc.select("article").first();
			Elements ptext = article.children();
			article.getElementsByTag("p");
			for (org.jsoup.nodes.Element ts : ptext) {
				text =ts.text();
				System.out.println(ts.text());
				lines.add(text);
			}
			
		}
		
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return lines;
		
		
	}
	
	
	public void convertHtml2Txt(String input_path, String output_path){
		listFiles lf = new listFiles();
	    List<File> files = lf.listf(input_path);
	    for (File f : files){
	        
	        	String inputfile = f.getAbsolutePath();
	        	String outputfile = output_path+f.getAbsolutePath().split("/html/")[1].split("html")[0]+"txt";
	        	List<String> lines = parseDocumentFromFile(inputfile);
	        	readwriteFiles rw = new readwriteFiles();
	        	rw.write(outputfile, lines);
	           	System.out.println("Conversion of "+ inputfile.split("/html/")[1]+" into "+outputfile.split("/txt/")[1]+" done!");
	        	     
	    }
	}
	        	
	        	
	public static void main(String[] args) {
		HtmlExtraction he = new HtmlExtraction();
//		System.out
//				.println(he
//						.getContent("https://de.wikipedia.org/wiki/Heinrich_Christian_Burckhardt"));

		
		he.parseDocumentFromFile("/home/kathrin/workspace/PraktikumWS1516/localFiles/inputPipeline/de/html/zeit.html");
		

	}

}
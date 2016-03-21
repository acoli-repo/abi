package textExtraction;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import textExtraction.GetFile;

public class PDFToTextConverter {
	private static GetFile gf = new GetFile();

	// allow pdf files selection for converting
	public static void generateTxtFiles(String pdfpath, String txtpath) {

		String[] Files = gf.getPDFfile(pdfpath);
		System.out.println("Please wait...");
		

		for (int i = 0; i < Files.length; i++) {
             String newFilename=Files[i].replace(".pdf", "");
			convertPDFToText(pdfpath + Files[i], txtpath + newFilename + ".txt");
		}
		System.out.println("Conversion complete");

	}

	public static void convertPDFToText(String src, String desc) {
		//ProcessBuilder pdf2xml = new ProcessBuilder("pdftoxml.linux64.exe.1.2_7");
		
		try {
			// create file writer
			// FileWriter fw = new FileWriter(txtpath+desc);
			// create buffered writer
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(desc), "utf-8");
			BufferedWriter bw = new BufferedWriter(writer);
			// create pdf reader
			PdfReader pr = new PdfReader(src);
			// get the number of pages in the document
			int pNum = pr.getNumberOfPages();
			// extract text from each page and write it to the output text file
			for (int page = 1; page <= pNum; page++) {
				String text = PdfTextExtractor.getTextFromPage(pr, page);
				bw.write(text);
				bw.newLine();

			}
			bw.flush();
			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	public static void main(String[] args) {
		
	}
//		generateTxtFiles("/home/kathrin/workspace/PraktikumWS1516/src/inputPipeline/pdf/", "/home/kathrin/workspace/PraktikumWS1516/src/outputPipeline/txt/");
//		SBD.main(null);
//	} --> in Pipeline.java übertragen
	
	
//thread.waitfor -->sbd
	//github repo acoli repo
	//sbd für Deutsch
	//sbd ohne -t! (mate macht das)
	//output von pdf-txt normalisieren (fi ersetzen!!)(eventuell programm, dass ligaturen allgemein ersetzt im Netz)
//process.execute --> pfad zu programm (pdf2xml, um wirklichn nur den txt zu extrahieren (bilder, formeln etc weg)
	//deutsch und englisch trennen (models von mate)
	// sprache einstellen können vor pipeline execution
	
	

}

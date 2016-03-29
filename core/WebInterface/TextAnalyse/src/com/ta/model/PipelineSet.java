package com.ta.model;

import org.hibernate.validator.constraints.NotEmpty;

public class PipelineSet {

	@NotEmpty
	private String sprache;
	@NotEmpty
	private String pdfclasspath;
	@NotEmpty
	private String htmlclasspath;
	@NotEmpty
	private String outputtextclasspath;
	@NotEmpty
	private String outputrdfclasspath;
	@NotEmpty
	private String outputturtleclasspath;
	
	
	public String getOutputturtleclasspath() {
		return outputturtleclasspath;
	}
	public void setOutputturtleclasspath(String outputturtleclasspath) {
		this.outputturtleclasspath = outputturtleclasspath;
	}
	public String getSprache() {
		return sprache;
	}
	public void setSprache(String sprache) {
		this.sprache = sprache;
	}
	public String getPdfclasspath() {
		return pdfclasspath;
	}
	public void setPdfclasspath(String pdfclasspath) {
		this.pdfclasspath = pdfclasspath;
	}
	public String getHtmlclasspath() {
		return htmlclasspath;
	}
	public void setHtmlclasspath(String htmlclasspath) {
		this.htmlclasspath = htmlclasspath;
	}
	public String getOutputtextclasspath() {
		return outputtextclasspath;
	}
	public void setOutputtextclasspath(String outputtextclasspath) {
		this.outputtextclasspath = outputtextclasspath;
	}
	public String getOutputrdfclasspath() {
		return outputrdfclasspath;
	}
	public void setOutputrdfclasspath(String outputrdfclasspath) {
		this.outputrdfclasspath = outputrdfclasspath;
	}
	
	
}

/*  **************************************************************
 *   Projekt         : Text Analyse (java)
 *  --------------------------------------------------------------
 *   Autor(en)       : Kathrin Donandt, Zhanhong Huang
 *   Beginn-Datum    : 04.20.2016
 *  --------------------------------------------------------------
 *   copyright (c) 2016  Uni Frankfurt Informatik
 *   Alle Rechte vorbehalten.
 *  **************************************************************
 */
package com.ta.model;

import org.hibernate.validator.constraints.NotEmpty;
/**
 *  data  model for SparQLBean
 *  
 * @author  Kathrin Donandt, Zhanhong Huang
 *
 */
public class SparQLBean {

	@NotEmpty
	private String spql;
	
	private String rdftriplefile;
	
	private String visualpath;
	
	private String localhost;
	
	private String errormsg;
	
	public String getErrormsg() {
		return errormsg;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getLocalhost(){
		return localhost;
	}
	
	public void setLocalhost(String localhost){
		this.localhost = localhost;
	}

	public String getVisualpath() {
		return visualpath;
	}

	public void setVisualpath(String visualpath) {
		this.visualpath = visualpath;
	}

	public String getSpql() {
		return spql;
	}

	public void setSpql(String spql) {
		this.spql = spql;
	}

	public String getRdftriplefile() {
		return rdftriplefile;
	}

	public void setRdftriplefile(String rdftriplefile) {
		this.rdftriplefile = rdftriplefile;
	}
	
}

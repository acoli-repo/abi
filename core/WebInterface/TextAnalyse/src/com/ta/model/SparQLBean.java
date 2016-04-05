package com.ta.model;

import org.hibernate.validator.constraints.NotEmpty;

public class SparQLBean {

	@NotEmpty
	private String spql;
	
	private String rdftriplefile;
	
	private String visualpath;
	
	private String localhost;
	
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

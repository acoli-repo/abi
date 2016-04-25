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
package com.ta.visualisation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.ta.bean.RWProperties;
import com.ta.model.SparQLBean;

/**
 * 
 * @author Kathrin Donandt, Zhanhong Huang
 *
 */

public class VisualizeQueryResult {
	protected static RWProperties rwpp = new RWProperties();

	/**
	 * opens visualisation for triples (construct query)
	 * 
	 * @param ttlfile_queryresult: absolut path to triples file (.json)
	 * @throws IOException if any error in IO
	 */
	public void visualizeTriple(String ttlfile_queryresult) throws IOException {
		SparQLBean sq = new SparQLBean();
		sq.setLocalhost("http://localhost:9000/construct.html");
		System.err.println(sq.getLocalhost());
		String path_to_visualize = rwpp.getPropValues("path_to_visualize");
		String output = path_to_visualize+"triples.json";
		
		String scriptlocation = rwpp.getPropValues("path_to_converters")
				+ "parse_triples.py";
		try {
			ProcessBuilder pb = new ProcessBuilder("python", scriptlocation,
					ttlfile_queryresult, output);
			Process p;
			p = pb.start();
			String aLine = "";
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((aLine = input.readLine()) != null) {
				System.out.println(aLine);
			}
			input.close();

		} catch (FileNotFoundException e) {
			System.out.print(e.getMessage());
		} catch (IOException e) {
			System.out.print(e.getMessage());
		}

	}
	/**
	 * opens visualisation for table result (select query)
	 * @throws IOException if any error in IO
	 */
	public void visualizeSelect() throws IOException{
		SparQLBean sq = new SparQLBean();
		sq.setLocalhost("http://localhost:9000/select.html");
		System.err.println(sq.getLocalhost());
		
	}
}
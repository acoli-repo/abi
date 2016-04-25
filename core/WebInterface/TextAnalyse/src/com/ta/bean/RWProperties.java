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
package com.ta.bean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ta.resource.Globals;

/**
 * read and write properties
 * 
 * @author Kathrin Donandt, Zhanhong Huang
 *
 */
public class RWProperties {
	protected static Logger log = Logger.getLogger(Class.class.getName());
	InputStream input = null;
	OutputStream output = null;

	public String getPropValues(String key) {
		String result = "";
		try {
			Properties prop = new Properties();
			// configuration or src/main/resources
			input = new FileInputStream(Globals._MESSAGE_PATH);

			// load a properties file
			prop.load(input);
			result = prop.getProperty(key);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.debug(e.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * do write
	 * 
	 * @param k
	 *            key
	 * @param v
	 *            value
	 * @param filepath
	 *            file path
	 */
	public void writeProp(String k, String v, String filepath) {
		readwriteFiles wf = new readwriteFiles();
		List<String> lines = wf.read(filepath);
		List<String> final_lines = new ArrayList<String>();
		for (String l : lines) {
			if (l.split("=")[0].equals(k)) {
				final_lines.add(k + "=" + v + "\n");
			} else {
				final_lines.add(l + "\n");
			}
		}
		wf.write(filepath, final_lines);
	}

	/**
	 * get value with key
	 * 
	 * @param key key 
	 * @return value
	 */
	public boolean getBtnPropValues(String key) {
		boolean result = false;
		try {
			Properties prop = new Properties();
			input = new FileInputStream(Globals._MESSAGE_PATH);

			// load a properties file
			prop.load(input);
			result = Boolean.parseBoolean(prop.getProperty(key));

		} catch (IOException ex) {
			log.debug(ex.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.debug(e.getMessage());
				}
			}
		}

		return result;
	}

	/**
	 *  do set value
	 *  
	 * @param key key 
	 * @param value value
	 */
	public void setPropValues(String key, String value) {
		FileInputStream in;
		try {
			in = new FileInputStream(Globals._MESSAGE_PATH);
			Properties props = new Properties();
			props.load(in);
			in.close();

			FileOutputStream out;

			out = new FileOutputStream(Globals._MESSAGE_PATH);

			props.setProperty(key, value);

			props.store(out, null);

			out.close();
		} catch (FileNotFoundException e) {
			log.debug(e.getMessage());
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
	}

}

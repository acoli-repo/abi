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
package com.ta.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ta.bean.CheckFile;
import com.ta.bean.RWProperties;
import com.ta.bean.readwriteFiles;
import com.ta.model.LoginData;
import com.ta.model.SparQLBean;
import com.ta.remote.BlazegraphSesameRemote;
import com.ta.resource.Globals;

/*
 * author: 
 * 
 */

@Controller
public class WebController {

	protected static RWProperties rwpp = new RWProperties();
    protected static String tempdir= System.getProperty("java.io.tmpdir");
	/**
	 * get welcome site
	 * 
	 * @param modelmap
	 * @return view of welcome
	 * @throws IOException
	 *             if no found, throw IOExc.
	 * @throws InterruptedException
	 *             if type error.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index(ModelMap modelmap) throws IOException,
			InterruptedException {
		modelmap.put("action", "welcome");
		modelmap.put("title", "Home");

		return "welcome";
	}

	/**
	 * get about site
	 * 
	 * @return view of about.jsp
	 * 
	 * @throws IOException
	 *             if no found, throw IOExc.
	 */
	@RequestMapping("/about")
	public ModelAndView about() throws IOException {
		return new ModelAndView("about", "message", null);
	}

	/**
	 * get contact site
	 * 
	 * @return view of contact.jsp
	 */
	@RequestMapping("/contact")
	public ModelAndView contactus() {
		return new ModelAndView("contact", "message", null);
	}

	/**
	 * get blazegraph database site
	 * 
	 * @return view of blazegraph.jsp
	 */
	@RequestMapping("/blazegraph")
	public ModelAndView blazegraph() {
		return new ModelAndView("blazegraph", "message", null);
	}

	/**
	 * get textAnalyse site
	 * 
	 * @return view of textAnalyse.jsp
	 * @throws IOException
	 *             if no found.
	 */
	@RequestMapping("/textAnalyse")
	public ModelAndView textAnalyse() throws IOException {
		String path_to_visualize = rwpp.getPropValues("path_to_visualize");
		ProcessBuilder pb1 = new ProcessBuilder("sh",
				path_to_visualize + "httpserverConstructSelect.sh",
				path_to_visualize);
		pb1.start();
		System.out.println("Visualisation Server Started!");

		return new ModelAndView("textAnalyse", "ta", new SparQLBean());
	}

	/**
	 * get successSparQl site
	 * 
	 * @param sq
	 *            model SparQLBean
	 * @param result
	 *            the result of textAnalyse
	 * @return view of successSparQl.jsp
	 * @throws IOException
	 *             if no found.
	 */
	@SuppressWarnings("static-access")
	@RequestMapping("/successSparQl")
	public String processForm(@Valid @ModelAttribute("ta") SparQLBean sq,
			BindingResult result) throws IOException {

		CheckFile cf = new CheckFile();

		BlazegraphSesameRemote bgsr = new BlazegraphSesameRemote();

		String rdftriplefile = "";

		rdftriplefile = bgsr.decideQueryType(sq.getSpql());
		sq.setRdftriplefile(rdftriplefile);

		if (result.hasErrors()) {

			return "textAnalyse";

		}

		String localhost = rwpp.getPropValues("localhost");
		sq.setLocalhost(localhost);
		Integer nb = Integer.valueOf(rwpp
				.getPropValues("number_of_triples_rows"));

		if (cf.isOutputSucessed(rdftriplefile) == false) {
			readwriteFiles rwf = new readwriteFiles();
			List<String> lines = rwf.read(rwpp.getPropValues("locationTA")
					+ File.separator + "sparQLerrorFile.txt");
			String linesString = StringUtils.join(lines, ",");
			sq.setRdftriplefile(linesString);
			// sq.setRdftriplefile(rwpp.getPropValues("path_to_converter_error")
			// .replace("this_is_a_newline", "\n"));
			return "errorSparQl";
		}
		if (nb > 200) {

			String msg = "Das Ergebnis enthält " + nb
					+ " Triples oder Reihen. Wollen Sie trotzdem fortfahren?";

			sq.setErrormsg(msg);
			return "successSparQl";
		} else {

			return "visualtemp";
		}
	}

	/**
	 * get visual site
	 * 
	 * @param sq
	 *            model SparQLBean
	 * @param result
	 *            the result of textAnalyse
	 * @return view of visual.jsp
	 */
	@RequestMapping("/visual")
	public String processForm2(@Valid @ModelAttribute("ta") SparQLBean sq,
			BindingResult result) {
		// if (result.hasErrors()) {
		//
		// return "successSparQl";
		// } else {

		return "visual";
		// }
	}

	/**
	 * get login site
	 * 
	 * @return view of login.jsp
	 */
	@RequestMapping("/login")
	public ModelAndView login() {

		RWProperties rwp = new RWProperties();
		String locationTA = rwp.getPropValues("locationTA");
		String locationPipeline = rwp.getPropValues("locationPipeline");
		String urlBlazegraph = rwpp.getPropValues("urlBlazegraph");
		String input = locationPipeline
				+ "ResourcesPipeline/localFiles/inputPipeline/";
		ProcessBuilder pb2 = new ProcessBuilder("sh", locationTA
				+ File.separator+ "upload.sh", locationTA + File.separator, input,
				locationPipeline, urlBlazegraph);

		try {
			pb2.start();
			System.out.println("Upload Server Started !");
		} catch (IOException e) {
			System.out.println("Upload Server failed to start :( !");
			e.printStackTrace();
		}

		ProcessBuilder pb3 = new ProcessBuilder("sh", locationTA
				+ File.separator+ "download.sh", locationTA + File.separator);
		try {
			pb3.start();
			System.out.println("Download Server Started!");
		} catch (IOException e) {
			System.out.println("Download Server failed to start!");
			e.printStackTrace();
		}

		return new ModelAndView("login", "logindata", new LoginData());
	}

	/**
	 * get verwaltung site
	 * 
	 * @param ld
	 *            model LoginData
	 * @param result
	 *            the result of last view
	 * @param model
	 *            model of verwaltung view
	 * @return view of verwaltung.jsp
	 */
	@RequestMapping("/verwaltung")
	public String processLoginForm(
			@Valid @ModelAttribute("logindata") LoginData ld,
			BindingResult result, Model model) {

		if (result.hasErrors() || !ld.getUsername().equals(Globals.USER_NAME)
				|| !ld.getPassword().equals(Globals.PASS_WORT)) {

			return "login";
		} else {

			return "verwaltung";
		}
	}

}
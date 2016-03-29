package com.ta.controller;

import javax.validation.Valid;

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
import com.ta.model.LoginData;
import com.ta.model.PipelineSet;
import com.ta.model.SparQLBean;
import com.ta.remote.BlazegraphSesameRemote;
import com.ta.resource.Globals;
/*
 * author: 
 * 
 */
import com.ta.visualisation.VisualizeQueryResult;

@Controller
public class WebController {

	@RequestMapping(method = RequestMethod.GET)
	public String index(ModelMap modelmap) {
		modelmap.put("action", "welcome");
		modelmap.put("title", "Home");
		return "welcome";
	}

	@RequestMapping("/about")
	public ModelAndView about() {
		return new ModelAndView("about", "message", null);
	}

	@RequestMapping("/contact")
	public ModelAndView contactus() {
		return new ModelAndView("contact", "message", null);
	}

	@RequestMapping("/blazegraph")
	public ModelAndView blazegraph() {
		return new ModelAndView("blazegraph", "message", null);
	}

	@RequestMapping("/textAnalyse")
	public ModelAndView textAnalyse() {

		return new ModelAndView("textAnalyse", "ta", new SparQLBean());
	}

	@SuppressWarnings("static-access")
	@RequestMapping("/successSparQl")
	public String processForm(@Valid @ModelAttribute("ta") SparQLBean sq, BindingResult result) {

		//RWProperties rwp = new RWProperties();
		CheckFile cf = new CheckFile();

		BlazegraphSesameRemote bgsr = new BlazegraphSesameRemote();

		String rdftriplefile = "";

		rdftriplefile = bgsr.decideQueryType(sq.getSpql());
		sq.setRdftriplefile(rdftriplefile);

		VisualizeQueryResult vqr = new VisualizeQueryResult();
		vqr.visualizeTriple(rdftriplefile);
//		vqr. visualizeTriple("/home/kathrin/Schreibtisch/TextAnalyse/src/com/ta/converters/rdftriples.ttl");
		
		if (result.hasErrors()) {

			return "textAnalyse";
			
		}
		
		if (cf.isOutputSucessed(rdftriplefile) == false) {

			return "errorSparQl";
		} else {
            String view="file:///home/kathrin/Schreibtisch/TextAnalyse/WebContent/WEB-INF/jsp/visual.html";
			System.out.println("do sparQLabfrage:" + sq.getSpql());
			sq.setVisualpath(view);
			return "successSparQl";
		}
	}

	@RequestMapping("/visual")
	public String processForm2(@Valid @ModelAttribute("ta") SparQLBean sq, BindingResult result) {
//		if (result.hasErrors()) {
//			
//			return "successSparQl";
//		} else {

			return "visual";
//		}
	}

	@RequestMapping("/login")
	public ModelAndView login() {
		return new ModelAndView("login", "logindata", new LoginData());
	}

	@RequestMapping("/verwaltung")
	public String processLoginForm(@Valid @ModelAttribute("logindata") LoginData ld, BindingResult result,
			Model model) {
		if (result.hasErrors() || !ld.getUsername().equals(Globals.USER_NAME)
				|| !ld.getPassword().equals(Globals.PASS_WORT)) {
			return "login";
		} else {
			RWProperties rwp = new RWProperties();
			PipelineSet pp = new PipelineSet();
			pp.setSprache(rwp.getPropValues("sprache"));
			pp.setPdfclasspath(rwp.getPropValues("pdfclasspath"));
			pp.setHtmlclasspath(rwp.getPropValues("htmlclasspath"));
			pp.setOutputtextclasspath(rwp.getPropValues("outputtextclasspath"));
			pp.setOutputrdfclasspath(rwp.getPropValues("outputrdfclasspath"));
			pp.setOutputturtleclasspath(rwp.getPropValues("outputturtleclasspath"));

			model.addAttribute("pipeline", pp);
			return "verwaltung";
		}
	}

	@RequestMapping("/successSave")
	public String processPipileForm(@Valid @ModelAttribute("pipeline") PipelineSet ps, BindingResult result) {
		RWProperties rwp = new RWProperties();

		if (result.hasErrors()) {
			return "verwaltung";

		} else {

			rwp.setPropValues("sprache", ps.getSprache());
			rwp.setPropValues("pdfclasspath", ps.getPdfclasspath());
			rwp.setPropValues("htmlclasspath", ps.getHtmlclasspath());
			rwp.setPropValues("outputtextclasspath", ps.getOutputtextclasspath());
			rwp.setPropValues("outputrdfclasspath", ps.getOutputrdfclasspath());
			rwp.setPropValues("outputturtleclasspath", ps.getOutputturtleclasspath());

			return "successSave";
		}
	}

}
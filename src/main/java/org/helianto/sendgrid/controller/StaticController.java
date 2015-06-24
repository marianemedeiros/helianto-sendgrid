package org.helianto.sendgrid.controller;

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Static page controller.
 * 
 * @author mauriciofernandesdecastro
 */
@Controller
@RequestMapping("/static")
public class StaticController {

	private static final Logger logger = LoggerFactory.getLogger(StaticController.class);
	
	/**
	 * Build e-mail page.
	 * 
	 * @param model
	 * @param templateId
	 * @param matrixVars
	 */
	@RequestMapping(value="/template/{templateId}", method=RequestMethod.GET)
	public String message(Model model, @PathVariable String templateId, @MatrixVariable Map<String, LinkedList<String>> matrixVars
			, @RequestParam(required=false) String confirmationuri, @RequestParam(required=false) String rejecturi) {
		logger.debug("Static content from template: {}", templateId);
		
		model.addAttribute("staticuri", "#");
		
		model.addAttribute("staticContent", "/sendgrid/"+templateId);

		model.addAllAttributes(matrixVars);
		for (Entry<String, LinkedList<String>> entry : matrixVars.entrySet())
		{
			for (String string : entry.getValue()) {
				model.addAttribute(entry.getKey(), string);
				break;
			}
		}
		if(confirmationuri!=null){
			model.addAttribute("confirmationuri", confirmationuri);
		}
		if(rejecturi!=null){
			model.addAttribute("rejecturi", rejecturi);
		}
		
		return "frame-static";
	}

}

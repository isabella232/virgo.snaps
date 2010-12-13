/*******************************************************************************
 * Copyright (c) 2010, Pouzin Society, http://www.pouzinsociety.org
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Patsy Phelan, Pouzin Society - initial contribution
 *******************************************************************************/

package sample.snap;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sample.api.SampleDao;
import sample.api.SampleAPI;

@Controller
@RequestMapping("/sample/addDaoForm")
@SessionAttributes("dao")
public class SampleFormController {
	private final SampleAPI apiService;
	private static Log log = LogFactory.getLog(SampleFormController.class);

	@Autowired
	public SampleFormController(SampleAPI apiService) {
		this.apiService = apiService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(Model model) {
		log.info("setupForm()");
		SampleDao dao = new SampleDao("Put Your Value here");
		model.addAttribute("dao", dao);
		return "sample/getDaoForm";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit( @ModelAttribute("dao") SampleDao dao,  BindingResult result, SessionStatus status, HttpSession session) {
		log.info("calling OSGi apiService.add()");
		apiService.add(dao);
		log.info("back from OSGi apiService.add()");
		session.setAttribute("message", "called apiService.add(" +dao.getText() + ")");
		return "redirect:/sample/index.htm";
	}
}

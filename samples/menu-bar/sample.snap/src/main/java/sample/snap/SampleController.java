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

import java.util.List;
import sample.api.SampleDao;
import sample.api.SampleAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Annotation-driven <em>MultiActionController</em> that handles all non-form
 * URL's.
 *
 * Used to provide sample snap
 */
@Controller
public class SampleController {
	private final SampleAPI apiService;
	private static Log log = LogFactory.getLog(SampleController.class);

	@Autowired
	public SampleController(SampleAPI apiService) {
		this.apiService = apiService;
	}

	/**
	 * Custom handler for displaying index 
	 */
	@RequestMapping("/sample/index")
	public ModelMap indexHandler() {
		List<SampleDao> daoList;
		log.info("Calling OSGi Service");
		daoList = apiService.get();
		log.info("Returned from OSGi Service");
		return new ModelMap().addAttribute("daoList", daoList);
	}
	
	/**
	 * Custom handler for delete action
	 */
	@RequestMapping("/sample/delete")
	public String deleteHandler(@RequestParam("textVal") String text) {
		log.info("Calling OSGi Service");
		apiService.remove(new SampleDao(text));
		log.info("Returned from OSGi Service");
		return "redirect:/sample/index.htm";
	}
}

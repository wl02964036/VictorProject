package com.rx.web.controller.important;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.WebRequest;

import com.rx.web.service.HomeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {
	
	@Autowired
	HomeService service;

    @GetMapping(name = "root", path = "/")
    public String root(final ModelMap model) {
        return "redirect:/index";
    }

    @GetMapping(name = "index", path = "/index")
    public String index(final ModelMap model) {
        return "index";
    }

    @GetMapping(name = "login", path = "/login")
    public String login(final WebRequest request, final ModelMap model) {
        log.info("***** login *****");
        return "login";
    }

    // 權限錯誤頁
    @GetMapping(name = "accessDenied", path = "/accessDenied")
    public String accessDenied(final ModelMap model) {
    	log.info("{}", model);
        return "accessDenied";
    }
}

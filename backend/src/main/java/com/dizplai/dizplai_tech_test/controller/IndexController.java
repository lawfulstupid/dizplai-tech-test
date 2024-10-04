package com.dizplai.dizplai_tech_test.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class IndexController {

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index.html");
    }

}

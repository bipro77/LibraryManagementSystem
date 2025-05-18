package com.bookman.lms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/home")
public class HomepageController {

    @RequestMapping(value = { "/view" }, method = RequestMethod.GET)
    public String home() {
            return "777/homepage.html";
    }

}

package com.bookman.lms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    //@RequestMapping("/")
   // @RequestMapping(value = { "/","/index" }, method = RequestMethod.GET)
    @GetMapping(value={"/","/index" })
    public String index() {
            return "index";
    }
}

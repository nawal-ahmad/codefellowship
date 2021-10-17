package com.codefellowship.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller

public class HomeController {
    @GetMapping("/")
    public String getHomePage() {
        return "home";
    }
//    @GetMapping("/signup")
//    public String getSignUpPage() {
//        return "signup";
//    }
//    @GetMapping("/login")
//    public String logIn(){
//        return "signin";
//    }

}

package com.codefellowship.controllers;

import com.codefellowship.models.ApplicationUser;
import com.codefellowship.repositories.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller

public class MainController {

    @Autowired
    ApplicationUserRepository applicationUserRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @GetMapping("/")
    public String splash(){
        return"splash";
    }

    @GetMapping("/home")
    public String home(Model model){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();
            model.addAttribute("username" , username);
        } else {
            String username = principal.toString();
        }
        return"home";
    }

    @GetMapping("/signup")
    public String signUp(){
        return "signup";
    }

    @GetMapping("/login")
    public String logIn(){
        return "signin";
    }

    @PostMapping("/signup")
    public RedirectView signUp(@ModelAttribute ApplicationUser object){
        ApplicationUser newUser = new ApplicationUser(object.getUsername(),bCryptPasswordEncoder.encode(object.getPassword()) , object.getFirstName(), object.getLastName(), object.getDateOfBirth(), object.getBio());
        applicationUserRepository.save(newUser);
        return new RedirectView("login");
    }


    @GetMapping("/users/{id}")
    public String userInfo(Model model, @PathVariable("id") int id){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();
            model.addAttribute("username" , username);
        } else {
            String username = principal.toString();
        }
        model.addAttribute("user" , applicationUserRepository.findById(id).get());
        return "user";
    }
}

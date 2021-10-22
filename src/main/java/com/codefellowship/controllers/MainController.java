package com.codefellowship.controllers;

import com.codefellowship.models.ApplicationUser;
import com.codefellowship.models.Post;
import com.codefellowship.repositories.ApplicationUserRepository;
import com.codefellowship.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.ArrayList;

@Controller
public class MainController {
    @Autowired
    ApplicationUserRepository applicationUserRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    PostRepository postRepository;

    @GetMapping("/")
    public String getHome(){
        return "home";
    }

    @GetMapping("/signup")
    public String getSignUpPage() {
        return "signup";
    }

    @GetMapping("/login")
    public String getSignInPage() {
        return "login";
    }

    @GetMapping("/profile")
    public String profileInformation(Model model, Principal principal) {
        ApplicationUser applicationUser = applicationUserRepository.findApplicationUserByUsername(principal.getName());
        model.addAttribute("username", applicationUser);
        return "profile";
    }

    @PostMapping("/signup")
    public RedirectView attemptSignUp(@RequestParam String username, @RequestParam String password, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String dateOfBirth, @RequestParam String bio) {
        ApplicationUser applicationUser = new ApplicationUser(username, bCryptPasswordEncoder.encode(password), firstName, lastName, dateOfBirth, bio);
        applicationUser = applicationUserRepository.save(applicationUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(applicationUser, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new RedirectView("/login");
    }

    @GetMapping("users/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        model.addAttribute("username", applicationUserRepository.findApplicationUserById(id));
        return ("profile");
    }

    @GetMapping("/posts")
    public String getPostForUsername(Model model , Principal principal) {
        ApplicationUser applicationUser = applicationUserRepository.findApplicationUserByUsername(principal.getName());
        model.addAttribute("username" , applicationUser);
        return "posts";
    }

    @PostMapping("/posts")
    public RedirectView createPostUsername(Model model , Principal principal , String body)
    {
        ApplicationUser applicationUser = applicationUserRepository.findApplicationUserByUsername(principal.getName());
        Post post = new Post(applicationUser , body);
        post = postRepository.save(post);
        model.addAttribute("username" , applicationUser.getWrittenPost());
        return  new RedirectView("/profile");
    }



}

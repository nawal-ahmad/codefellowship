package com.codefellowship.controllers;

import com.codefellowship.models.ApplicationUser;
import com.codefellowship.models.Post;
import com.codefellowship.repositories.ApplicationUserRepository;
import com.codefellowship.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("/login")
    public RedirectView loginResponse(@ModelAttribute ApplicationUser user, Model model) {
        model.addAttribute("username", applicationUserRepository.findUserByUsername(user.getUsername()));
        return new RedirectView("myprofile");
    }

    @PostMapping("/signup")
    public RedirectView attemptSignUp(@ModelAttribute ApplicationUser user){
        ApplicationUser newUser = new ApplicationUser(user.getUsername(),
                bCryptPasswordEncoder.encode(user.getPassword()),
                user.getFirstName(), user.getLastName(), user.getDateOfBirth(), user.getBio());
        applicationUserRepository.save(newUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new RedirectView("/login");
    }

    @GetMapping("users/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        model.addAttribute("username", applicationUserRepository.findUserById(id));
        return ("profile");
    }

    @GetMapping("/myprofile")
    public String getUserProfile(Principal p, Model m){
        ApplicationUser user = applicationUserRepository.findUserByUsername(p.getName());
        m.addAttribute("username", p.getName());
        m.addAttribute("userProfile", user);
        return "profile";
    }


//        List<Post> posts = user.getposts();
//        m.addAttribute("posts");

    @PostMapping("/addPost")
    public RedirectView addPost(Principal p, String body){
        ApplicationUser newUser = applicationUserRepository.findUserByUsername(p.getName());
        Post post = new Post(newUser, body);
        postRepository.save(post);
        return new RedirectView("/myprofile");
    }


//    @GetMapping("/posts")
//    public String getPostForUsername(Model model , Principal principal) {
//        ApplicationUser applicationUser = applicationUserRepository.findApplicationUserByUsername(principal.getName());
//        model.addAttribute("username" , applicationUser);
//        return "posts";
//    }
//
//    @PostMapping("/posts")
//    public RedirectView createPostUsername(Model model , Principal principal , String body)
//    {
//        ApplicationUser applicationUser = applicationUserRepository.findApplicationUserByUsername(principal.getName());
//        Post post = new Post(applicationUser , body);
//        post = postRepository.save(post);
//        model.addAttribute("username" , applicationUser.getWrittenPost());
//        return  new RedirectView("/profile");
//    }



}

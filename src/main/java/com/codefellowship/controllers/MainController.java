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
import java.util.Set;

@Controller

public class MainController {

    @Autowired
    ApplicationUserRepository applicationUserRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    PostRepository postRepository;

    @GetMapping("/")
    public String getHome() {
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
    public RedirectView attemptSignUp(@ModelAttribute ApplicationUser user) {
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
    public String getUserProfile(Principal p, Model m) {
        ApplicationUser user = applicationUserRepository.findUserByUsername(p.getName());
        m.addAttribute("username", p.getName());
        m.addAttribute("userProfile", user);
        return "profile";
    }


    @PostMapping("/addPost")
    public RedirectView addPost(Principal p, String body) {
        ApplicationUser newUser = applicationUserRepository.findUserByUsername(p.getName());
        Post post = new Post(newUser, body);
        postRepository.save(post);
        return new RedirectView("/myprofile");
    }

    //    @GetMapping("/allusers")
//    public String showAllUsers(Principal p , Model m){
//        Iterable<ApplicationUser> allUsers = applicationUserRepository.findAll();
//        m.addAttribute("allUsers", allUsers);
//        return "allusers";
//    }
    @GetMapping("/allusers")
    public String getAllUsers(Principal principle, Model model) {
        model.addAttribute("userInfo", principle.getName());
        model.addAttribute("allusers", applicationUserRepository.findAll());
        ApplicationUser user = applicationUserRepository.findUserByUsername(principle.getName());
        model.addAttribute("userFollow", user.getFollowers());
        return "allusers";
    }

    @PostMapping("/follow")
    public RedirectView addFollow(Principal principle, @RequestParam long id) {
        ApplicationUser me = applicationUserRepository.findUserByUsername(principle.getName());
        ApplicationUser toFollow = applicationUserRepository.findById(id).get();
        me.getFollowers().add(toFollow);
        applicationUserRepository.save(me);
        return new RedirectView("/feed");
    }

    @GetMapping("/feed")
    public String getFollowingInfo(Principal principle, Model model) {
            model.addAttribute("userInfo", principle.getName());
            ApplicationUser user = applicationUserRepository.findUserByUsername(principle.getName());
            Set<ApplicationUser> whoIFollow = user.getFollowers();
            model.addAttribute("Allfollowing", whoIFollow);
        return "feed";
    }

}

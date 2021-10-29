package com.codefellowship.controllers;

import com.codefellowship.models.ApplicationUser;
import com.codefellowship.models.Post;
import com.codefellowship.repositories.ApplicationUserRepository;
import com.codefellowship.repositories.ApplicationUserRepository;
import com.codefellowship.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    ApplicationUserRepository ApplicationUserRepository;

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
        model.addAttribute("username", ApplicationUserRepository.findUserByUsername(user.getUsername()));
        return new RedirectView("myprofile");
    }

    @PostMapping("/signup")
    public RedirectView attemptSignUp(@ModelAttribute ApplicationUser user) {
        ApplicationUser newUser = new ApplicationUser(user.getUsername(),
                bCryptPasswordEncoder.encode(user.getPassword()),
                user.getFirstName(), user.getLastName(), user.getDateOfBirth(), user.getBio());
        ApplicationUserRepository.save(newUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new RedirectView("/login");
    }

    @GetMapping("/myprofile")
    public String getUserProfile(Principal p, Model m) {
        ApplicationUser user = ApplicationUserRepository.findUserByUsername(p.getName());
        m.addAttribute("username", p.getName());
        m.addAttribute("userProfile", user);
        return "profile";
    }


    @PostMapping("/addPost")
    public RedirectView addPost(Principal p, String body) {
        ApplicationUser newUser = ApplicationUserRepository.findUserByUsername(p.getName());
        Post post = new Post(newUser, body);
        postRepository.save(post);
        return new RedirectView("/myprofile");
    }

    @GetMapping("/user")
    public String profile(@RequestParam long id, Model model, Principal principal) {
        ApplicationUser user = ApplicationUserRepository.findById(id).get();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userProfile", user);
        model.addAttribute("logged", ((UsernamePasswordAuthenticationToken) principal).getPrincipal());
        return "user";
    }

    @GetMapping("/users")
    public String getUsers(Model model, Principal principal) {
        List<ApplicationUser> users = ApplicationUserRepository.findAll();
        model.addAttribute("allusers", users);
        ApplicationUser user = ApplicationUserRepository.findUserByUsername(principal.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userInfo", principal.getName());
        model.addAttribute("logged", ((UsernamePasswordAuthenticationToken) principal).getPrincipal());
        model.addAttribute("whoIFollow", user.getFollowers());
        return "allusers";
    }

    @PostMapping("/follow")
    public RedirectView followUser(@AuthenticationPrincipal ApplicationUser user, @RequestParam Long id) {
        ApplicationUser feed = ApplicationUserRepository.findUserByUsername(user.getUsername());
        ApplicationUser follow = ApplicationUserRepository.findById(id).get();
        feed.getFollowers().add(follow);
        ApplicationUserRepository.save(feed);
        return new RedirectView("/users");
    }

    @GetMapping("/feed")
    public String getUsersInfo(@AuthenticationPrincipal ApplicationUser user, Model model) {
        ApplicationUser feed = ApplicationUserRepository.findUserByUsername(user.getUsername());
        Set<ApplicationUser> following = feed.getFollowers();
        model.addAttribute("followers", following);
        return "feed";
    }


}

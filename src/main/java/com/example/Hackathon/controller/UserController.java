package com.example.Hackathon.controller;


import com.example.Hackathon.model.User;
import com.example.Hackathon.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    UserRepo userRepo;

    @PostMapping("/login")
    @CrossOrigin
    public String login(@RequestBody User user, HttpSession session) {
        User foundUser = userRepo.findByEmail(user.getEmail());

        if (foundUser == null) {
            return "user not found";
        }

        if (foundUser.getPassword().equals(user.getPassword())) {
            session.setAttribute("userId", foundUser.getUserId());
            return "user login successful";
        } else {
            return "No user/password combination";
        }
    }

    @PostMapping("/register")
    @CrossOrigin
    public String register(@RequestBody User user) {
        String email = user.getEmail();
        String password = user.getPassword();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        if (email == null || password == null || firstName == null || lastName == null){
            throw new IllegalArgumentException("Please supply all required values");
        }
        
        boolean alreadyExists = userRepo.findByEmail(email) != null;
        if (alreadyExists){throw new IllegalArgumentException("User already exists");}

        User newUser = new User();

        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);

        userRepo.save(newUser);

        return "created new user";
    }

    @GetMapping("/logout")
    @CrossOrigin
    public String logout(HttpSession session){
        session.invalidate();
        return "Logged out successfully";
    }

}

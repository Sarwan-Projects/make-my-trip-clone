package com.makemytrip.makemytrip.controller;

import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController
{
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Users login(@RequestParam String email, @RequestParam String password)
    {
        return userService.login(email,password);
    }

    @PostMapping("/signup")
    public ResponseEntity<Users> signup(@RequestBody Users users)
    {
        return ResponseEntity.ok(userService.signup(users));
    }

    @GetMapping("/email")
    public ResponseEntity<Users> getuserbyemail(@PathVariable String email)
    {
        Users users = userService.getUserByEmail(email);
        if(users != null)
        {
            ResponseEntity.ok(users);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/edit")
    public Users editprofile(@RequestParam String id, @RequestBody Users updatedUser)
    {
        return userService.editProfile(id, updatedUser);
    }
}

package com.makemytrip.makemytrip.service;

import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users login(String email, String password)
    {
        Users user = userRepository.findByEmail(email);
        if(user != null && passwordEncoder.matches(password,user.getPassword()))
        {
            return user;
        }
        return null;
    }
    public Users signup(Users users)
    {
        if(userRepository.findByEmail(users.getEmail()) != null)
        {
            throw new RuntimeException("Email is Already Exist");
        }
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        if(users.getRole() == null)
        {
            users.setRole("USER");
        }
        return userRepository.save(users);
    }
    public Users getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Users editProfile(String id, Users updatedUser)
    {
        Users users = userRepository.findById(id).orElse(null);
        if(users != null)
        {
            users.setFirstName(updatedUser.getFirstName());
            users.setLastName(updatedUser.getLastName());
            users.setPhoneNumber(updatedUser.getPhoneNumber());
            return userRepository.save(users);
        }
        return null;
    }
}

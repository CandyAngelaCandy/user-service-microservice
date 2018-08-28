package com.thoughtworks.training.huangyanyan.todoserice.service;

import com.thoughtworks.training.huangyanyan.todoserice.model.User;
import com.thoughtworks.training.huangyanyan.todoserice.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import io.jsonwebtoken.Jwts;


@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public boolean verify(String userName, String password) {

//        boolean present = userRepository.findByName(userName).map(User::getPassword).filter(password::equals).isPresent();

        User user = userRepository.findByName(userName).get();

        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException(String.format("wrong password"));
        }

        return true;

    }

    public User list() {
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepository.findOne(user.getId()).get();
    }

    public void save(User user) {
        String password = user.getPassword();
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
    }

    public User findUserById(Integer userId) {
        return userRepository.findOne(userId);
    }

    public boolean verifyInternalToken(int userId, String userName) {

        if(userRepository.findOne(userId).isPresent()){

            User user = userRepository.findOne(userId).get();

            return user.getName().equals(userName);
        }
        return false;

    }

    public User getUserByToken(String token) {

        Claims TokenClaims = Jwts.parser()
                .setSigningKey("kitty".getBytes(Charset.forName("UTF-8")))
                .parseClaimsJws(token)
                .getBody();

        Integer userId = TokenClaims.get("userId", Integer.class);
        String userName = TokenClaims.get("userName",String.class);

        User user = userRepository.findOne(userId);

         if(user.getName().equals(userName)){
                return user;
         }
        return null;

    }

    public User findOneByName(String userName) {
        return userRepository.findByName(userName).get();
    }
}
package com.example.userservice.services;

import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repos.TokenRepo;
import com.example.userservice.repos.UserRepo;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final TokenRepo tokenRepo;
    private UserRepo userRepo;
    private BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, TokenRepo tokenRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepo = tokenRepo;
    }

    public User signUp(String name, String email, String password) {
        // Create a new user
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(passwordEncoder.encode(password));
        return userRepo.save(user);
    }

    public Token login(String email, String password) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new UsernameNotFoundException("User not found");
        }
        Token token = generateToken(user);
        return tokenRepo.save(token);
    }

    private Token generateToken(User user) {
        Token token = new Token();
        token.setValue(RandomStringUtils.randomAlphanumeric(10));
        token.setUser(user);
        token.setExpiryAt(System.currentTimeMillis() + 1000 * 60 * 60 );
        return token;
    }

    public User validateToken(String token) {
        Optional<Token> tokenOptional = tokenRepo.findByValueAndDeletedAndExpiryAtGreaterThan(token, false, System.currentTimeMillis());
        if (tokenOptional.isEmpty()) {
            throw new UsernameNotFoundException("Token not found");
        }

        return tokenOptional.get().getUser();
    }
}

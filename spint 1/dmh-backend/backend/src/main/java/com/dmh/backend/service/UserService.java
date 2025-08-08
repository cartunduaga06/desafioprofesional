package com.dmh.backend.service;

import com.dmh.backend.dto.LoginRequest;
import com.dmh.backend.dto.LoginResponse;
import com.dmh.backend.dto.RegisterRequest;
import com.dmh.backend.model.User;
import com.dmh.backend.repository.UserRepository;
import com.dmh.backend.security.JwtTokenProvider;
import com.dmh.backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.*;

/**
 * Service encapsulating user-related business logic, including registration,
 * authentication and session management.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    /**
     * In-memory store for revoked JWT tokens. In a production scenario this could be persisted in a cache
     * like Redis or a database table to scale horizontally.
     */
    private final Set<String> revokedTokens = Collections.synchronizedSet(new HashSet<>());

    /**
     * Word list used to generate random aliases. Loaded at startup from classpath.
     */
    private List<String> wordList;

    /**
     * The file location (classpath) containing the dictionary of words for alias generation.
     */
    @Value("classpath:words.txt")
    private org.springframework.core.io.Resource wordsResource;

    private final SecureRandom random = new SecureRandom();

    @PostConstruct
    public void loadWords() {
        List<String> words = new ArrayList<>();
        try (InputStream is = wordsResource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    words.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load words file", e);
        }
        if (words.isEmpty()) {
            throw new IllegalStateException("Words list is empty");
        }
        this.wordList = words;
    }

    /**
     * Register a new user and generate a CVU and alias.
     *
     * @param request registration payload
     * @return the created user
     */
    @Transactional
    public User register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        // Generate CVU and alias until unique
        String cvu = generateUniqueCvu();
        String alias = generateUniqueAlias();
        // Encode password
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setCvu(cvu);
        user.setAlias(alias);
        return userRepository.save(user);
    }

    /**
     * Authenticate a user and generate a JWT.
     *
     * @param request login payload
     * @return response containing JWT and user details
     */
    public LoginResponse authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = tokenProvider.generateToken(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return new LoginResponse(token,
                userDetails.getFullName(),
                userDetails.getUsername(),
                userDetails.getCvu(),
                userDetails.getAlias());
    }

    /**
     * Revoke a token by adding it to the blacklist.
     *
     * @param token the JWT to revoke
     */
    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            revokedTokens.add(token);
        }
    }

    /**
     * Check if a token has been revoked.
     *
     * @param token the JWT
     * @return true if revoked
     */
    public boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }

    // Helper methods

    private String generateUniqueCvu() {
        String cvu;
        do {
            cvu = generateRandomNumericString(22);
        } while (userRepository.existsByCvu(cvu));
        return cvu;
    }

    private String generateRandomNumericString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateUniqueAlias() {
        String alias;
        do {
            alias = generateRandomAlias();
        } while (userRepository.existsByAlias(alias));
        return alias;
    }

    private String generateRandomAlias() {
        // Pick three random words and join with dots
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            int index = random.nextInt(wordList.size());
            sb.append(wordList.get(index));
            if (i < 2) {
                sb.append('.');
            }
        }
        return sb.toString().toLowerCase();
    }
}

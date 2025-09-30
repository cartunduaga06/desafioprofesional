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

import jakarta.annotation.PostConstruct;
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
    @Value("classpath:alias.txt")
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
        // Check if DNI already exists
        if (userRepository.existsByDni(request.getDni())) {
            throw new IllegalArgumentException("DNI is already in use");
        }
        // Generate CVU and alias until unique
        String cvu = generateUniqueCvu();
        String alias = generateUniqueAlias();
        // Encode password
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDni(request.getDni());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setCvu(cvu);
        user.setAlias(alias);
        user.setBalance(java.math.BigDecimal.ZERO);
        return userRepository.save(user);
    }

    /**
     * Obtiene un usuario por su identificador. Si no existe lanza NoSuchElementException.
     *
     * @param id identificador
     * @return usuario
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("User not found"));
    }

    /**
     * Devuelve una representación del usuario sin exponer la contraseña.
     *
     * @param id identificador del usuario
     * @return DTO con los datos del usuario
     */
    public com.dmh.backend.dto.UserResponse getUserProfile(Long id) {
        User user = getUserById(id);
        return new com.dmh.backend.dto.UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getDni(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getCvu(),
                user.getAlias(),
                user.getBalance()
        );
    }

    /**
     * Actualiza selectivamente los datos del usuario. Si algún campo del request es null o vacío, no se modifica.
     * Comprueba la unicidad de email y DNI cuando se actualizan.
     *
     * @param id      identificador del usuario a actualizar
     * @param request datos a actualizar
     * @return DTO actualizado
     */
    @Transactional
    public com.dmh.backend.dto.UserResponse updateUser(Long id, com.dmh.backend.dto.UserUpdateRequest request) {
        User user = getUserById(id);
        // Actualizar nombre
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
        }
        // Actualizar DNI
        if (request.getDni() != null && !request.getDni().isBlank() && !request.getDni().equals(user.getDni())) {
            if (userRepository.existsByDni(request.getDni())) {
                throw new IllegalArgumentException("DNI is already in use");
            }
            user.setDni(request.getDni());
        }
        // Actualizar teléfono
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        // Actualizar email
        if (request.getEmail() != null && !request.getEmail().isBlank() && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email is already in use");
            }
            user.setEmail(request.getEmail());
        }
        // Persistir cambios
        User saved = userRepository.save(user);
        return new com.dmh.backend.dto.UserResponse(
                saved.getId(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getDni(),
                saved.getPhoneNumber(),
                saved.getEmail(),
                saved.getCvu(),
                saved.getAlias(),
                saved.getBalance()
        );
    }

    /**
     * Devuelve información de la cuenta asociada al usuario (cvu, alias, balance).
     *
     * @param id identificador del usuario/cuenta
     * @return DTO de la cuenta
     */
    public com.dmh.backend.dto.AccountResponse getAccount(Long id) {
        User user = getUserById(id);
        return new com.dmh.backend.dto.AccountResponse(
                user.getId(),
                user.getCvu(),
                user.getAlias(),
                user.getBalance()
        );
    }

    /**
     * Actualiza datos de la cuenta, actualmente sólo permite modificar el alias.
     *
     * @param id      identificador del usuario/cuenta
     * @param request datos de actualización
     * @return DTO actualizado
     */
    @Transactional
    public com.dmh.backend.dto.AccountResponse updateAccount(Long id, com.dmh.backend.dto.AccountUpdateRequest request) {
        User user = getUserById(id);
        if (request.getAlias() != null && !request.getAlias().isBlank() && !request.getAlias().equals(user.getAlias())) {
            // Validar unicidad de alias
            if (userRepository.existsByAlias(request.getAlias())) {
                throw new IllegalArgumentException("Alias is already in use");
            }
            user.setAlias(request.getAlias());
        }
        User saved = userRepository.save(user);
        return new com.dmh.backend.dto.AccountResponse(
                saved.getId(),
                saved.getCvu(),
                saved.getAlias(),
                saved.getBalance()
        );
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
        return new LoginResponse(
                token,
                userDetails.getFirstName(),
                userDetails.getLastName(),
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

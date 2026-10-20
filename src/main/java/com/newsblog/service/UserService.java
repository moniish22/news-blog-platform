package com.newsblog.service;

import com.newsblog.dto.UserDto;
import com.newsblog.model.Role;
import com.newsblog.model.User;
import com.newsblog.repository.UserRepository;
import com.newsblog.util.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Accounts, roles, profile and password reset. Also plugs the database into Spring Security. */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("No account for " + email));
        return org.springframework.security.core.userdetails.User.withUsername(u.getEmail())
                .password(u.getPassword())
                .roles(u.getRole().name())
                .disabled(!u.isEnabled())
                .build();
    }

    /** Self-registration always creates a READER; admins can promote users later. */
    @Transactional
    public User register(UserDto dto) {
        String email = dto.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        User u = new User();
        u.setName(dto.getName().trim());
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setRole(Role.READER);
        return userRepository.save(u);
    }

    /** Returns the logged-in user, or null for anonymous visitors. */
    @Transactional(readOnly = true)
    public User currentUser(Principal principal) {
        return principal == null ? null : userRepository.findByEmail(principal.getName()).orElse(null);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public List<User> findAll() { return userRepository.findAll(); }

    public long count() { return userRepository.count(); }
    public long countByRole(Role role) { return userRepository.countByRole(role); }

    @Transactional
    public User updateProfile(User user, String name, String bio, String avatar) {
        user.setName(name.trim());
        user.setBio(bio == null || bio.isBlank() ? null : bio.trim());
        String av = avatar == null ? "" : avatar.trim();
        user.setAvatar(av.startsWith("http://") || av.startsWith("https://") ? av : null);
        return userRepository.save(user);
    }

    @Transactional
    public void changeRole(Long id, Role role) {
        User u = getById(id);
        u.setRole(role);
        userRepository.save(u);
    }

    @Transactional
    public void toggleEnabled(Long id) {
        User u = getById(id);
        u.setEnabled(!u.isEnabled());
        userRepository.save(u);
    }

    /** Creates a one-hour reset token; empty if the email is unknown (the caller must not reveal that). */
    @Transactional
    public Optional<String> createResetToken(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase()).map(u -> {
            String token = UUID.randomUUID().toString();
            u.setResetToken(token);
            u.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(u);
            return token;
        });
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        if (token == null || newPassword == null || newPassword.length() < 6) return false;
        Optional<User> found = userRepository.findByResetToken(token)
                .filter(u -> u.getResetTokenExpiry() != null && u.getResetTokenExpiry().isAfter(LocalDateTime.now()));
        found.ifPresent(u -> {
            u.setPassword(passwordEncoder.encode(newPassword));
            u.setResetToken(null);
            u.setResetTokenExpiry(null);
            userRepository.save(u);
        });
        return found.isPresent();
    }

    public boolean isResetTokenValid(String token) {
        return token != null && userRepository.findByResetToken(token)
                .filter(u -> u.getResetTokenExpiry() != null && u.getResetTokenExpiry().isAfter(LocalDateTime.now())).isPresent();
    }
}

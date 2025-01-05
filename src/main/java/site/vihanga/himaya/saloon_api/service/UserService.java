package site.vihanga.himaya.saloon_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.vihanga.himaya.saloon_api.config.JwtUtil;
import site.vihanga.himaya.saloon_api.model.User;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.vihanga.himaya.saloon_api.repository.UserRepository;

import javax.print.attribute.standard.PresentationDirection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  JwtUtil jwtUtil;


    public String registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists with this email");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        if (Objects.equals(user.getRoles(), "")  || user.getRoles() == null){
            user.setRoles("USER");
        }
        User savedUSer = userRepository.save((user));
        return jwtUtil.generateToken(savedUSer.getId().toString());
    }

    public Map<String, Object> loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate the JWT token
        String token = jwtUtil.generateToken(user.getId().toString());

        // Determine user role
        String role = user.getRoles();

        // Build the response object
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", role);

        return response; // The frontend can handle redirection based on role
    }


    public User getUserProfile(ObjectId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public void forgotPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        userRepository.save(user);
    }

    public void deleteUser(ObjectId id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User could not be deleted because it does not exist");
        }
        userRepository.deleteById(id);
    }

    public User updateUser(ObjectId userId, User updatedUser) {
        // Find the user by ID
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields
        if (updatedUser.getFullname() != null) {
            existingUser.setFullname(updatedUser.getFullname());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getContact() != null) {
            existingUser.setContact(updatedUser.getContact());
        }
        if (updatedUser.getAddress() != null) {
            existingUser.setAddress(updatedUser.getAddress());
        }

        // Save updated user
        return userRepository.save(existingUser);
    }

}

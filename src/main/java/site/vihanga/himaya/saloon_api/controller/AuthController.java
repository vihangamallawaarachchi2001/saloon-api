package site.vihanga.himaya.saloon_api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import site.vihanga.himaya.saloon_api.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.vihanga.himaya.saloon_api.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        String Token = userService.registerUser(user);
        return ResponseEntity.ok(Map.of("token", Token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            // Call the service to authenticate the user and get the token and role
            Map<String, Object> authResponse = userService.loginUser(loginData.get("email"), loginData.get("password"));

            // Return the response as-is
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            // Handle exceptions and return an appropriate error response
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }


    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam ObjectId userId) {
        User user = userService.getUserProfile(userId);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userService.getAllUsers(pageable);

        // Prepare the response
        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        userService.forgotPassword(request.get("email"), request.get("newPassword"));
        return ResponseEntity.ok("Password updated successfully");
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestParam ObjectId id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "User deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable ObjectId userId,
            @RequestBody User updatedUser) {
        try {
            User updated = userService.updateUser(userId, updatedUser);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}

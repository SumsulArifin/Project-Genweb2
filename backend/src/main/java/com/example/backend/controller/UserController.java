package com.example.backend.controller;
import com.example.backend.entity.model.User;
import com.example.backend.entity.request.AuthenticationRequest;
import com.example.backend.entity.request.ChangePasswordRequest;
import com.example.backend.entity.request.UserRequest;
import com.example.backend.entity.response.AuthenticationResponse;
import com.example.backend.repository.UserRepository;
import com.example.backend.services.UserExcelExporter;
import com.example.backend.services.UserServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/auth")
@CrossOrigin("*")
public class UserController {


    @Autowired
    UserServices userServices;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Logger logger= LoggerFactory.getLogger(UserController.class);
    @Autowired
    private ObjectMapper mapper;

    @PostMapping("/register")
    public ResponseEntity<?> uploadUserWithImage(
            @RequestParam("user") String userData,
            @RequestParam("file") MultipartFile file) throws IOException {
        User user=mapper.readValue(userData,User.class);
        if (file != null && !file.isEmpty()) {
            user.setImgName(file.getOriginalFilename());
            user.setProfileImg(file.getBytes());
        }

        return ResponseEntity.ok(userServices.register(user));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    )
    {
        return ResponseEntity.ok(userServices.login(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        userServices.refreshToken(request, response);
    }
    @PostMapping("/changePassword")
    public void changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication instanceof UsernamePasswordAuthenticationToken) {
            User user = (User) authentication.getPrincipal();
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new IllegalStateException("Wrong password");
            }
            if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
                throw new IllegalStateException("Passwords do not match");
            }user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
        } else {
            throw new IllegalStateException("User is not authenticated or is of an unexpected type");
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable int id, @ModelAttribute User updatedUser, @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        Optional<User> existingUserOptional = userRepository.findById(id);

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            if (file != null && !file.isEmpty()) {
                existingUser.setImgName(file.getOriginalFilename());
                existingUser.setProfileImg(file.getBytes());
            }
            userRepository.save(existingUser);
            return ResponseEntity.ok("User data updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/image/{id}")

    public ResponseEntity<byte[]> getUserImage(@PathVariable int id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(user.getProfileImg(), headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") int id) {
        userServices.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/all-with-images")
    public ResponseEntity<List<UserRequest>> getAllUsersWithImages() {
        List<UserRequest> usersWithImages = userServices.getAllUsersWithImages();
        return ResponseEntity.ok(usersWithImages);
    }

    @PostMapping("/upload-excel")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        List<User> users = userServices.processExcelFile(file);
        return ResponseEntity.ok("Excel file uploaded and processed successfully.");
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headervalue = "attachment; filename=Student_info.xlsx";

        response.setHeader(headerKey, headervalue);
        List<User> listStudent = userRepository.findAll();
        UserExcelExporter exp = new UserExcelExporter(listStudent);
        exp.export(response);
    }

}


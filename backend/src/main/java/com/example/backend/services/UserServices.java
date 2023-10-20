package com.example.backend.services;
import com.example.backend.entity.model.User;
import com.example.backend.entity.request.AuthenticationRequest;
import com.example.backend.entity.request.ChangePasswordRequest;
import com.example.backend.entity.request.UserRequest;
import com.example.backend.entity.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Component
public interface UserServices {
    public User getImageById(int id);
    public List<User> getAllUsers();
    void deleteUser(int id);
    public List<User> processExcelFile(MultipartFile file);
    public AuthenticationResponse register(User request);
    public AuthenticationResponse login(AuthenticationRequest request);
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
    public List<UserRequest> getAllUsersWithImages();
    public void changePassword(ChangePasswordRequest request, Principal connectedUser);





}

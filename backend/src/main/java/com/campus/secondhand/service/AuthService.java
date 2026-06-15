package com.campus.secondhand.service;

import com.campus.secondhand.dto.Requests;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.User;

public interface AuthService {
    Views.LoginView register(Requests.Register request);
    Views.LoginView login(Requests.Login request, boolean adminLogin);
    User profile();
    User updateProfile(Requests.ProfileUpdate request);
    void updatePassword(Requests.PasswordUpdate request);
}

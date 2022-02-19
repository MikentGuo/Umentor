package com.Umentor.UmentorprojectforProgrammingIII.controller;

import com.Umentor.UmentorprojectforProgrammingIII.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AddUserToView {

    @ModelAttribute("user")
    public User addUserToModel(Authentication authentication) {
        return authentication != null ? (User) authentication.getPrincipal() : null;
    }
}

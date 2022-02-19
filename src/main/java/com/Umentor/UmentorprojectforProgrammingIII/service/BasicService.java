package com.Umentor.UmentorprojectforProgrammingIII.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface BasicService {

    UserDetails getUser(String username) throws UsernameNotFoundException;
}

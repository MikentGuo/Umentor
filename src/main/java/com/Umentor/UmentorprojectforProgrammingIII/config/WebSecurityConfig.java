package com.Umentor.UmentorprojectforProgrammingIII.config;


import com.Umentor.UmentorprojectforProgrammingIII.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.TimeUnit;

import static com.Umentor.UmentorprojectforProgrammingIII.model.UserRole.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    private final RegistrationService registrationService;

    @Autowired
    public WebSecurityConfig(PasswordEncoder passwordEncoder, RegistrationService registrationService) {
        this.passwordEncoder = passwordEncoder;
        this.registrationService = registrationService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/", "/register/**", "/about", "/courses", "/contact", "/event", "/profile/image/**").permitAll()
                    .antMatchers("/profile/**").authenticated()
                    .antMatchers("/student/**").hasAnyRole(STUDENT.name())
                    .antMatchers("/teacher/**").hasAnyRole(TEACHER.name())
                    .antMatchers("/course-modify/**").hasAnyRole(TEACHER.name())
                    .antMatchers("/admin/**").hasRole(ADMIN.name())
                .and()
                .formLogin()
                    .loginPage("/login").permitAll()
                    .failureUrl("/login_err")
                    .defaultSuccessUrl("/",true)
                .and()
                    .rememberMe().tokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(7))
                    .key("a-very-secure-key")
                    .userDetailsService(registrationService)
                .and()
                .exceptionHandling()
                    .accessDeniedPage("/login")
                .and()
                .logout()
                    .logoutSuccessUrl("/")
                    .permitAll();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(registrationService);
        return provider;
    }
}

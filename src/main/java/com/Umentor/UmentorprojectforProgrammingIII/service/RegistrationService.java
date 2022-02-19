package com.Umentor.UmentorprojectforProgrammingIII.service;

import com.Umentor.UmentorprojectforProgrammingIII.email.EmailSender;
import com.Umentor.UmentorprojectforProgrammingIII.entity.ConfirmationToken;
import com.Umentor.UmentorprojectforProgrammingIII.entity.User;
import com.Umentor.UmentorprojectforProgrammingIII.model.UserRole;
import com.Umentor.UmentorprojectforProgrammingIII.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegistrationService implements UserDetailsService, BasicService {

    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;

    private final static String PW_ERROR = "Password must be at least 6 " +
            "characters long and must contain at least one uppercase letter, one lower case letter, and one number. It must not be longer than 100 char.";

    private final String SERVER_URL = "http://localhost:8080";

    @Autowired
    public RegistrationService(UserRepository userRepository, ConfirmationTokenService confirmationTokenService, PasswordEncoder passwordEncoder, EmailSender emailSender) {
        this.userRepository = userRepository;
        this.confirmationTokenService = confirmationTokenService;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
    }

    @Override
    public User getUser(String email) {
        if (!userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("User not found");
        }
        return userRepository.findByEmail(email).get();
    }

    @Transactional
    public String addUser(@Valid User user, BindingResult result, RedirectAttributes redirectAttributes) {
        boolean userExists = userRepository.findByEmail(user.getEmail()).isPresent();
        if (userExists) {
            FieldError emailTaken = new FieldError("user", "email", "this email is already registered");
            result.addError(emailTaken);
        }
        if (!user.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,100}$")) {
            FieldError pwErr = new FieldError("user", "password", PW_ERROR);
            result.addError(pwErr);
        }
        if (!user.getPassword().equals(user.getPasswordRepeat())) {
            FieldError pwRepeat = new FieldError("user", "passwordRepeat", "Please enter the same password");
            result.addError(pwRepeat);
        }
        if (result.hasErrors()) {
            return "register";
        }

        String encodePW = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePW);
        user.setRole(UserRole.STUDENT);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                user
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        String link = SERVER_URL + "/confirm?token=" + token;
        emailSender.send(user.getEmail(), buildEmail(user.getName(), link));
        redirectAttributes.addFlashAttribute("registered", "Registration success, an email has been sent with a link to activate your account.");

        return "redirect:/success";
    }

    @Transactional
    public String confirmToken(String token, RedirectAttributes redirectAttributes) {
        if (!confirmationTokenService.getToken(token).isPresent()) {
            redirectAttributes.addFlashAttribute("tokenNotFound", "Invalid token");
            return "redirect:/error";
        }

        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token).get();

        if (confirmationToken.getConfirmedAt() != null) {
            redirectAttributes.addFlashAttribute("confirmedToken", "email already activated");
            return "redirect:/error";
        }

        LocalDateTime expireAT = confirmationToken.getExpiresAt();

        if (expireAT.isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("expiredToken", "Token expired");
            return "redirect:/error";
        }

        confirmationTokenService.setConfirmedDT(token);
        userRepository.enableUser(confirmationToken.getUser().getEmail());
        redirectAttributes.addFlashAttribute("enabled", "Your account is activated.");

        return "redirect:/success";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(String.format("Email %s not registered", username)));
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 30 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}

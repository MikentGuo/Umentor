package com.Umentor.UmentorprojectforProgrammingIII.controller;

import com.Umentor.UmentorprojectforProgrammingIII.entity.User;
import com.Umentor.UmentorprojectforProgrammingIII.model.PasswordChange;
import com.Umentor.UmentorprojectforProgrammingIII.model.Teacher;
import com.Umentor.UmentorprojectforProgrammingIII.service.CourseService;
import com.Umentor.UmentorprojectforProgrammingIII.service.RegistrationService;
import com.Umentor.UmentorprojectforProgrammingIII.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class UserController {

    private final RegistrationService registrationService;

    private final CourseService courseService;

    private final UserService userService;

    @Autowired
    public UserController(RegistrationService registrationService, CourseService courseService, UserService userService) {
        this.registrationService = registrationService;
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login_err")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView mav = new ModelAndView("register");
        User user = new User();
        mav.addObject("userReg", user);
        return mav;
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("userReg") User user, BindingResult result, RedirectAttributes redirectAttributes) {
        return registrationService.addUser(user, result, redirectAttributes);
    }

    @GetMapping(path = "/confirm")
    public String comfirm(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        return registrationService.confirmToken(token, redirectAttributes);
    }

    @GetMapping("/success")
    public String about()
    {
        return "success";
    }

    @GetMapping("/profile")
    public ModelAndView teacherProfile(Principal principal) {
        ModelAndView mav = new ModelAndView("profile");
        mav.addObject("showCourse", true);
        mav.addObject("courseList", courseService.getCoursesWithLikesByUser(principal));
        mav.addObject("likedList", courseService.getLikedCoursesByStudent(principal));
        return mav;
    }

    @GetMapping("profile/change_pw")
    public ModelAndView passwordForm() {
        ModelAndView mav = new ModelAndView("profile");
        mav.addObject("showPWForm", true);
        PasswordChange change = new PasswordChange();
        mav.addObject("change", change);
        return mav;
    }

    @PostMapping("profile/change_pw")
    public ModelAndView changePW(PasswordChange change, Principal principal) {
        String email = principal.getName();
        ModelAndView mav = userService.changePW(change, email);
        mav.addObject("showPWForm", true);
        mav.setViewName("profile");
        return mav;
    }

    @PostMapping("/teacher/profile/update")
    public String updateTeacherProfile(@Valid Teacher teacher, BindingResult result, Model model, Principal principal) {
        model.addAttribute("showImgForm", true);

        return userService.changeTeacherProfile(teacher, result, principal);
    }

}

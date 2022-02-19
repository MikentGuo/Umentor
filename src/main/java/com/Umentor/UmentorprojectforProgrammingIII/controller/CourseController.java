package com.Umentor.UmentorprojectforProgrammingIII.controller;

import com.Umentor.UmentorprojectforProgrammingIII.entity.Course;
import com.Umentor.UmentorprojectforProgrammingIII.entity.User;
import com.Umentor.UmentorprojectforProgrammingIII.model.UserInfo;
import com.Umentor.UmentorprojectforProgrammingIII.service.CourseService;
import com.Umentor.UmentorprojectforProgrammingIII.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;


@Controller
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    @Autowired
    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }



    @GetMapping("/courses")
    public ModelAndView courses(Principal principal)
    {
        ModelAndView mav = new ModelAndView("courses");
        mav.addObject("courseList", courseService.getCoursesWithLikes());
        if (principal != null) {
            mav.addObject("likedList", courseService.getLikedCoursesByStudent(principal));
        }

        return mav;
    }

    @GetMapping("/teacher/course-modify/{courseId}")
    public String getCourseModifyPage(@PathVariable("courseId") Long courseId, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        model.addAttribute("tempCourse", new Course());
        return courseService.getModifiedCourse(courseId, principal, redirectAttributes, model);
    }

    @PostMapping("/teacher/course-modify/{courseId}")
    public String modifyCourse(@PathVariable("courseId") Long courseId, Course tempCourse, RedirectAttributes redirectAttributes) {
        return courseService.modifyCourse(courseId, tempCourse, redirectAttributes);
    }

    @GetMapping("/course-details/{id}")
    public String course_details(@PathVariable("id") Long id, Model model, Authentication authentication)
    {
        boolean isSub = false;

        if(authentication != null) {
            UserInfo userinfo = new UserInfo();
            User user = (User) authentication.getPrincipal();
            userinfo.setId(user.getId());
            userinfo.setUsername(user.getName());
            userinfo.setAvatarFilePath("profile/image/"+user.getId());
            model.addAttribute("userInfo", userinfo);
            User student = userService.getUserById(user.getId());
            isSub = courseService.checkStudentSubscription(id, student);
            if(!isSub) {
                Course course = courseService.getCourse(id);
                User teacher = course.getTeacher();
                System.out.println("course teacher:" + teacher.getId());
                System.out.println("userId:" + user.getId());
                if(teacher.getId().equals(user.getId()))
                    isSub = true;
            }
        }

        model.addAttribute("isSub", isSub);
        model.addAttribute("courseid", id);
        model.addAttribute("course", courseService.getCourse(id));

        return "course-details";
    }

    @GetMapping("/student/subscribe/{courseId}")
    public String courseSubscription(@PathVariable("courseId") Long courseId, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        return courseService.addSubscription(courseId, principal, redirectAttributes, model);
    }

}

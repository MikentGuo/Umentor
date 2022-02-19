package com.Umentor.UmentorprojectforProgrammingIII.controller;

import com.Umentor.UmentorprojectforProgrammingIII.entity.User;
import com.Umentor.UmentorprojectforProgrammingIII.model.JsonFav;
import com.Umentor.UmentorprojectforProgrammingIII.model.Teacher;
import com.Umentor.UmentorprojectforProgrammingIII.service.CourseService;
import com.Umentor.UmentorprojectforProgrammingIII.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

@RestController
public class GeneralRestController {

    private final UserService userService;

    private final CourseService courseService;

    @Autowired
    public GeneralRestController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/profile/image/{id}")
    public byte[] downloadUserProfileImage(@PathVariable("id") Long id) {
        return userService.downloadUserImageById(id);
    }

    @GetMapping("/courses/image/{courseId}")
    public byte[] downloadCourseImage(@PathVariable("courseId") Long courseId) {
        return courseService.downloadUserProfileImage(courseId);
    }

    @PostMapping("/course/toggle_fav")
    public void ToggleFav(@RequestBody JsonFav jsonFav, Principal principal) {
        courseService.toggleFav(jsonFav, principal);
    }

    @GetMapping("/profile/upload_image")
    public ModelAndView imageForm(Principal principal) {
        ModelAndView mav = new ModelAndView("profile");
        mav.addObject("showImgForm", true);
        User user = userService.getUser(principal.getName());
        mav.addObject("user", user);
        Teacher teacher = new Teacher();
        teacher.setUserName(user.getName());
        teacher.setDescription(user.getDescription());
        teacher.setTitle(user.getTitle());
        mav.addObject("teacher", teacher);
        return mav;
    }

    @PostMapping(
            path = "/profile/avatar/upload/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void uploadUserProfileImageByAdmin(@PathVariable Long id,
                                               @RequestParam("file") MultipartFile file, HttpServletResponse response) {
        userService.uploadAvatar(id, file);
        try {
            response.sendRedirect(String.format("/admin/trainers"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping(
            path = "/profile/avatar/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ModelAndView uploadUserProfileImage(Principal principal,
                                               @RequestParam("file") MultipartFile file) {
        userService.uploadAvatar(principal, file);
        ModelAndView mav = new ModelAndView("profile");
        mav.addObject("showImgForm", true);
        return mav;
    }

    @PostMapping(
            path = "/course/image/upload/{courseId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void uploadCourseImage(@PathVariable("courseId") Long id,
                                               @RequestParam("file") MultipartFile file, HttpServletResponse response) {
        courseService.uploadCoursePhoto(id, file);

        try {
            response.sendRedirect(String.format("/teacher/course-modify/%d", id));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping(
            path = "/admin/course/image/upload/{courseId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void uploadCourseImageByAdmin(@PathVariable("courseId") Long id,
                                         @RequestParam("file") MultipartFile file, HttpServletResponse response) {
        courseService.uploadCoursePhoto(id, file);

        try {
            response.sendRedirect(String.format("/admin/courses"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

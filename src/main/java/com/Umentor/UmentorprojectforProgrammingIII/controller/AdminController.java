package com.Umentor.UmentorprojectforProgrammingIII.controller;

import com.Umentor.UmentorprojectforProgrammingIII.entity.Course;
import com.Umentor.UmentorprojectforProgrammingIII.entity.Event;
import com.Umentor.UmentorprojectforProgrammingIII.entity.User;
import com.Umentor.UmentorprojectforProgrammingIII.model.Teacher;
import com.Umentor.UmentorprojectforProgrammingIII.model.UserInfo;
import com.Umentor.UmentorprojectforProgrammingIII.model.UserRole;
import com.Umentor.UmentorprojectforProgrammingIII.service.CourseService;
import com.Umentor.UmentorprojectforProgrammingIII.service.MainService;
import com.Umentor.UmentorprojectforProgrammingIII.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private final UserService userService;
  private final CourseService courseService;
  private final MainService mainService;
  private final PasswordEncoder passwordEncoder;

  public AdminController(UserService userService,
                         CourseService courseService,
                         MainService mainService,
                         PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.courseService = courseService;
    this.passwordEncoder = passwordEncoder;
    this.mainService = mainService;
  }

  // -----------------------------User
  @GetMapping("/users")
  public String listUser(Model model) {
    List<User> users = userService.getAllUsers();
    for (User user : users) {
      if(user.getRole().equals(UserRole.STUDENT)) {
        user.setCoursenum(user.getCourseList().size());
      }
      else if(user.getRole().equals(UserRole.TEACHER)) {
        List<Course> list = courseService.getTeacherCourses(user);
          user.setCoursenum(list.size());
      }
      else {
        user.setCoursenum(user.getCourseList().size());
      }
    }
    model.addAttribute("users", users);
    return "admin/users";
  }

  @GetMapping("/users/new")
  public String createUserForm(Model model) {
    User user = new User();
    model.addAttribute("userNew", user);
    return "admin/create_user";
  }

  @GetMapping("/users/edit/{id}")
  public String editUserForm(@PathVariable Long id, Model model) {
    User user = userService.getUserById(id);
    System.out.println("editUserForm:" + user.toString());
    UserInfo userInfo = new UserInfo();
    userInfo.setId(user.getId());
    userInfo.setUsername(user.getName());
    userInfo.setEmail(user.getEmail());
    userInfo.setRole(user.getRole().toString());
    model.addAttribute("userInfo", userInfo);
    return "admin/edit_user";
  }

  @GetMapping("/users/{id}")
  public String deleteUser(@PathVariable Long id, Model model) {
    return "admin/users";
  }

  @GetMapping("/users/courses/{id}")
  public String listUserCourses(@PathVariable Long id, Model model) {
    User user = userService.getUserById(id);
    model.addAttribute("courses", user.getCourseList());
    return "admin/courses";
  }

  @PostMapping("/users")
  public String createUser(@ModelAttribute("userNew") User user) {
    System.out.println("createUser:" + user.toString());
    String password = passwordEncoder.encode("Password123");
    User user1 = new User(
        UserRole.STUDENT,
        user.getName(),
        user.getEmail(),
        password
    );
    user1.setEnabled(true);
    userService.saveUser(user1);
    return "redirect:/admin/users";
  }

  @PostMapping("/users/{id}")
  public String updateUser(@PathVariable Long id,
                           @ModelAttribute("userInfo") UserInfo user,
                           Model model) {
    User curUser = userService.getUserById(id);
    curUser.setName(user.getUsername());
    curUser.setEmail(user.getEmail());
    String role = user.getRole();
    if(role.equals("Student")){
      curUser.setRole(UserRole.STUDENT);
    }
    else if(role.equals("Teacher")){
      curUser.setRole(UserRole.TEACHER);
    }
    else if(role.equals("Admin")) {
      curUser.setRole(UserRole.ADMIN);
    }

    userService.saveUser(curUser);
    return "redirect:/admin/users";
  }

  //------------------Course
  @GetMapping("/courses")
  public String listCourse(Model model) {
    model.addAttribute("courses", courseService.getAllCourses());
    return "admin/courses";
  }

  @GetMapping("/courses/new")
  public String createCourseForm(Model model) {
    Course course = new Course();
    model.addAttribute("course", course);
    return "admin/create_course";

  }

  @PostMapping("/courses")
  public String createNewCourse(Course course, RedirectAttributes redirectAttributes) {
    return courseService.addNewCourse(course, redirectAttributes);
  }

  @GetMapping("/courses/edit/{id}")
  public String editCourseForm(@PathVariable Long id, Model model) {
    Course course = courseService.getCourse(id);
    model.addAttribute("course", course);
    return "admin/edit_course";
  }

  @GetMapping("/courses/students/{id}")
  public String listCourseStudents(@PathVariable Long id, Model model) {
    Course course = courseService.getCourse(id);
    List<User> users = course.getStudentList();
    model.addAttribute("users", users);
    return "admin/course_students";
  }

  @GetMapping("/courses/{id}")
  public String deleteCourse(@PathVariable Long id, Model model) {
    courseService.removeCourseById(id);
    return "redirect:/admin/courses";
  }

  @PostMapping("/courses/{id}")
  public String updateCourse(@PathVariable Long id,
                             @ModelAttribute("course") Course course,
                             RedirectAttributes redirectAttributes) {
    return courseService.modifyCourseByAdmin(id, course, redirectAttributes);
  }

  //-----Trainer
  @GetMapping("/trainers")
  public String listTrainers(Model model) {
    model.addAttribute("trainers", userService.getAllTeachers());
    return "admin/trainers";
  }

  @GetMapping("/trainer/edit/{id}")
  public String editTrainerForm(@PathVariable Long id, Model model) {
    Teacher teacher = new Teacher();
    model.addAttribute("trainer", teacher);
    User curTeacher = userService.getUserById(id);
    model.addAttribute("curTeacher", curTeacher);
    return "admin/edit_trainer";
  }

  @PostMapping("/trainer/edit/{id}")
  public String updateTrainerById(@PathVariable Long id, @Valid Teacher teacher, BindingResult result, Model model) {
    model.addAttribute("trainer", teacher);
    User curTeacher = userService.getUserById(id);
    model.addAttribute("curTeacher", curTeacher);
    return userService.modifyTeacherByAdmin(id, teacher, result);
  }

  //---------------event
  @GetMapping("/events")
  public String listEvent(Model model) {
    model.addAttribute("events", mainService.getAllEvents());
    return "admin/events";
  }

  @GetMapping("/events/new")
  public String creatEventForm(Model model) {
    Event event = new Event();
    model.addAttribute("event", event);
    return "admin/create_event";
  }

  @PostMapping("/events")
  public String createNewEvent(Event event, RedirectAttributes redirectAttributes) {
    return mainService.addNewEvent(event, redirectAttributes);
  }

  @GetMapping("/events/edit/{id}")
  public String editEventForm(@PathVariable Long id, Model model) {
    Event event = mainService.getEvent(id);
    model.addAttribute("event", event);
    return "admin/edit_event";
  }

  @GetMapping("/events/{id}")
  public String deleteEvent(@PathVariable Long id) {
    mainService.removeEventById(id);
    return "redirect:/admin/events";
  }

}

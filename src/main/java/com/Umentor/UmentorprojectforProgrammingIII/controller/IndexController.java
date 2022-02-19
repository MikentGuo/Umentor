package com.Umentor.UmentorprojectforProgrammingIII.controller;

import com.Umentor.UmentorprojectforProgrammingIII.entity.Course;
import com.Umentor.UmentorprojectforProgrammingIII.entity.Event;
import com.Umentor.UmentorprojectforProgrammingIII.entity.User;
import com.Umentor.UmentorprojectforProgrammingIII.model.ContactMsg;
import com.Umentor.UmentorprojectforProgrammingIII.model.UserInfo;
import com.Umentor.UmentorprojectforProgrammingIII.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class IndexController {

    private MainService mainService;

    @Autowired
    public IndexController(MainService mainService) {
        this.mainService = mainService;
    }

    @GetMapping("/")
    public String index(Model model)
    {
        model.addAttribute("courseList", mainService.getPopularCoursesWithLikes());
        model.addAttribute("teacherList", mainService.getPopularCoursesTeachers());

        model.addAttribute("dataCount", mainService.getDataCount());

        return "index";
    }

    @GetMapping("/trainers")
    public String trainers(Model model)
    {
        model.addAttribute("teacherList", mainService.getAllTeachers());

        return "trainers";
    }

    @GetMapping("/events")
    public String events(Model model)
    {
        List<Event> list = (List<Event>) mainService.findAll(3);
        System.out.println("total event:" + list.size());

        model.addAttribute("events", list);
        return "events";
    }

    @GetMapping("/about")
    public String about(Model model)
    {
        model.addAttribute("dataCount", mainService.getDataCount());
        model.addAttribute("testimonialList", mainService.getTestimonials());
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model)
    {
        ContactMsg msg = new ContactMsg();
        model.addAttribute("contactMsg", msg);
        return "contact";
    }

    @PostMapping("/contact")
    public String sendContactMsg(ContactMsg contactMsg, RedirectAttributes redirectAttributes) {
        return mainService.sendContactMsg(contactMsg, redirectAttributes);
    }

    @GetMapping("/chatroom/{id}")
    public String chatroom(@PathVariable("id") Long id, Model model, Authentication authentication)
    {
        if(authentication == null) {
            return "redirect:/login";
        }

        Course course = mainService.getCourse(id);
        if(course == null){
            return "redirect:/login";
        }

        model.addAttribute("courseid", id);

        String title = course.getTitle();
        model.addAttribute("coursetitle", title);
        System.out.println("coursetitle:" + title);

        UserInfo userinfo = new UserInfo();
        User user = (User) authentication.getPrincipal();
        userinfo.setId(user.getId());
        userinfo.setUsername(user.getName());
        userinfo.setAvatarFilePath("profile/image/"+user.getId());
        model.addAttribute("userInfo", userinfo);

        return "chatroom";
    }

    @GetMapping("/classroom/{id}")
    public String classroom(@PathVariable("id") Long id, Model model, Authentication authentication)
    {
        if(authentication == null) {
            return "redirect:/login";
        }
        // TODO: need to chech if the user has subscribe this class

        Course course = mainService.getCourse(id);
        if(course == null){
            return "redirect:/login";
        }

        model.addAttribute("courseid", id);
        model.addAttribute("coursetitle", course.getTitle());

        UserInfo userinfo = new UserInfo();
        User user = (User) authentication.getPrincipal();
        userinfo.setId(user.getId());
        userinfo.setUsername(user.getName());
        userinfo.setAvatarFilePath("profile/image/"+user.getId());
        model.addAttribute("userInfo", userinfo);

        return "classroom";
    }

    @GetMapping("/inprogress")
    public String underConstruction() {
        return "in_construction";
    }
}

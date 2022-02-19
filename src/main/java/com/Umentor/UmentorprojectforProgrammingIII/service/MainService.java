package com.Umentor.UmentorprojectforProgrammingIII.service;

import com.Umentor.UmentorprojectforProgrammingIII.entity.Course;
import com.Umentor.UmentorprojectforProgrammingIII.entity.Event;
import com.Umentor.UmentorprojectforProgrammingIII.entity.Testimonial;
import com.Umentor.UmentorprojectforProgrammingIII.entity.User;
import com.Umentor.UmentorprojectforProgrammingIII.model.*;
import com.Umentor.UmentorprojectforProgrammingIII.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MainService {
  private final CourseRepository courseRepository;
  private final EventRepository eventRepository;
  private final StudentLikeRepository studentLikeRepository;
  private final TestimonialRepository testimonialRepository;
  private final UserRepository userRepository;
  private final EmailService emailService;
  private final DataCount dataCount;

  public MainService(CourseRepository courseRepository, EventRepository eventRepository, StudentLikeRepository studentLikeRepository, TestimonialRepository testimonialRepository, UserRepository userRepository, EmailService emailService) {
    this.courseRepository = courseRepository;
    this.eventRepository = eventRepository;
    this.studentLikeRepository = studentLikeRepository;
    this.testimonialRepository = testimonialRepository;
    this.userRepository = userRepository;
    this.emailService = emailService;
    dataCount = new DataCount(
        findAll(0).size(),
        findAll(1).size(),
        findAll(2).size(),
        findAll(3).size());
  }

  public List<? extends Object> findAll(int flag)  {
    if(flag == 0)
      return userRepository.findAllByRole(UserRole.STUDENT);

    if(flag == 1)
      return userRepository.findAllByRole(UserRole.TEACHER);

    if(flag == 2)
      return courseRepository.findAll();

    if(flag == 3)
      return eventRepository.findAll();

    return null;
  }

  public void updateDataCount(int flag){
    if(flag == 0)
       dataCount.setStudent(findAll(flag).size());

    if(flag == 1)
      dataCount.setTrainer(findAll(flag).size());

    if(flag == 2)
      dataCount.setCourse(findAll(flag).size());

    if(flag == 3)
      dataCount.setEvent(findAll(flag).size());
  }

  public DataCount getDataCount(){
    return dataCount;
  }

  public List<Course> getAllCourses() {
    return courseRepository.findAll();
  }

  public List<CourseWithLikes> getPopularCoursesWithLikes(){
    List<CourseWithLikes> fullCourseList = new ArrayList<>();
    List<Course> courseList = courseRepository.findByIsRecommended(true);
    for (Course course : courseList) {
      int likes = studentLikeRepository.countAllByCourse_Id(course.getId());
      fullCourseList.add(new CourseWithLikes(course, likes));
      System.out.println("The number of likes are " + likes);
    }
    return fullCourseList;
  }

  public List<User> getPopularCoursesTeachers() {
    List<User> teacherList = new ArrayList<>();
    List<Course> courseList = courseRepository.findByIsRecommended(true);
    for (Course course : courseList) {
      User user = userRepository.findById(course.getTeacher().getId()).get();
      teacherList.add(user);
    }
    return teacherList;
  }

  public List<TestimonialInfo> getTestimonials() {
    List<Testimonial> list = testimonialRepository.findAll();
    List<TestimonialInfo> infoList = new ArrayList<>();
    for (Testimonial item : list) {
      Long id = item.getStudent().getId();
      User user = userRepository.findById(id).get();
      TestimonialInfo info = new TestimonialInfo();
      info.setUserid(id);
      info.setUsername(user.getName());
      info.setTitle(item.getTitle());
      info.setBody(item.getBody());
      infoList.add(info);
    }
    return infoList;
  }

  public Course getCourse(Long id) {
    return courseRepository.getById(id);
  }

  public List<User> getAllTeachers() {
    return userRepository.findAllByRole(UserRole.TEACHER);
  }

  public String sendContactMsg(ContactMsg contactMsg, RedirectAttributes redirectAttributes) {
    emailService.getContactMsg(contactMsg.getEmail(), buildEmail(contactMsg));
    redirectAttributes.addFlashAttribute("success", "Your message has been sent. Thank you!");
    return "redirect:/contact";
  }

  private String buildEmail(ContactMsg contactMsg) {
    return String.format("""
            Name: %s 
                            
            Email: %s 
                            
            Subject: %s 
                            
            Message: %s 
                            
            """, contactMsg.getFullName(), contactMsg.getEmail(), contactMsg.getSubject(), contactMsg.getBody());
  }

  public List<Event> getAllEvents(){
    return eventRepository.findAll();
  }

  public Event getEvent(Long id) {
    return eventRepository.getById(id);
  }

  public String addNewEvent(Event event, RedirectAttributes redirectAttributes) {
    LocalDateTime dateTime = null;
    if (event.getTitle().isEmpty() || event.getTitle().length() > 30) {
      redirectAttributes.addFlashAttribute("titleErr", "Title must be between 1 and 30 characters");
    }
    if (event.getBody().length() < 5 || event.getBody().length() > 1000) {
      redirectAttributes.addFlashAttribute("bodyErr", "Content must be between 5 and 1000 characters");
    }
    if (!event.getStringStartTime().isEmpty()) {
      dateTime = LocalDateTime.parse(event.getStringStartTime());
      if (dateTime.isBefore(LocalDateTime.now())) {
        redirectAttributes.addFlashAttribute("startTimeErr", "Event start time must be later than current time");
      } else {
        event.setStartTime(dateTime);
      }
    } else {
      redirectAttributes.addFlashAttribute("startTimeErr", "Event start time cannot be empyt");
    }

    if (redirectAttributes.containsAttribute("titleErr")
        || redirectAttributes.containsAttribute("bodyErr")
        || redirectAttributes.containsAttribute("startTimeErr")
        ) {
      return "redirect:/admin/events/new";
    }

    eventRepository.save(event);
    return "redirect:/admin/events";
  }

  public void removeEventById(Long id) {
    eventRepository.deleteById(id);
  }

}

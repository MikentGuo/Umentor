package com.Umentor.UmentorprojectforProgrammingIII.service;

import com.Umentor.UmentorprojectforProgrammingIII.entity.Course;
import com.Umentor.UmentorprojectforProgrammingIII.entity.StudentLike;
import com.Umentor.UmentorprojectforProgrammingIII.entity.User;
import com.Umentor.UmentorprojectforProgrammingIII.model.BucketName;
import com.Umentor.UmentorprojectforProgrammingIII.model.CourseWithLikes;
import com.Umentor.UmentorprojectforProgrammingIII.model.JsonFav;
import com.Umentor.UmentorprojectforProgrammingIII.model.UserRole;
import com.Umentor.UmentorprojectforProgrammingIII.repository.CourseRepository;
import com.Umentor.UmentorprojectforProgrammingIII.repository.StudentLikeRepository;
import com.Umentor.UmentorprojectforProgrammingIII.repository.UserRepository;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final StudentLikeRepository studentLikeRepository;

    private final FileService fileService;

    private final UserService userService;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserRepository userRepository, FileService fileService, StudentLikeRepository studentLikeRepository, UserService userService) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.studentLikeRepository = studentLikeRepository;
        this.userService = userService;
    }

    public Course getCourse(Long id) throws UsernameNotFoundException {
        if (courseRepository.findById(id).isEmpty()) {
            throw new IllegalStateException("Course not found");
        }
        return courseRepository.findById(id).get();
    }

    public void saveCourse(Course course) {
        courseRepository.save(course);
    }

    public List<CourseWithLikes> getCoursesWithLikes() {
        List<CourseWithLikes> fullCourseList = new ArrayList<>();
        List<Course> courseList = courseRepository.findAll();
        for (Course course : courseList) {
            int likes = studentLikeRepository.countAllByCourse_Id(course.getId());
            fullCourseList.add(new CourseWithLikes(course, likes));
            System.out.println("The number of likes are " + likes);
        }
        return fullCourseList;
    }

    public List<CourseWithLikes> getCoursesWithLikesByUser(Principal principal) {
        List<CourseWithLikes> fullCourseList = new ArrayList<>();
        List<Course> courseList;
        User user = userService.getUser(principal.getName());
        if (user.getRole().equals(UserRole.STUDENT)) {
            courseList = user.getCourseList();
            for (Course course : courseList) {
                int likes = studentLikeRepository.countAllByCourse_Id(course.getId());
                fullCourseList.add(new CourseWithLikes(course, likes));
            }
            return fullCourseList;
        } else if (user.getRole().equals(UserRole.TEACHER)) {
            courseList = courseRepository.findAllByTeacher(user);
            for (Course course : courseList) {
                int likes = studentLikeRepository.countAllByCourse_Id(course.getId());
                fullCourseList.add(new CourseWithLikes(course, likes));
            }
            return fullCourseList;
        }

        return null;
    }

    public List<Course> getLikedCoursesByStudent(Principal principal) {
        User student = userService.getUser(principal.getName());
        List<Course> list = new ArrayList<>();
        if (!student.getRole().equals(UserRole.STUDENT)) {
            return list;
        }
        List<StudentLike> likeList = studentLikeRepository.findAllByStudent(student).get();
        for (StudentLike like : likeList) {
            list.add(like.getCourse());
        }
        return list;
    }

    public byte[] downloadUserProfileImage(Long courseId) {
        Course course = getCourse(courseId);
        String defaultImg = "course_default.jpg";
        String path = String.format("%s/%s", BucketName.COURSE_IMAGE.getBucketName(), course.getId());

        if (course.getPhotoFilePath() == null) {
            return fileService.download(BucketName.COURSE_IMAGE.getBucketName(), defaultImg);
        }

        String key = course.getPhotoFilePath();
        return fileService.download(path, key);
    }

    @Transactional
    public void uploadCoursePhoto(Long courseId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }
        if (Arrays.asList(ContentType.IMAGE_JPEG, ContentType.IMAGE_PNG, ContentType.IMAGE_GIF, ContentType.IMAGE_BMP).contains(file.getContentType())) {
            throw new IllegalStateException("File must be an image");
        }
        Course course = getCourse(courseId);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        String path = String.format("%s/%s", BucketName.COURSE_IMAGE.getBucketName(), course.getId());
        String name = String.format("%s-%s", file.getName(), UUID.randomUUID());
        if (course.getPhotoFilePath() != null) {
            fileService.deleteImage(path, course.getPhotoFilePath());
        }
        try {
            fileService.saveImage(path, name, Optional.of(metadata), file.getInputStream());
            course.setPhotoFilePath(name);
            courseRepository.save(course);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    @Transactional
    public void toggleFav(JsonFav jsonFav, Principal principal) {
        User student = userService.getUser(principal.getName());
        Course course = getCourse(jsonFav.getCourseId());
        if (jsonFav.getMethod().equals("like")) {
            StudentLike newLike = new StudentLike(student, course);
            studentLikeRepository.save(newLike);
        } else if (jsonFav.getMethod().equals("unlike")) {
            studentLikeRepository.deleteByStudentAndCourse(student, course);
        }
    }

    public String getModifiedCourse(Long courseId, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        User teacher = userService.getUser(principal.getName());
        if (courseRepository.findById(courseId).isEmpty()) {
            redirectAttributes.addFlashAttribute("forbidden", "Course not found");
            return "redirect:/error";
        }
        Course course = courseRepository.findById(courseId).get();
        if (!course.getTeacher().equals(teacher)) {
            redirectAttributes.addFlashAttribute("forbidden", "You are not the teacher of this course: " + course.getTitle());
            return "redirect:/error";
        }
        model.addAttribute("course", course);
        model.addAttribute("studentList", course.getStudentList());
        return "course_modify";
    }

    public List<Course> getAllCourses() {
        List<Course> courseList = courseRepository.findAll();
        return courseList;
    }

    public boolean checkStudentSubscription(Long courseId, User student) {
        Course course = getCourse(courseId);
        if (student.getCourseList().contains(course)) {
            return true;
        }
        return false;
    }
    public String addSubscription(Long courseId, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        User student = userService.getUser(principal.getName());
        Course course = getCourse(courseId);
        if (course.getStudentList().size() >= course.getSeats()) {
            redirectAttributes.addFlashAttribute("forbidden", String.format("Course %s is already fully booked", course.getTitle()));
            return "redirect:/error";
        }
        if (student.getCourseList().contains(course)) {
            redirectAttributes.addFlashAttribute("forbidden", "You have already booked this course");
            return "redirect:/error";
        }
        student.addCourse(course);
        userRepository.save(student);
        redirectAttributes.addFlashAttribute("success", "you have booked this course");
        return String.format("redirect:/course-details/%d", courseId);
    }

    public String modifyCourse(Long courseId, Course tempCourse, RedirectAttributes redirectAttributes) {
        Course course = getCourse(courseId);
        LocalDateTime dateTime = null;
        if (tempCourse.getTitle().isEmpty() || tempCourse.getTitle().length() > 30) {
            redirectAttributes.addFlashAttribute("titleErr", "Title must be between 1 and 30 characters");
        }
        if (tempCourse.getBody().length() < 5 || tempCourse.getBody().length() > 1000) {
            redirectAttributes.addFlashAttribute("bodyErr", "Introduction must be between 5 and 1000 characters");
        }
        if (tempCourse.getSeats() < course.getStudentList().size() || tempCourse.getSeats() > 30) {
            redirectAttributes.addFlashAttribute("seatsErr", String.format("Seats can not be more than 30, or less than current subscription: %d", course.getStudentList().size()));
        }
        if (!tempCourse.getStringStartTime().isEmpty()) {
            dateTime = LocalDateTime.parse(tempCourse.getStringStartTime());
            if (dateTime.isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("startTimeErr", "Course start time must be later than current time");
            } else {
                course.setStartTime(dateTime);
            }
        }

        if (redirectAttributes.containsAttribute("titleErr")
                || redirectAttributes.containsAttribute("bodyErr")
                || redirectAttributes.containsAttribute("seatsErr")
                || redirectAttributes.containsAttribute("startTimeErr")) {
            return String.format("redirect:/teacher/course-modify/%d", course.getId());
        }
        course.setTitle(tempCourse.getTitle());
        course.setBody(tempCourse.getBody());
        course.setSeats(tempCourse.getSeats());
        courseRepository.save(course);
        redirectAttributes.addFlashAttribute("success", "Course details updated");
        return String.format("redirect:/teacher/course-modify/%d", course.getId());
    }

    public String modifyCourseByAdmin(Long courseId, Course tempCourse, RedirectAttributes redirectAttributes) {
        Course course = getCourse(courseId);
        LocalDateTime dateTime = null;
        if (tempCourse.getTitle().isEmpty() || tempCourse.getTitle().length() > 30) {
            redirectAttributes.addFlashAttribute("titleErr", "Title must be between 1 and 30 characters");
        }
        if (tempCourse.getBody().length() < 5 || tempCourse.getBody().length() > 1000) {
            redirectAttributes.addFlashAttribute("bodyErr", "Introduction must be between 5 and 1000 characters");
        }
        if (tempCourse.getSeats() < course.getStudentList().size() || tempCourse.getSeats() > 30) {
            redirectAttributes.addFlashAttribute("seatsErr", String.format("Seats can not be more than 30, or less than current subscription: %d", course.getStudentList().size()));
        }
        if (!tempCourse.getStringStartTime().isEmpty()) {
            dateTime = LocalDateTime.parse(tempCourse.getStringStartTime());
            if (dateTime.isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("startTimeErr", "Course start time must be later than current time");
            } else {
                course.setStartTime(dateTime);
            }
        }
        if (tempCourse.getTeacherId() != null) {
            if (userRepository.findById(tempCourse.getTeacherId()).isEmpty()) {
                redirectAttributes.addFlashAttribute("teacherErr", "Invalid teacher id");
            } else {
                User newTeacher = userService.getUserById(tempCourse.getTeacherId());
                if (newTeacher == null || !newTeacher.getRole().equals(UserRole.TEACHER)) {
                    redirectAttributes.addFlashAttribute("teacherErr", "Invalid teacher id");
                } else {
                    course.setTeacher(newTeacher);
                }
            }
        }
        if (redirectAttributes.containsAttribute("titleErr")
                || redirectAttributes.containsAttribute("bodyErr")
                || redirectAttributes.containsAttribute("seatsErr")
                || redirectAttributes.containsAttribute("startTimeErr")
                || redirectAttributes.containsAttribute("teacherErr")) {
            return String.format("redirect:/admin/courses/edit/%d", course.getId());
        }
        course.setTitle(tempCourse.getTitle());
        course.setBody(tempCourse.getBody());
        course.setSeats(tempCourse.getSeats());
        courseRepository.save(course);
        redirectAttributes.addFlashAttribute("success", "Course details updated");
        return "redirect:/admin/courses";
    }

    public String addNewCourse(Course course, RedirectAttributes redirectAttributes) {
        LocalDateTime dateTime = null;
        if (course.getTitle().isEmpty() || course.getTitle().length() > 30) {
            redirectAttributes.addFlashAttribute("titleErr", "Title must be between 1 and 30 characters");
        }
        if (course.getBody().length() < 5 || course.getBody().length() > 1000) {
            redirectAttributes.addFlashAttribute("bodyErr", "Introduction must be between 5 and 1000 characters");
        }
        if (course.getSeats() < course.getStudentList().size() || course.getSeats() > 30) {
            redirectAttributes.addFlashAttribute("seatsErr", String.format("Seats can not be more than 30, or less than current subscription: %d", course.getStudentList().size()));
        }
        if (!course.getStringStartTime().isEmpty()) {
            dateTime = LocalDateTime.parse(course.getStringStartTime());
            if (dateTime.isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("startTimeErr", "Course start time must be later than current time");
            } else {
                course.setStartTime(dateTime);
            }
        } else {
            redirectAttributes.addFlashAttribute("startTimeErr", "Course start time cannot be empyt");
        }
        if (course.getTeacherId() != null) {
            if (userRepository.findById(course.getTeacherId()).isEmpty()) {
                redirectAttributes.addFlashAttribute("teacherErr", "Invalid teacher id");
            } else {
                User newTeacher = userService.getUserById(course.getTeacherId());
                if (newTeacher == null || !newTeacher.getRole().equals(UserRole.TEACHER)) {
                    redirectAttributes.addFlashAttribute("teacherErr", "Invalid teacher id");
                } else {
                    course.setTeacher(newTeacher);
                }
            }
        } else {
            redirectAttributes.addFlashAttribute("teacherErr", "Invalid teacher id");
        }
        if (redirectAttributes.containsAttribute("titleErr")
                || redirectAttributes.containsAttribute("bodyErr")
                || redirectAttributes.containsAttribute("seatsErr")
                || redirectAttributes.containsAttribute("startTimeErr")
                || redirectAttributes.containsAttribute("teacherErr")) {
            return "redirect:/admin/courses/new";
        }

        courseRepository.save(course);
        return "redirect:/admin/courses";
    }

    public void removeCourseById(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getTeacherCourses(User teacher) {
        return courseRepository.findAllByTeacher(teacher);
    }

}

package com.Umentor.UmentorprojectforProgrammingIII.service;

import com.Umentor.UmentorprojectforProgrammingIII.entity.Course;
import com.Umentor.UmentorprojectforProgrammingIII.model.*;
import com.Umentor.UmentorprojectforProgrammingIII.entity.User;
import com.Umentor.UmentorprojectforProgrammingIII.repository.CourseRepository;
import com.Umentor.UmentorprojectforProgrammingIII.repository.UserRepository;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;



import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Service
public class UserService implements BasicService {

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    private final FileService fileService;

    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, CourseRepository courseRepository, FileService fileService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.fileService = fileService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUser(String email) throws UsernameNotFoundException {
        if (!userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("User not found");
        }
        return userRepository.findByEmail(email).get();
    }

    @Transactional
    public void uploadAvatar(Principal principal, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }
        if (Arrays.asList(ContentType.IMAGE_JPEG, ContentType.IMAGE_PNG, ContentType.IMAGE_GIF, ContentType.IMAGE_BMP).contains(file.getContentType())) {
            throw new IllegalStateException("File must be an image");
        }
        User user = getUser(principal.getName());

        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getId());
        String name = String.format("%s-%s", file.getName(), UUID.randomUUID());
        if (user.getAvatarFilePath() != null) {
            fileService.deleteImage(path, user.getAvatarFilePath());
        }
        try {
            fileService.saveImage(path, name, Optional.of(metadata), file.getInputStream());
            user.setAvatarFilePath(name);
            userRepository.save(user);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Transactional
    public void uploadAvatar(Long id, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }
        if (Arrays.asList(ContentType.IMAGE_JPEG, ContentType.IMAGE_PNG, ContentType.IMAGE_GIF, ContentType.IMAGE_BMP).contains(file.getContentType())) {
            throw new IllegalStateException("File must be an image");
        }
        User user = getUserById(id);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getId());
        String name = String.format("%s-%s", file.getName(), UUID.randomUUID());
        if (user.getAvatarFilePath() != null) {
            fileService.deleteImage(path, user.getAvatarFilePath());
        }
        try {
            fileService.saveImage(path, name, Optional.of(metadata), file.getInputStream());
            user.setAvatarFilePath(name);
            userRepository.save(user);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte[] downloadUserProfileImage(Principal principal) {
        User user = getUser(principal.getName());
        String studentDefaultImg = "student_default.png";
        String teacherDefaultImg = "teacher_default.png";
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getId());

        if (user.getAvatarFilePath() == null) {
            if (user.getRole().equals(UserRole.STUDENT)) {
                return fileService.download(BucketName.PROFILE_IMAGE.getBucketName(), studentDefaultImg);
            } else if (user.getRole().equals(UserRole.TEACHER)) {
                return fileService.download(BucketName.PROFILE_IMAGE.getBucketName(), teacherDefaultImg);
            }
        }

        String key = user.getAvatarFilePath();
        return fileService.download(path, key);
    }

    public byte[] downloadUserImageById(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        User user = userRepository.getById(id);
        String studentDefaultImg = "student_default.png";
        String teacherDefaultImg = "teacher_default.png";
        String adminDefaultImg = "admin_default.png";
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getId());

        if (user.getAvatarFilePath() == null) {
            if (user.getRole().equals(UserRole.STUDENT)) {
                return fileService.download(BucketName.PROFILE_IMAGE.getBucketName(), studentDefaultImg);
            } else if (user.getRole().equals(UserRole.TEACHER)) {
                return fileService.download(BucketName.PROFILE_IMAGE.getBucketName(), teacherDefaultImg);
            } else {
                return fileService.download((BucketName.PROFILE_IMAGE.getBucketName()), adminDefaultImg);
            }
        }

        String key = user.getAvatarFilePath();
        return fileService.download(path, key);
    }

    @Transactional
    public ModelAndView changePW(@Valid PasswordChange change, String email) {
        ModelAndView mav = new ModelAndView();
        User user = getUser(email);
        if (!passwordEncoder.matches(change.getOrigPW(), user.getPassword())) {
            mav.addObject("origPWError", true);
        }
        if (!change.getNewPW().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,100}$")) {
            mav.addObject("newPWError", true);
        }
        if (!change.getNewPW().equals(change.getNewPWRepeat())) {
            mav.addObject("pwRepeatError", true);
        }
        if (!mav.isEmpty()) {
            return mav;
        }

        String newPW = passwordEncoder.encode(change.getNewPW());
        user.setPassword(newPW);
        userRepository.save(user);
        mav.addObject("success", true);
        return mav;
    }

    public List<Course> getStudentCourses(Principal principal) {
        User student = getUser(principal.getName());
        return student.getCourseList();
    }

    public List<Course> addTeacherCourses(Principal principal) {
        User teacher = getUser(principal.getName());
        List<Course> list = courseRepository.findAllByTeacher(teacher);
        return list;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Long id){
        return userRepository.getById(id);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public String changeTeacherProfile(@Valid Teacher teacher, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "profile";
        }
        User user = getUser(principal.getName());
        user.setUsername(teacher.getUserName());
        user.setTitle(teacher.getTitle());
        user.setDescription(teacher.getDescription());
        userRepository.save(user);
        return "profile";
    }

    public String modifyTeacherByAdmin(Long id, @Valid Teacher teacher, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/edit_trainer";
        }
        User user = getUserById(id);
        user.setUsername(teacher.getUserName());
        user.setTitle(teacher.getTitle());
        user.setDescription(teacher.getDescription());
        userRepository.save(user);
        return "redirect:/admin/trainers";
    }

    public List<User> getAllTeachers() {
        return userRepository.findAllByRole(UserRole.TEACHER);
    }


}

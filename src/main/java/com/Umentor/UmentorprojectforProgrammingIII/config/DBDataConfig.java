package com.Umentor.UmentorprojectforProgrammingIII.config;

import com.Umentor.UmentorprojectforProgrammingIII.entity.Course;
import com.Umentor.UmentorprojectforProgrammingIII.entity.User;
import com.Umentor.UmentorprojectforProgrammingIII.model.UserRole;
import com.Umentor.UmentorprojectforProgrammingIII.repository.CourseRepository;
import com.Umentor.UmentorprojectforProgrammingIII.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DBDataConfig {

    private final PasswordEncoder passwordEncoder;

    public DBDataConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner addData(UserRepository userRepository, CourseRepository courseRepository) {

        if (userRepository.findFirstByUsernameIsNotNull().isEmpty()) {
            String password = passwordEncoder.encode("Password123");
            return args -> {
                User jerry123 = new User(
                        UserRole.ADMIN,
                        "Jerry",
                        "jerry@gmail.com",
                        password
                );

                User terry123 = new User(
                        UserRole.STUDENT,
                        "Terry",
                        "terry@gmail.com",
                        password
                );

                User mary123 = new User(
                        UserRole.TEACHER,
                        "Mary",
                        "mary@gmail.com",
                        password
                );

                userRepository.saveAll(
                        List.of(jerry123, terry123, mary123));
            };
        }

        // courses
        if (courseRepository.findFirstByTitleIsNotNull().isEmpty()) {
            User teacher = userRepository.getById(3L);
            return args -> {
                Course course1 = new Course(
                        teacher,
                        "title1",
                        "body",
                        20,
                        LocalDateTime.now().minusDays(7)
                );
                Course course2 = new Course(
                        teacher,
                        "title1",
                        "body",
                        20,
                        LocalDateTime.now().minusDays(7)
                );
                Course course3 = new Course(
                        teacher,
                        "title1",
                        "body",
                        20,
                        LocalDateTime.now().minusDays(7)
                );

                courseRepository.saveAll(
                        List.of(course1, course2, course3)
                );
            };
        }

        return args -> {};
    }
}

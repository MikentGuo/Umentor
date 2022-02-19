package com.Umentor.UmentorprojectforProgrammingIII.repository;

import com.Umentor.UmentorprojectforProgrammingIII.entity.Course;
import com.Umentor.UmentorprojectforProgrammingIII.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByTeacher(User teacher);
    List<Course> findByIsRecommended(boolean isRecommed);

    Optional<Course> findFirstByTitleIsNotNull();

    @Override
    Page<Course> findAll(Pageable pageable);
}

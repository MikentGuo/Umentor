package com.Umentor.UmentorprojectforProgrammingIII.repository;

import com.Umentor.UmentorprojectforProgrammingIII.entity.Course;
import com.Umentor.UmentorprojectforProgrammingIII.entity.StudentLike;
import com.Umentor.UmentorprojectforProgrammingIII.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentLikeRepository extends JpaRepository<StudentLike, Long> {

    int countAllByCourse_Id(Long courseId);

    Optional<List<StudentLike>> findAllByStudent(User student);

    @Modifying
    @Query("DELETE FROM StudentLike s WHERE s.student=?1 AND s.course=?2")
    void deleteByStudentAndCourse(User user, Course course);
}

package com.Umentor.UmentorprojectforProgrammingIII.model;

import com.Umentor.UmentorprojectforProgrammingIII.entity.Course;

import java.util.Objects;

public class CourseWithLikes {

    private Course course;

    private int likes;

    public CourseWithLikes(Course course, int likes) {
        this.course = course;
        this.likes = likes;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseWithLikes that = (CourseWithLikes) o;
        return likes == that.likes && Objects.equals(course, that.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, likes);
    }
}

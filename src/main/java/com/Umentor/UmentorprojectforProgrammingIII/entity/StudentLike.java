package com.Umentor.UmentorprojectforProgrammingIII.entity;

import javax.persistence.*;

@Entity
@Table(name = "student_like")
public class StudentLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "student_id", nullable = false)
  private User student;

  @ManyToOne
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;

  public StudentLike() {
  }

  public StudentLike(Long id, User student, Course course) {
    this.id = id;
    this.student = student;
    this.course = course;
  }

  public StudentLike(User student, Course course) {
    this.student = student;
    this.course = course;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getStudent() {
    return student;
  }

  public void setStudent(User student) {
    this.student = student;
  }

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  @Override
  public String toString() {
    return "StudentLike{" +
            "id=" + id +
            ", student=" + student +
            ", course=" + course +
            '}';
  }
}
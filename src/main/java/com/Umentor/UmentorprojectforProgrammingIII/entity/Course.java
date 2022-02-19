package com.Umentor.UmentorprojectforProgrammingIII.entity;


import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course")
public class Course {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "teacher_id", nullable = false)
  private User teacher;

  @ManyToMany(cascade = {
          CascadeType.PERSIST,
          CascadeType.MERGE
  })
  @JoinTable(name = "student_course",
      joinColumns = @JoinColumn(name = "course_id"),
      inverseJoinColumns = @JoinColumn(name = "student_id"))
  private List<User> studentList = new ArrayList<>();

  private String title;

  private String photoFilePath;

  private String body;

  private boolean isRecommended = false;

  private int seats;

  @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm a")
  private LocalDateTime startTime;

  @Transient
  private String stringStartTime;
  private String classLink;

  @Transient
  private Long teacherId;

  public Course() {
  }

  public Course(Long id, User teacher, String title, String photoFilePath, String body, boolean isRecommended, int seats, LocalDateTime startTime, String classLink) {
    this.id = id;
    this.teacher = teacher;
    this.title = title;
    this.photoFilePath = photoFilePath;
    this.body = body;
    this.isRecommended = isRecommended;
    this.seats = seats;
    this.startTime = startTime;
    this.classLink = classLink;
  }

  public Course(User teacher, String title, String body, int seats, LocalDateTime startTime) {
    this.teacher = teacher;
    this.title = title;
    this.body = body;
    this.seats = seats;
    this.startTime = startTime;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getTeacher() {
    return teacher;
  }

  public void setTeacher(User teacher) {
    this.teacher = teacher;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  public String getPhotoFilePath() {
    return photoFilePath;
  }

  public void setPhotoFilePath(String photoFilePath) {
    this.photoFilePath = photoFilePath;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public boolean isRecommended() {
    return isRecommended;
  }

  public void setRecommended(boolean recommended) {
    isRecommended = recommended;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public String getStringStartTime() {
    return stringStartTime;
  }

  public void setStringStartTime(String stringStartTime) {
    this.stringStartTime = stringStartTime;
  }

  public Long getTeacherId() {
    return teacherId;
  }

  public void setTeacherId(Long teacherId) {
    this.teacherId = teacherId;
  }

  public String getClassLink() {
    return classLink;
  }

  public void setClassLink(String classLink) {
    this.classLink = classLink;
  }

  public int getSeats() {
    return seats;
  }

  public void setSeats(int seats) {
    this.seats = seats;
  }

  public List<User> getStudentList() {
    return studentList;
  }

  public void addStudent(User student) {
    studentList.add(student);
    student.getCourseList().add(this);
  }

  @Override
  public String toString() {
    return "Course{" +
            "id=" + id +
            ", teacher=" + teacher +
            ", studentList=" + studentList +
            ", title='" + title + '\'' +
            ", photoFilePath='" + photoFilePath + '\'' +
            ", body='" + body + '\'' +
            ", isRecommended=" + isRecommended +
            ", seats=" + seats +
            ", startTime=" + startTime +
            ", classLink='" + classLink + '\'' +
            '}';
  }
}

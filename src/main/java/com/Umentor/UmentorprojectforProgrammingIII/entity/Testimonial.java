package com.Umentor.UmentorprojectforProgrammingIII.entity;

import javax.persistence.*;

@Entity
@Table(name = "testimonial")
public class Testimonial {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "student_id", nullable = false)
  private User student;

  private String title;

  private String body;

  public Testimonial() {
  }

  public Testimonial(Long id, User student, String title, String body) {
    this.id = id;
    this.student = student;
    this.title = title;
    this.body = body;
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  @Override
  public String toString() {
    return "Testimonial{" +
            "id=" + id +
            ", student=" + student +
            ", title='" + title + '\'' +
            ", body='" + body + '\'' +
            '}';
  }
}
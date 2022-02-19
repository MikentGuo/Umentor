package com.Umentor.UmentorprojectforProgrammingIII.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event")
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String body;

  private String photoFilePath;

  private LocalDateTime startTime;

  @Transient
  private  String stringStartTime;

  public Event() {
  }

  public Event(Long id, String title, String body, String photoFilePath, LocalDateTime startTime) {
    this.id = id;
    this.title = title;
    this.body = body;
    this.photoFilePath = photoFilePath;
    this.startTime = startTime;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getPhotoFilePath() {
    return photoFilePath;
  }

  public void setPhotoFilePath(String photoFilePath) {
    this.photoFilePath = photoFilePath;
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

  @Override
  public String toString() {
    return "Event{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", body='" + body + '\'' +
        ", photoFilePath='" + photoFilePath + '\'' +
        ", startTime=" + startTime +
        '}';
  }
}
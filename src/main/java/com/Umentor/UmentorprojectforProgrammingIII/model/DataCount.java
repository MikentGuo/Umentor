package com.Umentor.UmentorprojectforProgrammingIII.model;

public class DataCount {
  private int student;
  private int course;
  private int event;
  private int trainer;

  public DataCount() {
  }

  public DataCount(int student, int trainer, int course, int event) {
    this.student = student;
    this.course = course;
    this.event = event;
    this.trainer = trainer;
  }

  public int getStudent() {
    return student;
  }

  public void setStudent(int student) {
    this.student = student;
  }

  public int getCourse() {
    return course;
  }

  public void setCourse(int course) {
    this.course = course;
  }

  public int getEvent() {
    return event;
  }

  public void setEvent(int event) {
    this.event = event;
  }

  public int getTrainer() {
    return trainer;
  }

  public void setTrainer(int trainer) {
    this.trainer = trainer;
  }
}

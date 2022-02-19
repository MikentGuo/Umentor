package com.Umentor.UmentorprojectforProgrammingIII.model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

public class Teacher {

  @Pattern(regexp = "^[a-zA-z \\-.]{4,20}$", message=
          "Name can only contains letters (uppercase and/or lowercase). And must be between 4-20 characters.")
  private String userName;

  @Pattern(regexp = "^[a-zA-z \\-.]{4,20}$", message=
          "Title can only contains letters (uppercase and/or lowercase). And must be between 4-20 characters.")
  private String title;

  @Length(max = 500)
  private String description;

  public Teacher() {
  }

  public Teacher(String userName, String title, String description) {
    this.userName = userName;
    this.title = title;
    this.description = description;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}

package com.Umentor.UmentorprojectforProgrammingIII.model;

public class UserInfo {
  private Long id;
  private String username;
  private String title;
  private String avatarFilePath;
  private String description;
  private String role;
  private String email;

  public UserInfo() {
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAvatarFilePath() {
    return avatarFilePath;
  }

  public void setAvatarFilePath(String avatarFilePath) {
    this.avatarFilePath = avatarFilePath;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  @Override
  public String toString() {
    return "UserInfo{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", title='" + title + '\'' +
        ", avatarFilePath='" + avatarFilePath + '\'' +
        ", description='" + description + '\'' +
        ", role='" + role + '\'' +
        ", email='" + email + '\'' +
        '}';
  }
}

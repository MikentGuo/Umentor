package com.Umentor.UmentorprojectforProgrammingIII.model;

public class TestimonialInfo {
  private Long userid;
  private String username;
  private String title;
  private String body;

  public TestimonialInfo(Long userid, String username, String title, String body) {
    this.userid = userid;
    this.username = username;
    this.title = title;
    this.body = body;
  }

  public TestimonialInfo() {
  }

  public Long getUserid() {
    return userid;
  }

  public void setUserid(Long userid) {
    this.userid = userid;
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

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}

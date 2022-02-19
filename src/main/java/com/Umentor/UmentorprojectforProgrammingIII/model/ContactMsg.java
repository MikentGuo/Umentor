package com.Umentor.UmentorprojectforProgrammingIII.model;

public class ContactMsg {

    private String fullName;

    private String email;

    private String subject;

    private String body;

    public ContactMsg() {
    }

    public ContactMsg(String fullName, String email, String subject, String body) {
        this.fullName = fullName;
        this.email = email;
        this.subject = subject;
        this.body = body;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

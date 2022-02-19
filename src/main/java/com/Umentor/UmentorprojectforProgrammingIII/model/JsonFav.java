package com.Umentor.UmentorprojectforProgrammingIII.model;

public class JsonFav {

    private Long courseId;

    private String method;

    public JsonFav(Long courseId, String method) {
        this.courseId = courseId;
        this.method = method;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}

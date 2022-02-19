package com.Umentor.UmentorprojectforProgrammingIII.model;

public enum BucketName {
    PROFILE_IMAGE("fsd01-umentor/profile"),
    EVENT_IMAGE("fsd01-umentor/event"),
    COURSE_IMAGE("fsd01-umentor/course");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}

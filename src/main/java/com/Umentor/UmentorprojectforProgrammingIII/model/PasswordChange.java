package com.Umentor.UmentorprojectforProgrammingIII.model;

public class PasswordChange {

    private String origPW;

    private String newPW;

    private String newPWRepeat;

    public PasswordChange() {
    }

    public PasswordChange(String origPW, String newPW, String newPWRepeat) {
        this.origPW = origPW;
        this.newPW = newPW;
        this.newPWRepeat = newPWRepeat;
    }

    public String getOrigPW() {
        return origPW;
    }

    public void setOrigPW(String origPW) {
        this.origPW = origPW;
    }

    public String getNewPW() {
        return newPW;
    }

    public void setNewPW(String newPW) {
        this.newPW = newPW;
    }

    public String getNewPWRepeat() {
        return newPWRepeat;
    }

    public void setNewPWRepeat(String newPWRepeat) {
        this.newPWRepeat = newPWRepeat;
    }

    @Override
    public String toString() {
        return "PasswordChange{" +
                "origPW='" + origPW + '\'' +
                ", newPW='" + newPW + '\'' +
                ", newPWRepeat='" + newPWRepeat + '\'' +
                '}';
    }
}

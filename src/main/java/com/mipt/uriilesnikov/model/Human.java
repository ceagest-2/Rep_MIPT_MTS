package com.mipt.uriilesnikov.model;

public class Human {
    private String firstname;
    private String lastname;
    private int age;
    private boolean userWorking;

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getAge() {
        return age;
    }

    public boolean isUserWorking() {
        return userWorking;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setUserWorking(boolean userWorking) {
        this.userWorking = userWorking;
    }
}

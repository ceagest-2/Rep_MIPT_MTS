package com.mipt.uriilesnikov;

public abstract class ForEmployees {
    public abstract void work(int number);

    public boolean goHome(String currentTime, String timeToGoHome) {
        return currentTime.equals(timeToGoHome);
    }
}

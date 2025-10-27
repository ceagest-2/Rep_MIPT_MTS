package com.mipt.uriilesnikov;

public class MainClass {
    private int firstField;
    private String secondField;
    protected static double thirdField;
    public final long fourthField = 10;

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.println("Iter: " + i);
        }
    }
}

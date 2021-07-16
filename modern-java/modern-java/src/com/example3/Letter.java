package com.example3;

public class Letter {
    public static String addHeader(String text){
        return "header : " + text;
    }

    public static String addFooter(String text) {
        return text + " Kind regars";
    }

    public static String checkSpelling(String text) {
        return text.replaceAll("labda", "lambda");
    }
}

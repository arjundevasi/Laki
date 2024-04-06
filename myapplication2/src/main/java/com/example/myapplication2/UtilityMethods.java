package com.example.myapplication2;

public class UtilityMethods {

    public static String capitalizeFullName(String fullName){

        String[] words = fullName.split(" "); // Split the input string into words

        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                // Capitalize the first letter of the word and append the rest of the letters in lowercase
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }

        return result.toString().trim();
    }

    public static String capitalizeCategoryName(String category){
        StringBuilder result = new StringBuilder();

        if (category.length() > 0){
            result.append(Character.toUpperCase(category.charAt(0))).append(category.substring(1).toLowerCase());        }
        return result.toString().trim();
    }

    public static String findTimeElapsedFromTimestamp(String timestamp){
        long currentTimestamp = System.currentTimeMillis();
        long commentTimestamp = Long.parseLong(timestamp);

        long timePassed = currentTimestamp - commentTimestamp;

        long days = timePassed / (1000 * 60 * 60 * 24);

        long hours = (timePassed / (1000 * 60 * 60)) % 24;

        long minutes = timePassed / (1000 * 60);

        String timeAgo;

        if (days > 0) {
            timeAgo = days + "d";
        }else if (hours > 0){
            timeAgo = hours + "h";
        }else if (minutes > 0){
            timeAgo = minutes + "m";
        } else {
            timeAgo = "1m";
        }

        return timeAgo;
    }
}

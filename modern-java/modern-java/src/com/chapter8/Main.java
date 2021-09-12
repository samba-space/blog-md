package com.chapter8;

import com.common.Dish;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    static class Abc{
        public int abc;

        @Override
        public String toString() {
            return "Abc{" +
                    "abc=" + abc +
                    '}';
        }
    }
    public static void main(String[] args) throws IOException {

        List<Dish> menu = Arrays.asList(
                new Dish("pork", false, 600, Dish.Type.MEAT),
                new Dish("beef", false, 600, Dish.Type.MEAT),
                new Dish("chicken", false, 600, Dish.Type.MEAT),
                new Dish("french fries", true, 600, Dish.Type.OTHER),
                new Dish("rice", true, 600, Dish.Type.OTHER),
                new Dish("season fruit", true, 600, Dish.Type.OTHER),
                new Dish("pizza", true, 600, Dish.Type.OTHER),
                new Dish("prawns", false, 220, Dish.Type.FISH),
                new Dish("salmon", false, 111, Dish.Type.FISH)
        );

        Map<String, Integer> ageOfFriends = Map.of("Park", 30, "Kim", 28, "Song", 31);
        ageOfFriends.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(System.out::println);

        Integer ageOfPark = ageOfFriends.getOrDefault("Park", 1);
        System.out.println("ageOfPark = " + ageOfPark);
    }
}

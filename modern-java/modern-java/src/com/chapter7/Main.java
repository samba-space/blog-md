package com.chapter7;

import com.chapter6.CaloricLevel;
import com.common.Dish;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.*;

public class Main {
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

        menu.stream()
                .parallel()
    }
}

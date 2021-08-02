package com.chapter5;

import com.common.Dish;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class Main {
    public static void main(String[] args) throws IOException {

        List<Dish> menu = Arrays.asList();
//        List<Dish> menu = Arrays.asList(
//                new Dish("pork", false, 400, Dish.Type.MEAT),
//                new Dish("beef", false, 390, Dish.Type.MEAT),
//                new Dish("chicken", false, 389, Dish.Type.MEAT),
//                new Dish("french fries", true, 360, Dish.Type.OTHER),
//                new Dish("rice", true, 300, Dish.Type.OTHER),
//                new Dish("season fruit", true, 222, Dish.Type.OTHER),
//                new Dish("pizza", true, 221, Dish.Type.OTHER),
//                new Dish("prawns", false, 220, Dish.Type.FISH),
//                new Dish("salmon", false, 111, Dish.Type.FISH)
//        );


        OptionalInt maxCalories = menu.stream().mapToInt(Dish::getCalories).max();
        int max = maxCalories.orElse(1);
        System.out.println("max = " + max);
    }
}

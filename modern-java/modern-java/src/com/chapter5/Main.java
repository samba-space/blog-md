package com.chapter5;

import com.common.Dish;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class Main {
    public static void main(String[] args) throws IOException {

        List<Dish> menu = Arrays.asList(
                new Dish("pork", false, 400, Dish.Type.MEAT),
                new Dish("beef", false, 390, Dish.Type.MEAT),
                new Dish("chicken", false, 389, Dish.Type.MEAT),
                new Dish("french fries", true, 360, Dish.Type.OTHER),
                new Dish("rice", true, 300, Dish.Type.OTHER),
                new Dish("season fruit", true, 222, Dish.Type.OTHER),
                new Dish("pizza", true, 221, Dish.Type.OTHER),
                new Dish("prawns", false, 220, Dish.Type.FISH),
                new Dish("salmon", false, 111, Dish.Type.FISH)
        );
//
        List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
        numbers.stream()
                .filter(i -> i % 2 == 0)
                .distinct()
                .forEach(System.out::println);


        List<Dish> vegetarianMenu = menu.stream()
                                        .filter(Dish::isVegetarian)
                                        .collect(toList());

        List<Dish> slicedMenu1 = menu.stream()
                .takeWhile(dish -> dish.getCalories() < 300)
                .collect(toList());

        List<Dish> dishes2 = menu.stream()
                .filter(dish -> dish.getCalories() > 300)
                .limit(3)
                .collect(toList());
        String str = "Hello";
//

        List<String> dishNames = menu.stream()
                .map(Dish::getName)
                .collect(toList());

        List<String> words = Arrays.asList("modern", "java", "action");
        List<Integer> wordLengths = words.stream()
                .map(String::length)
                .collect(toList());

        List<Integer> dishNameLengths = menu.stream()
                .map(Dish::getName)
                .map(String::length)
                .collect(toList());


        List<Stream<String>> collect = words.stream()
                .map(word -> word.split(""))
                .map(Arrays::stream)
                .distinct()
                .collect(toList());
//

        List<Integer> numbers1 = Arrays.asList(1, 2, 3);
        List<Integer> numbers2 = Arrays.asList(3, 4);


        numbers1.stream()
                .map(number1 -> numbers2.stream()
                        .map(number2 -> new int[]{number1, number2})
                )
                .distinct()
                .collect(toList());

        numbers1.stream()
                .flatMap(number1 -> numbers2.stream()
                        .map(number2 -> new int[]{number1, number2})
                )
                .distinct()
                .collect(toList());


        Optional<Dish> dish = menu.stream()
                .filter(Dish::isVegetarian).findAny();


        List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5, 6, 9);

        Optional<Integer> num1 = someNumbers.parallelStream()
                .filter(n -> n % 3 == 0)
                .findFirst();

        Optional<Integer> num2 = someNumbers.parallelStream()
                .filter(n -> n % 3 == 0)
                .findAny();

        System.out.println("num1 = " + num1);
        System.out.println("num2 = " + num2);



        if(menu.stream().anyMatch(Dish::isVegetarian)){

        }





        Integer[] arr = new Integer[]{};
        Stream<Integer> stream = Arrays.stream(arr);
        Stream<Integer> arr1 = Stream.of(arr);


    }
}

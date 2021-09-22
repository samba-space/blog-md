package com.chapter6;

import com.common.Dish;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

        Map<Dish.Type, List<Dish>> collect = menu.stream()
                .collect(groupingBy(Dish::getType, filtering(dish -> dish.getCalories() > 500, toList())));
        Map<Dish.Type, List<String>> collect1 = menu.stream()
                .collect(groupingBy(Dish::getType, mapping(dish -> dish.getName(), toList())));

        Map<String, List<String>> dishTags = new HashMap<>();
        dishTags.put("pork", Arrays.asList("greasy", "salty"));

//        Map<Dish.Type, Set<String>> collect2 = menu.stream()
//                .collect(groupingBy(Dish::getType,
//                        flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())
//                        ));
//
//        Map<Dish.Type, Optional<Dish>> collect3 = menu.stream().collect(
//                groupingBy(Dish::getType,
//                        maxBy(Comparator.comparingInt(Dish::getCalories)))
//        );
//        Collection<Dish> collect4 = menu.stream().collect(toCollection(ArrayList::new));
//
//        Optional<Dish> mostCalorieDish = menu.stream()
//                        .collect(maxBy(Comparator.comparingInt(Dish::getCalories)));
//
//        int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
//
//        double avgCalories = menu.stream().collect(averagingInt(Dish::getCalories));



        Map<Dish.Type, List<Dish>> caloricDishedByType = menu.stream()
                .collect(groupingBy(Dish::getType, filtering(dish -> dish.getCalories() > 500, toList())));

        System.out.println("caloricDishedByType = " + caloricDishedByType);

        Map<Dish.Type, Dish> mostCaloricByType = menu.stream()
                .collect(groupingBy(Dish::getType,
                        collectingAndThen(
                                maxBy(Comparator.comparingInt(Dish::getCalories)),
                                Optional::get
                        )));
        Map<Dish.Type, Integer> totalCaloriesByType = menu.stream()
                .collect(groupingBy(Dish::getType,
                        summingInt(Dish::getCalories)));

        Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType = menu.stream()
                .collect(groupingBy(Dish::getType, mapping(dish -> {
                    if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                    else return CaloricLevel.FAT;
                }, toSet())));

        Map<Boolean, List<Dish>> partitionedMenu = menu.stream().collect(partitioningBy(Dish::isVegetarian));

        Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType = menu.stream()
                .collect(
                        partitioningBy(Dish::isVegetarian,
                                groupingBy(Dish::getType)
                        ));

    }

    public class Person {
        private Optional<Car> car;
        public Optional<Car> getCar() {
            return car;
        }
    }

    public class Car {
        private Optional<Insurance> insurance;
        public Optional<Insurance> getInsurance() {
            return insurance;
        }
    }

    public class Insurance {
        private String name;
        public String getName() {
            return name;
        }
    }



}

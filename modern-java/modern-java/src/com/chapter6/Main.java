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

        Map<Dish.Type, List<Dish>> collect = menu.stream()
                .collect(groupingBy(Dish::getType, filtering(dish -> dish.getCalories() > 500, toList())));
        Map<Dish.Type, List<String>> collect1 = menu.stream()
                .collect(groupingBy(Dish::getType, mapping(dish -> dish.getName(), toList())));

        Map<String, List<String>> dishTags = new HashMap<>();
        dishTags.put("pork", Arrays.asList("greasy", "salty"));

        Map<Dish.Type, Set<String>> collect2 = menu.stream()
                .collect(groupingBy(Dish::getType,
                        flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())
                        ));

        Map<Dish.Type, Optional<Dish>> collect3 = menu.stream().collect(
                groupingBy(Dish::getType,
                        maxBy(Comparator.comparingInt(Dish::getCalories)))
        );
        Collection<Dish> collect4 = menu.stream().collect(toCollection(ArrayList::new));
    }
}

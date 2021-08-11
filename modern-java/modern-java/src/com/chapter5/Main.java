package com.chapter5;

import com.common.Dish;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
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
        IntStream range1 = IntStream.range(1, 100);
        IntStream.rangeClosed(1, 100).mapToObj(b -> new int[]{1, 2});

        Stream<int[]> stream1 = IntStream.rangeClosed(1, 100)
                .filter(num -> num % 2 == 0)//IntStream
                .boxed()//Stream<Integer>
                .map(num -> new int[]{num, num * num});

        Stream<int[]> stream2 = IntStream.rangeClosed(1, 100)
                .filter(num -> num % 2 == 0)//IntStream
                .mapToObj(num -> new int[]{num, num * num});
        Stream<String> stream = Stream.of("Modern", "Java", "In", "Action");



        Stream.of("java.specification.name", "java.vm.version", "user")
                .flatMap(key -> Stream.ofNullable(System.getProperty(key)))
                .forEach(System.out::println);

        Stream.of("java.specification.name", "java.vm.version", "user")
                .flatMap(key -> System.getProperty(key) == null ? Stream.empty() : Stream.of(System.getProperty(key)))
                .forEach(System.out::println);

        int[] ints = {1, 2, 3, 4, 5};

        Stream<int[]> ofStream = Stream.of(ints);
        //static<T> Stream<T> of(T t)

        IntStream arraysStream = Arrays.stream(ints);
        //static IntStream stream(int[] array)


        Integer[] integers = {1, 2, 3, 4, 5};

        Stream<Integer> ofStream2 = Stream.of(integers);
        //static<T> Stream<T> of(T... values)
        //public static<T> Stream<T> of(T... values) {
        //        return Arrays.stream(values);
        //}

        Stream<Integer> arraysStream2 = Arrays.stream(integers);
        //static <T> Stream<T> stream(T[] array)

        long uniqueWords = 0;
        try (Stream<String> lines = Files.lines(Paths.get("data.txt"), Charset.defaultCharset())) {
            uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" ")))
                               .distinct()
                               .count();
        } catch (IOException e) {
        }

        IntStream.iterate(0, n -> n < 100, n -> n + 4)
                .forEach(System.out::println);

        IntStream.iterate(0, n -> n + 4)
                .filter(n -> n < 100)
                .forEach(System.out::println);

        IntStream.iterate(0, n -> n + 4)
                .takeWhile(n -> n < 100)
                .forEach(System.out::println);

        Stream.generate(Math::random)
                .limit(5)
                .forEach(System.out::println);

        Long collect = menu.stream().collect(counting());
        long count = menu.stream().count();

        Optional<Dish> mostCalorieDish = menu.stream().collect(maxBy(Comparator.comparingInt(Dish::getCalories)));

        long totalCalories = menu.stream().collect(summingInt(Dish::getCalories));

        menu.stream().map(Dish::getName).collect(Collectors.joining());
        
    }
}

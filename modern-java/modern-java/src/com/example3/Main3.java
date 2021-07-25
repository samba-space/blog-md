package com.example3;

import com.example.Main;
import com.sun.corba.se.impl.orbutil.ObjectUtility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;

public class Main3 {
    private static class Apple{
        private Color color;
        private int weight;

        public Apple(int weight) {
            this.weight = weight;
        }

        public Apple(Color color, int weight) {
            this.color = color;
            this.weight = weight;
        }

        public Integer getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
    }
    private enum Color{
        RED, GREEN
    }
    @FunctionalInterface
    public interface BufferedReaderProcessor{
        String process(BufferedReader b) throws IOException;
    }
    public static void main(String[] args) throws IOException {
//        String oneLine = processFile((BufferedReader br) -> br.readLine());
//        String twoLines = processFile((BufferedReader br) -> br.readLine() + br.readLine());
//
//        IntPredicate evenNumbers = (int i) -> i % 2 == 0;
//
//        List<String> list = new ArrayList<>();
//        Predicate<String> p = s -> list.add(s);
//        Consumer<String> c = s -> list.add(s);
//        Function<String, Boolean> filter = (String s) -> s.isEmpty();
//        Consumer<String> cs = (String s) -> s.isEmpty();
//
//        int portNumber = 1;
//        Runnable runnable = () -> System.out.println("portNumber = " + portNumber);
//
//        ToIntFunction<String> function = Integer::parseInt;
//        BiPredicate<List, Object> contains = List::contains;
//
//        list.sort((o1, o2) -> 0);


        Function<String, String> addHeader = Letter::addHeader;


        System.out.println(addHeader.andThen(Letter::checkSpelling)
                .andThen(Letter::addFooter).apply("labda"));


        ToIntBiFunction<Apple, Apple> appleComparator =




                (Apple a1, Apple a2)  ->  a1.getWeight().compareTo(a2.getWeight());


    }
    public static String processFile(BufferedReaderProcessor p) throws IOException{
        try(BufferedReader br = new BufferedReader(new FileReader("data.txt"))){
            return p.process(br);
        }
    }


    public static String processFile() throws IOException{
        try(BufferedReader br = new BufferedReader(new FileReader("data.txt"))){
            return br.readLine();
        }
    }
}

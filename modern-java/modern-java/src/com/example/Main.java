package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;

public class Main {
    private static int number;
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

        public int getWeight() {
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

    public static void main(String[] args) {
	// write your code here
        System.out.println("Main.main");
        List<Apple> inventory = new ArrayList<>();
        inventory.add(new Apple(Color.RED, 1));
        inventory.add(new Apple(Color.GREEN, 2));
        inventory.add(new Apple(Color.RED, 3));
        inventory.add(new Apple(Color.RED, 4));

        List<Apple> greenApples = filterApples(inventory, apple -> Color.GREEN.equals(apple.getColor()));
        Predicate<Apple> keyEventDispatcher = apple -> Color.GREEN.equals(apple.getColor());
        List<Apple> redApples = filterApples(inventory, keyEventDispatcher.negate());


    }

    public static List<Apple> filterGreenApples(List<Apple> inventory){
        List<Apple> result = new ArrayList<>();
        Predicate<Integer> p;

        for (Apple apple : inventory) {
            if(Color.GREEN.equals(apple.getColor())){
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color){
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if(apple.getColor().equals(color)){
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterApples(List<Apple> inventory, Color color, int weight, boolean flag){
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if((flag && apple.getColor().equals(color)) || (!flag && apple.getWeight() > weight)){
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p){
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if(p.test(apple)){
                result.add(apple);
            }
        }
        return result;
    }
}

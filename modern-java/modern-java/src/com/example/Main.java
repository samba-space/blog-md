package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Main {
    private class Apple{
        private Color color;
        private int weight;

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
        List<Apple> greenApples = filterApples(inventory, Color.GREEN, 0, true);
        List<Apple> heavyApples = filterApples(inventory, Color.RED, 150, false);
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
}

# 스트림 활용
스트림 API가 지원하는 다양한 연산을 알아보자.

## 필터링
필터링은 Predicate 필터링과 고유 요소만 필터링하는 법에 대해 배운다.

### Predicate로 필터링
filter 메서드는 Predicate를 인수로 받아 Predicate와 일치하는 모든 요소를 포함하는 스트림을 반환한다.

``` java
List<Dish> vegetarianMenu = menu.stream()
                                .filter(Dish::isVegetarian)// 메서드 참조로 (d) -> d.isVegetarian과 같음
                                .collect(toList());
```

### 고유 요소만 필터링(distinct)
스트림은 고유 요소로 이루어진 스트림을 반환하는 **distinct** 메서드를 지원한다.

아래 코드는 중복이 제거된 짝수인 2, 4가 출력된다.
``` java
List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
numbers.stream()
        .filter(i -> i % 2 == 0)
        .distinct()
        .forEach(System.out::println);
```

## 슬라이싱
### Predicate로 슬라이싱
자바 9에서는 스트림의 요소를 효과적으로 선택할 수 있도록 **takeWhile**, **dropWhile** 메서드를 제공한다.
takeWhile, dropWhile 모드 무한 스트림에서도 동작한다.

#### takeWhile 메서드
takeWhile 메서드를 이용하여 Predicate와 일치하는 요소까지만 슬라이스할 수 있다.  
Predicate가 거짓이 되면 반복 작업을 중단하여 모든 요소를 반복하는 filter 메서드보다  
매우 큰 스트림을 처리할 경우 효과적인다.  

아래 코드는 menu의 칼로리가 오름차순으로 정렬되어 있으면, 300칼로리 미만의 요리만 반환한다.

``` java
List<Dish> slicedMenu = menu.stream()
                             .takeWhile(dish -> dish.getCalories() < 300)
                             .collect(toList());
```

#### dropWhile 메서드
dropWhile 메서드는 takeWhile과 반대로 Predicate와 일치하는 요소들 drop하고 나머지 요소들을 슬라이스한다.

아래 코드는 300칼로리 미만의 요리는 버리고 나머지 요리를 반환한다.

``` java
List<Dish> slicedMenu = menu.stream()
                             .drop(dish -> dish.getCalories() < 300)
                             .collect(toList());
```

### 스트림 축소
**limit(n)** 메서드는 처음 n개 요소의 스트림을 반환한다.
스트림이 내림차 정렬 상태라면 최대 n개를 반환할 수 있다.  

아래 코드는 300칼로리보다 큰 요리 중 처음 3개 요리를 반환한다.

``` java
List<Dish> dishes = menu.stream()
                         .filter(dish -> dish.getCalories() > 300)
                         .limit(3)
                         .collect(toList());
```

### 요소 건너뛰기
**skip(n)** 메서드는 처음 n개 요소를 제외한 스트림을 반환한다.  
스트림이 n개이하면 빈 스트림을 반환한다.  

아래 코드는 300칼로리보다 큰 요리 중 처음 2개를 제외한 나머지 요리를 반환한다.

``` java
List<Dish> dishes = menu.stream()
                         .filter(dish -> dish.getCalories() > 300)
                         .skip(2)
                         .collect(toList());
```
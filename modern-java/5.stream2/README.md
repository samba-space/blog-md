# 스트림 활용
스트림 API가 지원하는 다양한 연산을 알아보자.

## 필터링
필터링은 Predicate 필터링과 고유 요소만 필터링하는 법에 대해 배운다.

### filter
filter 메서드는 Predicate를 인수로 받아 Predicate와 일치하는 모든 요소를 포함하는 스트림을 반환한다.

``` java
List<Dish> vegetarianMenu = menu.stream()
                                .filter(Dish::isVegetarian)// 메서드 참조로 (d) -> d.isVegetarian과 같음
                                .collect(toList());
```

### distinct
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
자바 9에서는 스트림의 요소를 효과적으로 선택할 수 있도록 **takeWhile**, **dropWhile** 메서드를 제공한다.
takeWhile, dropWhile 모드 무한 스트림에서도 동작한다.

### takeWhile
takeWhile 메서드를 이용하여 Predicate와 일치하는 요소까지만 슬라이스할 수 있다.  
Predicate가 거짓이 되면 반복 작업을 중단하여 모든 요소를 반복하는 filter 메서드보다  
매우 큰 스트림을 처리할 경우 효과적인다.  

아래 코드는 menu의 칼로리가 오름차순으로 정렬되어 있으면, 300칼로리 미만의 요리만 반환한다.

``` java
List<Dish> slicedMenu = menu.stream()
                             .takeWhile(dish -> dish.getCalories() < 300)
                             .collect(toList());
```

### dropWhile
dropWhile 메서드는 takeWhile과 반대로 Predicate와 일치하는 요소들 drop하고 나머지 요소들을 슬라이스한다.

아래 코드는 300칼로리 미만의 요리는 버리고 나머지 요리를 반환한다.

``` java
List<Dish> slicedMenu = menu.stream()
                             .drop(dish -> dish.getCalories() < 300)
                             .collect(toList());
```

### limit
**limit(n)** 메서드는 처음 n개 요소의 스트림을 반환한다.
스트림이 내림차 정렬 상태라면 최대 n개를 반환할 수 있다.  

아래 코드는 300칼로리보다 큰 요리 중 처음 3개 요리를 반환한다.

``` java
List<Dish> dishes = menu.stream()
                         .filter(dish -> dish.getCalories() > 300)
                         .limit(3)
                         .collect(toList());
```

### skip
**skip(n)** 메서드는 처음 n개 요소를 제외한 스트림을 반환한다.  
스트림이 n개이하면 빈 스트림을 반환한다.  

아래 코드는 300칼로리보다 큰 요리 중 처음 2개를 제외한 나머지 요리를 반환한다.

``` java
List<Dish> dishes = menu.stream()
                         .filter(dish -> dish.getCalories() > 300)
                         .skip(2)
                         .collect(toList());
```

## 맵핑
특정 객체에서 특정 데이터를 선택하는 작업은 자주 수행되는 연산이다.  
**map**, **flatMap** 메서드는 특정 데이터를 선택하는 기능을 제공한다.

### map
**map** 메서드는 전달받은 함수를 스트림의 각 element에 적용한다. 함수를 적용한 결과가 새로운 element로 매핑된다. (mapping은 modify보단 transforming에 가깝다)  

여러가지 예를 살펴보자.

``` java
List<String> dishNames = menu.stream()
                             .map(Dish::getName)
                             .collect(toList());
```

요리객체를 요리명으로 매핑하는 예이다.

``` java
List<String> words = Arrays.asList("modern", "java", "action");
List<Integer> wordLengths = words.stream()
                                 .map(String::length)
                                 .collect(toList());
```

문자를 문자의 길이로 매핑하고 있다.

``` java
List<Integer> dishNameLengths = menu.stream()
                                    .map(Dish::getName)
                                    .map(String::length)
                                    .collect(toList());
```

요리객체를 요리명으로 매핑하고, 매핑한 요리명을 요리명 길이로 매핑하고 있다.

### flatMap
스트림의 각 element에 매핑함수를 적용하여 생성된 스트림의 element를 새로운 스트림에 병합하여 반환한다.(평면화한다고 표현)  

예를 들어 단어 목록이 있고, 단어 목록의 고유 문자로 이루어진 목록을 만들어 보자.

```java
List<String> words = Arrays.asList("modern", "java", "action");
        
words.stream()//Stream<String>
        .map(word -> word.split(""))//Stream<String[]>
        .map(Arrays::stream)//Stream<Stream<String>>
        .distinct()
        .collect(toList());//List<Stream<String>>
```

map 메서드를 이용한 경우, ```map(Arrays::stream)```에서 스트림의 element가 ```Stream<String>```되어,  
distinct 메서드가 제대로 동작하지 않게 된다.  

flatMap을 이용하여 문제를 해결해보자.

``` java
words.stream()
        .map(word -> word.split(""))
        .flatMap(Arrays::stream)//Stream<String>
        .distinct()
        .collect(toList());//List<String>
```

flatMap을 이용하면, 스트림의 element가 ```Stream<String>```이 아닌 ```String```이 되는 것을 확인할 수 있다.

## 검색과 매칭
특정 속성이 데이터 목록에 있는지 검색하는 데이터 처리도 자주 사용된다.

### anyMatch
**anyMatch** 메서드는 주어진 Predicate와 스트림의 element가 적어도 1개의 element와 일치하는지 확인할 때 사용한다.  
**anyMatch**는 최종 연산 메서드로 boolean을 반환한다.

아래 코드는 메뉴에 채식요리가 있는지 확인한다.
``` java
if(menu.stream().anyMatch(Dish::isVegetarian)){
...            
}
```

### allMatch
**allMatch** 메서드는 주어진 Predicate와 스트림의 모든 element가 일치하는지 확인한다.

아래 코드는 모든 메뉴가 채식인지 확인한다.
``` java
if(menu.stream().allMatch(Dish::isVegetarian)){
...            
}
```

### noneMatch
**noneMatch** 메서드는 주어진 Predicate와 스트림의 모든 element가 일치하지 않는지 확인한다.  
allMatch 메서드와 반대의 동작을 한다.

아래 코드는 모든 메뉴가 채식이 아닌지 확인한다.

``` java
if(menu.stream().noneMatch(Dish::isVegetarian)){
...            
}
```

### findAny
**findAny** 메서드는 스트림에서 임의의 element를 반환한다.

아래 코드는 filter와 findAny를 이용해서 채식요리를 가져온다.
``` java
Optional<Dish> dish = menu.stream()
                          .filter(Dish::isVegetarian)
                          .findAny();
```

### findFirst
**findFirst** 메서드는 스트림에서 첫번째 element를 반환한다.

숫자 목록 중 첫번째 3의 배수를 찾는 방법을 알아보자.

``` java
List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5, 6, 9);
Optional<Integer> first = someNumbers.stream()
                                        .filter(n -> n % 3 == 0)
                                        .findFirst();//3
```

### findAny vs findFirst
**직렬 스트림**(sequential stream)에서 findAny, findFirst 모두 대부분의 경우 스트림의 첫 번째 요소를 반환한다.(findAny는 이 동작을 보장하지 않는다.) 

**병렬 스트림**에서 **findAny**는 가장 먼저 탐색된 element를 반환한다. 이는 병렬 작업에서 최대 성능을 허용하기 위한 것이다.  
만약 순서가 상관이 없다면 병렬 스트림에서는 제약이 적은 findAny를 사용하자.  
**findFirst**는 병렬 처리를 해도 순서에 우선순위를 두어 첫번째 element를 반환한다.(직렬 스트림과 값이 같다)

아래 코드는 병렬 스트림에서 findFirst와 findAny의 차이를 확인할 수 있다.
findAny는 시행 시 마다 값이 3, 6, 9로 바뀌고 findFirst는 3으로 고정된다.

``` java
List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5, 6, 9);

Optional<Integer> num1 = someNumbers.parallelStream()
                                    .filter(n -> n % 3 == 0)
                                    .findFirst();

Optional<Integer> num2 = someNumbers.parallelStream()
                                    .filter(n -> n % 3 == 0)
                                    .findAny();

System.out.println("num1 = " + num1);
System.out.println("num2 = " + num2);

//출력
//num1 = Optional[3]
//num2 = Optional[6]
```

> 쇼트서킷

### reduce
**reduce** 메서드는 결과가 나올 때까지 스트림의 모든 element를 반복적으로 처리한다.

#### 장점, 병렬화
reduce를 이용하면 내부 반복이 추상화되면서 내부 구현에서 병렬로 reduce를 실행할 수 있게 된다.  
기존의 반복적으로 합계를 구하는 방법(mutable accumulator pattern)은 변수(sum)를 공유해야 하므로 병렬화하기 쉽지 않다.(병렬화와 거리가 먼 기법)

합계를 구하는 코드를 병렬화하면 아래와 같다.

``` java
int sum = numbers.parallelStream().reduce(0, Integer::sum);
```

위의 코드를 병렬로 실행하려면 reduce에 넘겨준 람다의 상태가 바뀌지 말아야 하며, 연산 순서와 관계 없이 결과가 바뀌지 않는 구조여야 한다.

#### 1. sum
숫자 리스트의 합계를 구할 때, reduce를 살펴보자.  

``` java
int sum1 = numbers.stream()
                 .reduce(0, (a, b) -> a + b);

//메서드 참조를 이용 (두 숫자를 더하는 정적 메서드 sum)
int sum2 = numbers.stream()
                 .reduce(0, Integer::sum);
```

스트림이 하나의 값으로 줄어들 때까지 람다는 각 element를 반복해서 조합한다.

```java 
T reduce(T identity, BinaryOperator<T> accumulator); 
```

reduce의 각 파라미터는 다음과 같다.
- T : 초기 값  
- BinaryOperator\<T\> : 2개의 element의 조합해서 새로운 값을 만든다.

초기 값 파라미터가 없는 reduce 메서드는 아래와 같다.

``` java
Optional<T> reduce(BinaryOperator<T> accumulator);
```

이 경우 Optional을 반환하는 이유는 스트림에 element가 없을 경우, 초기 값이 없어  
합계를 반환할 수 없기 때문이다.

#### max, min
``` java
Optional<Integer> max = numbers.stream().reduce(Integer::max);
Optional<Integer> min = numbers.stream().reduce(Integer::min);
```

max의 경우 람다 표현식 ```(x, y) -> x > y ? x : y``` 로 사용할 수 있지만,  
위와 같이 메서드 참조를 사용하면 가독성이 더 좋다.

#### map-reduce 패턴
map과 reduce를 연결하는 기법으로 쉽게 병렬화하는 특징이 있다.(구글이 웹검색에 적용하면서 
유명해졌다.)

다음은 스트림의 요리 개수를 구하는 map-reduce 패턴이다.

``` java
int count = menu.stream()
                 .map(d -> 1)
                 .reduce(0, Integer::sum);
```

더 쉽게는 stream의 count 메서드를 이용하면 된다.

``` java
long count = menu.stream().count();
```

### 스트림 연산 정리
#### stateless
map, filter 등은 입력 스트림에서 각 element를 받아 0 또는 결과를 출력 스트림으로 보낸다.  
이들은 내부 상태를 갖지 않는 stateless operation이다.

#### stateful
reduce, max 같은 연산은 결과를 누적할 내부 상태가 필요하다. 스트림에서 처리하는 element수와 관계없이  
내부 상태의 크기는 한정(bounded)되어 있다. 내부 상태를 갖는 stateful operation이라고 한다.

sorted, distinct 연산은 과거의 이력을 알고 있어야 한다. 어떤 element를 출력 스트림에 추가하려면,  
모든 요소가 버퍼에 추가되어 있어야 한다. 연산을 수행하는 데 필요한 저장소 크기는 정해져있지 않다.

#### 중간 최종 연산 표

## 숫자형 스트림
### 기본형 특화 스트림
자바 8에서는 스트림 API 박싱 비용을 피할 수 있도록, 3가지 기본형 특화 스트림(primitive stream specialization)을 제공한다. 각 인터페이스는 sum, max 등 숫자 관련 리듀싱 연산 수행 메서드를 제공한다.

- int : IntStream
- double : DoubleStream
- long : LongStream

특화 스트림은 오직 boxing 과정에서 일어나는 효율성과 관련 있으며 스트림에 추가 기능을 제공하지는 않는다.

#### mapToInt, mapToDouble, mapToLong
스트림을 특화 스트림으로 변환할 때 세가지 메서드를 가장 많이 사용한다.  
map과 정확히 같은 기능을 수행하지만, ```Stream<T>``` 대신 특화 스트림을 반환한다.

``` java
int calories = menu.stream()
                   .mapToInt(Dish::getCalories)
                   .sum();
```

위의 예에서 mapToInt는 ```Stream<Integer>```가 아닌 ```IntStream```을 반환한다.  
또, IntStream이 제공하는 sum메서드를 이용해 합계를 구한다. 스트림이 비어있다면 sum은 0을 반환한다.  
IntStream은 max, min, average 등의 유틸 메서드도 쩨공한다.
#### boxed
boxed 메서드로 특화 스트림에서 스트림으로 복원할 수있다. IntStream은 기본형의 정수값만 만들 수 있다.  
IntStream의 map 메서드는 IntUnaryOperator(int를 인수로 받아 int를 반환) 람다를 인수로 받는다.  
만약 int가 아닌 Dish같은 객체값을 반환하고 싶다면 일반 스트림의 map 연산이 필요하다.

boxed 예를 살펴보자.

``` java
IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
Stream<Integer> stream = intStream.boxed();
```

#### mapToObj
특화 스트림의 요소에 인수로 주어진 함수를 적용한 결과로 구성된 **객체 값 스트림**을 반환한다.  
특화 스트림의 boxed, map을 mapToObj 하나로 구현할 수 있다. 예를 살펴보자.  

``` java
Stream<int[]> stream1 = IntStream.rangeClosed(1, 100)
                                 .filter(num -> num % 2 == 0)//IntStream
                                 .boxed()//Stream<Integer>
                                 .map(num -> new int[]{num, num * num});

Stream<int[]> stream2 = IntStream.rangeClosed(1, 100)
                                 .filter(num -> num % 2 == 0)//IntStream
                                 .mapToObj(num -> new int[]{num, num * num});
```

짝수를 찾아 해당 수, 해당 수 제곱을 배열로 리턴한 예이다.

#### OptionalInt
합계에서는 0이라는 기본 값이 문제가 되지 않지만, IntStream에서 최대값을 찾을 때 0이라는 기본값이 있다면 이때문에 잘못된 결과가 도출될 수 있다. element가 없는 상황인지 실제 최대값이 0인지 구분할 수 없기 때문이다.  

값의 존재 여부를 알 수 있는 Optional이 있다. Optional은 reference type뿐 아니라 기본형 버전도 제공한다.
**OptionalInt**, **OptionalDouble**, **OptionalLong** 3가지를 제공한다.

아래 코드는 최대값 element를 찾는다. 만약 최대값이 없을 때(menu가 empty), 기본값을 1로 설정했다.

``` java
OptionalInt maxCalories = menu.stream()
                              .mapToInt(Dish::getCalories)
                              .max();
int max = maxCalories.orElse(1);
```

### range, rangeClosed
자바 8의 IntStream, LongStream는 정적 메서드 **range**, **rangeClosed**를 제공한다.  
**range**, **rangeClosed** 메서드는 특정 범위의 숫자 스트림을 만들수 있다.  

**range** 메서드는 시작값과 종료값이 범위에 포함되지 않는다.  
**rangeClosed** 메서드는 시작값과 종료값이 범위에 포함된다.  
아래 코드는 range, rangeClosed의 예이다.

``` java
IntStream range1 = IntStream.range(1, 100);//2~99
IntStream range2 = IntStream.rangeClosed(1, 100);//1~100
```

### 숫자 스트림 활용 : 피타고라스 수

## 스트림 만들기
다양한 방식으로 스트림을 만드는 법을 알아보자.

### Stream.of
정적메서드 Stream.of는 임의의 값을 인수로 받아 스트림을 만들 수 있다.

아래 코드는 of로 문자열 스트림을 만들어 대문자로 매핑 후 출력하는 예이다.
``` java
Stream<String> stream = Stream.of("Modern", "Java", "In", "Action");
stream.map(String::toUpperCase).forEach(System.out::println);
```
### Stream.ofNullable
자바 9에서 추가된 메서드로, null이 될 수 있는 객체를 스트림으로 만들 수 있다.  
단일 element를 포함한 스트림을 리턴하며, null일 경우 empty 스트림을 리턴한다.

ofNullable이 없이 스트림을 생성할 경우, 명시적으로 null check를 해주어야 한다.

``` java
//명시적 check
Stream<String> checkStream = (value == null) ? Stream.empty() : Stream.of(value);
//ofNullable
Stream<String> nullableStream = Stream.ofNullable(value);
```

null일수도 있는 객체를 포함하는 스트림 값을 flatMap과 사용하는 상황에서 더 유용하게 사용할 수 있다. 예를 살펴보자.

``` java
Stream.of("java.specification.name", "java.vm.version", "user")
      .flatMap(key -> Stream.ofNullable(System.getProperty(key)))
      .forEach(System.out::println);
//출력
//Java Platform API Specification
//openj9-0.23.0
```

System.getProperty는 key에 해당하는 값이 있으면 리턴하고, 없으면 null을 리턴한다.  
user에 해당하는 값이 없으므로 출력이 2개만 되는것을 확인할 수 있다.

### Arrays.stream
배열을 인수로 받아 스트림을 만들 수 있다.
int배열을 IntStream을 리턴하고, Integer 배열은 Stream\<Integer\>을 리턴한다.

## Reference
- Modern Java in Action
- https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html
# 컬렉터(Collector)
컬렉터와 컬렉터의 다양한 메서드들에 대해 알아보자.
## Collector란?
Collector 인터페이스 구현은 스트림의 element를 어떤 식으로 도출할지 지정한다.  
toList, groupingBy 등이 있으며, 다수준으로 그룹화를 할 때, 명령형 프로그래밍과 함수형 프로그래밍의 차이점이  
더욱 두드러진다. 명령형 코드는 다중 루프와 조건문을 추가하며 가독성과 유지보수성이 크게 떨어진다.  

### 장점
잘 설계된 함수형 API의 또 다른 장점은 **높은 수준의 조합성**과 **재사용성**이다.  
collect로 결과를 수집하는 과정을 간단하면서도 유연한 방식으로 정의할 수 있다는 점이 컬렉터의 최대 강점이다.  

스트림에 collect를 호출하면 스트림의 요소에 리듀싱 연산이 수행된다. collect에서 리듀싱 연산을 이용해서 스트림의  
각 요소를 방문하면서 컬렉터가 작업을 처리한다.

## Collectors
Collectors 유틸리티 클래스는 자주 사용하는 컬렉터 인스턴스를 손쉽게 생성할 수 있는 정적 팩토리 메서드를 제공한다.  
Collectors에서 제공하는 메서드의 기능은 크게 **요소를 하나의 값으로 리듀스, 요약**, **요소 그룹화**, **요소 분할** 3가지이다.

## 리듀싱과 요약
Collector로 스트림의 모든 항목을 하나의 결과로 합칠 수 있다.

### counting
counting 팩토리 메서드가 반환하는 컬렉터로 개수를 계산한다. counting 컬렉터는 보통 다른 컬렉터와 함께 사용할 때 위력적이다.  
``` java
// 메뉴의 개수를 계산한다.
long howManyDishes1 = menu.stream().collect(Collectors.counting());
// 더 간략하게
long howManyDishes2 = menu.stream().count();
```

### maxBy, minBy
Collectors.maxBy, Collectors.minBy 메서드를 이용해서 스트림의 최대값과 최솟값을 계산할 수 있다.  
두 메서드는 인수로 스트림 요소를 비교할 때 사용할 Comparator를 인수로 받는다.  

아래 코드는 가장 칼로리가 높은 Dish를 찾는다. 

``` java
Optional<Dish> mostCalorieDish = menu.stream()
                        .collect(maxBy(Comparator.comparingInt(Dish::getCalories)));
```

### summingInt
Collectors.summingInt는 특별한 요약 팩터리 메서드로, 객체를 int로 매핑하는 함수를 인수로 받는다.  
인수로 전달된 함수는 객체를 int로 매핑한 컬렉터를 반환한다. 그리고 summingInt가 collect 메서드로 전달되면 요약 작업을 수행한다.  
추가로 summingLong, summingDouble 메서드도 제공한다.

아래 코드는 메뉴 목록의 총 칼로리를 계산하는 코드이다.

``` java
int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
```

### averagingInt
평균값을 계산할 수 있으며, averagingLong, averaginDouble 등의 메서드도 제공된다.  

아래 코드는 메뉴의 평균 칼로리를 구하는 코드이다.

``` java
double avgCalories = menu.stream().collect(averagingInt(Dish::getCalories));
```
### summarizingInt
개수, 최대값, 최소값, 합계, 평균 등 두개 이상의 연산을 한번에 수행해야 할 때도 있다.  
이럴때 summarizingInt가 반환하는 컬렉터를 사용할 수 있다.  
summarizingLong, summarizingDouble 메서드도 제공된다.

아래 코드의 IntSummaryStatistics 클래스로 모든 정보가 수집된다.

``` java
IntSummaryStatistics menuStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));
//출력 : IntSummaryStatistics{count=9, sum=2613, min=111, average=290.333333, max=400}
```

### joining
스트림의 각 객체의 toString 메서드를 호출해서 추출한 모든 문자열을 하나의 문자열로 연결해서 반환한다.
joining 메서드는 내부적으로 StringBuilder를 이용해서 문자열을 하나로 만든다.

아래 코드는 요리의 이름을 연결한 예이다. join은 구분자를 넣어줄 수 있게 오버로딩된 메서드도 제공한다.

``` java
String shortMenu = menu.stream().map(Dish::getName).collect(joining());
//출력 : porkbeefchicken
String shortMenu = menu.stream().map(Dish::getName).collect(joining(", "));
//출력 : pork, beef, chicken
```

### reducing (범용 리듀싱 요약 연산)
앞서 살펴본 모든 컬렉터는 reducing 팩토리 메서드로도 정의할 수 있다.  
앞서 특화된 컬렉터를 사용한 이유는 프로그래머의 편의성, 가독성 때문이다.  

아래 코드는 모든 메뉴의 칼로리 합계를 계산한다.

``` java
int totalCalories = menu.stream().collect(reducing(0, Dish::getCalories, (i, j) -> i + j));
```

reducing 메서드는 아래 3가지 인수를 받으며 각각은 아래와 같다.
- U identity : 리듀싱 연산의 시작 값 또는 스트림에 요소가 없을 때는 반환 값
- Function<? super T, ? extends U> mapper : 변환 함수(mapping)
- BinaryOperator\<U> op : 같은 종류의 두 항목을 같은 종류의 하나의 값 반환한다.

인수가 1개인 reducing 메서드는 스트림의 첫번째 요소를 시작요소로 하고, 항등 함수(자신을 반환하는 함수)가 변환 함수에 해당한다.  빈 스트림일 경우 Optional을 반환한다.

스트림 인터페이스에서 제공하는 메서드를 이용하는 것에 비해 컬렉터를 이용하는 코드가 더 복잡하다.  
대신 재사용성과 커스터마이즈 가능성을 제공하는 높은 수준의 추상화와 일반화를 얻을 수 있다.

## 그룹화
명령형으로 그룹화를 구현하려면 까다롭고, 할일도 많고, 에러도 자주 발생한다.  
자바 8의 함수형을 이용하면 가독성 있는 한 줄의 코드로 그룹화를 구현할 수 있다.

### groupingBy
groupingBy 팩토리 메서드를 이용해서 쉽게 그룹화할 수 있다.  

아래 코드는 메뉴를 타입별로 요리를 그룹화 했다. 여기서 groupingBy의 인수로 전달된 함수 getType은  
이 함수를 기준으로 스트림이 그룹화되므로 분류 함수(classification function)라고 부른다.

``` java
Map<Dish.Type, List<Dish>> dishesByType = menu.stream()
        .collect(groupingBy(Dish::getType));
```

또, 일반적으로 스트림에서 같은 그룹으로 분류된 모든 요소에 리듀싱 작업을 수행할 때는 groupingBy에  
두번째 인수로 전달한 컬렉터를 사용한다. mapping 메서드로 만들어진 컬렉터도 groupingBy와 자주 사용된다.  

다음은 메뉴에 있는 모든 요리의 칼로리 합계를 구하려고 만든 컬렉터(summingInt)를 재사용한 코드이다.

``` java
Map<Dish.Type, Integer> totalCaloriesByType = menu.stream()
        .collect(groupingBy(Dish::getType,
                summingInt(Dish::getCalories)));
```

### 그룹화된 요소를 조작
groupingBy 메서드는 두번째 인수로 컬렉터를 받도록 오버로드되어 있다.  
또 해당 메서드를 이용해 그룹화된 요소를 조작할 수 있다.  

#### filtering
아래 코드의 filtering 메서드는 Collectors의 정적 팩토리 메서드이다.  
filtering 메서드의 프레디케이트로 각 그룹의 요소와 필터링 된 요소를 재그룹화 한다.

``` java
Map<Dish.Type, List<Dish>> caloricDishedByType = menu.stream()
        .collect(groupingBy(Dish::getType, filtering(dish -> dish.getCalories() > 500, toList())));

//stream.filter.groupingBy는 만약 FISH 타입에 해당하는 요소가 없다면 키 자체가 사라진다.
//반면 위의 코드는 FISH=[] 처럼 키 자체가 사라지지 않는다.
```

#### mapping
mapping 메서드는 그룹화된 요소를 변환하는 작업을 할 수 있다.(mapping 메서드는 groupingBy와 자주 사용된다.)  
mapping 메서드는 스트림의 인수를 변환하는 함수와 변환 함수의 결과 객체를 누적하는 컬렉터를 인수로 받는다. 
입력 요소를 누적하기 전에 매핑 함수를 적용해서 주어진 형식의 컬렉터에 맞게 변환하는 역할을 한다.  
또, flatMapping으로 두 수준의 리스트를 한 수준으로 평면화할 수 있다.

아래 처럼 각 그룹의 요리 이름 목록으로 변환할 수 있다.

``` java
Map<Dish.Type, List<String>> dishNamesByType = menu.stream()
        .collect(groupingBy(Dish::getType, mapping(Dish::getName, toList())));
```

각 요리 형식에 존재하는 모든 칼로리 레벨을 알고 싶을 경우, 아래처럼 구현할 수 있다.

``` java
Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType = menu.stream()
        .collect(groupingBy(Dish::getType, mapping(dish -> {
                if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                else return CaloricLevel.FAT;
        }, toSet())));
```

Set의 형식을 정하고 싶다면 toSet 대신 toCollection을 이용하여 toCollection(HashSet::new) 같이 Set의 형식을 정할 수 있다.

#### collectingAndThen
collectingAndThen 팩토리 메서드로 컬렉터가 반환한 결과를 다른 형식으로 활용할 수 있다.  
collectingAndThen은 적용할 컬렉터와 변환 함수를 인수로 받아 다른 컬렉터를 반환한다.  

아래 코드는 각 메뉴 타입별 가장 높은 칼로리의 음식을 그룹화하는 예이다.

``` java
Map<Dish.Type, Dish> mostCaloricByType = menu.stream()
        .collect(groupingBy(Dish::getType,
                collectingAndThen(
                        maxBy(Comparator.comparingInt(Dish::getCalories)),//적용할 컬렉터
                        Optional::get//변환 함수
                )));
```

maxBy 팩토리 메서드가 생성하는 컬렉터의 결과 형식에 따라 맵의 값이 Optional 형식이 된다.  
collectingAndThen을 사용하지 않고, 그룹화하면 ```Map<Dish.Type, Optional<Dish>>``` 타입이 된다.  
그런데 groupingBy 컬렉터는 첫 번째 요소를 찾은 이후에 맵에 새로운 키를 추가한다.(LAZY)  
그렇기 때문에 메뉴의 요리 중 Optional.empty()를 값으로 가지는 요리는 존재하지 않는다.  
그러므로 변환 함수에 Optional::get을 사용하여 Optional에 포함된 값을 추출하여도 안전한 코드이다.

## 분할(partitioningBy)
분할은 분할 함수(partitioning function)라 불리는 Predicate를 분류 함수로 사용하는 특수한 그룹화 기능이다.  
Predicate 분류 함수를 사용하므로 그룹화 맵은 true, false 두 개의 그룹으로 분류된다.  
분할의 장점은 참, 거짓 두가지 요소의 스트림 리스트를 모두 유지하는 것이다.  

아래 코드는 isVegetarian 분할 함수를 이용하여, 모든 요리를 채식과 채식이 아닌 요리로 분리한 예이다.(filter 메서드로도 구현 가능)

``` java
Map<Boolean, List<Dish>> partitionedMenu = menu.stream().collect(partitioningBy(Dish::isVegetarian));
```

또한 컬렉터를 두 번째 인수로 전달할 수 있는 오버로드된 partitioningBy 메서드도 제공된다.  
아래 예는 채식과 채식이 아닌 요리를 요리 종류로 그룹화해서 두 수준의 맵이 반환되었다.

``` java
Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType = menu.stream()
        .collect(
                partitioningBy(Dish::isVegetarian,
                        groupingBy(Dish::getType)
                ));
```
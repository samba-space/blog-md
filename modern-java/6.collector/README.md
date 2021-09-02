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


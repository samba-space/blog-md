# 동작 파라미터화 (Behavior parameterization)

**동작 파라미터화**란 코드를 메서드 파라미터로 전달하는 것이다.(해당 코드의 실행은 나중에 프로그램에서 호출한다.)  
메소드가 다양한 동작(또는 전략)을 받아서 내부적으로 다양한 동작을 수행할 수 있다.  
**동작 파라미터화**는 요구사항 변화에 더 잘 대응할 수 있는 코드를 구현할 수 있으며, 비용을 줄일 수 있다.

## 동작 파라미터화를 하지 않았을 경우
만약 녹색 사과만을 필터링하는 기능을 추가한다면, 아래와 같이 구현할 수 있다.

**단순히 조건문에 Color를 넣은 경우**
``` java
public static List<Apple> filterGreenApples(List<Apple> inventory){
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
        if(Color.GREEN.equals(apple.getColor())){
            result.add(apple);
        }
    }
    return result;
}
```

그러나 요구사항으로 빨간 사과도 필터링하는 기능이 추가된다면, filterRedApples라는 메소드를 새로 만들어 색 조건을 바꿀 것이다.  
필터링할 색만 추가됐지만, 거의 동일한 메소드를 추가해야 한다.
변화되는 요구사항에 적절하게 대응할 수 없는 것을 볼 수 있다.  
이와 같이 **비슷한 코드가 반복해서 존재할 경우 그 코드를 추상화하자**.

**Color를 파라미터화 한 경우**
``` java
public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color){
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
        if(apple.getColor().equals(color)){
            result.add(apple);
        }
    }
    return result;
}
```

Color를 파라미터로 받고 있다. 실제 호출하는 client 코드는 아래와 같다.

``` java
List<Apple> greenApples = filterApplesByColor(inventory, Color.GREEN);
List<Apple> redApples = filterApplesByColor(inventory, Color.RED);
```

그런데 요구사항으로 가벼운 사과와 무거운 사과를 구분할 수 있어야 한다는 기능이 추가된다면 어떻게 될까?Color와 마찬가지로 무게의 기준도 변할 수 있으므로, 무게를 파라미터로 추가하였다.

``` java
public static List<Apple> filterApplesWeight(List<Apple> inventory, int weight){
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
        if(apple.getWeight() > weight){
            result.add(apple);
        }
    }
    return result;
}
```

좋은 해결책이라고 할 수 있다. 그러나 목록을 탐색하고, 필터링 조건을 적용하는 코드가  
filterApplesByColor와 filterApplesByWeight가  대부분 중복된다.  
이는 소프트웨어 공학의 **DRY(don't repeat yourself)** 원칙을 어긴다.  
또한, 탐색 부분이 바뀐다면 모든 메소드를 찾아가서 고쳐야 한다는 문제가 있다.(비용이 높다)

**모든 속성을 파라미터로 한 경우**  
실무에선 절대 이 방법을 사용하지 말자.

``` java
public static List<Apple> filterApples(List<Apple> inventory, Color color, int weight, boolean flag){
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
        if((flag && apple.getColor().equals(color)) || (!flag && apple.getWeight() > weight)){
            result.add(apple);
        }
    }
    return result;
}
```

소스코드는 flag에 따라 필터 조건을 선택하고 있다. 요구사항이 바뀌었을 때 유연하게 대응할 수 없다.  
아래 해당 메서드를 호출하는 client 코드는 true, false가 무엇을 의미하는지 알 수가 없다.

``` java
List<Apple> greenApples = filterApples(inventory, Color.GREEN, 0, true);
List<Apple> heavyApples = filterApples(inventory, Color.RED, 150, false);
```

## 동작 파라미터화
참 또는 거짓을 반환하는 함수를 Predicate라고 한다. 다음은 선택 조건을 결정하는 인터페이스를 정의하자.

``` java
public interface ApplePredicate {
    boolean test( Apple apple );
}

public class AppleHeavyWeightPredicate implements ApllePredicate {
    public boolean test(Apple apple){
        return apple.getWeight() > 150;
    }
}

public class AppleGreenColorPredicate implements ApllePredicate {
    public boolean test(Apple apple){
        return Color.GREEN.equals(apple.getColor());
    }
}
```

ApplePredicate는 사과 선택 전략을 캡슐화했다. 이를 **전략 패턴**이라고 한다.  
**전략 패턴**은 각 알고리즘(=전략)을 캡슐화하는 알고리즘 패밀리를 정의해둔 다음에 런타임에 알고리즘을 선택하는 기법이다.  
(알고리즘 패밀리 - ApplePredicate / 전략 - AppleHeavyWeightPredicate, AppleGreenColorPredicate)  

filterApples 메서드가 ApplePredicate 객체를 파라미터로 받도록 고쳐보자.

**동작을 추상화해서 필터링(한 개의 파라미터로 다양한 동작)**
``` java
public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
    List<apple> result = new ArrayList<>();
    for(Apple apple : inventory) {
        if(p.test(apple)) {
            result.add(apple);
        }
    }
    return result;
}
```

동작 파라미터화를 하면, 메서드 내부에서 **컬렉션을 반복하는 로직**과 **컬렉션의 각 요소에 적용할 동작**(Predicate)을  
분리할 수 있다는 점이 소프트웨어 엔지니어링적으로 큰 이득을 얻는다.  

**전달한 ApplePredicate 객체에 의해 filterApples 메서드의 동작이 결정된다.**(즉, filterApples 메서드의 동작을 파라미터화 한 것이다.)  
다양한 ApplePredicate를 만들어 filterApples 메서드로 전달할 수 있는 유연성을 누릴 수 있다.(유연한 API를 만들 때 매우 중요한 역할을 한다.)

그러나 매번 ApplePredicate를 구현하는 여러 클래스를 정의하고, 인스턴스화해야 하는 건 번거롭고 시간 낭비이다.(로직과 관련 없는 코드도 많다.)  
**익명 클래스**와 **람다 표현식**으로 번거로움을 해결할 수 있다.  

## 복잡한 과정을 간소화
익명 클래스와 람다 표현식으로 간소화할 수 있으며, 이에 대해 알아보도록 하자.

### 익명 클래스
익명 클래스는 이름 없는 클래스로 클래스 선언과 인스턴스화를 동시에 할 수 있다.(local class와 비슷한 개념)  
다음 소스코드는 익명 클래스를 사용하여 ApplePredicate를 구현하는 객체를 만드는 방법이다.

**익명 클래스 사용**
``` java
List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
    public boolean test(Apple apple) {
        return Color.RED.equals(apple.getColor());
    }
})
```

익명 클래스도 여전히 문제가 있다. **코드가 장황하다는 것**과 많은 프로그래머가 **익명 클래스의 사용에 익숙하지 않다**는 것이다.  
장황한 코드는 구현하고 유지 보수하는 데 오랜 시간이 걸리고, 한눈에 이해하기 어렵다.  
이를 극복하기 위해 **람다 표현식**을 사용하면 된다. 

### 람다 표현식
람다 표현식을 사용하면, 아래와 같이 간결해지면서 가독성이 좋아지는 것을 볼 수 있다.

``` java
List<Apple> result = filterApples(inventory, (Apple apple) -> RED.equals(apple.getColor()));
```

더 나아가 사과뿐만 아니라 다른 물체에도 필터를 적용할 수 있게 할 수 있다.

**리스트 형식으로 추상화**
``` java
static public <T> List<T> filter(List<T> list, Predicate<T> p){
	List<T> result = new ArrayList<>();
	for(T e: list){
        if(p.test(e)){
			result.add(e);
		}
	}
	return result;
}
```

사과 뿐 아니라 다양한 물체의 리스트에 필터 메서드를 사용할 수 있다.  
아래는 호출 시 소스코드이며, 유연성과 간결함을 확인할 수 있다.

``` java
List<Apple> redApples = filter(inventory, (Apple apple) -> Color.RED.equals(apple.getColor()));  
List<Apple> evenNumber = filter(numbers, (Integer i) -> i % 2 == 0);
```
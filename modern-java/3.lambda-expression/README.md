# 람다 표현식 (lambda expression)
람다 표현식은 메서드로 전달할 수 있는 익명 함수를 단순화한 것이다. 람다를 이용해서 간결한 방식으로 코드를 전달할 수 있으며, 동작 파라미터 형식의 코드를 더 쉽게 구현할 수 있다. **결과적으로 코드가 간결하고 유연해진다.**

## 람다 표현식의 특징
- 익명 : 보통의 메서드와 달리 이름이 없다.
- 함수 : 메서드처럼 클래스에 종속되지 않아 함수라고 부른다.
- 전달 : 표현식을 메서드 인수로 전달하거나 변수로 저장할 수 있다.
- 간결성 : 익명클래스의 불필요한 코드를 구현할 필요가 없다.
<br>

## 기본 문법
람다의 기본 문법은 표현식 스타일, 블록 스타일이 있다.  
### 1. 표현식 스타일  
``(parameters) -> expression``
``` java
() -> "example"
```

### 2. 블록 스타일  
``(parameters) -> { statement; }``
``` java
() -> { return "example"; }
```
<br>

## 람다는 어떻게 사용할까?
람다 표현식은 **함수형 인터페이스 문맥(context)**에서 사용할 수 있다. 함수형 인터페이스에 대해 알아보자.

### 함수형 인터페이스 (functional interface)
함수형 인터페이스는 추상 메서드가 오직 하나인 인터페이스이다.(디폴트 메서드가 있어도 관계없다.)  
람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달 할 수 있다.  
즉, 전체 표현식을 함수형 인터페이스를 구현한 클래스의 인스턴스로 취급할 수 있다.

함수형 인터페이스 `Runnable`이다. `@FunctionalInterface`는 함수형 인터페이스임을 나타내는 어노테이션이며, 실제 함수형 인터페이스가 아닐 경우 컴파일 에러를 발생시킨다.

```java
@FunctionalInterface
public interface Runnable {
    public abstract void run();
}
```

```Runnable```이 함수형 인터페이스이므로 람다 표현식을 사용할 수 있다.

``` java
//람다 표현식 사용
Runable r1 = () -> System.out.println("Hello world");

//익명 클래스 사용
Runable r2 = new Runnable() {
    public void run() {
        System.out.println("Hello world");
    }
}
//함수형 인터페이스 타입의 인수를 갖는 메서드
public static void process(Runnable r) {
    r.run();
}
process(() -> System.out.println("Hello world"));//람다표현식 전달
```

추가적으로 함수형 인터페이스는 checked Exception을 던지는 동작을 허용하지 않는다. 예외를 던지는 람다를 만들려면 checked Exception을 선언하는 함수형 인터페이스를 직접 정의하거나 람다를 try/catch 블록으로 감싸야 한다.

### 함수 디스크립터 (function descriptor)
함수형 인터페이스의 추상 메서드 시그니처는 람다 표현식의 시그니처를 가리킨다. 함수형 인터페이스의 추상 메서드 시그니처를 **함수 디스크립터**라고 한다.

인수가 없으며 void를 반환하는 람다 표현식으로 ``Runnable`` 인터페이스의 run 메서드의 시그니처와 같다.  

``` java
Runnable r1 = () -> System.out.println("Hello world");
```
<br>

## 람다 활용(실행 어라운드 패턴)
실제 자원을 처리하는 코드를 setup(설정)과 cleanup(정리) 과정으로 둘러싸는 형태의 코드를 실행 어라운드 패턴(execute around pattern)이라고 한다. setup과 cleanup 과정은 대부분 비슷하므로, setup과 cleanup은 재사용하고 실제 처리만 다른 동작을 수행할 수 있게 **동작 파라미터화** 하는 것이 좋다.

``` java
public String processFile() throws IOException{
    try(BufferedReader br = new BufferedReader(new FileReader("data.txt"))){
        return br.readLine(); //실제 처리
    }
}
```
> ##### 자바 7의 try-with-resources를 사용해서 자원을 명시적으로 닫을 필요가 없으므로 간결한 코드를 구현할 수 잇다.

위의 processFile 메서드의 동작을 파라미터화 해보자.  
BufferedReader를 이용해서 다른 동작을 수행할 수 있도록 processFile로 동작을 전달해야 한다.  

processFile 메서드가 한번에 두행을 읽게 하려면  
```(BufferdReader br) -> br.readLine() + br.readLine()``` 처럼 BufferedReader를 인수로 받아 String을 반환하는 람다가 필요하다.  

시그니처가 일치하는 함수형 인터페이스 context에 람다를 사용할 수 있으므로,  
```BufferdReader -> String```이고 IOException을 던질 수 있는 함수형 인터페이스를 만들어야 한다.

``` java
@FunctionalInterface
public interface BufferedReaderProcessor{
    String process(BufferedReader b) throws IOException;
}
```

해당 함수형 인터페이스를 파라미터로 수정한 processFile 메서드는 아래와 같다.  
이제, 함수형 인터페이스의 process 메서드의 시그니처와 일치하는 람다를 전달 할 수 있다.

``` java
public static String processFile(BufferedReaderProcessor p) throws IOException{
    try(BufferedReader br = new BufferedReader(new FileReader("data.txt"))){
        return p.process(br);
    }
}
```

실제 람다를 이용해서 메서드 호출은 아래처럼 할 수 있다.

``` java
String oneLine = processFile((BufferedReader br) -> br.readLine());
String twoLines = processFile((BufferedReader br) -> br.readLine() + br.readLine());
```
<br>

## 함수형 인터페이스 사용
다양한 람다 표현식을 사용하려면 공통의 함수 디스크립터를 기술하는 함수형 인터페이스 집합이 필요하다. 자바 8에서는 java.util.function 패키지로 여러가지 새로운 함수형 인터페이스를 제공한다. 다양한 함수형 인터페이스에 대해 알아 보자. 

### 1. Predicate
`test` 추상 메서드를 정의하며, `test`는 제네릭 형식 T 객체를 인수로 받아 boolean을 반환한다. T 객체를 사용하는 boolean 표현식이 필요할 때 사용할 수 있다.
```java
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}
```

### 2. Consumer
`accept`라는 추상 메서드를 정의하며, `accept`는 제네릭 형식 T 객체를 인수로 받아 void를 반환한다. T 객체를 인수로 받아 단순히 동작을 수행할 때 사용할 수 있다.
```java
@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}

```

### 3. Function
`apply`라는 추상 메서드를 정의하며, `apply`는 제네릭 형식 T를 인수로 받아 R을 반환한다. 입력을 출력으로 매핑하는 경우(문자열의 길이를 추출할 때) 사용할 수 있다.
```java
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}
```

### 4. 기본형 특화
자바 8에서는 기본형을 입출력으로 사용하는 상황에서 오토박싱 동작을 피할 수 있도록 특화된 형식의 함수형 인터페이스를 제공한다.

제네릭 파라미터에는 참조형만 사용할 수 있다. 기본형을 전달하게 되면 아래와 같이 기본형을 참조형으로 박싱하게 된다.
이런 과정은 비용이 소모된다. 기본형을 감싸는 래퍼는 힙에 저장되어 메모리를 더 소비하고, 기본형을 가져올 때도 메모리를 탐색하는 과정이 필요하다. 이러한 비용을 줄이기 위해 기본형 특화 함수형 인터페이스를 제공한다.

```java
Predicate<Integer> numberPredicate= (number) -> number == 1;
```

일반적으로 기본형식을 함수형 인터페이스의 이름 앞에 붙여 `IntPredicate`, `LongPredicate`, `DoublePredicate` 등과 같이 인터페이스 명을 가진다.

## type 검사, type 추론, 제약
람다 표현식 자체에는 람다가 어떤 함수형 인터페이스를 구현하는지의 정보가 포함되어 있지 않다. 람다 표현식을 더 제대로 이해하려면 람다의 실제 type을 파악해야 한다.

### type 검사
람다가 사용되는 context를 이용해서 람다의 type을 추론할 수 있다. 어떤 context(할당문, 메서드호출(파라미터, 리턴값), 형변환 등)에서 기대되는 람다 표현식의 type을 target type이라고 한다.

```java
filter(inventory, (Apple apple) -> apple.getWeight() > 150);
```
위와 같이 람다 표현식이 사용되었을 때, type 검사 과정을 알아보자.
1. filter 메서드 선언을 확인한다.(`filter(List<Apple> inventory, Predicate<Apple> p)`)
2. target type은 `Predicate<Apple>`고, 추상 메서드 `test`는 Apple을 인수로 받아 boolean을 반환하는 함수 디스크립터를 묘사한다. 
3. 함수 디스크립터 `Apple -> boolean`과 람다의 시그니처 일치하는지 확인한다.

### 같은 람다, 다른 함수형 인터페이스
target type이라는 특징 때문에 같은 람다 표현식이더라도 호환되는 추상 메서드를 가진 다른 함수형 인터페이스로   
사용될 수 있다.

같은 람다 표현식이지만, target type이 각각 `Callable<Integer>`, `PrivilegedAction<Integer>`이다.
```java
Callable<Integer> c = () -> 42;
PrivilegedAction<Integer> p = () -> 42;
```

비슷하게 자바 7에서 다이아몬드 연산자로 context에 따른 제네릭 타입을 추론할 수 있다.

```java
List<String> listOfStrings = new ArrayList<>;
List<Integer> listOfIntegers = new ArrayList<>;
```
또, 특별한 void 호환 규칙이 있다. 람다의 바디가 일반 표현식(expression)이 있으면 void를 반환하는 함수 디스크립터와 호환된다. 예를 들어 List의 add 메서드는 boolean을 반환하므로 `s -> list.add(s)` 람다 표현식은 target type이 `Predicate<String>`이지만, `Consumber<String>`도 호환이 된다.

```java
Predicate<String> p = s -> list.add(s);
Consumer<String> b = s -> list.add(s);
```
> ##### 표현식은 변수, 상수, 연산자, 메서드 호출로 구성된 것을 말하며, 표현식이 모여 statement, statement가 모여 block을 구성한다.

### type 추론
target type을 이용해서 함수 디스크립터를 알 수 있으므로 컴파일러는 람다의 시그니처도 추론할 수 있다.

filter의 `Predicate<Apple>`의 `test` 메서드로 람다의 시그니처를 추론할 수 있다. 람다의 type 추론 대상 파라미터가 1개일 경우 괄호도 생략 가능하다.
```java
List<Apple> greenApples = filter(inventory, apple -> GREEN.equals(apple.getColor());
```

상황에 따라 명시적으로 파라미터에 type을 포함하는 것이 좋을 때도 있고, 생략하는 것이 가독성을 향상시킬 때도 있다. 무엇이 좋은지 정답은 없고 상황에 따라 개발자가 결정해야 한다.

```java
Comparator<Apple> c1 =
        (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
Comparator<Apple> c2 =
        (a1, a2) -> a1.getWeight().compareTo(a2.getWeight());
```
### 지역 변수 사용
람다 표현식에서는 익명 함수가 하는 것처럼 자유 변수(외부 변수)를 활용할 수 있다. 이와 같은 동작을 람다 캡처링(capturing lambda)이라고 부른다.  
자유 변수 사용에도 제약이 있다. 람다는 인스턴스 변수와 정적 변수를 자유롭게 캡처할 수 있다. 지역 변수는 명시적으로 final을 선언되어야 하거나 effectively final 해야한다.(final 변수처럼 한번만 할당)

effectively final 하기 때문에 사용될 수 있고,
```java
int port = 1337;
Runnable r = () -> System.out.println(port);
```

port를 두번 할당하므로 컴파일할 수는 코드이다.

```java
int port = 1337;
Runnable r = () -> System.out.println(port);
port = 3333;
```

이와 같은 제약이 있는 이유는 지역 변수는 스택에 저장되는데, 멀티 스레드 환경에서 변수를 할당하는 스레드가 사라져서 변수 할당이 해제되었지만 람다를 실행하는 스레드에서는 해당 변수에 접근하려 할 수 있다.  
따라서 자바 구현에서는 원래 변수에 접근을 허용하지 않고 자유 지역 변수의 복사본을 제공한다. 그러므로 복사본의 값이 바뀌지 않아야 하므로 지역 변수에는 한번만 값을 할당해야 한다는 제약이 생긴 것이다.
<br>

## 메서드 참조
메서드 참조를 이용하면 기존의 메서드 정의를 재활용해서 람다처럼 전달할 수 있다. 메서드 참조는 특정 메서드만을 호출하는 람다의 축약형이라고 할 수 있다.  
명시적으로 메서드명을 참조함으로써 가독성을 높일 수 있다.

## 메서드 참조를 만드는 방법
### 1. 정적 메서드 참조
`Integer::paresInt`는 `Integer`의 정적 메서드 `parseInt` 의 메서드 참조이다. 단축 규칙은 다음과 같다 `(args) -> ClassName.staticMethod(args)` => `ClassName::staticMethod`
### 2. 다양한 형식의 인스턴스 메서드 참조
`String::length`는 `String`의 `length` 메서드의 메서드 참조이다. 단축 규칙은 다음과 같다 `(arg0, rest) -> arg0.instanceMethod(rest)` => `ClassName::instanceMethod`
### 3. 기존 객체의 인스턴스 메서드 참조
`transaction::getValue()`는 Transaction 객체를 할당 받은 지역변수 `transaction`의 `getValue` 메서드의 메서드 참조이다. 단축 규칙은 다음과 같다 `(args) -> expr.instanceMethod(args)` => `expr::instanceMethod`   

private helper 메서드를 정의한 상황에서 유용하다. `filter`의 두번째 파라미터인 `Predicate<String>`에 메서드 참조를 사용할 수 있다.
```java
private boolean isValidName(String string) {
    return Character.isUpperCase(string.charAt(0));
}
filter(words, this::isValidName);
```
### 4. 생성자 참조
`ClassName::new`와 같이 기존 생성자의 참조를 만들 수 있다. 정적 메서드 참조와 유사하다.  

```java
Supplier<Apple> c1 = Apple::new;
Supplier<Apple> c2 = () -> new Apple();
Apple apple1 = c1.get();//새로운 Apple 객체를 만듬

BiFunction<Color, Integer, Apple> c3 = Apple::new;//Apple(String color, int weight) 생성자 참조
BiFunction<Color, Integer, Apple> c4 = (color, weight) -> new Apple(color, weight);
Apple apple2 = c3.apply(Green, 110);//새로운 Apple 객체를 만듬
```
<br>

## 람다 표현식을 조합할 수 있는 유용한 메서드
자바 8 API의 몇몇 함수형 인터페이스는 람다 표현식을 조합할 수 있도록 유틸리티 메서드를 제공한다.(디폴트 메서드)

### Comparator 조합, 연결
`comparing`을 이용해서 비교에 사용할 키를 추출하는 `Function` 기반의 `Comparator`를 반환할 수 있다.
```java
Comparator<Apple> c = Comparator.comparing(Apple::getWeight);
```

내림차순으로 정렬하고 싶다면 인터페이스 자체에서 주어진 비교자의 순서를 바꾸는 `reverse` 메서드를 사용하면 된다.

```java
inventory.sort(comparing(Apple::getWeight).reversed());
```

만약 값이 같은 경우에는 무엇을 먼저 나열해야할까? 이럴 경우 `thenComparing` 메서드로 두번째 비교자를 만들수 있다. `thenComparing` 메서드는 함수를 인수로 받아 첫번째 비교자를 이용해서 두 객체가 같다고 판단되면 두번째 비교자에 객체를 전달한다.  

사과의 무게가 같다면 원산지 국가별로 사과를 정렬하는 예이다.
```java
inventory.sort(comparing(Apple::getWeight)
         .reversed()
         .thenComparing(Apple::getCountry));
```

### Predicate 조합
복잡한 `Predicate`를 만들 수 있도록 `negate`, `and`, `or` 세가지 메서드를 제공한다.  

빨간색이 아닌 사과 처럼 특정 `Predicate`를 반전시킬 때 `negate` 메서드를 사용할 수 있다.

```java
Predicate<Apple> notRedApple = redApple.negate();
```

`and` 메서드를 이용해서 빨간색이면서 무거운 사과를 선택하도록 두 람다를 조합할 수 있다.
```java
Predicate<Apple> redAndHeavyApple = redApple.and(apple -> apple.getWeight() > 150);
```

`or` 메서드를 이용해서 빨간색이면서 무거운 사과 또는 그냥 녹색 사과 조건을 만들 수 있다.
```java
Predicate<Apple> redAndHeavyApple = redApple.and(apple -> apple.getWeight() > 150)
                                            .or(apple -> GREEN.equals(apple.getColor()));
```

### Function 조합
`Function` 인스턴스를 반환하는 `andThen`, `compose` 두가지 디폴트 메서드를 제공한다.

`andThen` 메서드는 주어진 함수를 먼저 적용한 결과를 다른 함수의 입력으로 전달하는 함수를 반환한다.
```java
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x * 2;
Function<Integer, Integer> h = f.andThen(g);//f -> g
int result = h.apply(1);//4 반환
```

`compose` 메서드는 인수로 주어진 함수를 먼저 실행한 다음 그 결과를 외부 함수의 인수로 제공한다. `andThen`과 반대다.
```java
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x * 2;
Function<Integer, Integer> h = f.compose(g);//g -> f
int result = h.apply(1);//3 반환
```
<br>

## Reference
- Modern Java in Action
# 람다 표현식 (lambda expression)
람다 표현식은 메서드로 전달할 수 있는 익명 함수를 단순화한 것이다. 람다를 이용해서 간결한 방식으로 코드를 전달할 수 있으며,  
동작 파라미터 형식의 코드를 더 쉽게 구현할 수 있다. 결과적으로 코드가 간결하고 유연해진다.

## 람다 표현식의 특징
- 익명 : 보통의 메서드와 달리 이름이 없다.
- 함수 : 메서드처럼 클래스에 종속되지 않아 함수라고 부른다.
- 전달 : 표현식을 메서드 인수로 전달하거나 변수로 저장할 수 있다.
- 간결성 : 익명클래스의 불필요한 코드를 구현할 필요가 없다.

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
## 람다는 어떻게 사용할까?
람다 표현식은 **함수형 인터페이스 문맥(context)**에서 사용할 수 있다. 함수형 인터페이스에 대해 알아보자.

### 함수형 인터페이스 (functional interface)
함수형 인터페이스는 추상 메서드가 오직 하나인 인터페이스이다.(디폴트 메서드가 있어도 관계없다.) 람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달 할 수 있다.  
즉, 전체 표현식을 함수형 인터페이스를 구현한 클래스의 인스턴스로 취급할 수 있다.

함수형 인터페이스 ```Runnable```이다. @FunctionalInterface는 함수형 인터페이스임을 나타내는 어노테이션이며, 실제 함수형 인터페이스가 아닐 경우 컴파일 에러를 발생시킨다.

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

## 람다 활용(실행 어라운드 패턴)
실제 자원을 처리하는 코드를 setup(설정)과 cleanup(정리) 과정으로 둘러싸는 형태의 코드를 실행 어라운드 패턴(execute around pattern)이라고 한다.  
setup과 cleanup 과정은 대부분 비슷하므로, setup과 cleanup은 재사용하고 실제 처리만 다른 동작을 수행할 수 있게 **동작 파라미터화** 하는 것이 좋다.

``` java
public String processFile() throws IOException{
    try(BufferedReader br = new BufferedReader(new FileReader("data.txt"))){
        return br.readLine(); //실제 처리
    }
}
```
> 자바 7의 try-with-resources를 사용해서 자원을 명시적으로 닫을 필요가 없으므로 간결한 코드를 구현할 수 잇다.

위의 processFile 메서드의 동작을 파라미터화 해보자.  
BufferedReader를 이용해서 다른 동작을 수행할 수 있도록 processFile로 동작을 전달해야 한다.  

processFile 메서드가 한번에 두행을 읽게 하려면  
```(BufferdReader br) -> br.readLine() + br.readLine();``` 처럼  
BufferedReader를 인수로 받아 String을 반환하는 람다가 필요하다.  

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
이런 과정은 비용이 소모된다. 기본형을 감싸는 래퍼는 힙에 저장되어 메모리를 더 소비하고, 기본형을 가져올 때도 메모리를 탐색하는 과정이 필요하다.  
이러한 비용을 줄이기 위해 기본형 특화 함수형 인터페이스를 제공한다.

```java
Predicate<Integer> numberPredicate= (number) -> number == 1;
```

일반적으로 기본형식을 함수형 인터페이스의 이름 앞에 붙여 `IntPredicate`, `LongPredicate`, `DoublePredicate` 등과 같이 인터페이스 명을 가진다.
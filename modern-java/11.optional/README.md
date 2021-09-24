# null 대신 Optional 클래스
역사적으로 프로그래밍 언어에서 null 참조로 값이 없음을 표현해왔다. 그러나 null 참조로 인하여 발생할 수 있는 문제가 많다.  
이를 보완하기 위해, 자바 8에서는 Optional이라는 새로운 클래스를 제공한다. 먼저 null의 문제점에 대해 알아보자.

## null의 문제점
1965년 토니 호어는 ALGOL을 설계하면서 처음 null 참조가 등장했다. 그 당시에는 null 참조 및 예외로 값이 없는 상황을  
가장 단순하게 구현할 수 있다고 판단했고 결과적으로 null 및 관련 예외가 탄생했다. 
차후에 토니 호어는 null은 십업 달러짜리 실수라고 하기도 했다. null의 이론적, 실용적 문제를 알아보자.

### null로 인한 문제
- 에러의 근원이다. - NullPointerException은 자바에서 가장 흔히 발생하는 에러이다.
- 코드를 어지럽힌다. - 중첩된 null 확인 코드(deep doubt)로 인하여 가독성이 떨어진다.
- 아무 의미가 없다. - null은 아무 의미도 표현하지 않는다. 정적 형식 언어에서 값이 없음을 표현하는 방식으로는 적절하지 않다.
- 자바 철학에 위배된다. - 자바는 모든 포인터를 숨겼지만, null은 예외이다.
- type 시스템에 구멍을 만든다. - null은 모든 참조 type에 할당할 수 있다. 이는 시스템에서 null이 어떤 의미로 사용되었는지 알 수 없다.

## Optional 클래스 소개
Optional은 선택형값을 캡슐화하는 클래스이다. 값이 있으면 Optional 클래스는 값을 감싸고,  
값이 없으면 Optional.empty 메서드로 Optional을 반환한다. Optional.empty는 특별한 싱글턴 인스턴스를 반환하는 정적 팩토리 메서드이다.

아래의 예를 살펴보자. Optional\<Car>를 사용하여 값이 없을 수 있음을 명시적으로 보여준다.  
Optional 클래스를 사용하면서 모델의 semantic이 더 명확해졌다. 보험회사는 반드시 이름을 가져야 하며 없을 경우 예외를  
처리하는 코드를 추가하는 것이 아니라 이름이 없는 이유가 무엇인지 밝혀서 문제를 해결해야 한다.

```java
public class Person {
    private Optional<Car> car;//차가 없을 수도 있다.
    public Optional<Car> getCar() {
        return car;
    }
}

public class Car {
    private Optional<Insurance> insurance;//보험이 없을 수도 있다.
    public Optional<Insurance> getInsurance() {
        return insurance;
    }
}

public class Insurance {
    private String name;//보험회사는 이름이 반드시 있어야한다.
    public String getName() {
        return name;
    }
}
```

모든 null 참조를 Optional로 고치는 것은 바람직하지 않다.  
**Optional의 역할은 더 이해하기 쉬운 API를 설계하도록 돕는 것이다.** 메서드 시그니처만 보고도 선택형값인지  
여부를 확인할 수 있다. 또 Optional은 값이 없을 수 있는 상황에 적절하게 대응하도록 강제하는 효과가 있다.

## Optional 적용 패턴
실제 Optional을 어떻게 활용할 수 있는지 알아보자.

### Optional 객체 생성
- Optional.emty : 빈 Optional 객체를 얻을 수 있다.
```java
Optional<Car> optCar = Optional.empty();
```

- Optional.of : null이 아닌 값을 포함하는 Optional을 만들 수 있다.
```java
Optional<Car> optCar = Optional.of(car);
```

car가 null이면 바로 NullPointerException이 발생한다. Optional을 사용하지 않을 경우 Car의 프로퍼티에 접근하려 할 때 에러가 발생할 것이다.

- Optional.ofNullable : null값을 저장할 수 있는 Optional을 만들 수 있다.
```java
Optional<Car> optCar = Optional.ofNullable(car);
```

car가 null이면 빈 Optional 객체가 반환된다.

### map으로 Optional의 값을 추출하고 변환하기
객체의 정보를 추출할 때 Optional을 많이 사용한다. 예를 들어 보험회사의 이름을 가져올 때를 살펴보자.

```java
String name = null;
if(insurance != null) {
    name = insurance.getName();
}
```

이름 정보에 접근하려면 insurance가 null인지 확인해야한다. 이런 유형의 패턴에 Optional의 map 메서드를 사용하면 된다.

```java
Optional<Insurance> optInsurance = Optional.ofNullable(insurance);
Optional<String> name = optInsurance.map(Insurance::getName);
```
스트림의 map 메서드와 개념적으로 비슷하며, Optional이 값이 있으면 map의 인수로 제공된 함수가 적용되고, 그렇지 않다면 아무 일도 일어나지 않는다.

### flatMap으로 중첩 Optional 평탄화하기
아래의 소스는 컴파일 되지 않는다. optPerson의 map 호출결과 Optional\<Optional\<Car>>이므로 getInsurance 메서드를 지원하지 않는다.  

```java
Optional<Person> optPerson = Optional.of(person);
Optional<String> name =
        optPerson.map(Person::getCar)//Optional<Optional<Car>> 반환
                .map(Car::getInsurance)//요소가 Optional<Car>이므로 Car가 아님 
                .map(Insurance::getName);
```

flatMap 메서드를 이용해서 중첩 Optional을 하나의 Optional로 바꿀수 있다. 둘 중 하나라도 null이면 empty Optional을 생성한다.

```java
Optional<String> name =
        optPerson.flatMap(Person::getCar)//Optional<Car> 반환
                .flatMap(Car::getInsurance)//요소가 Car
                .map(Insurance::getName);
```

### orElse로 기본 값 반환하기
orElse 메서드는 Optional이 비어있을 때 default 값을 제공한다. 아래의 예처럼 결과 Optional이 비어있으면 "Unknown"을 기본 값으로 사용한다.

```java
public String getCarInsuranceName(Optional<Person> person) {
    return person.flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");
}
```

이 메서드는 null 확인과 분기문을 만들지 않아도 되는 깔끔한 코드이다.  
또, Optional을 사용하여 도메인 모델과 관련한 암묵적인 지식에 의존하지 않고 명시적으로 type system을 정의할 수 있다.  
Optional을 인수로 받거나 Optional을 반환하는 메서드는 사용하는 사람에게 이 메서드가 빈 값을 받거나 빈 밧을 반환할 수 있음을 문서화해서 제공하는 것과 같다.

### 도메인 모델에 Optional을 사용했을 때 데이터를 직렬화할 수 없다. 
브라이언 고츠는 Optional 용도 자체를 선택형 반환값을 지원하는 것이라고 목박았다.  
즉 Optional 클래스는 필드 형식으로 사용할 것을 가정하지 않았으므로 Serializable 인터페이스를 구현하지 않는다.  
직렬화 모델을 사용하는 도구나 프레임워크에서 문제가 발생할 수 있다. 그럼에도 Optional로 도메인 모델을 구성하는 것이 바람직하다.  

직렬화가 꼭 필요하다면 아래 예처럼 필드에 Optional을 쓰기보단 Optional로 값을 반환받을 수 있는 메서드를 추가하는 것을 권장한다.

```java
public class Person {
    private Car car;
    public Optional<Car> getCarAsOptional() {
        return Optional.ofNullable(car);
    }
}
```

### stream으로 Stream\<Optional> 조작
자바 9에서 Optional을 포함하는 스트림을 쉽게 처리할 수 있도록 Optional에 stream 메서드가 추가되었다.  
Optional.stream은 각 Optional이 비어있는지 아닌지에 따라 Optional을 0개 이상의 요소를 포함하는 스트림으로 변환한다.

```java
public Set<String> getCarInsuranceNames(List<Person> persons) {
    return persons.stream()
            .map(Person::getCar)//Stream<Optional<Car>>
            .map(optCar -> optCar.flatMap(Car::getInsurance))//Stream<Optional<Insurance>>
            .map(optIns -> optIns.map(Insurance::getName))//Stream<Optional<String>>
            .flatMap(Optional::stream)//1. Optional.Stream : Stream<Stream<String>>, 2. flatMap : Stream<String>
            .collect(Collectors.toSet());
}
```

위의 코드에서 flatMap(Optional::Stream)의 과정은 한 단계의 연산으로 값을 포함하는 Optional을 언랩하고 비어있는 Optional은 건너뛸 수 있다.

Optional.stream을 이용하지 않았다면, 결과를 얻기 위해 empty Optional을 제거하고 값을 언랩해야 한다.
아래 코드를 확인해보자. 

```java
Stream<Optional<String>> stream = persons.stream()
        .map(Person::getCar)
        .map(optCar -> optCar.flatMap(Car::getInsurance))
        .map(optIns -> optIns.map(Insurance::getName));
Set<String> result = stream.filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());
```

### default action과 Optional 언랩
아래의 default action과 Optional 언랩 메서드를 배워보자.
- get()  
: 값을 읽는 가장 간단한 메서드이며, 가장 안전하지 않다.  
값이 없으면 NoSuchElementException이 발생하므로, 값이 반드시 있는 상황에서만 사용하자.
- orElse(T other)  
: empty Optional일 경우 default 값을 리턴한다.
- orElseGet(Supplier<? extends T> other)  
: orElse의 lazy 버전 메서드이며, empty Optional일 경우 Supplier가 실행된다.  
디폴트 메서드가 오랜 시간이 걸리거나 Optional이 비어있을 때만 기본값을 생성하고 싶다면 orElseGet을 사용해야 한다.
- orElseThrow(Supplier<? extends X> exceptionSupplier)  
: Optional이 비어있을 때 예외를 발생시키며, 예외의 종류를 선택할 수 있다.
- ifPresent(Consumer<? super T> consumer)  
: Optional의 값이 존재하면 인수로 넘겨준 consumer가 실행된다.
- ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction)  
: 값이 존재할 때는 action이 실행되고, Optional이 비어있을 때, Runnable이 실행된다.

## 두개의 Optional 합치기
가장 저렴한 보험료를 찾는 메서드이다. 

```java
public Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, Optional<Car> car) {
    if (person.isPresent() && car.isPresent()) {
        return Optional.of(findCheapestInsurance(person.get(), car.get()));
    } else {
        return Optional.empty();
    }
}
```

이 메서드는 isPresent를 써서 Optional의 값 포함여부를 확인한다. null 확인 코드와 다른점이 없다.  
Optional에서 제공하는 기능으로 이 코드를 개선해보자.

```java
public Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, Optional<Car> car) {
    return person.flatMap(p -> car.map(c -> findCheapestInsurance(p, c)));
}
```

위와 같이 개선할 수 있으며, 조건문 없이 메서드를 재구현할 수 있다.  
둘 중 하나라도 빈 Optional이라면 empty Optional이 반환된다.  
두 값 모두 비어있지 않다면, findCheapestInsurance를 안전하게 호출할 수 있다.

## filter로 특정 값 거르기
filter 메서드로 특정 값을 거를 수 있다. filter 메서드는 프레디케이트를 인수로 받는다.  
프레디케이트와 일치하면 그 값을 반환하고(Optional에 아무 변화도 없음) 그렇지 않으면 empty Optional을 반환한다.  
또, Optional이 비어있다면 filter 연산은 실행되지 않는다.

보험사의 이름을 찾으려면 아래와 같이 보험사가 null인지 체크하고 이름을 비교한다.  

```java
if(insurance != null && "ParkInsurance".equals(insurance.getName())){
    System.out.println("ok");
}
```

Optional.filter를 이용하면 아래와 같이 구현할 수 있다.

```java
optInsurance.filter(i -> "ParkInsurance".equals(i.getName()))
            .ifPresent(x -> System.out.println("ok"));
```

## Optional을 사용한 실용 예제
네이티브 자바 API는 호환성을 유지하다보니 Optional을 적절하게 활용하지 못하고 있다.  
Optional 기능을 활용할 수 있도록 utility 메서드를 추가하는 방식으로 이 문제를 해결할 수 있다.

### null이 될 수 있는 대상을 Optional로 감싸기
Map.get은 일치하는 key가 없다면 null을 반환한다. null보다는 Optional 반환하는 것이 바람직하다.  
Map.get 메서드의 시그니처는 고칠 수 없지만 get 메서드의 반환 값을 Optional로 감쌀 수 있다.

아래처럼 기존 방식은 null 체크를 하는 등 복잡한 코드를 짜야한다.  
반면에 Optional을 적용하여 깔끔하고 안전한 코드를 짤 수 있다.

```java
//기존 방식
Object value = map.get("key");

//Optional을 적용한 방식
Optional<Object> value = Optional.ofNullable(map.get("key"));
```

### Exception과 Optional 클래스
자바 API는 어떤 이유로 값을 제공할 수 없을 때 null 대신 Exception을 발생 시킨다.  
null은 if문으로 체크하지만, Exception은 try/catch로 처리해야한다.  

대표적으로 Integer.parseInt가 있는데, 문자열이 정수형태가 아닐 경우 NumberFormatException이 발생한다.  
기존 Integer.parseInt는 고칠 수 없으므로 Integer.parseInt를 감싸는 utility 메서드를 구현하여,  
Optional을 반환할 수 있다.

```java
public class OptionalUtility {
    
    private OptionalUtility() {}
    
    public static Optional<Integer> stringToInt(String s) {
        try {
            return Optional.of(Integer.parseInt(s));//변환 가능하면 Optional 반환
        } catch (NumberFormatException e) {
            return Optional.empty();//변환 가능하지 않으면 empty Optional 반환
        }
    }
}
```

위와 같이 Utility Class를 만들어서 사용하길 권장한다. 문자열을 Optional\<Integer>로 필요할 때 가져다 쓸수 있고,  
중복해서 try/catch문을 사용하지 않아도 되기 때문이다.

### 기본형 특화 Optional은 사용하지 말자
OptionalInt, OptionalLong, OptionalDouble과 같이 기본형 특화 Optional을 제공한다.  
기본형 특화 Optional을 사용하지 말아야하는 이유는 아래와 같이 3가지이다.
- Optional은 최대 요소가 1개이기 때문이다. 기본형 특화로 성능을 개선할 수 없다.  
- 기본형 특화 Optional은 Optional이 제공하는 map, flatMap, filter등을 지원하지 않는다.  
- 기본형 특화 Optional로 생성한 결과는 일반 Optional과 혼용할 수 없다.

### 응용
예를 통해서 알아보자. properties에서 name에 해당하는 duration을 읽는 예이다.  
해당 key가 존재하지 않거나 value가 0보다 작거나 정수가 아닌 문자열일 경우 0을 리턴한다.  

기존 optional을 사용하지 않은 코드는 아래와 같으며, 코드가 복잡하고 가독성도 안좋다.

```java
public int readDuration(Properties props, String name) {

    String value = props.getProperty(name);
    if (value != null) {
        try {
            int result = Integer.parseInt(value);
            if (result > 0) {
                return result;
            }
        } catch (NumberFormatException nfe) {}
    }
    return 0;
}
```

Optional을 활용하여 개선한 코드다. 여러 연산을 서로 연결되는 데이터베이스 질의문과 비슷한 형식을 갖는다.(Stream처럼)  
자세한 내용은 코드의 주석을 확인해보자.

```java
public int optionalReadDuration(Properties props, String name) {
    return Optional.ofNullable(props.getProperty(name))//Optional 객체로 변환
                   .flatMap(OptionalUtility::stringToInt)//유틸리티 메서드를 이용하여 파싱
                   .filter(result -> result > 0)//0이상을 필터링한다.
                   .orElse(0);//위의 과정 중 하나라도 empty Optional을 반환하면 0을 리턴한다.
}
```
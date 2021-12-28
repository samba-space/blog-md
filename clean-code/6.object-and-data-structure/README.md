## 자료 추상화
### 구체적인 Point 클래스
```java
public class Point {
    public double x;
    public double y;
}
```
- 구체적인 Point 클래스는 구현을 노출한다. 직교좌표계를 사용하는지 확실하다.
- 변수를 private으로 선언하고 getter, setter 함수를 제공해도 구현을 외부로 노출하는 셈이다.
- 구현을 감추려면 추상화가 필요하다. 

### 추상적인 Point 클래스
```java
public interface Point {
    double getX();
    double getY();
    void setCartesian(double x, double y);
    double getR();
    double getTheta();
    void setPolar(double r, double theta);
}
```
- 직교좌표계를 사용하는지, 극좌표계를 사용하는지 알 수 없다. 그럼에도 인터페이스는 자료 구조를 명백하게 표현한다.
- getter, setter 로 변수를 다룬다고 클래스가 되지 않는다. 그보다 추상 인터페이스를 제공해 사용자가 구현을 모른 채 자료의 핵심을 조작할 수 있어야 진정한 의미의 클래스다.

### 구체적인 Vehicle 클래스
```java
public interface Vehicle {
    double getFuelTankCapacityInGallons();
    double getGallonsOfGasoline();
}
```
- 자동차 연료 상태를 구체적인 숫자 값으로 알려준다. 두 함수가 변수값을 읽어 반환할 뿐이라는 사실이 확실하다.

### 추상적인 Vehicle 클래스
```java
public interface Vehicle {
    double getPercentFuelRemaining();
}
```
- 자동차 연료 상태를 백분율이라는 추상적인 개념으로 알려준다. 정보가 어디서 오는지 전혀 드러나지 않는다.
- 자료를 세세하게 공개하기보다는 추상적인 개념으로 표현하는 편이 좋다. 인터페이스나 getter, setter 함수만으로는 추상화가 이뤄지지 않는다.

## 자료/객체 비대칭
- 객체는 추상화 뒤로 자료를 숨긴 채 자료를 다루는 함수만 공개한다. 자료 구조는 자료를 그대로 공개하며 별다른 함수는 제공하지 않는다. **두 개념은 사실상 정반대다.**

### 절차적인 도형
```java
public class Square {
    public Point topLeft;
    public double side;
}

public class Circle {
    public Point center;
    public double radius;
}

public class Geometry {
    public double area(Object shape) throws NoSuchShapeException{
        if(shape instanceof Square) {
            ...
        }
        else if(shape instanceof Circle) {
            ...
        }
        else{
            throw new NoSuchShapeException();
        }
    }
}
```
- 클래스가 절차적이라 비판할 수 있다. 그러나 장단점이 있다.
- 장점은 Geometry 클래스에 둘레 길이를 구하는 perimeter() 함수를 추가하고 싶다면 도형 클래스들은 아무 영향도 받지 않는다는 것이다.(도형 클래스에 의존하는 다른 클래스까지도)
- 단점은 새 도형 클래스를 추가하려면, Geometry 클래스에 속한 함수를 모두 고쳐야 한다는 것이다.

### 다형적인 도형
```java
public class Square implements Shape {
    private Point topLeft;
    private double side;

    public double area() {
        return side * side;
    }
}

public class Circle implements Shape {
    private Point center;
    private double radius;
    public final double PI = 3.14;

    public double area() {
        return PI * radius * radius;
    }
}
```

- 객체 지향적인 도형 클래스다. area()는 다형메서드다.
- 장점은 Geometry 클래스는 필요없고, 그러므로 새 도형 클래스를 추가해도 기존 함수에 아무런 영향을 미치지 않는다는 것이다. 
- 단점은 도형 클래스에 새로운 함수를 추가하면 모든 도형 클래스를 고쳐야 한다는 것이다.

### 결론
- 새로운 함수가 아니라 새로운 자료 타입이 필요한 경우에는 클래스와 객체 지향 기법이 가장 적합하다.
- 새로운 자료 타입이 아니라 새로운 함수가 필요한 경우에는 절차적인 코드와 자료 구조가 더 적합하다.
- 분별 있는 프로그래머는 모든 것이 객체라는 생각이 미신임을 잘 안다. 때로는 단순한 자료 구조와 절차적인 코드가 가장 적합한 상황도 있다.

## 디미터 법치
- 디미터 법칙은 잘 알려진 휴리스틱(이론)으로, 모듈은 자신이 조작하는 객체의 속사정을 몰라야 한다는 법칙이다.
- 디미터의 법칙은 클래스 C의 메서드 f는 아래 객체의 메서드만 호출해야한다고 주장한다. 또, 허용된 메서드가 반환하는 객체의 메서드는 호출하면 안된다.(낯선 사람은 경계하고 친구랑만 놀라는 의미)
 - 클래스 C
 - f가 생성한 객체
 - f 인수로 넘어온 객체
 - C 인스턴스 변수에 저장된 객체

### 기차 충돌
아래와 같은 코드를 기차 충돌(train wreck)이라 부른다. 일반적으로 조잡하다 여겨지는 방식이므로 피하는 편이 좋다.
```java
final String outputDir = ctxt.getOptions().getScratchDir().getAbsolutePath();
```

아래와 같이 나누는 편이 좋다.
```java
Options opts = ctxt.getOptions();
File scratchDir = opts.getScratchDir();
final String outputDir = scratchDir.getAbsolutePath();
```

위의 두 예제는 ctxt, Options, ScratchDir이 객체인지 아니면 자료 구조인지에 따라 디미터의 법칙 위반여부를 알 수 있다. 객체라면 내부 구조를 숨겨야 하므로 디미터의 법칙을 위반한다. 자료 구조라면 내부 구조를 노출하므로 디미터 법칙이 적용되지 않는다.

### 잡종 구조
- 때때로 절반은 객체, 절반은 자료 구조인 잡종 구조가 나온다. 잡종 구조는 새로운 함수는 물론이고 새로운 자료 구조도 추가하기 어렵다. 
- 양쪽 세상에서 단점만 모아놓은 구조다. 그러므로 잡종 구조는 되도록 피하자.

### 구조체 감추기
- ctxt, options, scratchDir이 객체라면 기차 충돌 방식을 피해야한다. 객체라면 내부 구조를 감춰야 하니 때문이다.
- ctxt가 객체라면 뭔가를 하라고 말해야지 속을 드러내라고 말하면 안된다.
- 아래와 같은 방법도 좋지 않다. 왜냐하면 ctxt 객체에 공개해야 하는 메서드가 너무 많아지기 때문이다.
```java
ctxt.getAbsolutePathOfScratchDirectoryOption();
```
- 임시 디렉터리의 절대 경로를 얻으려는 이유가 임시 파일을 생성하기 위한 목적이기 때문에, ctxt 객체에 임시 파일을 생성하라고 시키는 것이 낫다.  
ctxt는 내부 구조를 드러내지 않으며, 모듈에서 해당 함수는 자신이 몰라야 하는 여러 객체를 탐색할 필요가 없다. 따라서 디미터 법칙을 위반하지 않는다.
```java
BufferedOutputStream bos = ctxt.createScratchFileStream(classFileName);
```

## 자료 전달 객체
- 자료 구조체의 전형적인 형태는 공개 변수만 있고 함수가 없는 클래스다. 이런 자료 구조체를 때로는 자료 전달 객체(DTO)라 한다.
- 조금 더 일반적인 형태는 Bean 구조다. Bean은 비공개 변수를 getter/setter 함수로 조작한다. 별다른 이익을 제공하지는 않는다.

### 활성 레코드
- DTO의 특수한 형태로, 공개 변수가 있거나 비공개 변수에 getter/setter 함수가 있는 자료 구조지만, save, find와 같은 탐색 함수도 제공한다.
- 활성 레코드는 데이터베이스 테이블이나 다른 소스에서 자료를 직접 변환한 결과다.
- 활성 레코드에 비지니스 규칙 메서드를 추가하지 말자. 활성 레코드는 자료 구조로 취급한다. 비지니스 규칙을 담으면서 내부 자료를 숨기는 객체는 따로 생성한다.




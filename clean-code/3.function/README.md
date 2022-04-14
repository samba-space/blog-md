## 작게 만들어라!
- 함수는 만드는 첫째 규칙은 작게다. 함수를 만드는 둘째 규칙은 더 작게다.
- 가로 150자를 넘어서는 안 된다. 100줄을 넘어서는 안된다. 사실 20줄도 길다.
### 블록과 들여쓰기
- if/else 문, while문 등에 들어가는 블록은 한 줄이어야 한다. 대게 거기서 함수를 호출한다. 그러면 바깥을 감싸는 함수가 작아질 뿐 아니라, 블록 안에서 호출하는 함수 이름을 적절히 짓는다면, 코드를 이해하기도 쉬워진다.
- 중첩 구조가 생길만큼 함수가 커져서는 안된다는 뜻이다. 함수에서 들여쓰기 수준은 1단이나 2단을 넘어서면 안된다.
## 한 가지만 해라!
- 지정된 함수 이름 아래에서 추상화 수준이 하나인 단계만 수행한다면 그 함수는 한가지 작업만 한다.
- 단순히 다른 표현이 아니라 의미 있는 이름으로 다른 함수를 추출할 수 있다면 그 함수는 여러 작업을 하는 것이다.
## 함수 당 추상화 수준은 하나로!
- 함수가 확실히 한가지 작업만 하려면 함수 내 모든 문장의 추상화 수준이 동일해야 한다.
- ```getHtml()```은 추상화 수준이 아주 높다. ```String pagePathName = PathParser.render(pagepath)```는 추상화 수준이 중간이다. ```.append("\n")```와 같은 코드는 추상화 수준이 아주 낮다.
- 한 함수 내에 추상화 수준을 섞으면 코드를 읽는 사람이 헷갈린다. 특정 표현이 근본 개념인지 아니면 세부사항인지 구분하기 어려운 탓이다.
- 위에서 아래로 코드 읽기: 내려가기 규칙
  - 코드는 위에서 아래로 이야기처럼 읽혀야 좋다. 한 함수 다음에는 추상화 수준이 한 단계 낮은 함수가 온다.
## Switch문
- 한가지만 하는 Switch문도 만들기 어렵다. 본질적으로 switch문은 N가지를 처리한다.
- switch 문은 완전히 피할수 없지만, 다형성을 이용해 switch 문을 저차원 클래스에 숨기고 반복하지 않는 방법은 있다.
```java
public Money calculatePay(Employee e) throws InvalidEmployeeType{
  switch(e.type) {
    case COMMISSIONED:
      return calculateCommissionedPay(e);
    case HOURLY:
      return calculateHourlyPay(e);
    case SALARIED:
      return calculateSalariedPay(e);
    default:
      throw new InvalidEmployeeType(e.type);
  }
}
```
- 위의 함수의 문제점
  1. 함수가 길다.
  2. 한가지 작업만 수행하지 않는다.
  3. SRP를 위반한다.
  4. OCP를 위반한다. 새 직원 유형을 추가할 때마다 코드를 변경해야하기 때문이다.
  5. 가장 큰 문제는 위 함수와 구조가 동일한 함수가 무한정 존재한다는 사실이다.
### 해결책
```java
public abstract class Employee {
  public abstract boolean isPayDay();
  public abstract Money calculatePay();
  public abstract void deliverPay(Money pay);
}
```
```java
public interface EmployeeFactory {
  public Employee makeEmployee(EmployeeRecode r) throws InvalidEmployeeType;
}
```
```java
public class EmployeeFactory implements EmployeeFactory {
  public Employee makeEmployee(EmployeeRecode r) throws InvalidEmployeeType {
    switch(r.type) {
      case COMMISSIONED:
        return new CommissionedEmployee(r);
      case HOURLY:
        return new HourlyEmployee(r);
      case SALARIED:
        return new SalariedEmployee(r);
      default:
        throw new InvalidEmployeeType(r.type);
    }
  }
}
```
- 해결책은 switch문을 추상 팩토리에 숨기는 것이다.
- 팩토리는 switch 문을 사용해 적절한 Employee 파생 클래스의 인스턴스를 생성한다. Employee 메서드 호출은 다형성으로 인해 실제 파생 클래스의 메서드가 실행된다.
- 가급적이면 다형적 객체를 생성하는 코드 안에서만 사용하자. 상속관계로 숨긴 후에는 절대로 다른 코드에 노출하지 않는다.
## 서술적인 이름을 사용하라!
- 서술적인 이름을 사용하면 개발자 머릿속에서도 설계가 뚜렷해지므로 코드를 개선하기 쉬워진다.
- 이름이 길어도 괜찮다. 길고 서술적인 이름이 짧고 어려운 이름보다 좋다.
- 함수가 작고 단순할수록 서술적인 이름을 고르기도 쉬워진다.
## 함수 인수
- 함수에서 이상적인 인수 개수는 0개다. 3개는 가능한 피하는 편이 좋다.
- 인수는 개념을 이해하기 어렵게 만든다.
- 코드를 읽는 사람에게는 ```includeSetupPageInto(new PageContent)``` 보다 ```includeSetupPage()```가 이해하기 더 쉽다. ```includeSetupPageInto(new PageContent)```는 함수 이름과 인수 사이에 추상화 수준이 다르다.
- 테스트 관점에서 보면 인수는 더 어렵다. 인수 조합으로 함수를 검증하는 테스트 케이스를 작성해야한다.

## 부수 효과를 일으키지 마라!
## 명령과 조회를 분리하라!
## 오류 코드보다 예외를 사용하라!
## 반복하지 마라!
## 구조적 프로그래밍
## 함수는 어떻게 짜죠?

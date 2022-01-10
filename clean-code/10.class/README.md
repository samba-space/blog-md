# 클래스
깨끗한 클래스에 대해 알아보자.

## 클래스 체계
- 클래스를 정의하는 자바 관례에 따르면, 가장 먼저 변수 목록이 나온다. 이후에 public 메서드가 나오고, private 메서드는 자신을 호출하는 public 메서드 직후에 나온다.
  - 변수의 순서 : public static 상수 -> private static 변수 -> private 인스턴스 변수

### 캡슐화
- 때로는 변수나 유틸리티 함수를 protected로 선언해 테스트 코드에 접근을 허용하기도 한다. 하지만 그전에 비공개 상태를 유지할 온갖 방법을 강구한다. 캡슐화를 풀어주는건 언제나 최후의 수단이기 때문이다.

## 클래스는 작아야 한다.
- 클래스를 만들 때 중요한 규칙은 클래스는 작아야 한다는 것이다.
- 클래스의 크기 척도는 클래스가 맡은 책임이다. SuperDashBoard는 메서드 수가 적음에도 책임이 너무 많다.

```java
public class SuperDashBoard extends JFrame implements MetaDataUser {
  public Component getLastFocusedComponent(){...}
  public void setLastFocused(Component lastFocused){...}
  public int getMajorVersionNumber(){...}
  public int getMinorVersionNumber(){...}
  public int getBuildNumber(){...}
}
```

- 클래스 이름은 해당 클래스 책임을 기술해야 한다. 클래스 이름에 Processor, Manager, Super 등과 같이 모호한 단어가 있다면 클래스가 책임이 많다는 것을 의미한다.
- 클래스 설명은 만일(if), 그리고(and), 하며(or), 하지만(but)을 사용하지 않고서 25단어 내외로 가능해야 한다.
  - SuperDashBoard 설명 : "SuperDashBoard는 마지막으로 포커스를 얻었던 컴포넌트에 접근하는 방법을 제공**하며**, 버전과 빌드 번호를 추적하는 메커니즘을 제공한다."


### 단일 책임 원칙 (SRP : Single Responsibility Principle)
- 클래스나 모듈을 변경할 이유가 단 하나뿐이어야 한다는 원칙이다. SRP는 책임이라는 개념을 정의하며 적절한 클래스 크기를 제시한다. 클래스는 책임, 즉 변경할 이유가 하나여야 한다는 의미다. 책임, 즉 변경할 이유를 파악하려 애쓰다 보면 코드를 추상화하기도 쉬워진다. 더 좋은 추상화가 더 쉽게 떠오른다.
  - SuperDashBoard는 변경할 이유가 두 가지다. 첫째, 소프트웨어 버전 정보를 추적하는데, 버전 정보는 소프트웨어를 출시 할 때마다 달라진다. 둘째, SuperDashBoard는 자바 스윙 컴포넌트를 관리한다. 즉 스윙 코드를 변경할 때마다 버전 번호가 달라진다.
- SRP는 객체 지향 설계에서 더욱 중요한 개념이다. 그럼에도 SRP는 클래스 설계자가 가장 무시하는 규칙 중 하나다.
  - 왜냐하면 우리들 대다수가 프로그램이 돌아가면 일이 끝났다고 여기기 때문이다. 깨끗하고 체계적인 소프트웨어라는 다음 관심사로 전환하지 않는다.
  - 또, 많은 개발자는 자잘한 단일 책임 클래스가 많아지면 큰 그림을 이해하기 어려워진다고 우려한다. 하지만 작은 클래스 여러개와 큰 클래스 몇 개뿐이든 익힐 내용의 양은 비슷하다.
- 규모가 어느 수준에 이르는 시스템은 논리가 많고 복잡하다. 이런 복잡성을 다루려면 체계적인 정리가 필수다.

### 응집도 (Cohesion)
- 클래스는 인스턴스 변수 수가 작아야 한다. 각 클래스 메서드는 클래스 인스턴스 변수를 하나 이상 사용해야 한다. 일반적으로 메서드가 변수를 더 많이 사용할수록 메서드와 클래스는 응집도가 더 높다.
- 우리는 응집도가 높은 클래스를 선호한다. 응집도가 높다는 말은 클래스에 속한 메서드와 변수가 서로 의존하며 논리적인 단위로 묶인다는 의미기 때문이다.
- 몇몇 메서드만이 사용하는 인스턴스 변수가 많아진다는건 새로운 클래스로 쪼개야 한다는 신호다.

### 응집도를 유지하면 작은 클래스 여럿이 나온다.
- 큰 함수를 작은 함수로 나눌 때, 로컬 변수를 새 함수의 인수로 넘기지 말고, 클래스 인스턴스 변수로 승격하자. 그러면 새 함수는 인수가 필요없고, 그만큼 함수를 쪼개기 쉬워진다.
- 로컬 변수를 클래스 인스턴스 변수로 승격할 경우, 클래스가 응집력을 잃는다. 몇몇 함수만 사용하는 인스턴스 변수가 늘기 때문이다. 이럴 때, 몇몇 함수와 인스턴스 변수를 독자적인 클래스로 분리하자.
  큰 함수를 작은 함수로 쪼개다 보면 종종 작은 클래스 여럿으로 쪼갤 기회가 생긴다. 그러면서 프로그램에 점점 체계가 잡히고 구조가 투명해진다.
- 이렇게 리팩터링 시에 가장 먼저, 원래 프로그램의 정확한 동작을 검증하는 테스트 슈트를 작성한다. 그 다음, 한번에 하나씩 수 차례에 걸쳐 조금씩 코드를 변경했다. 코드를 변경할 때마다 테스트를 수행해 원래 프로그램과 동일하게 동작하는지 확인한다.

## 변경하기 쉬운 클래스
- 깨끗한 시스템은 클래스를 체계적으로 정리해 변경에 수반하는 위험을 낮춘다.

<br>

```java
public class Sql {
  public String create(){...}
  public String insert(Object[] fields){...}
  public String selectAll(){...}
  private String selectWithCriteria(String criteria){...}
  ...
}
```

- Sql 클래스는 변경할 이유가 두 가지이므로 SRP를 위반한다.
  1. 새로운 SQL 문을 지원하려면 Sql 클래스를 수정해야 한다.
  2. 기존 SQL 문 하나를 수정할 때도 반드시 Sql 클래스를 수정해야 한다.
- 구조적으로도 SRP를 위반한다. 클래스 일부에서만 사용되는 비공개 메서드는 코드를 개선할 잠재적인 여지를 시사한다. (selectWithCriteria)

<br>

```java
abstract public class Sql {
  abstract public String generate();
}

public class CreateSql extends Sql {
  @Override
  public String generate(){...}
}

public class SelectSql extends Sql {
  @Override
  public String generate(){...}
}
...
```
- 기존 메서드들을 Sql 클래스에서 파생하는 클래스로 만들었다. 새로운 SQL문을 추가할 때 UpdateSql 클래스를 만들어 Sql를 상속받기만 하면된다. 즉 기존 클래스를 변경할 필요가 없다. 또 기존 SQL 문 하나를 수정해도 하나의 클래스의 메서드만 수정하면 된다. 즉 SPR을 지킨다.
- OCP(Open-Closed-Principle)도 지킨다. OCP란 클래스는 확장에 개방적이고 수정에 폐쇄적이어야 한다는 원칙이다. Sql 클래스는 새 기능에 개방적이면서 동시에 다른 클래스를 닫아놓는 방식으로 수정에 폐쇄적이다.
- 새 기능을 수정하거나 기존 기능을 변경할 때 건드릴 코드가 최소인 시스템 구조가 바람직하다. 이상적인 시스템이라면 새 기능을 추가할 때 시스템을 확장할 뿐 기존 코드를 변경하지는 않는다.

### 변경으로부터 격리
- 상세한 구현에 의존하는 클라이언트 클래스는 구현이 바뀌면 위험에 빠진다. 그러므로 인터페이스와 추상 클래스를 사용해 구현이 미치는 영향을 차단한다.
- 상세한 구현에 의존하는 코드는 테스트가 어렵다. 만약 상세 구현 코드가 동적으로 응답 값이 바뀌거나 할 수 있기 때문이다.이럴 경우 인터페이스를 생성한 후 메서드 하나를 선언한다. 고정 응답 값을 주는 테스트용 클래스를 만들어 해당 인터페이스를 구현한다.
- 테스트 가능할 정도로 시스템의 결합도를 낮추면 유연성과 재사용성도 높아진다. 결합도가 낮다는 소리는 각 시스템 요소가 다른 요소로부터 그리고 변경으로부터 잘 격리되어 있다는 의미다. 시스템 요소가 서로 잘 격리되어 있으면 각 요소를 이해하기도 더 쉬워진다.
- 결합도를 최소로 줄이면 자연스럽게 DIP(Dependency Inversion Principle)를 따르는 클래스가 나온다. DIP는 클래스가 상세한 구현이 아니라 추상화에 의존해야 한다는 원칙이다.
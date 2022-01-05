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
  public Component getLastFocusedComponent()...
  public void setLastFocused(Component lastFocused)...
  public int getMajorVersionNumber()...
  public int getMinorVersionNumber()...
  public int getBuildNumber()...
}
```

- 클래스 이름은 해당 클래스 책임을 기술해야 한다. 클래스 이름에 Processor, Manager, Super 등과 같이 모호한 단어가 있다면 클래스가 책임이 많다는 것을 의미한다.
- 클래스 설명은 만일(if), 그리고(and), 하며(or), 하지만(but)을 사용하지 않고서 25단어 내외로 가능해야 한다.
  - SuperDashBoard 설명 : "SuperDashBoard는 마지막으로 포커스를 얻었던 컴포넌트에 접근하는 방법을 제공**하며**, 버전과 빌드 번호를 추적하는 메커니즘을 제공한다."

### 단일 책임 원칙 (SRP : Single Responsibility Principle)

# 단위 테스트
애자일과 TDD 덕택에 단위 테스트를 자동화하는 프로그래머들이 이미 많아졌으며 점점 더 늘어나는 추세다. 하지만 우리 분야에 테스트를 추가하려고 급하게 서두르는 와중에 많은 프로그래머들이 제대로 된 테스트 케이스를 작성해야 한다는 좀 더 미묘한 사실을 놓쳐버렸다.

## TDD 법칙 세 가지
1. 실패하는 단위 테스트를 작성할 때까지 실제 코드를 작성하지 않는다.
2. 컴파일은 실패하지 않으면서 실행이 실패하는 정도로만 단위 테스트를 작성한다.
3. 현재 실패하는 테스트를 통과할 정도로만 실제 코드를 작성한다.

이렇게 일하면 실제 코드를 사실상 전부 테스트하는 테스트 케이스가 나온다. 그러나 실제 코드와 맞먹을 정도로 방대한 테스트 코드는 심각한 관리 문제를 유발하기도 한다.

## 깨끗한 테스트 코드 유지하기
- 지저분한 테스트 코드는 테스트를 안하는 것만 못하다. 왜냐하면, 실제 코드가 진화하면 테스트 코드도 변해야하는데, 테스트 코드가 지저분할수록 변경하기 어려워지기 때문이다.
- 테스트 슈트가 없으면 개발자는 자신이 수정한 코드가 제대로 도는지 확인할 방법이 없다. 테스트 슈트가 없으면 시스템 이쪽을 수정해도 저쪽이 안전하다는 사실을 검증하지 못한다.
- 테스트 코드는 실제 코드 못지 않게 중요하다. 그러므로 실제 코드 못지 않게 깨끗하게 짜야 한다.

### 테스트는 유연성, 유지보수성, 재사용성을 제공한다.
- 테스트 케이스가 있으면 변경이 두렵지 않다. 즉, 변경이 쉬워진다. 테스트 케이스가 없으면 모든 변경이 잠정적인 버그다.
- 테스트 코드가 지저분하면 코드를 변경하는 능력이 떨어지며 코드 구조를 개선하는 능력도 떨어진다. 또, 실제 코드도 지저분해진다.

## 깨끗한 테스트 코드
- 깨끗한 테스트 코드를 만들때 가장 중요한건 가독성이다. 가독성은 실제 코드보다 테스트 코드에 더욱 중요하다. 테스트 코드에 가독성을 높이려면 명료성, 단순성, 풍부한 표현력이 필요하다.
- BUILD-OPERATE-CHECK 패턴이 좀 더 깨끗하고 이해하기 쉬운 테스트 코드를 만든다.
  - BUILD : 테스트 자료를 만든다.
  - OPERATE : 테스트 자료를 조작한다.
  - CHECK : 조작한 결과가 올바른지 확인한다.
- 테스트 코드는 잡다하고 세세한 코드보다 진짜 필요한 자료 유형과 함수만 사용한다.

### 이중 표준
- 실제 환경과 테스트 환경은 요구사항이 판이하게 다르다.
- 예로 String을 + 연산으로 이어붙이는 것과 StringBuffer를 쓰는 것인데, 임베디드 시스템인 애플리케이션은 컴퓨터 자원과 메모리가 제한적일 가능성이 높다. 그러므로 신경써야하지만, 테스트 환경은 자원이 제한적일 가능성이 낮다.
- 이것이 이중 표준의 본질이다. 실제 환경에서는 절대로 안되지만 테스트 환경에서는 전혀 문제없는 방식이 있다. 대개 메모리, CPU 효율과 관련 있는 경우다. 코드의 깨끗함과는 철저히 무관하다.

## 테스트 당 assert 하나
- assert 문이 단 하나인 함수는 결론이 하나라서 코드를 이해하기 쉽고 빠르다.
- 테스트를 분리하면 중복되는 코드가 많아진다. TEMPLATE METHOD 패턴을 쓰거나, 독자적인 테스트 클래스를 만들어 @Before 함수에 given/when 부분을 넣어도 된다. 그러나 모두 배보다 배꼽이라 차라리 assert를 여러개 쓰는게 낫다.
- 가장 좋은 규칙은 assert문은 최대한 줄이며, 테스트 함수 하나는 개념 하나만 테스트하는 것이다.

## F.I.R.S.T
깨끗한 테스트는 다음 다섯가지 규칙을 따른다.
- Fast(빠르게) : 테스트는 빨라야 한다. 테스트는 자주 돌려야하기 때문이다.
- Independent(독립적으로) : 각 테스트는 서로 의존하면 안된다. 각 테스트는 독립적으로 그리고 어떤 순서로 실행해도 괜찮아야 한다. 의존하면 하나가 실패할 때 나머지도 잇달아 실패하므로 원인을 진단하기 어렵고 후반 테스트가 찾아내야 할 결함이 숨겨진다.
- Repeatable(반복가능하게) : 테스트는 어떤 환경에서도 반복 가능해야 한다.(네트워크가 없는 환경에서도) 테스트가 돌아가지 않는 환경이 하나라도 있다면 테스트가 실패한 이유를 둘러댈 변경이 생긴다.
- Self-Validating(자가검증하는) : 테스트는 bool 값으로 결과를 내야 한다. 통과 여부를 알려고 로그 파일을 읽게 만들어서는 안되기 때문이다.
- Timely(적시에) : 테스트는 적시에 작성해야 한다. 실제 코드를 구현한 다음에 테스트 코드를 만들면 실제 코드가 테스트하기 어렵다는 사실을 발견할지도 모른다.

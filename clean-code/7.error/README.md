# 오류 처리
오류 처리는 중요하다. 하지만 오류 처리 코드로 인해 프로그램 논리를 이해하기 어려워진다면 깨끗한 코드라 부르기 어렵다.

## 오류 코드보다 예외를 사용하라
### 오류 코드
```java
public class DeviceController {
    public void sendShutDown() {
        DeviceHandle handle = getHandle(DEV1);
        if (handle != DeviceHandle.INVALID) {
            retrieveDeviceRecord(handle);
            if (record.getStatus() != DEVICE_SUSPENDED) {
                pauseDevice(handle);
                clearDeviceWorkQueue(handle);
                closeDevice(handle);
            } else {
                logger.log("Device suspended. Unable to shut down");
            }
        } else {
            logger.log("Invalid handle for: " + DEV1.toString());
        }
    }
}
```
- 호출자 코드가 복잡해진다. 함수를 호출한 즉시 오류를 확인해야 하기 때문이다. 불행히도 이 단계는 잊어버리기 쉽다. 
- 오류가 발생하면 예외를 던지는 편이 낫다. 그러면 논리가 오류 처리 코드와 섞이지 않으므로, 호출자 코드가 더 깔끔해진다.


### 예외
```java
public class DeviceController {
    public void sendShutDown() {
        try {
            tryToShutDown();
        } catch (DeviceShutDownError e) {
            logger.log(e);
        }
    }

    private void tryToShutDown() throws DeviceShutDownError {
        DeviceHandle handle = getHandle(DEV1);
        retrieveDeviceRecord(handle);

        pauseDevice(handle);
        clearDeviceWorkQueue(handle);
        closeDevice(handle);
    }

    private DeviceHandler getHandle(DeviceID id) {
        ...
        throw new DeviceShutDownError("Invalid handle for: " + id.toString());
        ...
    }
}
```
- 단순히 코드가 보기만 좋아지지 않았다. 코드 품질도 나아졌다. 디바이스를 종료하는 알고리즘과 오류를 처리하는 알고리즘을 분리했기 때문이다.

## Try-Catch-Finally 문부터 작성하라
- 어떤면에서 try블록은 트랜잭션과 비슷하다. try 블록에서 무슨 일이 생기든 catch 블록은 프로그램 상태를 일관성 있게 유지해야 한다. 그러므로 예외가 발생할 코드를 짤 때는 try-catch-finally 문으로 시작하는 편이 낫다.
- 먼저 강제로 예외를 일으키는 테스트 케이스를 작성한 후 테스트를 통과하게 코드를 작성하는 방법을 권장한다. 그러면 자연스럽게 try 블록의 트랜잭션 범위부터 구현하게 되므로 범위 내에서 트랜잭션 본질을 유지하기 쉬워진다. 
- 파일이 없으면 예외를 던지는 경우. 아래 처럼 작성하며, 나머지 논리는 FileInputStream 생성 코드와 close 사이에 넣는다.(나머지 논리는 오류나 예외가 전혀 발생하지 않는다고 가정)
```java
public List<RecordedGrip> retrieveSection(String sectionName) {
    try {
        FileInputStream stream = new FileInputStream(sectionName);
        stream.close();
    } catch (FileNotFoundException e) {
        throw new StorageException("retrieval error", e);
    }
    return new ArrayList<RecordedGrip>();
}
```

## unchecked 예외를 사용하라
- checked exception은 OCP(Open Closed Principle)를 위반한다. 메서드에서 checked exception을 던졌는데 catch 블록이 세 단계 위에 있다면 그 사이 모든 메서드의 선언부에 해당 exception을 정의해야 한다. 
즉, 하위 단계에서 코드를 변경하면 상위 단계 메서드 선언부를 전부 고쳐야 한다는 말이다. 모든 메서드가 최하위 메서드에서 던지는 예외를 알아야 하므로 캡슐화가 깨진다.
- 아주 중요한 라이브러리를 작성한다면 모든 예외를 잡아야 하므로 checked exception도 유용하다. 일반적인 애플리케이션은 의존성이라는 비용이 이익보다 더 크다.

## 예외에 의미를 제공하라
- 예외를 던질 때는 전후 상황을 충분히 덧붙인다. 그러면 오류가 발생한 원인과 위치를 찾기 쉬워진다.
- 오류 메시지에 정보를 담아 예외와 함께 던진다. 실패한 연산 이름과 실패 유형도 언급한다.

## 호출자를 고려해 예외 클래스를 정의하라
- 오류를 분류하는 방법은 수없이 많다. 오류가 발생한 위치(오류가 발생한 컴포넌트), 유형(디바이스 실패, 네트워크 실패)으로도 가능하다.
- 하지만 애플리케이션에서 오류를 정의할 때 프로그래머에게 가장 중요한 관심사는 오류를 잡아내는 방법이 되어야 한다.

### 오류를 형편없이 분류한 사례
```java
ACMEPort port = new ACMEPort(12);
try {
    port.open();
} catch (DeviceResponseException e) {
    reportPortError(e);
    logger.log("DeviceResponseException", e);
} catch (ATM1212UnlockedException e) {
    reportPortError(e);
    logger.log("ATM1212UnlockedException", e);
} catch (GMXError e) {
    reportPortError(e);
    logger.log("GMXError", e);
}
```
- 중복이 심하지만, 대다수 상황에서 우리가 오류를 처리하는 방식은 비교적 일정하다. (1. 오류를 기록하고 2. 프로그램을 계속 수행해도 좋은지 확인한다.)
- 위 경우 예외에 대응하는 방식이 예외 유형과 무관하게 거의 동일하다. 그래서 코드를 간결하게 고치기 쉽다.

```java
LocalPort port = new LocalPort(12);
try {
    port.open();
} catch (PortDeviceFailure e) {
    reportPortError(e);
    logger.log(e.getMessage(), e);
}
public class LocalPort {
    private ACMEPort innerPort;

    public LocalPort(int portNumber) {
        innerPort = new ACMEPort(portNumber);
    }

    public void open() {
        try {
            innerPort.open();
        } catch (DeviceResponseException e) {
            throw new PortDeviceFailure(e);
        } catch (ATM1212UnlockedException e) {
            throw new PortDeviceFailure(e);
        } catch (GMXError e) {
            throw new PortDeviceFailure(e);
        }
    }
}
```
- 호출하는 라이브러리 API를 감싸면서 예외 유형 하나를 반환하면 된다.
- LocalPort 클래스는 단순히 ACMEPort 클래스가 던지는 예외를 잡아 변환하는 Wrapper 클래스일 뿐이다.
- LocalPort 클래스같은 Wrapper 클래스는 매우 유용하다. 실제로 외부 API를 사용할 때는 Wrapper 기법이 최선이다. 
  - 외부 API를 감싸면 외부 라이브러리와 프로그램 사이에서 의존성이 크게 줄어든다. 
  - 나중에 다른 라이브러리로 교체해도 비용이 적다. 
  - 또한 Wrapper 클래스에서 외부 API를 호출하는 대신 테스트 코드를 넣어주는 방법으로 프로그램을 테스트하기도 쉬워진다.
  - Wrapper 기법을 사용하면 특정 업체가 API를 설계한 방식에 발목이 잡히지 않는다.

## 정상 흐름을 정의하라
```java
try {
    MealExpenses expenses = expenseReportDAO.getMeals(employee.getID());
    m_total += expenses.getTotal();
} catch(MealExpensesNotFound e) {
    m_total += getMealPerDiem();
}
```
- 식비를 비용으로 청구했다면 직원이 청구한 식비를 총계에 더한다. 식비를 비용으로 청구하지 않았다면 일일 기본 식비를 총계에 더하는 코드이다.
- 그런데 예외가 논리를 따라가기 어렵게 만든다. 특수 상황을 처리할 필요가 없다면 코드가 훨씬 더 간결해질 것이다.

```java
MealExpenses expenses = expenseReportDAO.getMeals(employee.getID());
m_total += expenses.getTotal();

public class PerDiemMealExpense implements MealExpenses {
    public int getTotal() {
        // 기본 값으로 일일 기본 식비를 반환한다.
    }
}
```
- ExpenseReportDAO를 고쳐 언제나 MealExpense 객체를 반환한다. 청구한 식비가 없다면 일일 기본 식비를 반환하는 MealExpense 객체를 반환한다.
- 이를 특수 사례 패턴(Special case pattern)이라고 부른다. 클래스를 만들거나 객체를 조작해 특수 사례를 처리하는 방식이다. 
- 클래스나 객체가 예외적인 상황을 캡슐화해서 처리하므로 클라이언트 코드가 예외적인 상황을 처리할 필요가 없어진다. 

## null을 반환하지 마라
```java
public void test(Item item) {
    if(item != null) {
        ItemReader reader = repository.getItemReader();
        if(reader != null) {
            ...
        }
    }
}
```
- 오류를 유발하는 행위 중 첫째가 null을 반환하는 습관이다.
- null을 반환하는 코드는 일거리를 늘릴 뿐만 아니라 호출자에게 문제를 떠넘긴다. null check를 빼먹는다면 애플리케이션이 통제 불능에 빠질지도 모른다.
- null check 누락도 문제이지만, null check가 너무 많은 것도 문제다. 메서드에서 null을 반환하는 대신 예외를 던지거나 특수 사례 객체를 반환한다.(많은 경우 특수 사례 객체가 손쉬운 해결책이다.)

```java
List<Employee> employees = getEmployees();
if (employees != null) {
    for(Employee e : employees) {
        totalPay += e.getPay();
    }
}
```

getEmployees가 null 대신 빈 리스트를 반환하게 수정하면 아래와 같다.(Collections.emptyList() 사용)

```java
List<Employee> employees = getEmployees();
for(Employee e : employees) {
    totalPay += e.getPay();
}
```
- 코드도 깔끔해질뿐 아니라 NullPointerException이 발생할 가능성도 줄어든다.

## null을 전달하지 마라
- null을 반환하는 방식도 나쁘지만, 메서드로 null을 전달하는 방식은 더 나쁘다. 정상적인 인수로 null을 기대하는 API가 아니라면 메서드로 null을 전달하는 코드는 최대한 피한다.
- null check을 하여 새로운 예외 유형으로 던지는 방법(결국 새로운 예외 유형을 잡아내는 handler가 필요함), assert를 사용하는 방법이 있지만 null을 전달하면 여전히 실행 오류가 발생한다.
- 대다수 프로그래밍 언어는 호출자가 실수로 넘기는 null을 적절히 처리하는 방법이 없다. 애초에 null을 넘기지 못하도록 금지하는 정책이 합리적이다.
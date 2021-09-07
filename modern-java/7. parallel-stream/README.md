# 병렬 스트림(Parallel Stream)
병렬 스트림이란 각각의 스레드에서 처리할 수 있도록 스트림 요소를 여러 청크로 분할한 스트림이다.  
병렬 스트림을 이용하면 모든 멀티코어 프로세서가 각각의 청크를 처리하도록 할당할 수 있다.

병렬 스트림은 내부적으로 ForkJoinPool을 사용한다. 
아래처럼 병렬 처리할 스레드 수를 설정할 수 있지만, 일반적으로 기기의 프로세서 수와 같으므로 ForkJoinPool의 기본값을 쓰는 것을 권장한다.

``` java
System.setProperty(“java.util.concurrent.ForkJoinPool.common.parallelism”, “3”);
```

## 병렬 스트림으로 변환하기
기본적으로 컬렉션에 parallelStream을 호출하면 병렬 스트림이 생성된다.  
또 순차 스트림에서 parallel 메서드를 호출하면 병렬 스트림으로 바꿀수 있다.(스트림 자체는 아무 변화가 없고, 병렬 실행 플래그 값 설정)  
반대로 병렬 스트림에서 sequential 메서드로 순차 스트림으로 바꿀수 있다.
## 스트림 성능 측정
성능을 최적화할 때는 **측정**이 제일 중요하다. JMH(Java Microbenchmark Harness) 라이브러리를 이용해 벤치 마크를 구현해보자.  

```java
@Benchmark
public long sequentialSum() {
    return Stream.iterate(1L, i -> i + 1).limit(N)
            .reduce(0L, Long::sum);
}

@Benchmark
public long parallelSum() {
    return Stream.iterate(1L, i -> i + 1).limit(N)
            .parallel()
            .reduce(0L, Long::sum);
}

@Benchmark
public long iterativeSum() {
    long result = 0;
    for (long i = 0; i <= N; i++) {
        result += i;
    }
    return result;
}
```

각 메서드는 1에서 N까지의 합계를 구한다. 

```log
Benchmark           Mode  Cnt    Score   Error  Units
Test.iterativeSum   avgt   10    7.222 ± 0.292  ms/op
Test.parallelSum    avgt   10  118.203 ± 5.829  ms/op
Test.sequentialSum  avgt   10  122.620 ± 5.328  ms/op
```
벤치마크 결과를 보면 for문을 이용한 기본 반복이 가장 빨랐다.  
병렬 스트림은 순차 스트림과 차이가 거의 존재하지 않았고, for문에 비해 10배 넘게 느렸다.(책에서는 병렬 스트림이 순차 스트림보다 5배 정도 느렸다.)

그 이유는 iterate는 박싱된 객체가 만들어지므로 더하기 연산 시 언박싱을 해야한다.  
또, iterate는 본질적으로 순차적이다. 이전 연산의 결과에 따라 다음 함수의 입력이 달라지기 때문에 iterate 연산을 청크로 분할하기가 어렵다.  
병렬 처리하여도, 순차처리와 다를게 없고 스레드를 할당하는 오버헤드만 증가하게 된다.

### 해결책
해결책은 특화된 메서드를 사용하면 된다. 앞의 경우 LongStream.rangeClosed를 사용하면 된다.
LongStream.rangeClosed는 기본형 long을 직접 사용하므로 박싱, 언박싱 오버헤드가 사라진다.  
또 쉽게 청크로 분할할 수 있는 숫자 범위를 생산한다.

```java
@Benchmark
public long rangedSum() {
    return LongStream.rangeClosed(1, N)
            .reduce(0L, Long::sum);
}

@Benchmark
public long parallelRangedSum() {
    return LongStream.rangeClosed(1, N)
            .parallel()
            .reduce(0L, Long::sum);
}
```

기존 iterate에서 rangeClosed로 수정하여 실행하면, 아래와 같은 결과가 나온다.  
순차 스트림의 성능이 훨씬 좋아진 것을 확인할 수 있다.  
상황에 따라서 어떤 알고리즘을 병렬화하는 것보다 적절한 자료구조를 선택하는 것이 더 중요하다는 것을 알 수 있다.  

병렬 스트림의 경우 for문을 이용한 경우보다 더 빨라진 것을 볼 수 있다.  
올바른 자료구조를 선택해야 병렬 실행도 최적의 성능을 발휘할 수 있다.

```log
Benchmark               Mode  Cnt  Score   Error  Units
Test.parallelRangedSum  avgt   10  0.869 ± 0.134  ms/op
Test.rangedSum          avgt   10  5.306 ± 0.246  ms/op
```

## 병렬 스트림 사용 팁
- 멀티코어 간의 데이터 이동의 비용은 비싸다. 멀티코어 간에 데이터 전송 시간보다 훨씬 오래 걸리는 작업만 병렬로 수행하는것이 바람직하다.
- 확신이 서지 않으면 직접 측정하자. 적절한 벤치마크로 직접 성능을 측정하는 것이 바람직하다.
- 박싱을 주의하자. auto 박싱, 언박싱은 성능을 크게 저하시킬 수 있다.(되도록이면 IntStream과 같은 기본형 특화 스트림 사용)
- 순차 스트림보다 병렬 스트림에서 성능이 떨어지는 연산을 주의하자.
    - limit, findFirst는 요소 순서에 의존하는 연산이며, 병렬 스트림에서 수행하려면 비싼 비용이 든다.  
    병렬 스트림에서 요소 순서와 상관없는 findAny가 findFirst보다 성능이 좋다.  
    limit은 요소의 순서가 상관없다면 정렬된 스트림에 unordered를 호출하여 비정렬된 스트림을 얻어 호출하는 것이 효과적이다.
- 스트림을 수행하는 전체 파이프라인 연산 비용을 고려하자. 하나의 요소를 처리하는 비용이 높아진다면 병렬 스트림으로 성능 개선할 수 있는  
가능성이 있음을 의미한다.
- 소량의 데이터에서는 병렬 스트림이 도움이 되지 않는다. 병렬화 과정에서 생기는 비용을 상쇄시킬 만큼 이득을 얻지 못하기 때문이다.
- 스트림을 구성하는 자료구조가 적절한지 확인하자. LinkedList를 분할하려면 모든 요소를 탐색해야 하지만 ArrayList는 요소를 탐색하지 않고도  
리스트를 분할할 수 있다. 아래는 분해성에 따라 자료구조를 나열하였다.
    - 분해성 좋음 : ArrayList, IntStream.range, HashSet, TreeSet
    - 분해성 나쁨 : LinkedList, Stream.iterate
- 스트림의 특성과 파이프라인의 중간 연산이 스트림의 특성을 어떻게 바꾸는지에 따라 분해 과정의 성능이 달라질 수 있다.  
SIZED 스트림은 같은 크기의 두 스트림으로 분할할 수 있으므로 효과적으로 스트림을 병렬 처리할 수 있다.  
반면 필터 연산이 있으면, 길이를 예측할 수 없으므로 효과적으로 병렬 처리할 수 있을지 알 수 없다.
- 최종 연산의 병합 과정 비용을 살펴보자. 병합 과정 비용이 비싸면 병렬 스트림으로 얻은 성능의 이익이 상쇄될 수 있다.
- 공유된 상태를 바꾸는 알고리즘에서 병렬 스트림을 사용하지 말자. 다수의 스레드가 동시에 데이터에 접근하여 data race 문제가 일어난다.
package io.hhplus.tdd.point.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;

@SpringBootTest
public class PointUnitTest {
	
	private static final Logger log = LoggerFactory.getLogger(PointUnitTest.class);
	
	private static int MAX_THREAD = 3000;
	
	@Autowired
	private PointService pointService;
	
	@Test
	@DisplayName("[환경에 따른 추가 테스트] user1 정보를 확인하는 동작에 대한 테스트")
	void confirmUser1FromUserPoint() {
		
		/*
		 * given
		 * - 테스트에 사용할 변수 및 입력값을 정의한다.
		 * - 동작을 확인하기 위한 Mokito 정의도 포함(Database(Repository)의 객체를 Mokito화하여 사용)
		 * */
		long expectedPoint = 100L;
		
		/*
		 * when
		 * - 실제 동작이 이루어진다.
		 * - 동작에 따른 상태 변화를 기억하거나, 대조군으로 활용하기 위한 과정이다.
		 * - 검증 대상의 동작 하나만 기술한다.
		 * */
		long actualPoint = UserPoint.user1().point();
		
		/*
		 * Then
		 * - 최종적으로 테스트를 검증한다.
		 * - 테스트 과정을 종합한다.
		 * */
		assertEquals(expectedPoint, actualPoint);
	}
	
	@Test
	@DisplayName("[정책 검증] 충전하려는 포인트가 최대 포인트 액수(10000)를 초과하였을때 예외 동작에 대한 테스트")
	void throwExceptionWhenChargingPointIsOverMAXPoint() {
		
		/*
		 * given
		 * - 테스트에 사용할 변수 및 입력값을 정의한다.
		 * - 동작을 확인하기 위한 Mokito 정의도 포함(Database(Repository)의 객체를 Mokito화하여 사용)
		 * */
		long pointOverMaxPoint = 15000L;
		
		/*
		 * when
		 * - 실제 동작이 이루어진다.
		 * - 동작에 따른 상태 변화를 기억하거나, 대조군으로 활용하기 위한 과정이다.
		 * - 검증 대상의 동작 하나만 기술한다.
		 * */
		
		/*
		 * Then
		 * - 최종적으로 테스트를 검증한다.
		 * - 테스트 과정을 종합한다.
		 * */
		Assertions.assertThrows(Exception.class, ()->{
			UserPoint.user(1L, pointOverMaxPoint);
		});
	}
	
	@Test
	@DisplayName("[정책 검증] 사용하려는 포인트가 최소 포인트 액수(0) 미만일때 예외 동작에 대한 테스트")
	void throwExceptionWhenUsingPointIsUnderMINPoint() {
		
		/*
		 * given
		 * - 테스트에 사용할 변수 및 입력값을 정의한다.
		 * - 동작을 확인하기 위한 Mokito 정의도 포함(Database(Repository)의 객체를 Mokito화하여 사용)
		 * */
		long pointUnderMinPoint = -50L;
		
		/*
		 * when
		 * - 실제 동작이 이루어진다.
		 * - 동작에 따른 상태 변화를 기억하거나, 대조군으로 활용하기 위한 과정이다.
		 * - 검증 대상의 동작 하나만 기술한다.
		 * */
		
		/*
		 * Then
		 * - 최종적으로 테스트를 검증한다.
		 * - 테스트 과정을 종합한다.
		 * */
		Assertions.assertThrows(Exception.class, ()->{
			UserPoint.user(1L, pointUnderMinPoint);
		});
	}
	
	@Test
	@DisplayName("[메소드 synchronized(pointService 블록화를 통한 고유락) 동시성 검증] 5명의 user1 스레드가 5포인트씩 충전할때, 최종적으로 125포인트를 충전하는 동작에 대한 테스트")
	void case1IsSynchronizedOfChargedUser1PointWhen5ThreadCalledChargingService() throws InterruptedException {
		
		/*
		 * given
		 * - 테스트에 사용할 변수 및 입력값을 정의한다.
		 * - 동작을 확인하기 위한 Mokito 정의도 포함(Database(Repository)의 객체를 Mokito화하여 사용)
		 * */
		long userId = 1L;
		long expectedPoint = 3100L;
		long chargePoint = 1L;
		long expectedSuccessCount = 3000L;
		
		//동시성 테스트를 위한 executorService, atomic 변수 초기화
		CountDownLatch doneSignal = new CountDownLatch(MAX_THREAD);
	    ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD);
	    AtomicInteger successCount = new AtomicInteger();
		
		/*
		 * when
		 * - 실제 동작이 이루어진다.
		 * - 동작에 따른 상태 변화를 기억하거나, 대조군으로 활용하기 위한 과정이다.
		 * - 검증 대상의 동작 하나만 기술한다.
		 * - 모든 멀티 커스텀 스레드의 동작 완료를 보장하기 위해 join을 활용한다.
		 * */
		//전체 실행시간 확인을 위함
		long startTime = System.nanoTime();
		
		pointService.initPoint();
		
		for (int i = 0; i < MAX_THREAD; i++) {
            executorService.execute(() -> {
                try {
                	//서비스 동작에 대한 확인
                    successCount.getAndIncrement();
                    pointService.syncCharge1(userId, chargePoint);
                } catch(Exception e){
                	
                }finally {
                	//Thread 실행 횟수 확인
                    doneSignal.countDown();
                }
            });
        }
		
		//모든 스레드 동작을 완료할 때까지 대기
        doneSignal.await();
        
        //동시성 테스트 종료
        executorService.shutdown();
		long actualPoint = pointService.getPoint();
		
		//전체 실행시간 확인을 위함
		long endTime = System.nanoTime();
		
		//메소드 실행시간
		log.info("case 1 전체 실행 시간 : {}", String.valueOf((endTime-startTime)/10000L));
				
		/*
		 * Then
		 * - 최종적으로 테스트를 검증한다.
		 * - 테스트 과정을 종합한다.
		 * */
		assertEquals(expectedPoint, actualPoint);
		assertEquals(expectedSuccessCount, successCount.longValue());
	}
	
	@Test
	@DisplayName("[서비스 비즈니스 로직 내 충전하는 부분(블록)을 synchronized 처리하여 동시성 검증] 5명의 user1 스레드가 5포인트씩 충전할때, 최종적으로 125포인트를 충전하는 동작에 대한 테스트")
	void case2IsSynchronizedOfChargedUser1PointWhen5ThreadCalledChargingService() throws InterruptedException {
		
		/*
		 * given
		 * - 테스트에 사용할 변수 및 입력값을 정의한다.
		 * - 동작을 확인하기 위한 Mokito 정의도 포함(Database(Repository)의 객체를 Mokito화하여 사용)
		 * */
		long userId = 1L;
		long expectedPoint = 3100L;
		long chargePoint = 1L;
		long expectedSuccessCount = 3000L;
		
		//동시성 테스트를 위한 executorService, atomic 변수 초기화
		CountDownLatch doneSignal = new CountDownLatch(MAX_THREAD);
	    ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD);
	    AtomicInteger successCount = new AtomicInteger();
		
		/*
		 * when
		 * - 실제 동작이 이루어진다.
		 * - 동작에 따른 상태 변화를 기억하거나, 대조군으로 활용하기 위한 과정이다.
		 * - 검증 대상의 동작 하나만 기술한다.
		 * - 모든 멀티 커스텀 스레드의 동작 완료를 보장하기 위해 join을 활용한다.
		 * */
		//전체 실행시간 확인을 위함
		long startTime = System.nanoTime();
	
		pointService.initPoint();
		
		for (int i = 0; i < MAX_THREAD; i++) {
            executorService.execute(() -> {
                try {
                	//서비스 동작에 대한 확인
                    successCount.getAndIncrement();
                    pointService.syncCharge2(userId, chargePoint);
                } catch(Exception e){
                	
                }finally {
                	//Thread 실행 횟수 확인
                    doneSignal.countDown();
                }
            });
        }
		
		//모든 스레드 동작을 완료할 때까지 대기
        doneSignal.await();
        
        //동시성 테스트 종료
        executorService.shutdown();
		long actualPoint = pointService.getPoint();
        
		//전체 실행시간 확인을 위함
		long endTime = System.nanoTime();
		
		//메소드 실행시간
		log.info("case 2 전체 실행 시간 : {}", String.valueOf((endTime-startTime)/10000L));
				
		/*
		 * Then
		 * - 최종적으로 테스트를 검증한다.
		 * - 테스트 과정을 종합한다.
		 * */
		assertEquals(expectedPoint, actualPoint);
		assertEquals(expectedSuccessCount, successCount.longValue());
	}
	
	@Test
	@DisplayName("[concurrentHashMap의 동기화 자료구조 특성을 이용한 동시성 검증] 5명의 user1 스레드가 5포인트씩 충전할때, 최종적으로 125포인트를 충전하는 동작에 대한 테스트")
	void case3IsSynchronizedOfChargedUser1PointWhen5ThreadCalledChargingService() throws InterruptedException {
		
		/*
		 * given
		 * - 테스트에 사용할 변수 및 입력값을 정의한다.
		 * - 동작을 확인하기 위한 Mokito 정의도 포함(Database(Repository)의 객체를 Mokito화하여 사용)
		 * */
		long userId = 1L;
		long expectedPoint = 3100L;
		long chargePoint = 1L;
		long expectedSuccessCount = 3000L;
		
		//동시성 테스트를 위한 executorService, atomic 변수 초기화
		CountDownLatch doneSignal = new CountDownLatch(MAX_THREAD);
	    ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD);
	    AtomicInteger successCount = new AtomicInteger();
			    
		/*
		 * when
		 * - 실제 동작이 이루어진다.
		 * - 동작에 따른 상태 변화를 기억하거나, 대조군으로 활용하기 위한 과정이다.
		 * - 검증 대상의 동작 하나만 기술한다.
		 * - 모든 멀티 커스텀 스레드의 동작 완료를 보장하기 위해 join을 활용한다.
		 * */
		//전체 실행시간 확인을 위함
		long startTime = System.nanoTime();

		pointService.initPoint();
		
		for (int i = 0; i < MAX_THREAD; i++) {
            executorService.execute(() -> {
                try {
                	//서비스 동작에 대한 확인
                    successCount.getAndIncrement();
                   pointService.syncCharge3(userId, chargePoint);
                } catch(Exception e){
                	
                }finally {
                	//Thread 실행 횟수 확인
                    doneSignal.countDown();
                }
            });
        }
		
		//모든 스레드 동작을 완료할 때까지 대기
        doneSignal.await();
        
        //동시성 테스트 종료
        executorService.shutdown();
		long actualPoint = pointService.getPointOfConcurrentHashMap(userId);
		
		//전체 실행시간 확인을 위함
		long endTime = System.nanoTime();
		
		//메소드 실행시간
		log.info("case 3 전체 실행 시간 : {}", String.valueOf((endTime-startTime)/10000L));
				
		/*
		 * Then
		 * - 최종적으로 테스트를 검증한다.
		 * - 테스트 과정을 종합한다.
		 * */
		assertEquals(expectedPoint, actualPoint);
		assertEquals(expectedSuccessCount, successCount.longValue());
	}
	
	@Test
	@DisplayName("[ReentrantLock을 활용하여 스레드 락을 적용한 동시성 검증	] 3000명의 스레드가 1포인트씩 충전할때, 최종적으로 3100포인트를 충전하는 동작에 대한 테스트")
	void case4IsSynchronizedOfChargedUser1PointWhen5ThreadCalledChargingService() throws InterruptedException, ExecutionException {
		
		/*
		 * given
		 * - 테스트에 사용할 변수 및 입력값을 정의한다.
		 * - 동작을 확인하기 위한 Mokito 정의도 포함(Database(Repository)의 객체를 Mokito화하여 사용)
		 * */
		long userId = 1L;
		long expectedPoint = 3100L;
		long chargePoint = 1L;
		long expectedSuccessCount = 3000L;
		
		//동시성 테스트를 위한 executorService, atomic 변수 초기화
		CountDownLatch doneSignal = new CountDownLatch(MAX_THREAD);
	    ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD);
	    AtomicInteger successCount = new AtomicInteger();
	    
		/*
		 * when
		 * - 실제 동작이 이루어진다.
		 * - 동작에 따른 상태 변화를 기억하거나, 대조군으로 활용하기 위한 과정이다.
		 * - 검증 대상의 동작 하나만 기술한다.
		 * - executorService를 통해 멀티 스레드 환경 구성
		 * */
		//전체 실행시간 확인을 위함
		long startTime = System.nanoTime();
		
		pointService.initPoint();
		
		for (int i = 0; i < MAX_THREAD; i++) {
            executorService.execute(() -> {
                try {
                	//서비스 동작에 대한 확인
                    successCount.getAndIncrement();
                    pointService.syncCharge4(userId, chargePoint);
                } catch(Exception e){
                	log.info(e.getMessage());
                }finally {
                	//Thread 실행 횟수 확인
                    doneSignal.countDown();
                }
            });
        }
		
		//모든 스레드 동작을 완료할 때까지 대기
        doneSignal.await();
        
        //동시성 테스트 종료
        executorService.shutdown();
		long actualPoint = pointService.getPoint();
        
		//전체 실행시간 확인을 위함
		long endTime = System.nanoTime();
		
		//메소드 실행시간
		log.info("case 4 전체 실행 시간 : {}", String.valueOf((endTime-startTime)/10000L));
				
		/*
		 * Then
		 * - 최종적으로 테스트를 검증한다.
		 * - 테스트 과정을 종합한다.
		 * */
		assertEquals(expectedPoint, actualPoint);
		assertEquals(expectedSuccessCount, successCount.longValue());
	}
}

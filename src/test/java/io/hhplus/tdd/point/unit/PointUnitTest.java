package io.hhplus.tdd.point.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.hhplus.tdd.point.UserPoint;

@SpringBootTest
public class PointUnitTest {
	
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
}

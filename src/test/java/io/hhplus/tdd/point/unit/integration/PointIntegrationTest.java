package io.hhplus.tdd.point.unit.integration;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;

@SpringBootTest
public class PointIntegrationTest {
	
	@Autowired
	PointService pointService;
	
	@Test
	@DisplayName("[조회API 동작 검증] user1에 대한 포인트 정보를 조회하는 동작에 대한 테스트")
	void selectPointOfUser1() {
		/*
		 * given
		 * - 테스트에 사용할 변수 및 입력값을 정의한다.
		 * - 동작을 확인하기 위한 Mokito 정의도 포함(Database(Repository)의 객체를 Mokito화하여 사용)
		 * */
		long user1Id = 1L;
		long expectedPoint = 100L;
		
		/*
		 * when
		 * - 실제 동작이 이루어진다.
		 * - 동작에 따른 상태 변화를 기억하거나, 대조군으로 활용하기 위한 과정이다.
		 * - 검증 대상의 동작 하나만 기술한다.
		 * */
		long actualPoint = pointService.point(user1Id).point();
		
		/*
		 * Then
		 * - 최종적으로 테스트를 검증한다.
		 * - 테스트 과정을 종합한다.
		 * */
		assertEquals(expectedPoint, actualPoint);
	}
	
	@Test
	@DisplayName("[조회API 동작 검증] 존재하지 않는 사용자에 대한 dummy 포인트 정보(0)를 조회하는 동작에 대한 테스트")
	void selectDummyPointOfDummyUser() {

		/*
		 * given
		 * - 테스트에 사용할 변수 및 입력값을 정의한다.
		 * - 동작을 확인하기 위한 Mokito 정의도 포함(Database(Repository)의 객체를 Mokito화하여 사용)
		 * */
		long dummyUserId = 15L;
		long expectedPoint = 0L;
		
		/*
		 * when
		 * - 실제 동작이 이루어진다.
		 * - 동작에 따른 상태 변화를 기억하거나, 대조군으로 활용하기 위한 과정이다.
		 * - 검증 대상의 동작 하나만 기술한다.
		 * */
		long actualPoint = pointService.point(dummyUserId).point();
		
		/*
		 * Then
		 * - 최종적으로 테스트를 검증한다.
		 * - 테스트 과정을 종합한다.
		 * */
		assertEquals(expectedPoint, actualPoint);
	}
	
	@Test
	@DisplayName("[충전API 동작 검증] 충전금액을 매개변수로 전달받아 user1의 포인트 충전, 이를 user1 사용자 정보에 반영하는 동작에 대한 테스트")
	void chargePointToUser() throws Exception {
		/*
		 * given
		 * - 테스트에 사용할 변수 및 입력값을 정의한다.
		 * - 동작을 확인하기 위한 Mokito 정의도 포함(Database(Repository)의 객체를 Mokito화하여 사용)
		 * */
		long user1Id = 1L;
		long expectedPoint = 250L;
		long chargePoint = 150L;
		
		/*
		 * when
		 * - 실제 동작이 이루어진다.
		 * - 동작에 따른 상태 변화를 기억하거나, 대조군으로 활용하기 위한 과정이다.
		 * - 검증 대상의 동작 하나만 기술한다.
		 * */
		long actualPoint = pointService.charge(user1Id, chargePoint).point();
		
		/*
		 * Then
		 * - 최종적으로 테스트를 검증한다.
		 * - 테스트 과정을 종합한다.
		 * */
		assertEquals(expectedPoint, actualPoint);

	}
	
	@Test
	@DisplayName("[충전API 동작 검증] 사용자 정보가 존재하지 않을 경우, 신규 사용자 정보를 추가 및 포인트가 충전된 정보가 반영되는 동작에 대한 테스트")
	void chargePointToDummyUserAndThrowException() throws Exception {
		/*
		 * given
		 * - 테스트에 사용할 변수 및 입력값을 정의한다.
		 * - 동작을 확인하기 위한 Mokito 정의도 포함(Database(Repository)의 객체를 Mokito화하여 사용)
		 * */
		long dummyUserId = 10L;
		long chargePoint = 150L;
		
		/*
		 * when
		 * - 실제 동작이 이루어진다.
		 * - 동작에 따른 상태 변화를 기억하거나, 대조군으로 활용하기 위한 과정이다.
		 * - 검증 대상의 동작 하나만 기술한다.
		 * */
		long actualPoint = pointService.charge(dummyUserId, chargePoint).point();
		long actualUserId = pointService.charge(dummyUserId, chargePoint).id();
		
		/*
		 * Then
		 * - 최종적으로 테스트를 검증한다.
		 * - 테스트 과정을 종합한다.
		 * */
		assertEquals(dummyUserId, actualUserId);
		assertEquals(chargePoint, actualPoint);
	}
	
	@Test
	@DisplayName("[사용API 동작 검증] 사용금액을 매개변수로 전달받아 user1의 포인트 사용, 이를 user1 사용자 정보에 반영하는 동작에 대한 테스트")
	void usePointToUser() throws Exception {
		/*
		 * given
		 * - 테스트에 사용할 변수 및 입력값을 정의한다.
		 * - 동작을 확인하기 위한 Mokito 정의도 포함(Database(Repository)의 객체를 Mokito화하여 사용)
		 * */
		long user1Id = 1L;
		long expectedPoint = 50L;
		long usePoint = 50L;
		
		/*
		 * when
		 * - 실제 동작이 이루어진다.
		 * - 동작에 따른 상태 변화를 기억하거나, 대조군으로 활용하기 위한 과정이다.
		 * - 검증 대상의 동작 하나만 기술한다.
		 * */
		long actualPoint = pointService.use(user1Id, usePoint).point();
		
		/*
		 * Then
		 * - 최종적으로 테스트를 검증한다.
		 * - 테스트 과정을 종합한다.
		 * */
		assertEquals(expectedPoint, actualPoint);
		
	}
	
	@Test
	@DisplayName("[사용API 동작 검증] 사용자 정보가 존재하지 않을 경우, 포인트 사용이 불가하여 예외처리 하는 동작에 대한 테스트")
	void usePointToDummyUserAndThrowException() {
		/*
		 * given
		 * - 테스트에 사용할 변수 및 입력값을 정의한다.
		 * - 동작을 확인하기 위한 Mokito 정의도 포함(Database(Repository)의 객체를 Mokito화하여 사용)
		 * */
		long dummyUserId = 30L;
		long usePoint = 150L;
		
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
			pointService.use(dummyUserId, usePoint);
		});
	}
	
	@Test
	@DisplayName("[사용API 동작 검증] 사용자 정보가 존재하면서, 사용 후 포인트 잔액이 0보다 작을 경우 예외처리 하는 동작에 대한 테스트")
	void usePointOfUserAndUnderMINPointAndThrowException() throws Exception {
		/*
		 * given
		 * - 테스트에 사용할 변수 및 입력값을 정의한다.
		 * - 동작을 확인하기 위한 Mokito 정의도 포함(Database(Repository)의 객체를 Mokito화하여 사용)
		 * */
		long userId = 1L;
		long usePoint = 300L;
		
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
			pointService.use(userId, usePoint);
		});
	}
	
}

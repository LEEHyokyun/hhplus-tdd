package io.hhplus.tdd.point;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.lock.ThreadLock;

@Service
public class PointService {
	
	private static final Logger log = LoggerFactory.getLogger(PointService.class);
	
	private static final ConcurrentHashMap<Long , Long> concurrentHashMap = new ConcurrentHashMap<>();
	
	/*
	 * static 포인트 변수를 테스트 내부에 구성하려고 의도하였으나
	 * 동기화 범위 내 연산이 이루어질 수 있도록 서비스 내에 위치
	 * */
	private static long POINT = 100L;
	
	@Autowired
	UserPointTable userPointTable;
	
	@Autowired
	PointHistoryTable pointHistoryTable;
	
	//동시성 제어 테스트 검증용
	
	public UserPoint point(long id) {
		return userPointTable.selectById(id);
	}
	
	public UserPoint charge(long id, long amount) throws Exception {
		long beforePoint = userPointTable.selectById(id).point();
		
		//UserPoint의 포인트 유효성 검증
		UserPoint parameters = UserPoint.user(id, amount);
				
		//beforePoint가 0보다 크다면 기존 유저가 존재한다는 의미
		if(beforePoint > PointRule.NOT_EXIST_USER_POINT) {
			amount = parameters.point() + beforePoint;
		}
		
		//포인트 내역 기록
		pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
		
		return userPointTable.insertOrUpdate(parameters.id(), amount);
	}
	
	public UserPoint use(long id, long amount) throws Exception {
		
		long beforePoint = userPointTable.selectById(id).point();
		long afterPoint = beforePoint - amount;
		
		//UserPoint의 포인트 유효성 검증
		UserPoint parameters = UserPoint.user(id, amount);
				
		//beforePoint가 0이라면 유저 정보가 존재하지 않는다는 의미
		if(beforePoint == PointRule.NOT_EXIST_USER_POINT) {
			throw new Exception();
		}
		
		//beforePoint > 0이지만(기존 유저 정보가 존재) 사용 후의 포인트 잔액이 0보다 작을 경우
		if(beforePoint > 0 &&  afterPoint < PointRule.MIN_POINT) {
			throw new Exception();
		}
		
		//포인트 내역 기록
		pointHistoryTable.insert(id, afterPoint, TransactionType.USE, System.currentTimeMillis());
				
		return userPointTable.insertOrUpdate(parameters.id(), afterPoint);
	}
	
	/*
	 * 동시성 테스트 검증을 위한 서비스
	 * 나-1 : 충전 서비스 전체를 synchronized 적용한다.
	 * */
	public synchronized long syncCharge1(long id, long amount) {	
		//메소드 실행시간 확인을 위함
		long startTime = System.nanoTime();
		
		//static field를 통해 결과 누적
		POINT = POINT + amount;
		
		//메소드 실행시간 확인을 위함
		long endTime = System.nanoTime();
				
		//메소드 실행시간
		log.info("case 1 실행 시간 : {}", String.valueOf((endTime-startTime)/10L));
		return POINT;
	}
	
	/*
	 * 동시성 테스트 검증을 위한 서비스
	 * 나-2 : 충전 서비스의 충전 로직 블록을 synchronized 적용한다.
	 * */
	public long syncCharge2(long id, long amount) {	
		//메소드 실행시간 확인을 위함
		long startTime = System.nanoTime();
		//(*검증로직반영) static field를 통해 결과 누적
		synchronized (this) {
			POINT = POINT + amount;
		}
		
		//메소드 실행시간 확인을 위함
		long endTime = System.nanoTime();
				
		//메소드 실행시간
		log.info("case 2 실행 시간 : {}", String.valueOf((endTime-startTime)/10L));
		return POINT;
	}
	
	/*
	 * 동시성 테스트 검증을 위한 서비스
	 * 나-3 : concurrentHashMap의 동기화 자료구조의 특징을 활용한다.
	 * */
	public long syncCharge3(long id, long amount) {	
		//메소드 실행시간 확인을 위함
		long startTime = System.nanoTime();
		
		/*
		 * cocurrentHashMap이 보장해주는 동기화 처리 함수를 충전 연산에 활용한다.
		 * 값이 있으면 누적
		 * 값이 없으면 POINT + amount
		 * */
		if(concurrentHashMap.containsKey(id))
			concurrentHashMap.put(id, concurrentHashMap.get(id) + amount);
		else
			concurrentHashMap.put(id, POINT + amount);
		
		//메소드 실행시간 확인을 위함
		long endTime = System.nanoTime();
				
		//메소드 실행시간
		log.info("case 3 실행 시간 : {}", String.valueOf((endTime-startTime)/10L));
		return concurrentHashMap.get(id);
	}
	
	/*
	 * 동시성 테스트 검증을 위한 서비스
	 * 나-4 : ReentranceLock을 활용하여 스레드 락 동기화를 적용한다.
	 * */
	public UserPoint syncCharge4(long id, long amount) throws InterruptedException {	
		//메소드 실행시간 확인을 위함
		long startTime = System.nanoTime();
		
		/*
		 * atomic charge를 위해 전역 chargeLock을 lock한다.
		 * POINT 연산을 완료한 이후 chargeLock을 unlock한다.
		 * */
		ThreadLock.chargeLock.lock();
		POINT = POINT + amount;
		ThreadLock.chargeLock.unlock();
		
		//메소드 실행시간 확인을 위함
		long endTime = System.nanoTime();
				
		//메소드 실행시간
		log.info("case 4 실행 시간 : {}", String.valueOf((endTime-startTime)/10L));
		return userPointTable.insertOrUpdate(id, amount);
	}
	
	/*
	 * 동시성 테스트 검증을 위한 서비스
	 * concurrentHashMap의 포인트 값을 반환한다.
	 * */
	public long getPointOfConcurrentHashMap(long id) {
		return concurrentHashMap.get(id);
	}
	
	/*
	 * 동시성 테스트 검증을 위한 서비스
	 * concurrentHashMap의 포인트 값을 반환한다.
	 * */
	public long getPoint() {
		return POINT;
	}
	
	public void initPoint() {
		POINT = 100L;
	}
	
	/*
	 * 동시성 테스트 검증을 위한 서비스
	 * concurrentHashMap을 초기화한다.
	 * */
	public void initConcurrentHashMap() {
		concurrentHashMap.clear();
	}
	
	public List<PointHistory> history(long id){
		return pointHistoryTable.selectAllByUserId(id);
	}
}

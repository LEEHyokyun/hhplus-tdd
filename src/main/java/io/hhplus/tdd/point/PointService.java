package io.hhplus.tdd.point;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;

@Service
public class PointService {
	
	private static final Logger log = LoggerFactory.getLogger(PointService.class);
	
	@Autowired
	UserPointTable userPointTable;
	
	@Autowired
	PointHistoryTable pointHistoryTable;
	
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
	
	public List<PointHistory> history(long id){
		return pointHistoryTable.selectAllByUserId(id);
	}
}

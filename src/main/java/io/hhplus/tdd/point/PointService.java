package io.hhplus.tdd.point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hhplus.tdd.database.UserPointTable;

@Service
public class PointService {
	
	private static final Logger log = LoggerFactory.getLogger(PointService.class);
	
	@Autowired
	UserPointTable userPointTable;
	
	public UserPoint point(long id) {
		return userPointTable.selectById(id);
	}
	
	public UserPoint charge(long id, long amount) throws Exception {
		long beforePoint = userPointTable.selectById(id).point();
		
		//beforePoint가 0보다 크다면 기존 유저가 존재한다는 의미
		if(beforePoint > PointRule.NOT_EXIST_USER_POINT) {
			amount = amount + beforePoint;
		}
		
		UserPoint parameters = UserPoint.user(id, amount);
		return userPointTable.insertOrUpdate(parameters.id(), parameters.point());
	}
	
	public UserPoint use(long id, long amount) throws Exception {
		long beforePoint = userPointTable.selectById(id).point();
		
		//beforePoint가 0이라면 유저 정보가 존재하지 않는다는 의미
		if(beforePoint == PointRule.NOT_EXIST_USER_POINT) {
			log.info(String.valueOf("여기1"));
			throw new Exception();
		}
		
		//beforePoint > 0이지만(기존 유저 정보가 존재) 사용 후의 포인트 잔액이 0보다 작을 경우
		if(beforePoint > 0 && beforePoint - amount < PointRule.MIN_POINT) {
			log.info(String.valueOf("여기2"));
			throw new Exception();
		}
		
		UserPoint parameters = UserPoint.user(id, amount);
		return userPointTable.insertOrUpdate(parameters.id(), UserPoint.user1().point() - amount);
	}
}

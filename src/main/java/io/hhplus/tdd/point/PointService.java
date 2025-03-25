package io.hhplus.tdd.point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hhplus.tdd.database.UserPointTable;

@Service
public class PointService {
	
	@Autowired
	UserPointTable userPointTable;
	
	public UserPoint point(long id) {
		return userPointTable.selectById(id);
	}
	
	public UserPoint charge(long id, long amount) {
		long beforePoint = userPointTable.selectById(id).point();
		
		//beforePoint가 0보다 크다면 기존 유저가 존재한다는 의미
		if(beforePoint > PointRule.NOT_EXIST_USER_POINT) {
			amount = amount + beforePoint;
		}
		
		return userPointTable.insertOrUpdate(id, amount);
	}
}

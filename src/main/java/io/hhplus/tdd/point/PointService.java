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
}

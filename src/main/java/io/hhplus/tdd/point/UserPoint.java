package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
	
	//유저정보가 존재하지 않을 때 사용하는 dummy 정보(point는 0) 및 point가 없는 사용자에 대한 정보
    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
    
    //user1 사용자에 대한 정보
    public static UserPoint user1() {
    	return new UserPoint(1L, 100L, System.currentTimeMillis());
    }
    
    //포인트 유효성 검증을 포함한 user 사용자에 대한 정보
    public static UserPoint user(long id, long point) throws Exception {
    	if(point < PointRule.MIN_POINT || point > PointRule.MAX_POINT)
    		throw new Exception();
    	
    	return new UserPoint(id, point, System.currentTimeMillis());
    }
}

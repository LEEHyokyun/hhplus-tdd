package io.hhplus.tdd.database;

import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 해당 Table 클래스는 변경하지 않고 공개된 API 만을 사용해 데이터를 제어합니다.
 */
@Component
public class UserPointTable {

    private final Map<Long, UserPoint> table = new HashMap<>();
    
    private static UserPoint user1 = UserPoint.user1();
    
    public UserPoint selectById(Long id) {
    	//user1에 대한 정보를 table db에 생성(최초 생성)
    	table.put(1L, UserPoint.user1());
    	
        throttle(200);
        return table.getOrDefault(id, UserPoint.empty(id));
    }

    public UserPoint insertOrUpdate(long id, long amount) {
    	//생성 혹은 수정된 유저정보가 db에 반영
        throttle(300);
        UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
        table.put(id, userPoint);
        return userPoint;
    }
    
    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }
    
}

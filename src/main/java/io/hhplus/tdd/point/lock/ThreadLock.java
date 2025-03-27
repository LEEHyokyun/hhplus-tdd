package io.hhplus.tdd.point.lock;

import java.util.concurrent.locks.ReentrantLock;

public class ThreadLock {
	//charge lock을 static화 하여 charge lock을 사용하는 모든 스레드의 전역적 적용을 가능하도록 함
	public final static ReentrantLock chargeLock = new ReentrantLock();
}

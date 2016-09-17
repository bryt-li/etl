package cn.jdworks.etl.backend.biz;

import java.util.UUID;

public class ExecutorStat {
	
	private static final int HEARTBEAT_INTERVAL = 5000;

	private int ttl = 0;

	
	private UUID id;
	public UUID getId(){
		return this.id;
	}
	
	public ExecutorStat(UUID id){
		this.id = id;
		resetTTL();
	}
	
	public void resetTTL(){
		ttl = HEARTBEAT_INTERVAL * 3;
	}
	
	public void tickTTL(int tick){
		ttl -= tick;
	}
	
	public boolean isAlive(){
		return ttl>0;
	}
}

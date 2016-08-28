package cn.jdworks.etl.executor.biz;

import java.util.UUID;

public class TaskManager {
	private UUID uuid;

	public void init(UUID uuid){
		this.uuid = uuid;
	}
	
	public void shutdown(){
		
	}
}

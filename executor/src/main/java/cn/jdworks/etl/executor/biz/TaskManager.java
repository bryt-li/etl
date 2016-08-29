package cn.jdworks.etl.executor.biz;

import java.util.UUID;

import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(singleton = true)
public class TaskManager {
	
	private UUID uuid;

	public void startManager(UUID uuid){
		this.uuid = uuid;
	}
	
	public void shutdown(){
		
	}
}

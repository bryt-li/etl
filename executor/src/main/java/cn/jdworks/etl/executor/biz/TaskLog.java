package cn.jdworks.etl.executor.biz;

import java.io.Serializable;

public class TaskLog implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6340710846213494721L;
	
	public TaskLog(int id, long ts, String type, String message) {
		this.id = id;
		this.ts = ts;
		this.type = type;
		this.message = message;
	}

	public int id;
	public long ts;
	public String type;
	public String message;
}

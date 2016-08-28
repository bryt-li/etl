package cn.jdworks.etl.backend.app.biz;

import java.util.Hashtable;

public class ExecutorStat {
	//thread number of this executor
	@SuppressWarnings("unused")
	private int threadNum;
	public synchronized void downThreadNum() {
		this.threadNum--;
	}
	public synchronized void upThreadNum() {
		this.threadNum++;
	}
	
	//db connection of this executor
	private Hashtable<String,Integer> dbConns;
	public synchronized void downDbConnNum(String db) {
		Integer i = this.dbConns.get(db);
		if(i==null)
			this.dbConns.put(db, 0);
		else
			this.dbConns.put(db, i--);
	}
	public synchronized void upDbConnNum(String db) {
		Integer i = this.dbConns.get(db);
		if(i==null)
			this.dbConns.put(db, 0);
		else
			this.dbConns.put(db, i++);
	}
	
	public ExecutorStat(){
		this.threadNum = 0;
		this.dbConns = new Hashtable<String,Integer>();
	}
}

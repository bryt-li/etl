package cn.jdworks.etl.executor.biz;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

public class TaskRuntime extends Thread {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TaskRuntime.class);

	private App app;
	private Process taskProc;

	private int taskId;
	private String command;

	public TaskRuntime(App app, int id, String cmd) {
		this.app = app;
		this.taskId = id;
		this.command = cmd;
	}

	public String runTask() {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command(command.split(" "));
			this.taskProc = pb.start();
			this.start();
			return null;
		} catch (Throwable t) {
			t.printStackTrace();
			return t.getMessage();
		}
	}

	public void abortTask() {
		this.taskProc.destroyForcibly();
		try {
			this.join();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	//todo：监听标准输出
	public void run() {
		try {
			InputStream inputStream = this.taskProc.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				long ts = (new Date()).getTime();
				String[] ss = line.split(":");
				if(ss.length==1)
					this.app.addLogsToJmsSendingQueue(ss[0], this.taskId, ts, null);
				else if(ss.length==2){
					this.app.addLogsToJmsSendingQueue(ss[0], this.taskId, ts, ss[1]);
				}else{
					LOG.error("error task log format: "+ line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			long ts = (new Date()).getTime();
			this.app.addLogsToJmsSendingQueue("END",this.taskId, ts, "NORMALLY EXIT.");
		}
	}
}

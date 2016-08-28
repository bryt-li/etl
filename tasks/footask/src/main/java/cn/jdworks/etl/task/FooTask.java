package cn.jdworks.etl.task;

public class FooTask {

	private static void info(String log){
		System.out.println("INFO:"+log);
	}
	private static void warn(String log){
		System.out.println("WARN:"+log);
	}
	private static void error(String log){
		System.out.println("ERROR:"+log);
	}
	private static void fatal(String log){
		System.out.println("FATAL:"+log);
	}
	private static void debug(String log){
		System.out.println("DEBUG:"+log);
	}
	private static void connect(String db){
		System.out.println("CONNECT:"+db);
	}
	private static void disconnect(String db){
		System.out.println("DISCONNECT:"+db);
	}
	private static void threadCreated(){
		System.out.println("THREAD++");
	}
	private static void threadDestroyed(){
		System.out.println("THREAD--");
	}
	
	public static void main(String[] args) {

		info("task starts with args.");
		for (int i = 0; i < args.length; i++)
			debug("task arg: "+args[i]);
		
		createThread();
		threadCreated();
		threadDestroyed();
		
		connect("foo");
		disconnect("foo");
		
		
		
		warn("warn log test.");
		error("error log test.");
		fatal("fatal log test.");
	}
}

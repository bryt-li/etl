package cn.jdworks.etl.executor;

import java.util.UUID;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import cn.jdworks.etl.executor.biz.FtpTaskSynchronizer;
import cn.jdworks.etl.executor.biz.HeartbeatSender;
import cn.jdworks.etl.executor.biz.LogsReporter;
import cn.jdworks.etl.executor.biz.TaskManager;

@IocBean
public class EntrySetup implements Setup {
	private final Log LOG = Logs.getLog(this.getClass());

	private final UUID uuid = UUID.randomUUID();

	@Inject
	private TaskManager taskManager;

	@Inject
	private FtpTaskSynchronizer ftpTaskSynchronizer;

	@Inject
	private HeartbeatSender heartbeatSender;

	@Inject
	private LogsReporter logsReporter;

	@Inject("java:$config.get('serverAddr')")
	private String serverAddr;

	@Inject("java:$config.get('tasksDir')")
	private String tasksDir;
	
	public void init(NutConfig conf) {
		try {
			this.heartbeatSender.startSender(uuid, serverAddr, tasksDir);
			this.taskManager.startManager();
			this.ftpTaskSynchronizer.startSynchronizer();
			this.logsReporter.startReporter();
		} catch (Exception e) {
			LOG.fatal(e);
			//throw runtime exception to stop the webapp
			//but this behavior depends on container
			throw new RuntimeException();
		}
	}

	public void destroy(NutConfig conf) {

		try {
			this.heartbeatSender.shutdown();
			this.taskManager.shutdown();
			this.ftpTaskSynchronizer.shutdown();
			this.logsReporter.shutdown();
		} catch (Exception e) {
			LOG.fatal(e);
			throw new RuntimeException();
		}
	}
}

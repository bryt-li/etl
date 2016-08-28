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
public class MainSetup implements Setup {
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

	public void init(NutConfig conf) {
		try {
			this.taskManager.init(uuid);
			this.ftpTaskSynchronizer.init();
			this.heartbeatSender.init(uuid);
			this.logsReporter.init(uuid);
		} catch (Exception e) {
			LOG.fatal(e);
			throw new RuntimeException();
		}
	}

	public void destroy(NutConfig conf) {
		try {
			this.taskManager.shutdown();
			this.ftpTaskSynchronizer.shutdown();
			this.heartbeatSender.shutdown();
			this.logsReporter.shutdown();
		} catch (Exception e) {
			LOG.fatal(e);
			throw new RuntimeException();
		}
	}

}

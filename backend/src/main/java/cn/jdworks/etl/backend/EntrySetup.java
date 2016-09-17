package cn.jdworks.etl.backend;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import cn.jdworks.etl.backend.biz.ExecutorManager;
import cn.jdworks.etl.backend.biz.TimeTaskScheduler;

@IocBean
public class EntrySetup implements Setup {
	private final Log LOG = Logs.getLog(this.getClass());

	@Inject
	private ExecutorManager executorManager;

	@Inject
	private TimeTaskScheduler timeTaskScheduler;

	public void init(NutConfig conf) {
		Ioc ioc = conf.getIoc();
		Dao dao = ioc.get(Dao.class);
		
		Daos.createTablesInPackage(dao, "cn.jdworks.etl.backend.app.bean", true);

		// start exe ftp dir sync server
		try {
			
			// start executors manager
			this.executorManager.startManager();

			// start timetask scheduler
			this.timeTaskScheduler.startScheduler();
			
		} catch (Exception e) {
			LOG.fatal(e);
			// Throw runtime exception to prevent webapp starting
			// NOTE: this feature is dependent on webapp container
			throw new RuntimeException();
		}
	}

	public void destroy(NutConfig conf) {
		this.timeTaskScheduler.shutdown();
		this.executorManager.shutdown();
	}

}

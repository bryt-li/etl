package cn.jdworks.etl.backend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;

import cn.jdworks.etl.backend.app.biz.ExecutorManager;

public class ExecutorManagerTest {

	private ExecutorManager executorManager;
	private Dao dao;

//	@Before
	public void init() throws Exception {
		Ioc ioc = new NutIoc(new ComboIocLoader("*js", "conf/ioc/dao.js", "conf/ioc/biz.js", "*anno", "cn.jdworks.etl.backend"));
		this.dao = ioc.get(Dao.class, "dao");
		this.executorManager = ioc.get(ExecutorManager.class);
		this.executorManager.setDao(this.dao);
		this.executorManager.startExecutorManager();
	}

//	@Test(timeout = 20000)
	public void testExecute() throws InterruptedException {
		Thread.sleep(3000);
		for (int i = 0; i < 100; i++) {
			this.executorManager.executeTask(i, "TIME", "test task"+i);
		}
	}

//	@After
	public void destroy() {
	}

}

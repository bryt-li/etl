package cn.jdworks.etl.backend.biz;


import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(singleton = true)
public class ExeDirSyncFtpServer {

	private final Log LOG = Logs.getLog(this.getClass());
	
	@Inject("java:$config.getInt('ftpPort')")
	private int FTP_PORT;

	@Inject("java:$config.get('ftpHome')")
	private String FTP_HOME;

	private FtpServer server;

	public void startServer() throws FtpException {
		FtpServerFactory serverFactory = new FtpServerFactory();
		
		// set port
		ListenerFactory factory = new ListenerFactory();
		factory.setPort(FTP_PORT);
		serverFactory.addListener("default", factory.createListener());
		
		// allow anonymous
		ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
		connectionConfigFactory.setAnonymousLoginEnabled(true);
		serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());
		BaseUser user = new BaseUser();
		user.setName("anonymous");

		// set home directory
		user.setHomeDirectory(FTP_HOME);

		// start FTP server
		serverFactory.getUserManager().save(user);
		server = serverFactory.createServer();
		server.start();
		
		LOG.info("Exe Sync FTP server started.");
	}

	public void shutdown() {
		server.stop();
	}

}

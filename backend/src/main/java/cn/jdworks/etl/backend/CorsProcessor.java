package cn.jdworks.etl.backend;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.AbstractProcessor;

@IocBean(singleton=false)
public class CorsProcessor extends AbstractProcessor {

	private final Log LOG = Logs.getLog(this.getClass());

	public CorsProcessor(){
		LOG.debug("CORS Processor enabled.");
	}
	
    public void process(ActionContext ac) throws Throwable{
        HttpServletRequest req = ac.getRequest();
        HttpServletResponse res = ac.getResponse();

        //LOG.debugf("[%-4s]URI=%s", req.getMethod(), req.getRequestURI());

        res.setHeader("Access-Control-Allow-Headers", req.getHeader("Access-Control-Request-Headers"));
        res.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        res.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Max-Age", "60");
        String method = req.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return;
        }
        doNext(ac);
    }

}
package cn.jdworks.etl.executor.module;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;

@IocBean
@At("/exe")
@Ok("json")
@Fail("http:500")
public class ExeModule {

	@At
	public String foo(){
		return "FOO";
	}
	
	@At
	@POST
	public boolean task(String cmd) {
		return true;
	}

}

package cn.jdworks.etl.backend.module;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.view.HttpStatusView;
import org.nutz.mvc.view.RawView;
import org.nutz.mvc.view.ViewWrapper;

public abstract class BaseModule {
	
	/** 注入与属性同名的一个ioc对象 */
	@Inject
	protected Dao dao;

	@Inject("java:$conf.get('website.urlbase')")
	protected String urlbase;
	
	protected void saveMe(int id){
		Mvcs.getHttpSession(true).setAttribute("me", id);
	}
	
	protected Integer getMe(){
		return (Integer)Mvcs.getHttpSession(true).getAttribute("me");
	}
	
	protected String redirectToLoginPage(){
		Mvcs.getHttpSession(true).setAttribute("savedUrl", Mvcs.getReq().getRequestURI());
		return "redirect:/login";
	}
		
	protected String getSavedUrl(){
		return (String)Mvcs.getHttpSession(true).getAttribute("savedUrl");
	}
	
	protected QueryResult query(Class<?> klass, Condition cnd, Pager pager, String regex) {
		if (pager != null && pager.getPageNumber() < 1) {
			pager.setPageNumber(1);
		}
		List<?> roles = dao.query(klass, cnd, pager);
		dao.fetchLinks(roles, null);
	    if (pager != null ) {
	    	pager.setRecordCount(dao.count(klass, cnd));
	    }
		return new QueryResult(roles, pager);
	}
	
	protected NutMap ajaxOk(Object data) {
		return new NutMap().setv("ok", true).setv("data", data);
	}
	
	protected NutMap ajaxFail(String msg) {
		return new NutMap().setv("ok", false).setv("msg", msg);
	}
	
	public void init() throws Exception {
		if (urlbase == null)
			urlbase = "";
		urlbase += Mvcs.getServletContext().getContextPath();
	}
	
	// --------------------------
	// 常用HTTP状态
	public static final View HTTP_403 = new HttpStatusView(403);
	public static final View HTTP_404 = HttpStatusView.HTTP_404;
	public static final View HTTP_500 = HttpStatusView.HTTP_500;
	public static final View HTTP_502 = HttpStatusView.HTTP_502;
    public static final View HTTP_200 = new HttpStatusView(200);
    public static final View HTTP_304 = new HttpStatusView(304);
	
	// 生成json响应的辅助方法
	protected static NutMap _map(Object...args) {
		NutMap re = new NutMap();
		for (int i = 0; i < args.length; i+=2) {
			re.put(args[i].toString(), args[i+1]);
		}
		return re;
	}
	
	protected static <T> List<T> _list(T ... args) {
		return Arrays.asList(args);
	}
	
	protected static View _download(File f) {
		return new ViewWrapper(new RawView("stream"), f);
	}
	
	protected static View _download(String f) {
		return new ViewWrapper(new RawView("stream"), new File(f));
	}
}

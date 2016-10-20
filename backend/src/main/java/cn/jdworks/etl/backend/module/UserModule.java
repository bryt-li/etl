package cn.jdworks.etl.backend.module;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import cn.jdworks.etl.backend.bean.User;

@IocBean
@Ok("json")
@Fail("http:500")
public class UserModule extends BaseModule {

	private final Log LOG = Logs.getLog(this.getClass());

	@GET
	@At("/login")
	@Ok("re:jsp:user.login")
	public String loginPage(ViewModel model) {
		model.setv("js", "user/login.js");
		model.setv("css", "user/login.css");
		
		if (getMe() == null)
			return null;
		else {
			String url = getSavedUrl();
			if(url == null)
				return "redirect:dashboard";
			else
				return "redirect:" + url;
		}
	}

	@GET
	@At
	@Ok("re:jsp:user.profile")
	public String profile() {
		if (getMe() != null)
			return null;
		else
			return redirectToLoginPage();
	}

	@GET
	@At
	@Ok("re:jsp:dashboard")
	public String dashboard() {
		if (getMe() != null)
			return null;
		else
			return redirectToLoginPage();
	}

	@At
	@POST
	public Object login(@Param("username") String name, @Param("password") String password, HttpSession session) {
		User user = dao.fetch(User.class, Cnd.where("username", "=", name).and("password", "=", password));
		if (user == null) {
			return false;
		} else {
			session.setAttribute("me", user.getId());
			String url = getSavedUrl();
			if(url == null)
				return "dashboard";
			else
				return url;
		}
	}

	@At
	@Ok(">>:/")
	public void logout(HttpSession session) {
		session.invalidate();
	}

	@At
	public Object delete(@Param("id") int id, @Attr("me") int me) {
		if (me == id) {
			return new NutMap().setv("ok", false).setv("msg", "不能删除当前用户!!");
		}
		dao.delete(User.class, id); // 再严谨一些的话,需要判断是否为>0
		return new NutMap().setv("ok", true);
	}

	@At
	public int count() {
		return dao.count(User.class);
	}

}

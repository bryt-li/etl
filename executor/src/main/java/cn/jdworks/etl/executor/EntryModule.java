package cn.jdworks.etl.executor;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

/*
在整个应用启动或者关闭时，你想做一些额外的处理工作，你可以实现一个 org.nutz.mvc.Setup 接口，并将其配置在主模块上
*/
@SetupBy(value=EntrySetup.class, args="ioc:entrySetup")
/*
一个 Mvc 框架可以通过 Ioc 接口同一个 Ioc 容器挂接，挂接的方法很简单： 在主模块上声明 @IocBy

ComboIocProvider的args参数, 星号开头的是类名或内置缩写,剩余的是各加载器的参数
*js 是JsonIocLoader,负责加载js/json结尾的ioc配置文件
*anno 是AnnotationIocLoader,负责处理注解式Ioc, 例如@IocBean
*tx 是TransIocLoader,负责加载内置的事务拦截器定义, 1.b.52开始自带
*/
@IocBy(type=ComboIocProvider.class, args={"*js", "ioc/",
                                           "*anno", "cn.jdworks.etl.executor",
                                           "*tx"})
/*
将自动搜索主模块所在的包（包括子包）下所有的类，如果有类包括了一个以上的入口函数将被认为是模块类
*/
@Modules(scanPackage=true)
public class EntryModule{
}


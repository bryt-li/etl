# ETL项目通用工具库

# 编译
	mvn compile
	
# 测试
	mvn test

# 打包
	mvn package

# 安装到本地仓库
	mvn install
	
# 其它项目的pom.xml中引用即可
	<dependency>
      <groupId>cn.jdworks.etl</groupId>
      <artifactId>utils</artifactId>
      <version>1.0</version>
    </dependency>

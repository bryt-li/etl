# ETL脚本执行器

# 编译
	mvn package

# 运行
	mvn exec:java


# 打包运行
	mvn package exec:java

# 下载依赖项的源代码及文档
	mvn dependency:sources
	mvn dependency:resolve -Dclassifier=javadoc
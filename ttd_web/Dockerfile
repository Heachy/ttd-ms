# 镜像java版本，这里有坑，你可以看着项目里的pom.xml文件确定自己的版本，如果有问题的话多尝试几个
FROM openjdk:17
# VOLUME 指定了临时文件(使用容器卷)目录为/tmp,在主机/var/lib/docker目录下创建了一个临时文件并链接到容器的/tmp。
VOLUME /tmp
# 将jar包添加到容器中并更名为test2.jar
ADD /target/ttd_web-0.0.1-SNAPSHOT.jar ttd_web.jar
# 暴露端口号
EXPOSE 9737
# 容器启动时会运行的命令
ENTRYPOINT ["java","-jar","/ttd_web.jar"]

# 使用官方 OpenJDK 镜像作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制已构建的 jar 文件
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8081

# 运行应用
ENTRYPOINT ["java", "-jar", "app.jar"]
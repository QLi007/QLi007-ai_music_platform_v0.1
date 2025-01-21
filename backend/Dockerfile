# 构建阶段
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /workspace/app

# 复制pom.xml
COPY pom.xml .
# 下载依赖
RUN mvn dependency:go-offline

# 复制源代码
COPY src src

# 构建应用
RUN mvn package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 复制构建产物
COPY --from=build /workspace/app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java","-jar","app.jar"] 
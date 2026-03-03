# ============================================
# 多阶段构建 - 适用于 K8s 部署
# ============================================

# --- 构建阶段 ---
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build
COPY pom.xml .
COPY template-common/pom.xml template-common/
COPY template-dao/pom.xml template-dao/
COPY template-service/pom.xml template-service/
COPY template-biz/pom.xml template-biz/
COPY template-api/pom.xml template-api/
COPY template-client/pom.xml template-client/
COPY template-web/pom.xml template-web/
# 先下载依赖（利用 Docker 缓存加速）
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B 2>/dev/null || true
COPY . .
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests -B

# --- 运行阶段 ---
FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="bc"

# 安全：创建非 root 用户
RUN addgroup -S app && adduser -S app -G app

# 时区设置
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

WORKDIR /app

# 从构建阶段复制 jar
COPY --from=builder --chown=app:app /build/template-web/target/template-web.jar app.jar

# 切换到非 root 用户
USER app

# 暴露应用端口和管理端口
EXPOSE 9915

# JVM 参数优化（容器感知）
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/tmp/heapdump.hprof \
    -Djava.security.egd=file:/dev/./urandom"

# 使用 exec 格式的 ENTRYPOINT，确保信号正确传递（优雅停机）
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

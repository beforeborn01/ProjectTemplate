## 不同的分支对应不同的框架依赖
1. v1.1 -- 纯目录结构，不包含其他三方依赖
2. v-rich-v1 -- 对应troy技术栈依赖
3. v-spring-2.2.6  -- 对应spring-boot 2.3.12版本依赖、jdk1.8
4. v-spring-3  -- 对应spring-boot 3.x版本依赖，jdk17

## v-rich-v1 集成的三方组件

1. mybatis
2. redis
3. jetcache
4. nacos
5. openfeign
6. dubbo
7. 影子库支持
8. 大对象监控

待支持特性：

1. 日志格式支持pv
2. traceId贯穿流程
3. feign调用支持添加header
4. 动态数据源切换
<!-- 创建日期：2026-03-11 | 来源草稿：2026-03-11-1506-feishu-maven-nacos-k8s.md -->

# Maven 依赖治理规范

经 pom 代码评审后沉淀以下规则，适用于本项目所有模块。

## 版本管理

**规则：子模块版本统一使用 `${revision}`，禁止定义冗余版本变量**

```xml
<!-- ❌ 禁止：定义冗余版本变量 -->
<api-version>1.0-SNAPSHOT</api-version>
<web-version>1.0-SNAPSHOT</web-version>

<!-- ✅ 正确：子模块直接省略 <version> 或统一用 ${revision} -->
<revision>1.0-SNAPSHOT</revision>
```

## 模块职责边界

**规则：`template-api` 仅包含 DTO/接口定义，禁止引入 `spring-boot-starter-web`**

`template-api` 是被所有模块依赖的基础层，引入 `starter-web` 会传递 Tomcat、Jackson 等重量级依赖至全部下游模块。

```xml
<!-- template-api/pom.xml ❌ 禁止 -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## 测试依赖 Scope

**规则：测试框架依赖必须显式声明 `<scope>test</scope>`**

```xml
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter</artifactId>
  <scope>test</scope>  <!-- 必须声明，防止污染生产 classpath -->
</dependency>
```

## 工具库去重

**规则：`hutool-all` 等通用工具库在父 pom `dependencyManagement` 中统一管理，子模块不重复引入**

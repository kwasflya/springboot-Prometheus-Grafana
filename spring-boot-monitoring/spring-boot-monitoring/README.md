# Spring Boot 监控程序使用说明

## 项目概述

本项目是一个基于Spring Boot Actuator和Prometheus的通用监控解决方案，提供了应用健康状态、性能指标、API调用和数据库连接等全方位监控功能。通过集成Prometheus，可以实现监控数据的可视化展示和告警管理。

## 功能特点

- **健康状态监控**：自定义磁盘空间和内存使用率健康检查
- **性能指标监控**：CPU、内存、线程等系统性能指标
- **API调用监控**：请求次数、响应时间、错误率等API指标
- **数据库连接监控**：连接池状态、活跃连接数等数据库指标
- **自定义业务指标**：支持自定义业务指标监控
- **Prometheus集成**：所有监控指标均可通过Prometheus采集
- **可视化支持**：可与Grafana等工具集成，实现数据可视化

## 技术栈

- Spring Boot 2.7.0
- Spring Boot Actuator
- Micrometer (Prometheus)
- Spring Data JPA
- H2数据库（可替换为其他数据库）
- Lombok

## 快速开始

### 环境要求

- JDK 11+
- Maven 3.6+

### 构建与运行

1. 克隆项目到本地
```bash
git clone [项目地址]
cd spring-boot-monitoring
```

2. 使用Maven构建项目
```bash
mvn clean package
```

3. 运行应用
```bash
java -jar target/spring-boot-monitoring-0.0.1-SNAPSHOT.jar
```

4. 访问监控端点
- 应用主页：http://localhost:8080
- Actuator端点：http://localhost:8080/actuator
- 健康检查：http://localhost:8080/actuator/health
- Prometheus指标：http://localhost:8080/actuator/prometheus
- H2控制台：http://localhost:8080/h2-console

## 监控端点说明

### Actuator端点

Spring Boot Actuator提供了多个内置端点，可通过配置文件开启或关闭：

- `/actuator/health`：应用健康状态
- `/actuator/info`：应用信息
- `/actuator/metrics`：应用指标
- `/actuator/prometheus`：Prometheus格式的指标数据
- `/actuator/env`：环境变量
- `/actuator/loggers`：日志配置
- `/actuator/mappings`：请求映射
- `/actuator/beans`：Spring Bean列表
- `/actuator/threaddump`：线程转储

### 自定义API端点

本项目提供了几个演示API端点，用于测试监控功能：

- `/api/hello`：基本API测试
- `/api/status`：应用状态API
- `/api/simulate-load`：模拟CPU负载
- `/api/error-test`：模拟随机错误
- `/api/logs`：查看日志记录
- `/api/logs/generate`：生成随机日志

## 配置说明

### application.properties

主要配置项说明：

```properties
# 应用基本配置
spring.application.name=spring-boot-monitoring
server.port=8080

# Actuator 配置
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
management.endpoint.metrics.enabled=true
management.endpoint.prometheus.enabled=true
management.metrics.export.prometheus.enabled=true

# 数据库配置
spring.datasource.url=jdbc:h2:mem:monitordb
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true

# 自定义监控配置
monitor.api.enabled=true
monitor.database.enabled=true
```

### 自定义配置

可以通过修改配置文件来自定义监控行为：

- 调整暴露的Actuator端点：`management.endpoints.web.exposure.include`
- 修改健康检查详情显示：`management.endpoint.health.show-details`
- 配置数据库连接：`spring.datasource.*`
- 启用/禁用特定监控功能：`monitor.*`

## 与Prometheus集成

### Prometheus配置

1. 安装Prometheus（https://prometheus.io/download/）

2. 配置Prometheus采集目标（prometheus.yml）：

```yaml
scrape_configs:
  - job_name: 'spring-boot-monitoring'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['localhost:8080']
```

3. 启动Prometheus：

```bash
./prometheus --config.file=prometheus.yml
```

4. 访问Prometheus UI：http://localhost:9090

## 与Grafana集成

### Grafana配置

1. 安装Grafana（https://grafana.com/grafana/download）

2. 启动Grafana：

```bash
./bin/grafana-server
```

3. 访问Grafana UI：http://localhost:3000（默认用户名/密码：admin/admin）

4. 添加Prometheus数据源：
   - 配置 > Data Sources > Add data source
   - 选择Prometheus
   - URL设置为：http://localhost:9090
   - 点击"Save & Test"

5. 导入仪表盘：
   - 创建 > Import
   - 导入JVM (Micrometer)仪表盘ID：4701
   - 导入Spring Boot 2.1仪表盘ID：10280

## 自定义监控指标

### 添加自定义业务指标

可以参考`BusinessMetrics.java`类，添加自定义业务指标：

```java
// 注册指标
Gauge.builder("business.custom.metric", valueProvider, ValueProvider::getValue)
     .description("自定义业务指标描述")
     .tag("tag_name", "tag_value")
     .register(meterRegistry);

// 更新指标
Counter.builder("business.custom.counter")
       .tag("type", "custom_operation")
       .register(meterRegistry)
       .increment();
```

## 常见问题

1. **无法访问Actuator端点**
   - 检查配置文件中的`management.endpoints.web.exposure.include`设置
   - 确认应用正常启动且端口未被占用

2. **Prometheus无法采集数据**
   - 检查Prometheus配置中的目标地址是否正确
   - 确认`/actuator/prometheus`端点可以正常访问

3. **自定义健康检查不生效**
   - 确认健康检查类已被正确注册为Spring Bean
   - 检查`management.endpoint.health.show-details`配置

4. **数据库监控指标缺失**
   - 确认数据库连接配置正确
   - 检查`DatabaseMetrics`类是否正确注册

## 扩展与定制

### 添加新的健康检查

创建新的健康检查类，实现`HealthIndicator`接口：

```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // 实现健康检查逻辑
        return Health.up().withDetail("custom", "details").build();
    }
}
```

### 添加新的监控指标

在服务或组件中注入`MeterRegistry`，然后注册新的指标：

```java
@Service
public class CustomService {
    private final MeterRegistry meterRegistry;
    
    public CustomService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // 注册指标
        Gauge.builder("custom.metric", this, CustomService::getValue)
             .description("自定义指标")
             .register(meterRegistry);
    }
    
    private double getValue() {
        // 返回指标值
        return 42.0;
    }
}
```

## 参考资料

- [Spring Boot Actuator文档](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer文档](https://micrometer.io/docs)
- [Prometheus文档](https://prometheus.io/docs/introduction/overview/)
- [Grafana文档](https://grafana.com/docs/grafana/latest/)

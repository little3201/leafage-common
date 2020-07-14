# abeille-common
common config, utils and etc.. of abeille


1. 多数据源使用示例：

```yaml
##=====datasource common=====##
spring:
  datasource:
    master:
      # datasource driver
      driver-class-name: com.mysql.cj.jdbc.Driver
      # datasource connection path
      jdbcUrl: jdbc:mysql://127.0.0.1:3306/abeille_basic_hypervisor?useSSL=false&serverTimezone=UTC
      # datasource username
      username: root
      # datasource password
      password: root
    slaver:
      # datasource driver
      driver-class-name: com.mysql.cj.jdbc.Driver
      # datasource connection path
      jdbcUrl: jdbc:mysql://127.0.0.1:3306/abeille_basic_hypervisor?useSSL=false&serverTimezone=UTC
      # datasource username
      username: root
      # datasource password
      password: root
```
# Leafage Common

<p align="center">
 <img src="https://sonarcloud.io/api/project_badges/measure?project=little3201_leafage-common&metric=alert_status" alt="Quality Gate Status">
 <img src="https://sonarcloud.io/api/project_badges/measure?project=little3201_leafage-common&metric=code_smells"/>
</p>

### 介绍：

leafage 开源项目的公共模块，提供通用工具和抽象接口；

公共依赖模块，包括业务基础操作（CRUD）接口、树结构构造抽象接口（AbstractBasicService），提供随机代码生成逻辑方法，poi导入、导出处理（ExcelReader），下分两个package：

- reactive: 以 web flux 方式构建

  - ServletBasicService: 基础业务接口；
  - ServletAbstractTreeNodeService: 树结构操作接口；

- servlet: 以 web servlet 方式构建

  - ReactiveBasicService: 基础业务接口；
  - ReactiveAbstractTreeNodeService: 树结构操作接口；

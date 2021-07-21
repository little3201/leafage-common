# Leafage Common

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=little3201_leafage-common&metric=alert_status)](https://sonarcloud.io/dashboard?id=little3201_leafage-common)

### 介绍：

leafage 开源项目的公共模块，提供通用工具和抽象接口；

leafage-basic下 assets 和 hypervisor 两个服务必须的依赖项目, 提供：

- BasicService, ReactiveBasicService: 基础业务接口；
- AbstractBasicService: 抽象接口，提供随机代码生成逻辑方法；
- FileBasicUtils: 文件操作工具类；
- TreeNode, ServletTreeNodeAware, ReactiveTreeNodeAware操作接口；
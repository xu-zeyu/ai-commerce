# AGENTS.md

## 目标

本文件用于约束在本仓库内工作的 AI Agent / 开发者，确保新增代码、目录、模块、类名、包名与当前项目技术栈及阿里 Java 开发手册中的命名规范保持一致。

结论优先：

- 本项目是 `Gradle` 多模块 `Java` 工程。
- 核心技术栈为 `Spring Boot`、`Spring Cloud Config`、`MyBatis-Plus`、`Redis`、`Sa-Token`、`WebSocket`、`RabbitMQ`。
- 命名规范以《阿里巴巴 Java 开发手册》为基线，并结合当前仓库已存在的模块结构执行。
- 新增代码优先“遵循现有模块风格”，不要在同一领域内混用多套命名体系。

## 项目结构

根工程：`ai-commerce`

当前模块分层如下：

- `ai-commerce-common`：公共能力、基础组件、常量、异常、工具类
- `ai-commerce-auth`：认证鉴权能力
- `ai-commerce-start/*`：starter 类基础设施模块
- `jinHan-gold/jinHan-gold-core`：业务核心域能力
- `jinHan-gold/jinHan-gold-admin-web`：管理后台接口
- `jinHan-gold/jinHan-gold-user-web`：用户前台接口
- `buildSrc`：Gradle 约定插件与构建脚本

## 模块命名规范

### 1. 根规则

- 模块名统一使用小写英文和中划线 `-`，不使用下划线、不使用空格。
- 模块名必须表达职责，不使用无语义缩写。
- 模块名应体现层次关系，优先使用 `父域-子域-职责` 结构。

### 2. 当前项目应遵循的模块模式

- 基础模块：`ai-commerce-common`、`ai-commerce-auth`
- 基础设施 starter：`ai-commerce-starter-{capability}`
- 业务父工程：`jinHan-gold`
- 业务核心模块：`jinHan-gold-core`
- 业务接口模块：`jinHan-gold-{scene}-web`

### 3. 新增模块命名示例

- 正确：`ai-commerce-starter-oss`
- 正确：`jinHan-gold-order-web`
- 正确：`jinHan-gold-member-core`
- 错误：`jinHanGoldCore`
- 错误：`gold_user_web`
- 错误：`common2`

## 包命名规范

### 1. 基础规则

- 包名统一使用全小写英文，不使用拼音缩写，不使用复数含义不清的名称。
- 包名按“公司/项目/模块/领域/分层”组织。
- 包名中禁止出现类名风格的驼峰写法。

### 2. 当前项目包名前缀

按现有代码，新增代码应优先沿用以下前缀：

- `com.aicommerce.common`
- `com.aicommerce.auth`
- `com.aicommerce.starter.{capability}`
- `com.jinHan.gold.core.{domain}`
- `com.jinHan.gold.admin`
- `com.jinHan.gold.api`

说明：

- 当前仓库同时存在 `com.aicommerce` 与 `com.jinHan.gold` 两套包前缀，属于既有事实。
- 新增代码不得擅自引入第三套顶级包前缀。
- 在已有模块内新增类时，必须延续该模块当前包前缀，不要跨模块漂移。

### 3. 包分层建议

结合现有项目，优先使用以下分层：

- `config`：配置类
- `controller`：接口层
- `domain`：领域层
- `domain.command`：命令对象
- `domain.model`：领域模型 / 实体
- `domain.mapper`：MyBatis Mapper
- `domain.handler`：领域处理器
- `infra`：基础设施适配
- `service`：通用服务
- `utils`：工具类
- `constant`：常量
- `exception`：异常

新增包名应使用稳定名词，不要使用：

- `utiles`
- `manager1`
- `handlerNew`
- `temp`
- `test2`

## 类命名规范

### 1. 通用规则

- 类名使用 UpperCamelCase。
- 类名必须是名词、名词短语，或明确职责的组合词。
- 不使用中文，不使用拼音，不使用无意义缩写。
- 布尔类名避免使用 `is` 前缀作为类名。

### 2. 按职责使用后缀

结合阿里规范与当前项目现状，新增类优先使用以下后缀：

- `*Controller`：接口控制器
- `*Request`：请求对象
- `*Response`：响应对象
- `*DTO`：跨层数据传输对象
- `*VO`：展示对象
- `*Command`：命令对象
- `*Handler`：领域处理器
- `*Service`：服务类
- `*Mapper`：MyBatis 映射接口
- `*Config` / `*Configure`：配置类
- `*Util`：轻量工具类
- `*Exception`：异常
- `*Enum`：枚举

### 3. 实体 / 模型命名

- 实体类、领域模型类应使用明确业务名词，如 `Order`、`AdminRole`、`ShippingRecord`。
- 枚举必须以 `Enum` 结尾，如 `OrderStatusEnum`。
- 不建议新增含义不清的复数实体名；现有 `Orders`、`Users` 属于历史代码，新增模型优先使用单数名词。

### 4. Handler 命名

当前仓库已经存在两种风格：

- `OrderCreateHandler`
- `ProductCreateCommandHandler`

处理规则：

- 同一业务子域内必须保持一种风格，不混用。
- 若目标目录已经以 `*Handler` 为主，则新增类沿用 `*Handler`。
- 若目标目录已经采用 `*CommandHandler`，则新增类继续沿用该后缀。

### 5. Controller 子目录对象命名

当前项目接口层目录存在：

- `controller/**/request`
- `controller/**/response`
- `controller/**/vo`
- `controller/file/dto`

约束如下：

- 入参对象优先统一为 `*Request`
- 出参对象优先统一为 `*Response`
- 纯展示视图对象可使用 `*VO`
- 若目录已固定为 `dto`，则只在确有跨层传输语义时使用 `*DTO`
- 不要在同一接口场景下同时引入 `DTO`、`VO`、`Response` 三套概念

## 方法、变量、常量命名规范

### 1. 方法名

- 方法名使用 lowerCamelCase。
- 方法名应为“动词 + 业务对象”或“动词 + 业务语义”。
- 查询使用 `get`、`find`、`query`、`pageQuery`
- 新增使用 `create`、`add`
- 修改使用 `update`
- 删除使用 `delete`、`remove`

避免：

- `doData()`
- `handleIt()`
- `aaa()`

### 2. 变量名

- 变量名使用 lowerCamelCase。
- 临时变量也必须可读，不使用单字符，循环变量除外。
- 集合变量使用复数或带业务语义，如 `orderList`、`permissionCodes`。
- 布尔变量使用 `is`、`has`、`can`、`enable` 等可判定前缀。

### 3. 常量名

- 常量全部使用大写字母和下划线。
- 常量名必须完整表达含义，如 `DEFAULT_PAGE_SIZE`、`LOGIN_SMS_EXPIRE_SECONDS`。
- 禁止硬编码魔法值散落在业务代码中。

## 数据库与 MyBatis 命名规范

- 数据库表名、字段名建议使用小写下划线风格。
- Mapper 接口必须以 `Mapper` 结尾。
- XML 文件名若存在，应与 `Mapper` 接口同名。
- Command / Request / Response 不要直接命名为 `DO`、`POJO`、`Bean`。
- 数据库实体若直接映射表，名称应表达业务含义，不用技术词替代业务词。

## Spring / Boot 相关命名规范

- 启动类使用 `*Application`，如 `AdminApplication`、`UserWebApplication`
- 配置类使用 `*Config` 或项目已存在的 `*Configure`
- 全局异常处理类使用 `*ExceptionHandler` 或 `GlobalExceptionHandler`
- 消费者类可用 `*Consumer`
- 定时任务类可用 `*Scheduler`

同一模块内优先保持一致，例如：

- 已存在 `SaTokenConfigure`，则同类配置不要再新增 `SaTokenConfiguration`
- 已存在 `TodoExpireScheduler`，则调度类继续使用 `*Scheduler`

## 目录命名规范

- 目录名统一小写。
- 多单词目录优先直接连写或使用稳定英文语义，遵循当前项目事实。
- Java 包目录必须与包声明一致。
- 禁止新增临时目录名，如 `new`, `bak`, `tmp`, `copy`。

## AI Agent 执行规则

AI Agent 在本仓库生成代码时必须遵守以下规则：

1. 新增模块前，先判断应归属 `common`、`auth`、`starter`、`core`、`admin-web`、`user-web` 中哪一层，不要直接平铺新模块。
2. 新增包时，先复用已有领域包，例如 `order`、`product`、`shipping`、`todo`、`auth`、`admin`。
3. 新增类名必须先对齐当前目录既有后缀体系，再决定使用 `Handler`、`CommandHandler`、`Service` 或 `DTO/VO/Response`。
4. 不要因为个人偏好把现有 `request` 改成 `dto`，或把现有 `handler` 改成 `service`。
5. 除非用户明确要求重构，否则对历史不一致命名仅“新增时兼容”，不要大范围重命名。
6. 若发现现有代码与阿里规范冲突，优先保证模块内部一致性，再考虑渐进式修正。

## 推荐示例

### 新增后台商品分页接口

- Controller：`ProductController`
- Request：`ProductPageRequest`
- Response：`ProductFindResponse` 或更明确的 `ProductPageResponse`
- Command：`ProductPageCommand`
- Handler：沿用当前子域风格，例如 `ProductPageCommandHandler`

### 新增订单领域能力

- 包：`com.jinHan.gold.core.order.domain`
- 命令：`OrderRefundCommand`
- 处理器：`OrderRefundHandler`
- Mapper：`OrderRefundMapper`
- 枚举：`OrderRefundStatusEnum`

### 新增 starter

- 模块：`ai-commerce-starter-oss`
- 包：`com.aicommerce.starter.oss`
- 配置类：`OssConfig`
- 服务类：`OssService`

## 禁止事项

- 禁止新增拼音命名。
- 禁止新增无业务语义缩写，如 `InfoMgr`, `TmpService`, `DataObj`。
- 禁止同一目录下混用 `Request/DTO/Form` 表达同一层入参。
- 禁止在已有单数命名体系里新增复数实体名。
- 禁止为了“看起来高级”引入与项目现状不一致的 DDD/CQRS 命名套件。

## 最终原则

本仓库命名规则遵循以下优先级：

1. 同目录 / 同模块现有风格一致
2. 本文件约束
3. 阿里巴巴 Java 开发手册

若三者冲突，默认先保证“局部一致性”，再做渐进式规范化。

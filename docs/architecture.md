# 架构设计

> 本文档描述 `fina-competition-engine` 的多模块 Maven 架构、模块职责与依赖关系。

---

## 目录

- [1. 设计原则](#1-设计原则)
- [2. 总体架构](#2-总体架构)
- [3. 模块详细设计](#3-模块详细设计)
  - [3.1 world-aquatics-commons](#31-world-aquatics-commons)
  - [3.2 world-aquatics-domain](#32-world-aquatics-domain)
  - [3.3 world-aquatics-rules-engine](#33-world-aquatics-rules-engine)
  - [3.4 world-aquatics-competition](#34-world-aquatics-competition)
  - [3.5 world-aquatics-timing](#35-world-aquatics-timing)
  - [3.6 world-aquatics-scoring](#36-world-aquatics-scoring)
  - [3.7 world-aquatics-data-exchange](#37-world-aquatics-data-exchange)
  - [3.8 world-aquatics-records](#38-world-aquatics-records)
  - [3.9 world-aquatics-officials](#39-world-aquatics-officials)
  - [3.10 world-aquatics-starter](#310-world-aquatics-starter)
- [4. 模块依赖关系](#4-模块依赖关系)
- [5. 关键设计决策](#5-关键设计决策)

---

## 1. 设计原则

- **高内聚**：每个模块只负责单一职责，模块内部功能紧密相关
- **低耦合**：模块间通过接口和领域模型交互，避免直接依赖实现细节
- **可扩展**：策略接口驱动，支持用户自定义规则、项目和计分方式
- **规则驱动**：以世界泳联 2026 技术规则为基准，内置标准实现，同时允许覆盖
- **零依赖（核心模块）**：`commons`、`domain`、`rules-engine`、`competition`、`timing`、`scoring`、`records`、`officials` 等核心模块不允许引入第三方运行时依赖

---

## 2. 总体架构

```
world-aquatics-parent (POM)
├── world-aquatics-commons          # 通用工具类、常量、异常
├── world-aquatics-domain           # 核心领域模型
├── world-aquatics-rules-engine     # 规则引擎（泳姿技术规则验证）
├── world-aquatics-competition      # 竞赛编排（分组、道次、晋级）
├── world-aquatics-timing           # 计时与成绩
├── world-aquatics-scoring          # 积分计算
├── world-aquatics-data-exchange    # 数据交换（配置解析、导入导出）
├── world-aquatics-records          # 世界纪录
├── world-aquatics-officials        # 技术官员
└── world-aquatics-starter          # 快速启动（Spring Boot 可选）
```

---

## 3. 模块详细设计

### 3.1 world-aquatics-commons

**职责**：提供跨模块复用的通用工具类、常量定义与异常基类。

**核心内容**：

| 类别 | 说明 |
|------|------|
| `constants` | 全局常量池（如默认泳道数、默认泳池长度、成绩精度） |
| `utils` | 时间格式化、校验工具、字符串处理等零依赖工具类 |
| `exception` | 异常基类：`DomainException`、`ValidationException`、`ConfigurationException` |
| `i18n` | 国际化资源加载基础设施（`MessageSource` 接口与默认实现） |

**依赖**：无

---

### 3.2 world-aquatics-domain

**职责**：定义游泳竞赛的核心领域对象，包括枚举、值对象和实体基类。

**核心内容**：

| 类名 | 类型 | 说明 |
|------|------|------|
| `Stroke` | Enum | 泳姿：FREESTYLE, BACKSTROKE, BREASTSTROKE, BUTTERFLY, MEDLEY |
| `Gender` | Enum | 性别：MALE, FEMALE, MIXED |
| `CourseType` | Enum | 泳池类型：LONG_COURSE(50m), SHORT_COURSE(25m) |
| `EventType` | Enum | 项目类型：INDIVIDUAL, RELAY |
| `Distance` | Value Object | 距离值对象，支持 50m ~ 1500m 及自定义 |
| `SwimmingEvent` | Entity | 游泳项目实体，组合泳姿 + 距离 + 性别 + 分级 |
| `Athlete` | Entity | 运动员实体（含残奥分级 S/SB/SM） |
| `RelayTeam` | Entity | 接力队实体（含 4 名队员及泳序） |
| `Meet` | Entity | 赛事实体（含赛程、泳池配置、赛制模式） |
| `Heat` | Entity | 预赛/半决赛/决赛组实体 |
| `Result` | Entity | 成绩实体（含总成绩、分段、计时系统来源） |
| `Entry` | Entity | 报名实体（含报名成绩、报名单元、分组信息） |

**关键设计决策**：
- 使用 **Java Record** 定义不可变值对象（`Distance`、`Time` 等）
- 使用 **Builder 模式** 构建复杂实体（`Meet`、`SwimmingEvent`）
- 所有时间使用 **java.time.Duration** 封装，精度统一由业务层控制为 0.01 秒
- 领域对象须实现 `Serializable`，方便上层自行持久化

**依赖**：`world-aquatics-commons`

---

### 3.3 world-aquatics-rules-engine

**职责**：实现 2026 年世界泳联规则中所有泳姿的技术规则验证。

**核心架构**：

```
RulesEngine (接口)
├── FreestyleRulesValidator        # 自由泳规则验证器
│   └── 实现 Article 5 所有规则
│   └── 包括新的 5.4 条（5 米潜入规则）
├── BackstrokeRulesValidator       # 仰泳规则验证器
│   └── 实现 Article 6 所有规则
│   └── 包括 6.5 条（5 米潜入规则）
├── BreaststrokeRulesValidator     # 蛙泳规则验证器
│   └── 实现 Article 7 所有规则
│   └── 包括拆分后的 7.2/7.3 和细化的 7.7
├── ButterflyRulesValidator        # 蝶泳规则验证器
│   └── 实现 Article 8 所有规则
│   └── 包括强化的 8.1-8.6（shall→must）
└── MedleyRulesValidator           # 混合泳规则验证器
    └── 实现 Article 9 所有规则
    └── 包括 9.3 条（自由泳段转身规则）
```

**规则验证结果模型**：

```java
public record RuleViolation(
    RuleArticle article,      // 违反的规则条款
    ViolationType type,       // 违规类型
    String description,       // 违规描述
    Instant timestamp,        // 违规发生时间
    int laneNumber,           // 泳道号
    String athleteId          // 运动员ID
) {}
```

**关键特性**：
- 支持**实时规则验证**（比赛进行中）和**赛后规则验证**
- 违规结果可导出为标准格式供裁判系统使用
- 规则引擎基于**策略模式**设计，便于未来规则更新
- 每个验证器返回详细的违规信息，包括规则条款引用

**依赖**：`world-aquatics-domain`, `world-aquatics-commons`

---

### 3.4 world-aquatics-competition

**职责**：实现预赛、半决赛、决赛的自动编排算法和泳道分配。

**核心算法**：

**预赛编排算法（基于 SW 3.1）**：

```java
public interface SeedingStrategy {
    List<Heat> seedHeats(List<Entry> entries, PoolConfiguration pool);
}

public class StandardSeedingStrategy implements SeedingStrategy {
    // 实现 SW 3.1.1.2 - SW 3.1.1.7 的所有编排逻辑
    // 包括 2 组、3 组、4 组+ 的特殊处理
    // 400/800/1500m 的特殊编排规则
}
```

**泳道分配算法（基于 SW 3.1.2）**：

| 泳道数 | 成绩排名 → 泳道映射 |
|-------|------------------|
| 6 泳道 | 1→3, 2→4, 3→2, 4→5, 5→1, 6→6 |
| 8 泳道 | 1→4, 2→5, 3→3, 4→6, 5→2, 6→7, 7→1, 8→8 |
| 10 泳道 | 1→4, 2→5, 3→3, 4→6, 5→2, 6→7, 7→1, 8→8, 9→9, 10→10 |

**Circle Seeding 支持**：实现预赛-决赛格式中的循环编排，将最快运动员分布在最快 3 组的中间泳道。

**关键扩展点**：
- `SeedingStrategy`：自定义编组策略
- `LaneAllocationStrategy`：自定义道次分配策略
- `GroupingStrategy`：自定义分组维度策略

**依赖**：`world-aquatics-domain`, `world-aquatics-commons`

---

### 3.5 world-aquatics-timing

**职责**：处理比赛计时数据、成绩计算和官方时间确定。

**核心功能**：

| 功能 | 说明 |
|-----|------|
| `TimingSystemInterface` | 计时系统接口抽象，支持电子 / 半自动 / 手动三种计时 |
| `OfficialTimeCalculator` | 官方时间计算器，按优先级确定最终成绩 |
| `SplitTimeProcessor` | 分段成绩处理器（每 50m） |
| `DQProcessor` | 取消资格处理器（成绩标记为 DSQ） |
| `ResultRanker` | 成绩排名器，处理并列与名次跳空 |

**官方时间确定逻辑（基于 Article 11）**：

1. 自动裁判设备时间优先
2. 半自动计时（按钮）次之
3. 手动计时（秒表）最后
4. 并列处理：百分之一秒相同视为并列，后续名次跳空

**依赖**：`world-aquatics-domain`, `world-aquatics-commons`

---

### 3.6 world-aquatics-scoring

**职责**：实现 World Aquatics Points 积分计算和赛事得分统计。

**World Aquatics Points 计算器**：

```java
public class WorldAquaticsPointsCalculator {

    /**
     * 积分计算公式: P = 1000 * (B/T)^3
     * 结果截断为整数（2026 年规则）
     */
    public int calculatePoints(Duration baseTime, Duration actualTime) {
        double b = baseTime.toMillis() / 1000.0;
        double t = actualTime.toMillis() / 1000.0;
        double points = 1000.0 * Math.pow(b / t, 3);
        return (int) points; // 截断为整数
    }

    /**
     * 根据目标积分反推所需时间
     */
    public Duration calculateTimeForPoints(Duration baseTime, int targetPoints) {
        double b = baseTime.toMillis() / 1000.0;
        double t = b / Math.cbrt(targetPoints / 1000.0);
        // 按规则需要递减 0.01 秒直到积分不再变化
        return fineTuneTime(t, targetPoints);
    }
}
```

**赛事得分统计**：

支持多种得分模式：

| 赛事类型 | 个人项目得分 | 接力项目得分 |
|---------|------------|------------|
| 双队赛 | 5-3-1-0 | 7-0 |
| 三角赛 | 6-4-3-2-1-0 | 8-4-0 |
| 8 泳道邀请赛 | 9-7-6-5-4-3-2-1 | 18-14-12-10-8-6-4-2 |
| 世锦赛（个人） | 18-16-15-14-13-12-11-10-8-7-6-5-4-3-2-1 | — |

**关键扩展点**：
- `ScoringStrategy`：自定义积分规则

**依赖**：`world-aquatics-domain`, `world-aquatics-timing`, `world-aquatics-commons`

---

### 3.7 world-aquatics-data-exchange

**职责**：实现与外部系统的数据交换，支持多种标准格式。

**支持的格式**：

| 格式 | 读支持 | 写支持 | 优先级 |
|-----|-------|-------|-------|
| JSON | 是 | 是 | 高 |
| YAML | 是 | 是 | 高 |
| Properties | 是 | 是 | 高 |

**设计要点**：
- 使用**适配器模式**统一不同格式的读写接口
- 所有格式转换需经过**验证层**确保数据完整性
- 零依赖约束：JSON/YAML 解析不引入第三方库，使用 JDK 内置或自行实现简单解析器

**依赖**：`world-aquatics-domain`, `world-aquatics-commons`

---

### 3.8 world-aquatics-records

**职责**：管理和验证各级别纪录。

**核心功能**：

| 功能 | 说明 |
|-----|------|
| `RecordDatabase` | 纪录数据库管理（调用方注入） |
| `RecordValidator` | 纪录验证器，检查纪录认定条件 |
| `RecordTypeClassifier` | 纪录类型分类（WR / WJR / 区域 / 国家 / 赛会） |
| `RecordApprovalWorkflow` | 纪录审批工作流标记 |

**验证规则（基于 Article 12）**：
- 泳池尺寸合规
- 计时系统合规（自动计时）
- 泳衣合规
- 年龄验证（世界青年纪录）
- 国籍验证（接力）
- 反兴奋剂要求

**依赖**：`world-aquatics-domain`, `world-aquatics-timing`, `world-aquatics-commons`

---

### 3.9 world-aquatics-officials

**职责**：管理裁判、发令员、转身检查员等技术人员。

**核心功能**：
- 技术官员角色和权限管理
- 裁判分配算法
- 犯规报告工作流
- 视频裁判系统接口（预留）

**依赖**：`world-aquatics-domain`, `world-aquatics-rules-engine`, `world-aquatics-commons`

---

### 3.10 world-aquatics-starter

**职责**：提供 Spring Boot 快速启动配置，简化集成（可选模块）。

**包含内容**：
- 自动配置（Auto-Configuration）
- 默认属性配置
- 示例应用
- REST API 控制器基类（可选，供上层扩展）

**约束**：此模块是唯一允许引入 Spring Boot 依赖的模块，核心模块仍保持零依赖。

**依赖**：所有上述模块（`world-aquatics-commons` ~ `world-aquatics-officials`）

---

## 4. 模块依赖关系

```
world-aquatics-parent (POM)
│
├── world-aquatics-commons
│   └── 无外部依赖
│
├── world-aquatics-domain
│   └── 依赖: world-aquatics-commons
│
├── world-aquatics-rules-engine
│   └── 依赖: world-aquatics-domain, world-aquatics-commons
│
├── world-aquatics-competition
│   └── 依赖: world-aquatics-domain, world-aquatics-commons
│
├── world-aquatics-timing
│   └── 依赖: world-aquatics-domain, world-aquatics-commons
│
├── world-aquatics-scoring
│   └── 依赖: world-aquatics-domain, world-aquatics-timing, world-aquatics-commons
│
├── world-aquatics-data-exchange
│   └── 依赖: world-aquatics-domain, world-aquatics-commons
│
├── world-aquatics-records
│   └── 依赖: world-aquatics-domain, world-aquatics-timing, world-aquatics-commons
│
├── world-aquatics-officials
│   └── 依赖: world-aquatics-domain, world-aquatics-rules-engine, world-aquatics-commons
│
└── world-aquatics-starter
    └── 依赖: 所有上述模块 + Spring Boot（可选）
```

**依赖原则**：
- 禁止循环依赖
- 上层模块可依赖下层模块，同层模块之间不直接依赖（通过 `domain` 或 `commons` 间接通信）
- `starter` 作为聚合模块，不承载业务逻辑

---

## 5. 关键设计决策

| 决策 | 方案 | 理由 |
|------|------|------|
| 值对象 | Java Record | 不可变、线程安全、自动生成 equals/hashCode/toString |
| 时间表示 | `java.time.Duration` | JDK 内置、精确、支持运算 |
| 成绩精度 | 百分之一秒（0.01） | 符合 2026 泳联规则，统一精度避免混乱 |
| 计时系统 | 三系统并行支持 | 实际比赛中常混合使用，引擎须兼容 |
| 道次分配 | SW 3.1.2 内置 + 策略接口 | 标准赛事开箱即用，特殊赛事可自定义 |
| 积分计算 | World Aquatics Points 公式内置 + 赛事得分模式内置 | 覆盖个人积分与团体积分两种场景 |
| 规则验证 | 策略模式 + 条款引用 | 规则频繁更新，策略模式便于替换；条款引用便于裁判追溯 |
| 数据交换 | 适配器模式 | 支持 JSON/YAML/配置类多种方式，不绑定具体格式 |
| 持久化 | 引擎完全不处理 | 由上层 Service / Mapper 负责，引擎只提供可序列化的 POJO |
| Spring Boot | 仅 starter 模块引入 | 核心模块保持零依赖，非 Spring 项目也能使用 |

---

> 本架构文档随项目迭代持续更新。

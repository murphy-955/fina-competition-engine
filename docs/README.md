# FINA Competition Engine

> 一个零依赖的 Java 室内游泳比赛全流程引擎，面向 Service 层领域开发。

## 简介

`fina-competition-engine` 是一个基于 Java 17 的 Maven 多模块项目，旨在为室内游泳比赛提供完整的**领域模型与业务规则引擎**。它不涉及数据持久层（Mapper）与 Web 层（Controller），而是通过高内聚、低耦合的领域对象与策略接口，简化上层 Service 的开发。

## 核心能力

- **多池型支持**：50m / 25m / 自定义长度，泳道数可配置（默认 8 道）
- **全项目覆盖**：自由泳、仰泳、蛙泳、蝶泳、混合泳（个人 + 接力），支持自定义项目
- **多级赛制**：预赛 → 半决赛 → 决赛（默认），可选预赛 → 决赛或直接决赛
- **残奥支持**：涵盖 S / SB / SM 分级体系
- **多种计时**：电子计时、半自动计时、纯手动计时统一支持
- **智能编排**：内置泳联 SW 3.1.2 道次分配规则，支持用户自定义编排策略
- **成绩处理**：精度统一至百分之一秒，支持分段成绩（Splits）、纪录校验与成绩公告
- **积分体系**：内置标准泳联团体积分规则，支持自定义
- **国际化**：内置中英文资源，支持扩展更多语言

## 快速开始

```xml
<dependency>
    <groupId>io.github.murphy955</groupId>
    <artifactId>fina-competition-engine-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## 项目定位

| 职责 | 本项目 | 上层业务系统 |
|------|--------|-------------|
| 领域模型（Entity / Value Object） | ✅ | 引用 / 扩展 |
| 业务规则（编排 / 计分 / 晋级） | ✅ | 调用 |
| 策略扩展接口（Event / Lane / Scoring） | ✅ | 实现 |
| 泳姿技术规则验证 | ✅ | 调用 |
| 数据持久化（Mapper / DAO） | ❌ | 自行实现 |
| Web 接口（Controller / API） | ❌ | 自行实现 |
| 事务与缓存 | ❌ | 自行实现 |

## 模块结构

项目采用 Maven 多模块架构，核心模块零依赖：

```
world-aquatics-parent
├── world-aquatics-commons          # 通用工具、常量、异常
├── world-aquatics-domain           # 核心领域模型
├── world-aquatics-rules-engine     # 泳姿技术规则验证
├── world-aquatics-competition      # 竞赛编排
├── world-aquatics-timing           # 计时与成绩
├── world-aquatics-scoring          # 积分计算
├── world-aquatics-data-exchange    # 数据交换
├── world-aquatics-records          # 世界纪录
├── world-aquatics-officials        # 技术官员
└── world-aquatics-starter          # Spring Boot 快速启动（可选）
```

## 文档导航

- [需求规格](requirements.md) — 完整的功能需求与设计约束
- [架构设计](architecture.md) — 多模块架构、模块职责与依赖关系

# 比赛编排（Seeding）

> 本文档描述 `world-aquatics-competition` 模块中比赛编排功能的使用方法，涵盖预赛、半决赛、决赛三种阶段的编排策略与道次分配规则。

---

## 目录

- [1. 功能概述](#1-功能概述)
- [2. 核心概念](#2-核心概念)
- [3. 快速开始](#3-快速开始)
- [4. 编排策略详解](#4-编排策略详解)
  - [4.1 预赛（Heats）](#41-预赛heats)
  - [4.2 半决赛（Semi-Finals）](#42-半决赛semi-finals)
  - [4.3 决赛（Finals）](#43-决赛finals)
- [5. 道次分配规则](#5-道次分配规则)
- [6. 自定义扩展](#6-自定义扩展)
- [7. 完整示例](#7-完整示例)

---

## 1. 功能概述

比赛编排功能负责将运动员按成绩排序，并分配到各组（Heat）和泳道（Lane）中。引擎内置了世界泳联（World Aquatics）2026-02-18 版竞赛规则的标准编排算法，支持：

- **预赛编排**：支持 1~N 组，快慢交替分配，长距离项目特殊规则
- **半决赛编排**：固定 2 组，按预赛成绩交替分配
- **决赛编排**：不分组，直接按成绩分配泳道
- **标准道次分配**：6/8/10 泳道池的中心对称规则

---

## 2. 核心概念

| 接口 / 类 | 职责 |
|-----------|------|
| `SeedingStrategy` | 编排策略接口，定义 `generateSeeding(Map, laneCount)` 方法 |
| `AbstractSeedingStrategy` | 抽象基类，封装排序、分组、泳道分配的公共流程 |
| `HeatsSeedingStrategy` | 预赛编排策略（支持普通项目和长距离项目） |
| `SemiFinalsSeedingStrategy` | 半决赛编排策略 |
| `FinalsSeedingStrategy` | 决赛编排策略 |
| `WorldAquaticsLaneAllocator` | 标准泳道分配器，按世界泳联规则分配道次 |
| `LaneAllocator` | 泳道分配策略接口，允许用户自定义道次分配算法 |
| `RaceKeyBuilder` | 项目 Key 生成器，用于将运动员按项目归类到 Map 中 |

---

## 3. 快速开始

```java
import io.github.murphy955.fina.domain.service.RaceKeyBuilder;
import io.github.murphy955.fina.competition.strategy.SeedingStrategy;
import io.github.murphy955.fina.competition.strategy.impl.HeatsSeedingStrategy;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;
import io.github.murphy955.fina.domain.entity.achievements.RaceTime;

import java.util.*;

// 1. 定义分组枚举（需实现 BaseGroup）
enum AgeGroup implements BaseGroup {
    U18("U18"), U20("U20"), SENIOR("SENIOR");
    private final String code;
    AgeGroup(String code) { this.code = code; }
    @Override public String getName() { return code; }
}

// 2. 创建 Key 生成器
// 3. 生成项目 Key
String key = RaceKeyBuilder.buildKey(
    Gender.MALE,           // 性别
    AgeGroup.U18,          // 年龄组
    "100",                 // 距离
    EventType.INDIVIDUAL,  // 项目类型
    Stroke.FREESTYLE       // 泳姿
);
// key = "MALE-U18-100-INDIVIDUAL-FREESTYLE"

// 4. 准备运动员
Map<String, List<Athlete>> entries = new HashMap<>();
List<Athlete> athletes = Arrays.asList(
    new Athlete("张三", RaceTime.parse("52.00")),
    new Athlete("李四", RaceTime.parse("53.50")),
    new Athlete("王五", RaceTime.parse("51.50")),
    // ... 更多运动员
);
entries.put(key, athletes);

// 5. 执行编排
SeedingStrategy strategy = new HeatsSeedingStrategy();
strategy.generateSeeding(entries, 8); // 8 泳道池

// 6. 读取结果
for (Athlete athlete : athletes) {
    System.out.println(athlete.getName()
        + " -> 第" + athlete.getGroup() + "组"
        + " 第" + athlete.getSwimLane() + "道");
}
```

---

## 4. 编排策略详解

### 4.1 预赛（Heats）

**类**：`HeatsSeedingStrategy`

根据报名人数和泳道数自动拆分为若干组，按世界泳联规则分配：

| 组数 | 分配规则 |
|------|---------|
| 1 组（≤泳道数） | 直接作为决赛，全部在同一组 |
| 2 组 | 最快→第 2 组，次快→第 1 组，交替分配 |
| 3 组（非长距离） | 最快→第 3 组，次快→第 2 组，第三快→第 1 组，循环分配 |
| 4 组及以上（非长距离） | 最后 3 组按循环规则，前面组依次顺序填充 |
| 长距离（400/800/1500m） | 最后 2 组按交替规则，前面组依次顺序填充 |

**普通项目**：
```java
SeedingStrategy strategy = new HeatsSeedingStrategy();
```

**长距离项目**（400m / 800m / 1500m）：
```java
SeedingStrategy strategy = new HeatsSeedingStrategy(true);
```

---

### 4.2 半决赛（Semi-Finals）

**类**：`SemiFinalsSeedingStrategy`

固定分为 2 组，按预赛成绩交替分配：

- 最快→第 2 场半决赛
- 次快→第 1 场半决赛
- 第三快→第 2 场半决赛
- ...以此类推

```java
SeedingStrategy strategy = new SemiFinalsSeedingStrategy();
strategy.generateSeeding(entries, 8);
```

---

### 4.3 决赛（Finals）

**类**：`FinalsSeedingStrategy`

不分组，所有运动员在同一决赛（Group = 1），按半决赛成绩（或报名成绩）分配泳道。

```java
SeedingStrategy strategy = new FinalsSeedingStrategy();
strategy.generateSeeding(entries, 8);
```

> **注意**：决赛通常只使用 8 泳道（即使预赛使用 10 泳道）。调用方应传入实际使用的泳道数。

---

## 5. 道次分配规则

引擎内置 `WorldAquaticsLaneAllocator`，按世界泳联标准规则分配泳道。面对出发端时，第 1 道在右侧（10 泳道池为第 0 道）。

### 8 泳道池

| 组内排名 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 |
|---------|---|---|---|---|---|---|---|---|
| 泳道号 | 4 | 5 | 3 | 6 | 2 | 7 | 1 | 8 |

### 6 泳道池

| 组内排名 | 1 | 2 | 3 | 4 | 5 | 6 |
|---------|---|---|---|---|---|---|
| 泳道号 | 3 | 4 | 2 | 5 | 1 | 6 |

### 10 泳道池

| 组内排名 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---------|---|---|---|---|---|---|---|---|---|----|
| 泳道号 | 4 | 5 | 3 | 6 | 2 | 7 | 1 | 8 | 0 | 9 |

---

## 6. 自定义扩展

### 6.1 自定义泳道分配策略

实现 `LaneAllocator` 接口即可：

```java
public class CustomLaneAllocator implements LaneAllocator {
    @Override
    public void apply(List<Athlete> sortedAthletes, int laneCount) {
        // sortedAthletes: 已按成绩排序的组内运动员列表
        // laneCount: 泳道数
        for (int i = 0; i < sortedAthletes.size(); i++) {
            sortedAthletes.get(i).setSwimLane(i + 1); // 简单按顺序分配
        }
    }
}
```

使用自定义分配器：
```java
strategy.generateSeeding(entries, 8, null, new CustomLaneAllocator());
```

### 6.2 自定义排序规则

```java
Comparator<Athlete> byName = Comparator.comparing(Athlete::getName);
strategy.generateSeeding(entries, 8, byName, null);
```

---

## 7. 完整示例

以下示例展示多项目同时编排的完整流程：

```java
// 定义分组
enum TestAgeGroup implements BaseGroup {
    U18("U18"), SENIOR("SENIOR");
    private final String code;
    TestAgeGroup(String code) { this.code = code; }
    @Override public String getName() { return code; }
}

// 初始化
Map<String, List<Athlete>> entries = new HashMap<>();

// 项目1：男子U18 100米自由泳（16人，预期2组）
String key1 = RaceKeyBuilder.buildKey(
    Gender.MALE, TestAgeGroup.U18, "100",
    EventType.INDIVIDUAL, Stroke.FREESTYLE
);
entries.put(key1, createAthletes(key1, 16, 50.0));

// 项目2：女子U18 100米蛙泳（5人，预期1组直接决赛）
String key2 = RaceKeyBuilder.buildKey(
    Gender.FEMALE, TestAgeGroup.U18, "100",
    EventType.INDIVIDUAL, Stroke.BREASTSTROKE
);
entries.put(key2, createAthletes(key2, 5, 70.0));

// 项目3：男子成年组 400米自由泳（20人，长距离，预期3组，最后2组按2组规则）
String key3 = RaceKeyBuilder.buildKey(
    Gender.MALE, TestAgeGroup.SENIOR, "400",
    EventType.INDIVIDUAL, Stroke.FREESTYLE
);
entries.put(key3, createAthletes(key3, 20, 240.0));

// 执行编排
SeedingStrategy heats = new HeatsSeedingStrategy();
heats.generateSeeding(entries, 8); // 普通项目

SeedingStrategy longDistanceHeats = new HeatsSeedingStrategy(true);
longDistanceHeats.generateSeeding(
    Collections.singletonMap(key3, entries.get(key3)), 8
); // 长距离项目

// 输出结果
entries.forEach((key, athletes) -> {
    System.out.println("\n=== " + key + " ===");
    athletes.forEach(a -> System.out.printf(
        "%s -> 第%d组 第%d道%n", a.getName(), a.getGroup(), a.getSwimLane()
    ));
});
```

---

> 本文档随 `world-aquatics-competition` 模块迭代更新。

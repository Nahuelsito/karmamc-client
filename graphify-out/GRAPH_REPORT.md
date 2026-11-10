# Graph Report - karma client  (2026-11-10)

## Corpus Check
- 32 files · ~11,901 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 5 file(s) not represented in the graph (top: .properties 2, (none) 1, .jar 1)

## Summary
- 333 nodes · 766 edges · 18 communities (5 shown, 13 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 28 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `16f06dea`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- .lines
- KarmaConfig.java
- Parse
- MenuReader.java
- .shortNumber
- HudEditScreen
- SidebarReader.java
- KarmaMC Client — Plan
- .lines
- Currency
- TokenPricesScreen
- .onInitializeClient
- org.junit.jupiter.api.Test
- .lines
- .lines

## God Nodes (most connected - your core abstractions)
1. `Parse` - 26 edges
2. `AfkSession` - 21 edges
3. `AfkActivity` - 19 edges
4. `AfkActivityTest` - 18 edges
5. `ParseTest` - 15 edges
6. `Catfish69 QoL for KarmaMC — Plan` - 15 edges
7. `KarmaClient` - 11 edges
8. `SpawnerSetupScreen` - 10 edges
9. `TokenPricesScreen` - 10 edges
10. `AfkCalcTest` - 10 edges

## Surprising Connections (you probably didn't know these)
- `AfkSession` --references--> `AfkActivity`  [EXTRACTED]
  src/main/java/cl/karmamc/client/AfkSession.java → src/main/java/cl/karmamc/client/AfkActivity.java
- `KarmaClient` --references--> `KarmaConfig`  [EXTRACTED]
  src/main/java/cl/karmamc/client/KarmaClient.java → src/main/java/cl/karmamc/client/KarmaConfig.java
- `SidebarReader` --references--> `GemInterval`  [EXTRACTED]
  src/main/java/cl/karmamc/client/SidebarReader.java → src/main/java/cl/karmamc/client/GemInterval.java

## Import Cycles
- None detected.

## Communities (18 total, 13 thin omitted)

### Community 4 - ".shortNumber"
Cohesion: 0.10
Nodes (4): Calc, InvestLeft, CalcTest, ParseTest

### Community 6 - "SidebarReader.java"
Cohesion: 0.13
Nodes (3): GemInterval, SidebarReader, GemIntervalTest

### Community 7 - "KarmaMC Client — Plan"
Cohesion: 0.11
Nodes (17): Aura (pendiente de validar), Catfish69 QoL for KarmaMC — Plan, Decisiones, Fases, Fuentes de datos (lectura de menús), Módulo 1 — HUD, Módulo 2 — `/fish spawnersetup` (comando client-side), Módulo 3 — `/fish tokenprices` (comando client-side) (+9 more)

### Community 9 - "Currency"
Cohesion: 0.29
Nodes (6): Currency, AURA, COINS, GEMS, MISSION_POINTS, MONEY

### Community 15 - "org.junit.jupiter.api.Test"
Cohesion: 0.19
Nodes (6): AfkActivity, Signal, AUTO_START, NONE, STOP, AfkActivityTest

## Knowledge Gaps
- **8 isolated node(s):** `NONE`, `STOP`, `AUTO_START`, `MONEY`, `GEMS` (+3 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 77 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **13 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Parse` connect `Parse` to `.shortNumber`?**
  _High betweenness centrality (0.067) - this node is a cross-community bridge._
- **What connects `NONE`, `STOP`, `AUTO_START` to the rest of the system?**
  _8 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `KarmaConfig.java` be split into smaller, more focused modules?**
  _Cohesion score 0.13157894736842105 - nodes in this community are weakly interconnected._
- **Why does `KarmaClient` connect `MenuReader.java` to `KarmaConfig.java`, `.onInitializeClient`?**
  _High betweenness centrality (0.058) - this node is a cross-community bridge._
- **Should `MenuReader.java` be split into smaller, more focused modules?**
  _Cohesion score 0.08282828282828283 - nodes in this community are weakly interconnected._
- **Why does `AfkSession` connect `.onInitializeClient` to `org.junit.jupiter.api.Test`?**
  _High betweenness centrality (0.048) - this node is a cross-community bridge._
- **Should `.shortNumber` be split into smaller, more focused modules?**
  _Cohesion score 0.10252100840336134 - nodes in this community are weakly interconnected._
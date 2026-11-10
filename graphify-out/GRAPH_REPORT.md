# Graph Report - karma client  (2026-11-09)

## Corpus Check
- 30 files · ~11,136 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 5 file(s) not represented in the graph (top: .properties 2, (none) 1, .jar 1)

## Summary
- 315 nodes · 724 edges · 19 communities (4 shown, 15 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 22 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `fe50fa22`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- .lines
- KarmaClient.java
- Parse
- .shortNumber
- HudEditScreen
- SidebarReader.java
- KarmaMC Client — Plan
- .lines
- Currency
- TokenPricesScreen
- .onInitializeClient
- org.junit.jupiter.api.Test
- KarmaConfig.java
- .lines
- .lines

## God Nodes (most connected - your core abstractions)
1. `Parse` - 24 edges
2. `AfkSession` - 21 edges
3. `AfkActivity` - 19 edges
4. `AfkActivityTest` - 18 edges
5. `ParseTest` - 14 edges
6. `Catfish69 QoL for KarmaMC — Plan` - 12 edges
7. `KarmaClient` - 11 edges
8. `SpawnerSetupScreen` - 10 edges
9. `TokenPricesScreen` - 10 edges
10. `HudEditScreen` - 9 edges

## Surprising Connections (you probably didn't know these)
- `AfkSession` --references--> `AfkActivity`  [EXTRACTED]
  src/main/java/cl/karmamc/client/AfkSession.java → src/main/java/cl/karmamc/client/AfkActivity.java
- `KarmaClient` --references--> `KarmaConfig`  [EXTRACTED]
  src/main/java/cl/karmamc/client/KarmaClient.java → src/main/java/cl/karmamc/client/KarmaConfig.java

## Import Cycles
- None detected.

## Communities (19 total, 15 thin omitted)

### Community 4 - ".shortNumber"
Cohesion: 0.12
Nodes (4): Calc, InvestLeft, CalcTest, ParseTest

### Community 7 - "KarmaMC Client — Plan"
Cohesion: 0.13
Nodes (14): Aura (pendiente de validar), Catfish69 QoL for KarmaMC — Plan, Decisiones, Fases, Fuentes de datos (lectura de menús), Módulo 1 — HUD, Módulo 2 — `/fish spawnersetup` (comando client-side), Módulo 3 — `/fish tokenprices` (comando client-side) (+6 more)

### Community 9 - "Currency"
Cohesion: 0.29
Nodes (6): Currency, AURA, COINS, GEMS, MISSION_POINTS, MONEY

### Community 15 - "org.junit.jupiter.api.Test"
Cohesion: 0.19
Nodes (6): AfkActivity, Signal, AUTO_START, NONE, STOP, AfkActivityTest

## Knowledge Gaps
- **8 isolated node(s):** `NONE`, `STOP`, `AUTO_START`, `MONEY`, `GEMS` (+3 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 73 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **15 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Parse` connect `Parse` to `KarmaClient.java`, `.shortNumber`?**
  _High betweenness centrality (0.069) - this node is a cross-community bridge._
- **What connects `NONE`, `STOP`, `AUTO_START` to the rest of the system?**
  _8 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `KarmaClient.java` be split into smaller, more focused modules?**
  _Cohesion score 0.07665505226480836 - nodes in this community are weakly interconnected._
- **Why does `KarmaClient` connect `KarmaClient.java` to `KarmaConfig.java`?**
  _High betweenness centrality (0.058) - this node is a cross-community bridge._
- **Should `MenuReader.java` be split into smaller, more focused modules?**
  _Cohesion score 0.13333333333333333 - nodes in this community are weakly interconnected._
- **Why does `AfkSession` connect `.onInitializeClient` to `KarmaClient.java`, `MenuReader.java`, `org.junit.jupiter.api.Test`?**
  _High betweenness centrality (0.052) - this node is a cross-community bridge._
- **Should `.shortNumber` be split into smaller, more focused modules?**
  _Cohesion score 0.11742424242424243 - nodes in this community are weakly interconnected._
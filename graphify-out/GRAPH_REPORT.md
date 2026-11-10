# Graph Report - karma client  (2026-11-09)

## Corpus Check
- 30 files · ~10,449 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 5 file(s) not represented in the graph (top: .properties 2, (none) 1, .jar 1)

## Summary
- 297 nodes · 669 edges · 18 communities (4 shown, 14 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 8 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `0fc6d568`
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

## God Nodes (most connected - your core abstractions)
1. `Parse` - 23 edges
2. `AfkSession` - 18 edges
3. `AfkActivityTest` - 18 edges
4. `ParseTest` - 13 edges
5. `KarmaClient` - 11 edges
6. `Catfish69 QoL for KarmaMC — Plan` - 11 edges
7. `SpawnerSetupScreen` - 10 edges
8. `TokenPricesScreen` - 10 edges
9. `AfkActivity` - 9 edges
10. `HudEditScreen` - 9 edges

## Surprising Connections (you probably didn't know these)
- `AfkSession` --references--> `AfkActivity`  [EXTRACTED]
  src/main/java/cl/karmamc/client/AfkSession.java → src/main/java/cl/karmamc/client/AfkActivity.java
- `KarmaClient` --references--> `KarmaConfig`  [EXTRACTED]
  src/main/java/cl/karmamc/client/KarmaClient.java → src/main/java/cl/karmamc/client/KarmaConfig.java

## Import Cycles
- None detected.

## Communities (18 total, 14 thin omitted)

### Community 1 - "KarmaClient.java"
Cohesion: 0.09
Nodes (3): AfkCalc, KarmaClient, AfkCalcTest

### Community 4 - ".shortNumber"
Cohesion: 0.11
Nodes (4): Calc, InvestLeft, CalcTest, ParseTest

### Community 7 - "KarmaMC Client — Plan"
Cohesion: 0.14
Nodes (13): Aura (pendiente de validar), Catfish69 QoL for KarmaMC — Plan, Decisiones, Fases, Fuentes de datos (lectura de menús), Módulo 1 — HUD, Módulo 2 — `/fish spawnersetup` (comando client-side), Módulo 3 — `/fish tokenprices` (comando client-side) (+5 more)

### Community 9 - "Currency"
Cohesion: 0.29
Nodes (6): Currency, AURA, COINS, GEMS, MISSION_POINTS, MONEY

## Knowledge Gaps
- **5 isolated node(s):** `MONEY`, `GEMS`, `AURA`, `MISSION_POINTS`, `COINS`
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 69 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **14 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Parse` connect `Parse` to `KarmaClient.java`, `.shortNumber`?**
  _High betweenness centrality (0.072) - this node is a cross-community bridge._
- **What connects `MONEY`, `GEMS`, `AURA` to the rest of the system?**
  _5 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `KarmaClient.java` be split into smaller, more focused modules?**
  _Cohesion score 0.0873440285204991 - nodes in this community are weakly interconnected._
- **Why does `KarmaClient` connect `KarmaClient.java` to `KarmaConfig.java`, `.onInitializeClient`?**
  _High betweenness centrality (0.055) - this node is a cross-community bridge._
- **Should `MenuReader.java` be split into smaller, more focused modules?**
  _Cohesion score 0.13793103448275862 - nodes in this community are weakly interconnected._
- **Why does `AfkSession` connect `.onInitializeClient` to `MenuReader.java`, `org.junit.jupiter.api.Test`?**
  _High betweenness centrality (0.046) - this node is a cross-community bridge._
- **Should `.shortNumber` be split into smaller, more focused modules?**
  _Cohesion score 0.11363636363636363 - nodes in this community are weakly interconnected._
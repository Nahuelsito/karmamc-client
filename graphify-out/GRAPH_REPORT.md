# Graph Report - karma client  (2026-11-09)

## Corpus Check
- 25 files · ~8,213 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 5 file(s) not represented in the graph (top: .properties 2, (none) 1, .jar 1)

## Summary
- 238 nodes · 487 edges · 15 communities (3 shown, 12 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 4 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `e83f1c75`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- .onInitializeClient
- KarmaClient.java
- Parse
- MenuReader.java
- .shortNumber
- HudEditScreen
- .tick
- KarmaMC Client — Plan
- .lines
- Currency
- TokenPricesScreen
- org.junit.jupiter.api.Test

## God Nodes (most connected - your core abstractions)
1. `Parse` - 22 edges
2. `ParseTest` - 13 edges
3. `KarmaClient` - 10 edges
4. `SpawnerSetupScreen` - 10 edges
5. `TokenPricesScreen` - 10 edges
6. `Catfish69 QoL for KarmaMC — Plan` - 10 edges
7. `HudEditScreen` - 9 edges
8. `Calc` - 8 edges
9. `KarmaHud` - 8 edges
10. `CalcTest` - 8 edges

## Surprising Connections (you probably didn't know these)
- `KarmaClient` --references--> `KarmaConfig`  [EXTRACTED]
  src/main/java/cl/karmamc/client/KarmaClient.java → src/main/java/cl/karmamc/client/KarmaConfig.java

## Import Cycles
- None detected.

## Communities (15 total, 12 thin omitted)

### Community 7 - "KarmaMC Client — Plan"
Cohesion: 0.15
Nodes (12): Aura (pendiente de validar), Catfish69 QoL for KarmaMC — Plan, Decisiones, Fases, Fuentes de datos (lectura de menús), Módulo 1 — HUD, Módulo 2 — `/fish spawnersetup` (comando client-side), Módulo 3 — `/fish tokenprices` (comando client-side) (+4 more)

### Community 9 - "Currency"
Cohesion: 0.29
Nodes (6): Currency, AURA, COINS, GEMS, MISSION_POINTS, MONEY

### Community 14 - "org.junit.jupiter.api.Test"
Cohesion: 0.15
Nodes (5): Calc, InvestLeft, InvestTooltip, CalcTest, InvestTooltipTest

## Knowledge Gaps
- **5 isolated node(s):** `MONEY`, `GEMS`, `AURA`, `MISSION_POINTS`, `COINS`
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 62 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **12 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Parse` connect `Parse` to `KarmaClient.java`, `.shortNumber`?**
  _High betweenness centrality (0.092) - this node is a cross-community bridge._
- **What connects `MONEY`, `GEMS`, `AURA` to the rest of the system?**
  _5 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `.onInitializeClient` be split into smaller, more focused modules?**
  _Cohesion score 0.12318840579710146 - nodes in this community are weakly interconnected._
- **Why does `KarmaClient` connect `KarmaClient.java` to `.onInitializeClient`?**
  _High betweenness centrality (0.063) - this node is a cross-community bridge._
- **Should `KarmaClient.java` be split into smaller, more focused modules?**
  _Cohesion score 0.0907258064516129 - nodes in this community are weakly interconnected._
- **Why does `HudEditScreen` connect `HudEditScreen` to `KarmaClient.java`?**
  _High betweenness centrality (0.044) - this node is a cross-community bridge._
- **Should `MenuReader.java` be split into smaller, more focused modules?**
  _Cohesion score 0.1471861471861472 - nodes in this community are weakly interconnected._
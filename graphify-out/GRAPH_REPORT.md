# Graph Report - karma client  (2026-11-09)

## Corpus Check
- 27 files · ~8,753 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 5 file(s) not represented in the graph (top: .properties 2, (none) 1, .jar 1)

## Summary
- 252 nodes · 531 edges · 16 communities (4 shown, 12 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 4 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `3c0ad52c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- .lines
- KarmaClient.java
- Parse
- .shortNumber
- HudEditScreen
- .strip
- KarmaMC Client — Plan
- org.junit.jupiter.api.Test
- Currency
- TokenPricesScreen
- org.junit.jupiter.api.Test
- .lines

## God Nodes (most connected - your core abstractions)
1. `Parse` - 23 edges
2. `ParseTest` - 13 edges
3. `Catfish69 QoL for KarmaMC — Plan` - 11 edges
4. `KarmaClient` - 10 edges
5. `SpawnerSetupScreen` - 10 edges
6. `TokenPricesScreen` - 10 edges
7. `HudEditScreen` - 9 edges
8. `Calc` - 8 edges
9. `KarmaHud` - 8 edges
10. `CalcTest` - 8 edges

## Surprising Connections (you probably didn't know these)
- `KarmaClient` --references--> `KarmaConfig`  [EXTRACTED]
  src/main/java/cl/karmamc/client/KarmaClient.java → src/main/java/cl/karmamc/client/KarmaConfig.java

## Import Cycles
- None detected.

## Communities (16 total, 12 thin omitted)

### Community 7 - "KarmaMC Client — Plan"
Cohesion: 0.14
Nodes (13): Aura (pendiente de validar), Catfish69 QoL for KarmaMC — Plan, Decisiones, Fases, Fuentes de datos (lectura de menús), Módulo 1 — HUD, Módulo 2 — `/fish spawnersetup` (comando client-side), Módulo 3 — `/fish tokenprices` (comando client-side) (+5 more)

### Community 8 - "org.junit.jupiter.api.Test"
Cohesion: 0.20
Nodes (4): GemShopTooltip, GemsTooltip, GemShopTooltipTest, GemsTooltipTest

### Community 9 - "Currency"
Cohesion: 0.29
Nodes (6): Currency, AURA, COINS, GEMS, MISSION_POINTS, MONEY

### Community 14 - "org.junit.jupiter.api.Test"
Cohesion: 0.20
Nodes (3): Calc, InvestLeft, CalcTest

## Knowledge Gaps
- **5 isolated node(s):** `MONEY`, `GEMS`, `AURA`, `MISSION_POINTS`, `COINS`
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 64 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **12 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Parse` connect `Parse` to `KarmaClient.java`, `.shortNumber`?**
  _High betweenness centrality (0.087) - this node is a cross-community bridge._
- **What connects `MONEY`, `GEMS`, `AURA` to the rest of the system?**
  _5 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `KarmaClient.java` be split into smaller, more focused modules?**
  _Cohesion score 0.07179487179487179 - nodes in this community are weakly interconnected._
- **Why does `HudEditScreen` connect `HudEditScreen` to `KarmaClient.java`?**
  _High betweenness centrality (0.042) - this node is a cross-community bridge._
- **Should `.strip` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._
- **Should `KarmaMC Client — Plan` be split into smaller, more focused modules?**
  _Cohesion score 0.14285714285714285 - nodes in this community are weakly interconnected._
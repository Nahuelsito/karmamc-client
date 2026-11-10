# Graph Report - karma client  (2026-11-09)

## Corpus Check
- 23 files · ~7,550 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 5 file(s) not represented in the graph (top: .properties 2, (none) 1, .jar 1)

## Summary
- 222 nodes · 444 edges · 15 communities (3 shown, 12 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 4 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `ce0ccad3`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- KarmaClient.java
- Parse
- .lines
- HudEditScreen
- .tick
- KarmaMC Client — Plan
- .lines
- Currency
- TokenPricesScreen
- org.junit.jupiter.api.Test

## God Nodes (most connected - your core abstractions)
1. `Parse` - 21 edges
2. `ParseTest` - 12 edges
3. `SpawnerSetupScreen` - 10 edges
4. `TokenPricesScreen` - 10 edges
5. `Catfish69 QoL for KarmaMC — Plan` - 10 edges
6. `HudEditScreen` - 9 edges
7. `KarmaClient` - 9 edges
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
Cohesion: 0.14
Nodes (4): Calc, InvestLeft, CalcTest, ParseTest

## Knowledge Gaps
- **5 isolated node(s):** `MONEY`, `GEMS`, `AURA`, `MISSION_POINTS`, `COINS`
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 59 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **12 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Parse` connect `Parse` to `KarmaClient.java`, `org.junit.jupiter.api.Test`?**
  _High betweenness centrality (0.094) - this node is a cross-community bridge._
- **What connects `MONEY`, `GEMS`, `AURA` to the rest of the system?**
  _5 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `KarmaClient.java` be split into smaller, more focused modules?**
  _Cohesion score 0.11076923076923077 - nodes in this community are weakly interconnected._
- **Why does `SpawnerSetupScreen` connect `TokenPricesScreen` to `KarmaConfig.java`, `KarmaClient.java`?**
  _High betweenness centrality (0.049) - this node is a cross-community bridge._
- **Should `.tick` be split into smaller, more focused modules?**
  _Cohesion score 0.14619883040935672 - nodes in this community are weakly interconnected._
- **Should `org.junit.jupiter.api.Test` be split into smaller, more focused modules?**
  _Cohesion score 0.14112903225806453 - nodes in this community are weakly interconnected._
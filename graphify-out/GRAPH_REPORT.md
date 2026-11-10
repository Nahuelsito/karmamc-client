# Graph Report - karma client  (2026-11-09)

## Corpus Check
- 21 files · ~6,494 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 5 file(s) not represented in the graph (top: .properties 2, (none) 1, .jar 1)

## Summary
- 196 nodes · 370 edges · 14 communities (3 shown, 11 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 3 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- org.junit.jupiter.api.Test
- KarmaClient.java
- Parse
- KarmaHud
- HudEditScreen
- .tick
- KarmaMC Client — Plan
- Currency
- TokenPricesScreen
- org.junit.jupiter.api.Test

## God Nodes (most connected - your core abstractions)
1. `Parse` - 17 edges
2. `SpawnerSetupScreen` - 10 edges
3. `TokenPricesScreen` - 10 edges
4. `HudEditScreen` - 9 edges
5. `KarmaClient` - 8 edges
6. `KarmaHud` - 8 edges
7. `ParseTest` - 8 edges
8. `KarmaMC Client — Plan` - 8 edges
9. `Calc` - 7 edges
10. `MenuReader` - 7 edges

## Surprising Connections (you probably didn't know these)
- `KarmaClient` --references--> `KarmaConfig`  [EXTRACTED]
  src/main/java/cl/karmamc/client/KarmaClient.java → src/main/java/cl/karmamc/client/KarmaConfig.java

## Import Cycles
- None detected.

## Communities (14 total, 11 thin omitted)

### Community 7 - "KarmaMC Client — Plan"
Cohesion: 0.18
Nodes (10): Aura (pendiente de validar), Decisiones, Fases, Fuentes de datos (lectura de menús), KarmaMC Client — Plan, Módulo 1 — HUD, Módulo 2 — `/spawnersetup` (comando client-side), Módulo 3 — `/tokenprices` (comando client-side) (+2 more)

### Community 9 - "Currency"
Cohesion: 0.29
Nodes (6): Currency, AURA, COINS, GEMS, MISSION_POINTS, MONEY

### Community 14 - "org.junit.jupiter.api.Test"
Cohesion: 0.16
Nodes (5): Calc, InvestLeft, InvestTooltip, CalcTest, InvestTooltipTest

## Knowledge Gaps
- **5 isolated node(s):** `MONEY`, `GEMS`, `AURA`, `MISSION_POINTS`, `COINS`
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 58 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **11 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Parse` connect `Parse` to `org.junit.jupiter.api.Test`?**
  _High betweenness centrality (0.092) - this node is a cross-community bridge._
- **What connects `MONEY`, `GEMS`, `AURA` to the rest of the system?**
  _5 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `KarmaClient.java` be split into smaller, more focused modules?**
  _Cohesion score 0.08866995073891626 - nodes in this community are weakly interconnected._
- **Why does `SpawnerSetupScreen` connect `TokenPricesScreen` to `KarmaClient.java`, `HudEditScreen`, `org.junit.jupiter.api.Test`?**
  _High betweenness centrality (0.059) - this node is a cross-community bridge._
- **Should `.tick` be split into smaller, more focused modules?**
  _Cohesion score 0.12987012987012986 - nodes in this community are weakly interconnected._
- **Why does `TokenPricesScreen` connect `TokenPricesScreen` to `KarmaClient.java`, `HudEditScreen`?**
  _High betweenness centrality (0.059) - this node is a cross-community bridge._
# Catfish69 QoL for KarmaMC — Plan

Mod client-side para el servidor KarmaMC. Fabric, Minecraft 26.2, Java 25, nombres oficiales Mojang.
Mod id: `karmaclient`. Nombre: "Catfish69 QoL for KarmaMC" (antes "KarmaMC Client"). Paquete: `cl.karmamc.client`.
Permitido por los admins: solo cálculo y visual. Nada que automatice clicks, envíe paquetes o dé ventaja física.

## Decisiones
- Todo en el HUD se muestra **por hora**.
- Formato de números corto: `150.4k`, `1.5m`, `2.3b` (1 decimal, sin decimal si es entero).
- HUD movible con pantalla de edición (comando `/fish hud` y tecla configurable) arrastrando con mouse. Posición guardada en config.
- Monedas modeladas: Dinero, Gemas, Aura, Puntos de Misión, Coins. Coins y Puntos de Misión no se usan en ningún módulo por ahora.
- No se lee chat por ahora: el ingreso pasivo no se anuncia en chat.
- Valores leídos se guardan en config para que el HUD funcione tras reiniciar sin reabrir menús.

## Fuentes de datos (lectura de menús)
El mod lee título de la pantalla de contenedor y nombre/lore de los ítems **solo de los slots del contenedor** (no el inventario del jugador). Quitar códigos de formato antes de comparar. Los ítems llegan después de abrir: re-escanear mientras la pantalla esté abierta.

| Menú (título) | Línea de lore | Dato |
|---|---|---|
| `Inversiones` | `Ingreso: $721/s` | Dinero/s de invest |
| `Bóveda de Minions` | `Dinero por segundo: $32` (un ítem por minion) | Suma = Dinero/s minions |
| `Nivel de Gemas` | `Ganas 33 Gemas cada 60s` | Gemas/60s |
| `Prestigio` | `1 Aura cada 3 horas` | Aura/3h (tentativo, ver abajo) |
| `Mejora de Venta` | `Progreso: $570.42m/1.5b (38.0%)` | Total vendido = 570.42m |

Ejemplos extra de números que deben parsear: `$51.5M`, `$1.28M`, `$570.42m`, `1.5b`, `$26,000,000`, `$1.000.000`, `$721`.
Regla: con sufijo k/m/b (mayús. o minús.) el punto es decimal; sin sufijo, `.` y `,` en grupos de 3 son separadores de miles.

### Aura (pendiente de validar)
Nadie ha prestigiado aún, así que no sabemos cómo cambia el menú. Por ahora: se parsea `N Aura cada 3 horas` en `Prestigio`, pero la línea de Aura/h solo aparece si el usuario la activa con `/fish aura on`. Cuando alguien prestigie, se manda captura y se ajusta.

## Módulo 1 — HUD
Líneas:
- `Dinero: $X/h` = (invest/s + minions/s) × 3600
- `Gemas: X/h` = gemas/60s × 60
- `Aura: X/h` = aura/3h ÷ 3 (solo si activado)
- `Spawners: $X/h` = resultado del Módulo 2, línea **separada** (no se suma al Dinero)

Línea sin dato todavía: mostrar `?` y el menú a abrir (ej. `Gemas: ? (/gemslevel)`).

## Módulo 2 — `/fish spawnersetup` (comando client-side)
Pantalla con un campo de cantidad por tipo. Dinero/h base por spawner (sin multiplicador):

| Tipo | Precio (gemas) | $/h base |
|---|---|---|
| Pollo | 400 | 3,200 |
| Vaca | 500 | 4,000 |
| Cerdo | 600 | 4,900 |
| Blaze | 750 | 6,000 |
| Esqueleto | 3,000 | 11,600 |
| Creeper | 5,000 | 20,000 |
| Golem | 5,500 | 26,000 |

Total/h = Σ(cantidad × $/h base) × multiplicador actual de venta.
Multiplicador actual = el mayor milestone ≤ total vendido (leído de `Mejora de Venta`). Bajo 10m = 1.0x.
Milestones (solo estos, nada entremedio):
10m→1.1x, 80m→1.2x, 120m→1.3x, 250m→1.4x, 1.5b→1.5x, 5b→1.6x, 7.5b→1.7x,
10b→1.8x, 12.5b→1.9x, 17b→2.0x, 25b→2.5x, 75b→3.0x.
Chequeo: 570.42m vendido → 1.4x (el menú muestra 1.5x como nivel *en progreso*).
La pantalla muestra multiplicador usado, total/h, y sin multiplicador si no se ha leído el menú.

## Módulo 3 — `/fish tokenprices` (comando client-side)
Pantalla con 2 campos (mismo estilo que `/fish spawnersetup`), guardados en config:
- Precio de 1k Gemas. Default `300k`.
- Precio de 1 Aura. Default `200k`.
Aceptan formato corto (`300k`, `1.5m`). Precio de Gemas solo se guarda; no se usa en cálculos aún.

### Trade con impuesto
`/trade` cobra 20% (redondeo hacia abajo) a lo que recibe el comprador. Escenario ideal: trades en múltiplos de 5 (5 enviadas → 4 recibidas).
Para recibir N: comprar `5 × ceil(N / 4)`. Costo = cantidad comprada × precio unitario.
Chequeo: N = 8.052 Aura → compra 10.065 → × $200k = $2.01b.

## Módulo 4 — Invest Left (tooltip en Aurashop)
Menú `Tienda de Aura`, ítem esmeralda `MEJORA DE INVERSIÓN`. Lore relevante:
- `Límite de inversión: +1.000.000` → lo que suma cada compra.
- `Tope del servidor: 320.000.000` → tope.
- `Precio: 30 Aura` → Aura por compra.
Límite actual = número de la derecha en `Inversiones` → `Invertido: $51.5M/51.5M` → 51.5M (guardar en config, campo nuevo `investLimit`).
Aura actual = scoreboard lateral, línea `Aura 18` (leer del sidebar; quitar formato).

Cálculo:
- compras = ceil((tope − límite actual) / incremento); 0 si ya está en el tope.
- auraTotal = compras × precio.
- auraFalta = max(0, auraTotal − auraActual).
- auraComprar = 5 × ceil(auraFalta / 4); costo = auraComprar × precio Aura (Módulo 3).
Chequeo: 320M − 51.5M → 269 compras → 8.070 Aura; tiene 18 → faltan 8.052 → comprar 10.065 → $2.01b.

Se agregan líneas al tooltip del ítem (solo cliente, solo en ese menú), ej.:
`Faltan 269 mejoras (8,070 Aura)` / `Tienes 18 → faltan 8,052 Aura` / `Por /trade: 10,065 Aura ≈ $2b` (formato corto de 1 decimal: 2.013b → `2b`).
Si falta un dato (límite o aura), decir qué abrir: `Abre /invest para calcular`.

## Fases
1. **Planificación** — hecho.
2. **Esqueleto** — hecho.
3. **Parsers y cálculos** — parseo de números, lectura de menús y scoreboard, tabla de multiplicadores, fórmula de trade, cálculo Invest Left; tests JUnit con los ejemplos y chequeos de este plan.
4. **Módulo 1** — HUD + pantalla de edición arrastrable.
5. **Módulos 2 y 3** — `/fish spawnersetup` y `/fish tokenprices`.
6. **Módulo 4** — tooltip Invest Left.
7. **Pulido y entrega** — prueba en servidor, jar final.

Cada fase la construye un subagente; el líder audita (review + build + tests) antes de pasar a la siguiente.

## v1.0.2 — Gemas hasta nivel 100 (tooltip)
Ítem estrella del menú `/gemslevel` (`NIVEL DE GEMS [CLICK]`). Se activa en cualquier ítem cuyo lore tenga `Tu nivel ahora:`; todo se lee del lore del ítem (sin config):
- `Tu nivel ahora: 25` → nivel L. `Nivel 26: cuesta $26,000,000` → costo C del siguiente. `Cada nivel cuesta $1.000.000 mas que el anterior.` → paso S.
- n = 100 − L; total = n×C + S×n(n−1)/2. Chequeo: L=25 → 75 niveles → $4.725b (`Cuesta $4.73b`).
- L ≥ 100 → `Nivel máximo (100)`. Falta un dato → no se agrega nada.

## v1.1.0 — Renombre y prefijo `/fish`
- Nombre visible: `Catfish69 QoL for KarmaMC` (antes `KarmaMC Client`) en `fabric.mod.json`, categoría de teclas y encabezado dorado de los tooltips. Mod Menu ya muestra la versión al lado, no va en el nombre.
- Sin cambios (para conservar ajustes): mod id `karmaclient`, paquete `cl.karmamc.client`, config `config/karmaclient.json`, tecla `key.karmaclient.hud`.
- Todos los comandos del mod bajo una raíz client-side `/fish`. Se eliminan `/karmaclient`, `/spawnersetup` y `/tokenprices`:
  - `/fish hud` — pantalla de edición del HUD.
  - `/fish aura on|off` — mostrar/ocultar Aura/h en el HUD.
  - `/fish spawnersetup` — Módulo 2.
  - `/fish tokenprices` — Módulo 3.
  - `/fish afk calcular <tiempo>` (en v1.1.0 era `/fish afk <tiempo>`; renombrado en v1.1.1) — fase B: imprime en el chat (solo cliente) cuánto rinde cada moneda pasiva en ese tiempo. Tiempo: número con hasta 2 decimales + una unidad `s`/`m`/`h`/`d` (sin distinguir mayúsculas), p. ej. `1h`, `90m`, `1.5d`; inválido o sin argumento → mensaje rojo de uso. Cálculo proporcional (promedios, no intervalos completos), mismos datos que el HUD (desde v1.1.2 gemas y aura cuentan solo intervalos completos, ver v1.1.2):
    - Dinero (verde) = (invest/s + minions/s) × segundos.
    - Gemas (rosa) = gemas/60s × segundos/60.
    - Spawners (verde, solo si hay alguno) = spawners/h × segundos/3600.
    - Aura (rojo, solo si `/fish aura on`) = aura/3h × segundos/10800.
    - Ej.: invest 14 $/s + 1 minion 14 $/s, 1 blaze, gemas nivel 1 (1/60s), `/fish afk calcular 1h` → `Dinero (invest + minions): $100.8k` (desde v1.1.3: `Invest: $50.4k`, `Minions: $50.4k`), `Gemas: 60`, `Spawners: $6k`.
- Los comandos del servidor (`/invest`, `/gemslevel`, `/prestige`, `/trade`) no cambian.

## v1.1.1
- **Tooltip en la Tienda de Gemas** (`/gemshop`, menú `Tienda de Gemas`). En cualquier ítem cuyo lore tenga `Precio: N Gemas` (ej. `GENERADOR BLAZE [CLICK]`: `❙ Gana unos $6.000 por hora`, `❙ Precio: 750 Gemas`, `❙ Comprado: 3.21k veces`) se agrega: línea en blanco, encabezado dorado `Catfish69 QoL for KarmaMC` y `Compras X por hora con tu /gemslevel`, con X = (gemas/60s × 60) / precio (formato corto, 2 decimales). Sin gemas/60s leído → `Abre /gemslevel para calcular`. Solo visual, solo en ese menú.
  - Chequeo: 33 gemas/60s → 1.980/h; precio 750 → `Compras 2.64 por hora con tu /gemslevel`.
- **Renombre del calculador AFK**: `/fish afk <tiempo>` pasa a `/fish afk calcular <tiempo>`; la forma corta deja de existir. `/fish afk calcular` sin tiempo → mensaje rojo de uso (`Ej: /fish afk calcular 1.5h`). `/fish afk` solo no hace nada.
- **Sesión AFK** (`/fish afk start` / `/fish afk stop`). Solo visual/estimación: no envía paquetes, no mueve ni hace clicks.
  - `start` inicia la sesión (si ya hay una: aviso amarillo). `stop` la termina (si no hay: aviso amarillo).
  - Mientras está activa, el HUD agrega debajo de sus líneas `AFK 01:23:05` (tiempo transcurrido, se actualiza cada segundo) y las mismas líneas y colores que `/fish afk calcular` para ese tiempo (dinero, gemas, spawners si hay, aura si `/fish aura on`), recalculadas cada 2 s.
  - Al terminar (comando o auto-stop): desaparece la sección del HUD, el chat muestra `Catfish69 QoL for KarmaMC — AFK terminado <tiempo>` (dorado) + las líneas de `calcular`, y suena el level-up del jugador (solo local).
  - Auto-stop por actividad: reemplazado en v1.1.2 (ver abajo). En v1.1.1 contaba también la cámara y había una Regla B (10 s de actividad continua); ambas se quitaron.
  - Desconexión o cambio de modalidad (nuevo mundo): la sesión termina y el resumen (+ sonido) se muestra al estar en el siguiente mundo.

## v1.1.2
- **Auto-stop de la sesión AFK, solo Regla A**: 5 o más eventos de entrada en cualquier ventana de 10 s → stop (resumen en chat + level-up). Se ignora todo durante los primeros 2 s tras `/fish afk start`.
  - **Evento de entrada**: nueva pulsación (flanco, no mantener) de atacar, usar, adelante, atrás, izquierda, derecha, sprint, saltar o agacharse; o abrir cualquier pantalla (transición sin pantalla → pantalla).
  - Mantener cualquier tecla/botón no detiene la sesión: solo la pulsación inicial cuenta una vez. Toggle-sprint/toggle-sneak se ven como mantenidos → solo cuenta el cambio de estado.
  - La cámara ya no cuenta nunca. La Regla B (10 s de actividad continua) se eliminó.
- **Fin por cobro**: los mensajes del servidor (chat de juego/sistema, sin formato) que empiezan con `[!] Cobraste ...` terminan la sesión por el mismo camino (HUD limpio, resumen, sonido). Ej.: `[!] Cobraste 619,423.00 $.` (invest), `[!] Cobraste $287.35K. Tus minions siguen trabajando.` (minions). Anclado al inicio: el chat de jugadores (con rango/nombre delante) nunca coincide. Sin sesión activa, un cobro reinicia el contador de inactividad.
- **Auto-inicio**: sin sesión activa, estando en un mundo, 5 min sin eventos de entrada (misma definición; los cobros también reinician) → la sesión empieza sola con el contador ya en 05:00 (inicio = hora de la última entrada) y las estimaciones cubriendo esos 5 min. Chat amarillo: `AFK detectado (5 min). Sesión AFK iniciada; /fish afk stop para terminar.` Sin período de gracia.
  - Al terminar cualquier sesión el contador de inactividad vuelve a 0. Fuera de un mundo (menú, carga) el contador se reinicia.
- **Intervalos completos** en `/fish afk calcular` y en la sesión (HUD + resumen):
  - Gemas = floor(segundos / 60) × gemas/60s. Ej.: 90 s → 1 intervalo; 1h → 60.
  - Aura = floor(segundos / 10800) × aura/3h. Ej.: 2h59m → 0; 3h → 1×.
  - Dinero y spawners siguen proporcionales.

## v1.1.3
- `/fish afk` (calcular, HUD y resumen): la línea de dinero se separa en `Invest: $X` y `Minions: $X`, ambas verdes. Orden: AFK {tiempo}, Invest, Minions, Gemas, Spawners (si hay), Aura (si está activada). Dato faltante → `Invest: ? (abre /invest)` / `Minions: ? (abre la bóveda de minions)`.

## v1.1.4
- Multiplicador oculto de gemas por rango de servidor (leído de la propia entrada del TAB) y pase premium (estrella `★` en el TAB). Aditivo: Cobra +15%, Venom +25%, Vyper +35%, Mamba +50%, Hydra +70%, Karma +90%, Karma+ +120%, pase +25%. Abyss multiplica: ×3 × (1 + pase).
- Intervalo de gemas leído del contador del scoreboard `Gemas 19.1k (35s)`: tras cada reinicio, valor > 40 → 60 s, si no → 40 s (outpost). Por defecto 60 s.
- Invest/s se lee de `Inversión $749/s` del scoreboard (incluye outposts); si falta, se usa el valor de /invest.
- HUD por hora, `/fish afk calcular` y la sesión AFK usan: invest del scoreboard, gemas = base /gemslevel × multiplicador cada intervalo.
- El "+Xm en invest" del rango ya viene incluido en el límite leído del servidor: sin cambios en Invest Left.

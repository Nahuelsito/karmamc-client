package cl.karmamc.client;

import java.util.Map;

/** Pure calculations from PLAN.md. */
public final class Calc {
	private Calc() {}

	/** Base money/h per spawner (no multiplier). Keys: lowercase Spanish names without accents. */
	public static final Map<String, Integer> SPAWNER_RATES = Map.of(
			"pollo", 3_200, "vaca", 4_000, "cerdo", 4_900, "blaze", 6_000,
			"esqueleto", 11_600, "creeper", 20_000, "golem", 26_000);

	/** Σ(count × base rate) × sell multiplier; unknown types and null counts are ignored. */
	public static double spawnersPerHour(Map<String, Integer> counts, double totalSold) {
		double base = 0;
		for (var e : counts.entrySet()) {
			Integer rate = SPAWNER_RATES.get(e.getKey());
			if (rate != null && e.getValue() != null) base += (double) e.getValue() * rate;
		}
		return base * sellMultiplier(totalSold);
	}

	private static final double[] MILESTONES = {10e6, 80e6, 120e6, 250e6, 1.5e9, 5e9, 7.5e9, 10e9, 12.5e9, 17e9, 25e9, 75e9};
	private static final double[] MULTIPLIERS = {1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0, 2.5, 3.0};

	/** Highest milestone <= total sold; 1.0x below 10m. */
	public static double sellMultiplier(double totalSold) {
		double mult = 1.0;
		for (int i = 0; i < MILESTONES.length && totalSold >= MILESTONES[i]; i++) mult = MULTIPLIERS[i];
		return mult;
	}

	/** Amount to send through /trade (20% tax, rounded down) so the buyer receives n: 5 x ceil(n/4). */
	public static long tradeBuy(long n) {
		return n <= 0 ? 0 : 5 * ((n + 3) / 4);
	}

	public record InvestLeft(long purchases, long auraTotal, long auraMissing, long auraBuy, double cost) {}

	/** Aura needed to reach the server cap of the Aurashop investment upgrade. */
	public static InvestLeft investLeft(double limit, double cap, double step, long price, long auraHave, double auraPrice) {
		long purchases = limit >= cap || step <= 0 ? 0 : (long) Math.ceil((cap - limit) / step);
		long total = purchases * price;
		long missing = Math.max(0, total - auraHave);
		long buy = tradeBuy(missing);
		return new InvestLeft(purchases, total, missing, buy, buy * auraPrice);
	}

	public static final int GEMS_MAX_LEVEL = 100;

	/** Money to go from gems level `level` to 100: n×cost + step×n(n−1)/2, cost = price of level+1; 0 at max. */
	public static double gemsLeft(long level, double cost, double step) {
		long n = Math.max(0, GEMS_MAX_LEVEL - level);
		return n * cost + step * n * (n - 1) / 2;
	}

	/** Hidden gem multiplier: 1 + rank% + pass 25%, except Abyss which is ×3 on top (Abyss + pass = 3.75). Unknown/null rank = no rank bonus. */
	public static double gemsMultiplier(String rank, boolean pass) {
		double p = pass ? 0.25 : 0;
		if ("Abyss".equals(rank)) return 3 * (1 + p);
		double r = rank == null ? 0 : switch (rank) {
			case "Cobra" -> 0.15;
			case "Venom" -> 0.25;
			case "Vyper" -> 0.35;
			case "Mamba" -> 0.50;
			case "Hydra" -> 0.70;
			case "Karma" -> 0.90;
			case "Karma+" -> 1.20;
			default -> 0;
		};
		return 1 + r + p;
	}
}

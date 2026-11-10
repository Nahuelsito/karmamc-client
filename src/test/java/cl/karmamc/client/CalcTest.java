package cl.karmamc.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CalcTest {
	@ParameterizedTest
	@CsvSource({
			"0, 1.0", "9999999, 1.0",
			"10000000, 1.1", "79999999, 1.1",
			"80000000, 1.2", "120000000, 1.3", "250000000, 1.4",
			"570420000, 1.4", "1499999999, 1.4",
			"1500000000, 1.5", "5000000000, 1.6", "7500000000, 1.7",
			"10000000000, 1.8", "12500000000, 1.9", "17000000000, 2.0",
			"24999999999, 2.0", "25000000000, 2.5", "75000000000, 3.0", "1000000000000, 3.0",
	})
	void sellMultiplier(double sold, double expected) {
		assertEquals(expected, Calc.sellMultiplier(sold));
	}

	@Test
	void tradeBuy() {
		assertEquals(0, Calc.tradeBuy(0));
		assertEquals(0, Calc.tradeBuy(-3));
		assertEquals(5, Calc.tradeBuy(1));
		assertEquals(5, Calc.tradeBuy(4));
		assertEquals(10, Calc.tradeBuy(5));
		assertEquals(10_065, Calc.tradeBuy(8_052));
	}

	@Test
	void investLeftPlanCheck() {
		Calc.InvestLeft r = Calc.investLeft(Parse.number("$51.5M"), 320e6, 1e6, 30, 18, 200_000);
		assertEquals(269, r.purchases());
		assertEquals(8_070, r.auraTotal());
		assertEquals(8_052, r.auraMissing());
		assertEquals(10_065, r.auraBuy());
		assertEquals(2_013_000_000.0, r.cost());
		assertEquals("2b", Parse.shortNumber(r.cost()));
	}

	@Test
	void investLeftAtCap() {
		Calc.InvestLeft r = Calc.investLeft(320e6, 320e6, 1e6, 30, 18, 200_000);
		assertEquals(new Calc.InvestLeft(0, 0, 0, 0, 0), r);
	}

	@Test
	void investLeftEnoughAura() {
		Calc.InvestLeft r = Calc.investLeft(319e6, 320e6, 1e6, 30, 100, 200_000);
		assertEquals(1, r.purchases());
		assertEquals(0, r.auraMissing());
		assertEquals(0, r.auraBuy());
	}

	@Test
	void spawnersPerHour() {
		assertEquals(0, Calc.spawnersPerHour(Map.of(), 0));
		// 2×3200 + 1×26000 = 32400, × 1.4 at 570.42m sold
		assertEquals(32_400 * 1.4, Calc.spawnersPerHour(Map.of("pollo", 2, "golem", 1, "dragon", 5), 570.42e6), 1e-6);
		assertEquals(4_000, Calc.spawnersPerHour(Map.of("vaca", 1), 0));
	}
}

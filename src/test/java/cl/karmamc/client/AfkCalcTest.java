package cl.karmamc.client;

import net.minecraft.ChatFormatting;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AfkCalcTest {
	private static List<String> plain(List<String> lines) {
		return lines.stream().map(ChatFormatting::stripFormatting).toList();
	}

	@Test
	void userExampleOneHour() {
		List<String> lines = AfkCalc.lines("1h", 3600, 14.0, 14.0, 1.0, false, null, Map.of("blaze", 1), 0.0);
		assertEquals(List.of("Catfish69 QoL for KarmaMC — AFK 1h", "Dinero (invest + minions): $100.8k", "Gemas: 60", "Spawners: $6k"),
				plain(lines));
		assertTrue(lines.get(0).startsWith(ChatFormatting.GOLD.toString()));
		assertTrue(lines.get(2).startsWith(ChatFormatting.LIGHT_PURPLE.toString()));
	}

	@Test
	void unknownValuesAndAura() {
		assertEquals(List.of("Catfish69 QoL for KarmaMC — AFK 30m", "Dinero: ? (abre /invest y la bóveda de minions)",
						"Gemas: ? (abre /gemslevel)", "Aura: ? (abre /prestige)"),
				plain(AfkCalc.lines("30m", 1800, null, null, null, true, null, Map.of("blaze", 0), null)));
	}

	@Test
	void gemsAndAuraWholeIntervals() {
		// 1.5h: 90 whole minutes of gems (90 x 1/180 = 0.5), no whole 3h aura interval; spawner line hidden with no spawners
		assertEquals(List.of("Catfish69 QoL for KarmaMC — AFK 1.5h", "Dinero (invest + minions): $3.6k", "Gemas: 0.5", "Aura: 0"),
				plain(AfkCalc.lines("1.5h", 5400, null, 0.6667, 1.0 / 180, true, 1.0, Map.of(), null)));
	}

	private static String gems(double seconds) { return plain(AfkCalc.lines("", seconds, null, null, 2.0, true, 3.0, Map.of(), null)).get(2); }

	private static String aura(double seconds) { return plain(AfkCalc.lines("", seconds, null, null, 2.0, true, 3.0, Map.of(), null)).get(3); }

	@Test
	void gemsCountOnlyFullMinutes() {
		assertEquals("Gemas: 0", gems(59));
		assertEquals("Gemas: 2", gems(90)); // 1 interval x 2
		assertEquals("Gemas: 120", gems(3600)); // 60 intervals
	}

	@Test
	void auraCountsOnlyFullThreeHours() {
		assertEquals("Aura: 0", aura(2 * 3600 + 59 * 60));
		assertEquals("Aura: 3", aura(3 * 3600)); // 1 interval x 3
		assertEquals("Aura: 3", aura(5 * 3600 + 59 * 60));
		assertEquals("Aura: 6", aura(6 * 3600));
	}
}

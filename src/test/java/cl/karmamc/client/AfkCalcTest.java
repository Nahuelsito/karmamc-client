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
		List<String> lines = AfkCalc.lines("1h", 3600, 14.0, 14.0, 1.0, 60, false, null, Map.of("blaze", 1), 0.0);
		assertEquals(List.of("Catfish69 QoL for KarmaMC — AFK 1h", "Invest: $50.4k", "Minions: $50.4k", "Gemas: 60", "Spawners: $6k"),
				plain(lines));
		assertTrue(lines.get(0).startsWith(ChatFormatting.GOLD.toString()));
		assertTrue(lines.get(1).startsWith(ChatFormatting.GREEN.toString()));
		assertTrue(lines.get(2).startsWith(ChatFormatting.GREEN.toString()));
		assertTrue(lines.get(3).startsWith(ChatFormatting.LIGHT_PURPLE.toString()));
	}

	@Test
	void unknownValuesAndAura() {
		assertEquals(List.of("Catfish69 QoL for KarmaMC — AFK 30m", "Invest: ? (abre /invest)", "Minions: ? (abre la bóveda de minions)",
						"Gemas: ? (abre /gemslevel)", "Aura: ? (abre /prestige)"),
				plain(AfkCalc.lines("30m", 1800, null, null, null, 60, true, null, Map.of("blaze", 0), null)));
	}

	@Test
	void gemsAndAuraWholeIntervals() {
		// 1.5h: 90 whole minutes of gems (90 x 1/180 = 0.5), no whole 3h aura interval; spawner line hidden with no spawners
		assertEquals(List.of("Catfish69 QoL for KarmaMC — AFK 1.5h", "Invest: ? (abre /invest)", "Minions: $3.6k", "Gemas: 0.5", "Aura: 0"),
				plain(AfkCalc.lines("1.5h", 5400, null, 0.6667, 1.0 / 180, 60, true, 1.0, Map.of(), null)));
	}

	private static String gems(double seconds) { return plain(AfkCalc.lines("", seconds, null, null, 2.0, 60, true, 3.0, Map.of(), null)).get(3); }

	private static String aura(double seconds) { return plain(AfkCalc.lines("", seconds, null, null, 2.0, 60, true, 3.0, Map.of(), null)).get(4); }

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

	@Test
	void outpostInterval() {
		// 40 s interval, 15 per payout; 100 s = 2 whole payouts
		double perPayout = 15;
		assertEquals("Gemas: 30", plain(AfkCalc.lines("", 100, null, null, perPayout, 40, false, null, Map.of(), null)).get(3));
		assertEquals("Gemas: 1.35k", plain(AfkCalc.lines("", 3600, null, null, perPayout, 40, false, null, Map.of(), null)).get(3));
	}
}

package cl.karmamc.client;

import net.minecraft.ChatFormatting;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GemsTooltipTest {
	private static List<String> plain(List<String> lines) {
		return lines.stream().map(ChatFormatting::stripFormatting).toList();
	}

	@Test
	void planCheck() {
		assertEquals(List.of("", "KarmaMC Client", "Faltan 75 niveles para nivel 100", "Cuesta $4.73b"),
				plain(GemsTooltip.lines(25.0, 26e6, 1e6)));
	}

	@Test
	void level99() {
		assertEquals(List.of("", "KarmaMC Client", "Faltan 1 nivel para nivel 100", "Cuesta $100m"),
				plain(GemsTooltip.lines(99.0, 100e6, 1e6)));
	}

	@Test
	void maxLevel() {
		assertEquals(List.of("", "KarmaMC Client", "Nivel máximo (100)"),
				plain(GemsTooltip.lines(100.0, null, null)));
	}

	@Test
	void missingValues() {
		assertEquals(List.of(), GemsTooltip.lines(null, 26e6, 1e6));
		assertEquals(List.of(), GemsTooltip.lines(25.0, null, 1e6));
		assertEquals(List.of(), GemsTooltip.lines(25.0, 26e6, null));
	}
}

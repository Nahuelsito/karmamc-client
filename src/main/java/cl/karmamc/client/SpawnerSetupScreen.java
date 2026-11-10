package cl.karmamc.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** /spawnersetup: owned count per spawner type, live $/h; counts saved on close. */
public class SpawnerSetupScreen extends Screen {
	private static final String[] KEYS = {"pollo", "vaca", "cerdo", "blaze", "esqueleto", "creeper", "golem"};
	private static final String[] LABELS = {"Pollo", "Vaca", "Cerdo", "Blaze", "Esqueleto", "Creeper", "Golem"};
	private static final int ROW = 16, LABEL_W = 60, BOX_W = 50, W = 200;
	private final EditBox[] boxes = new EditBox[KEYS.length];
	private int left, top;

	public SpawnerSetupScreen() {
		super(Component.literal("Spawner Setup"));
	}

	@Override
	protected void init() {
		left = (width - W) / 2;
		top = (height - (14 + KEYS.length * ROW + 52)) / 2 + 14;
		for (int i = 0; i < KEYS.length; i++) {
			Integer n = KarmaClient.config.spawners.get(KEYS[i]);
			EditBox box = new EditBox(font, left + LABEL_W, top + i * ROW, BOX_W, 14, Component.literal(LABELS[i]));
			box.setMaxLength(6);
			// init() reruns on resize: keep what was typed
			box.setValue(boxes[i] != null ? boxes[i].getValue() : n == null || n == 0 ? "" : n.toString());
			digitsOnly(box);
			boxes[i] = addRenderableWidget(box);
		}
		setInitialFocus(boxes[0]);
	}

	/** 26.2 EditBox has no setFilter, so strip non-digits after each change. */
	private static void digitsOnly(EditBox box) {
		box.setResponder(s -> {
			if (!s.matches("\\d*")) box.setValue(s.replaceAll("\\D", ""));
		});
	}

	private Map<String, Integer> counts() {
		Map<String, Integer> m = new HashMap<>();
		for (int i = 0; i < KEYS.length; i++) {
			String v = boxes[i].getValue();
			m.put(KEYS[i], v.isEmpty() ? 0 : Integer.parseInt(v));
		}
		return m;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
		super.extractRenderState(g, mouseX, mouseY, delta);
		g.centeredText(font, title, width / 2, top - 14, 0xFFFFFFFF);
		Map<String, Integer> counts = counts();
		for (int i = 0; i < KEYS.length; i++) {
			int y = top + i * ROW + 3;
			g.text(font, LABELS[i], left, y, 0xFFFFFFFF);
			double rate = (double) counts.get(KEYS[i]) * Calc.SPAWNER_RATES.get(KEYS[i]);
			g.text(font, "$" + Parse.shortNumber(rate) + "/h", left + LABEL_W + BOX_W + 6, y, 0xFFAAAAAA);
		}
		Double sold = KarmaClient.config.totalSold;
		double base = Calc.spawnersPerHour(counts, 0);
		double mult = sold == null ? 1.0 : Calc.sellMultiplier(sold);
		int y = top + KEYS.length * ROW + 4;
		g.text(font, "Base: $" + Parse.shortNumber(base) + "/h", left, y, 0xFFFFFFFF);
		g.text(font, "Multiplicador: " + (sold == null ? "1.0x (abre Mejora de Venta en /sell)"
				: String.format(Locale.ROOT, "%.1fx", mult)), left, y + 12, 0xFFFFFFFF);
		g.text(font, "Total: $" + Parse.shortNumber(base * mult) + "/h", left, y + 24, 0xFF55FF55);
		g.text(font, "En 24h: $" + Parse.shortNumber(base * mult * 24), left, y + 36, 0xFF55FF55);
	}

	@Override
	public void removed() {
		KarmaClient.config.spawners.putAll(counts());
		KarmaClient.config.save();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}

package cl.karmamc.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** /fish tokenprices: Gem and Aura prices in short format; only valid values are saved on close. */
public class TokenPricesScreen extends Screen {
	private static final int W = 160;
	private EditBox gems, aura;
	private int left, top;

	public TokenPricesScreen() {
		super(Component.literal("Precios de Tokens"));
	}

	@Override
	protected void init() {
		left = (width - W) / 2;
		top = (height - 80) / 2;
		// init() reruns on resize: keep what was typed
		gems = addRenderableWidget(priceBox(top + 26, gems, KarmaClient.config.gemPricePer1k));
		aura = addRenderableWidget(priceBox(top + 62, aura, KarmaClient.config.auraPrice));
		setInitialFocus(gems);
	}

	private EditBox priceBox(int y, EditBox old, double value) {
		EditBox box = new EditBox(font, left, y, W, 14, Component.empty());
		box.setResponder(s -> box.setTextColor(parse(s) == null ? 0xFFFF5555 : EditBox.DEFAULT_TEXT_COLOR));
		box.setValue(old != null ? old.getValue() : Parse.shortNumber(value, 3));
		return box;
	}

	/** Valid price = parseable and > 0. */
	private static Double parse(String s) {
		Double v = Parse.number(s);
		return v == null || v <= 0 ? null : v;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
		super.extractRenderState(g, mouseX, mouseY, delta);
		g.centeredText(font, title, width / 2, top, 0xFFFFFFFF);
		g.text(font, "Precio por 1k Gemas", left, top + 16, 0xFFFFFFFF);
		g.text(font, "Precio por 1 Aura", left, top + 52, 0xFFFFFFFF);
	}

	@Override
	public void removed() {
		Double g = parse(gems.getValue()), a = parse(aura.getValue());
		if (g != null) KarmaClient.config.gemPricePer1k = g;
		if (a != null) KarmaClient.config.auraPrice = a;
		KarmaClient.config.save();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}

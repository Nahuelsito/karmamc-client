package cl.karmamc.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/** Drag the HUD with the left mouse button; position is saved when the screen closes. */
public class HudEditScreen extends Screen {
	private int[] box = {0, 0, 0, 0};
	private boolean dragging;
	private double offX, offY;

	public HudEditScreen() {
		super(Component.literal("KarmaMC HUD"));
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
		super.extractRenderState(g, mouseX, mouseY, delta);
		box = KarmaHud.draw(g, font);
		g.outline(box[0] - 1, box[1] - 1, box[2] + 2, box[3] + 2, 0xFFFFFF55);
		g.centeredText(font, "Arrastra el HUD con el mouse. Esc para guardar.", width / 2, height - 20, 0xFFFFFFFF);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent e, boolean doubleClick) {
		if (e.button() == 0 && e.x() >= box[0] && e.x() < box[0] + box[2] && e.y() >= box[1] && e.y() < box[1] + box[3]) {
			dragging = true;
			offX = e.x() - box[0];
			offY = e.y() - box[1];
			return true;
		}
		return super.mouseClicked(e, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
		if (!dragging) return super.mouseDragged(e, dx, dy);
		KarmaClient.config.hudX = Math.max(0, Math.min((int) (e.x() - offX), width - box[2]));
		KarmaClient.config.hudY = Math.max(0, Math.min((int) (e.y() - offY), height - box[3]));
		return true;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent e) {
		if (e.button() == 0) dragging = false;
		return super.mouseReleased(e);
	}

	@Override
	public void removed() {
		KarmaClient.config.save();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}

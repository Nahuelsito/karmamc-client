package cl.karmamc.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;

/** `/fish afk start|stop`: tracks an AFK session, shows it on the HUD and prints a summary at the end. Display only. */
public final class AfkSession {
	private AfkSession() {}

	private static final int RECALC_TICKS = 40;

	private static AfkActivity activity;
	private static long startNanos;
	private static int ticks;
	private static List<String> cachedEstimates = List.of();
	private static float prevYaw, prevPitch;
	private static boolean hasPrevRotation;
	private static ClientLevel level;
	/** Summary of a session that ended while leaving a world; printed once the player is in another one. */
	private static List<String> pending;
	private static ClientLevel pendingLevel;

	public static boolean active() { return activity != null; }

	public static String start(Minecraft mc) {
		if (active()) return ChatFormatting.YELLOW + "Ya hay una sesión AFK activa. Usa /fish afk stop para terminarla.";
		activity = new AfkActivity();
		startNanos = System.nanoTime();
		ticks = 0;
		hasPrevRotation = false;
		level = mc.level;
		cachedEstimates = estimates(0);
		return ChatFormatting.GREEN + "Sesión AFK iniciada. Se detiene sola si te mueves, haces clicks o abres menús.";
	}

	/** Stops from the command; returns null when a session was running (the summary is printed), else a reply. */
	public static String stop(Minecraft mc) {
		if (!active()) return ChatFormatting.YELLOW + "No hay ninguna sesión AFK activa. Usa /fish afk start.";
		finish(mc);
		return null;
	}

	/** Disconnect: end the session now, print the summary in the next world. */
	public static void onDisconnect(Minecraft mc) {
		if (!active()) return;
		pending = summary();
		pendingLevel = level;
		activity = null;
	}

	public static void tick(Minecraft mc) {
		if (pending != null && mc.player != null && mc.level != null && mc.level != pendingLevel) {
			print(mc, pending);
			pending = null;
			pendingLevel = null;
		}
		if (!active()) return;
		if (mc.level != level) { // server switch (new world instance) or disconnect
			onDisconnect(mc);
			return;
		}
		if (mc.player == null) return;
		float yaw = mc.player.getYRot(), pitch = mc.player.getXRot();
		float camera = hasPrevRotation ? Math.abs(yaw - prevYaw) + Math.abs(pitch - prevPitch) : 0;
		prevYaw = yaw;
		prevPitch = pitch;
		hasPrevRotation = true;
		var o = mc.options;
		int keys = bit(o.keyAttack.isDown(), AfkActivity.ATTACK) | bit(o.keyUse.isDown(), AfkActivity.USE)
				| bit(o.keyUp.isDown(), AfkActivity.FORWARD) | bit(o.keyDown.isDown(), AfkActivity.BACK)
				| bit(o.keyLeft.isDown(), AfkActivity.LEFT) | bit(o.keyRight.isDown(), AfkActivity.RIGHT)
				| bit(o.keySprint.isDown(), AfkActivity.SPRINT);
		if (activity.tick(keys, camera, mc.gui.screen() != null)) {
			finish(mc);
			return;
		}
		if (++ticks % RECALC_TICKS == 0) cachedEstimates = estimates(elapsedSeconds());
	}

	/** HUD section: timer (live, whole seconds) + estimates cached every 2 s. Empty when no session. */
	public static List<String> hudLines() {
		if (!active()) return List.of();
		List<String> out = new ArrayList<>();
		out.add(ChatFormatting.GOLD + "AFK " + clock(elapsedSeconds()));
		out.addAll(cachedEstimates);
		return out;
	}

	private static void finish(Minecraft mc) {
		List<String> lines = summary();
		activity = null;
		print(mc, lines);
	}

	private static List<String> summary() {
		double s = elapsedSeconds();
		return lines("terminado " + clock(s), s);
	}

	private static void print(Minecraft mc, List<String> lines) {
		if (mc.player == null) return;
		for (String line : lines) mc.player.sendSystemMessage(Component.literal(line));
		mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1f));
	}

	private static List<String> estimates(double seconds) {
		List<String> all = lines("", seconds);
		return all.subList(1, all.size()); // drop the chat header
	}

	private static List<String> lines(String label, double seconds) {
		KarmaConfig c = KarmaClient.config;
		return AfkCalc.lines(label, seconds, c.investPerSecond, c.minionsPerSecond, c.gemsPer60s, c.showAura, c.auraPer3h,
				c.spawners, c.totalSold);
	}

	private static double elapsedSeconds() { return Math.floor((System.nanoTime() - startNanos) / 1e9); }

	static String clock(double seconds) {
		long s = (long) seconds;
		return String.format("%02d:%02d:%02d", s / 3600, s / 60 % 60, s % 60);
	}

	private static int bit(boolean down, int bit) { return down ? bit : 0; }
}

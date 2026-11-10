package cl.karmamc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

/** Reads `Aura N` from the sidebar the same way vanilla Hud picks and renders it. */
public final class SidebarReader {
	private SidebarReader() {}

	/** Latest Aura seen on the sidebar; null until first seen. In memory only. */
	public static Double aura;

	public static void tick(Minecraft mc) {
		if (mc.level == null || mc.player == null) return;
		Scoreboard sb = mc.level.getScoreboard();
		Objective obj = null;
		PlayerTeam own = sb.getPlayersTeam(mc.player.getScoreboardName());
		if (own != null) obj = own.getColor().map(color -> sb.getDisplayObjective(color.displaySlot())).orElse(null);
		if (obj == null) obj = sb.getDisplayObjective(DisplaySlot.SIDEBAR);
		if (obj == null) return;
		NumberFormat fmt = obj.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);
		for (PlayerScoreEntry e : sb.listPlayerScores(obj)) {
			if (e.isHidden()) continue;
			// Servers usually put the text in team prefix/suffix; the score itself may be the number or blank.
			String name = MenuReader.strip(PlayerTeam.formatNameForTeam(sb.getPlayersTeam(e.owner()), e.ownerName()));
			String value = MenuReader.strip(e.formatValue(fmt));
			Double v = Parse.scoreboardAura(name + " " + value);
			if (v != null) {
				aura = v;
				return;
			}
		}
	}
}

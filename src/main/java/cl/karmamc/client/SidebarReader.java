package cl.karmamc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

/** Reads `Aura N`, `Inversión $N/s` and the gem countdown from the sidebar the same way vanilla Hud picks and renders it, plus rank/pass from the own tab-list entry. */
public final class SidebarReader {
	private SidebarReader() {}

	/** Latest Aura seen on the sidebar; null until first seen. In memory only. */
	public static Double aura;
	/** Sidebar `Inversión` money/s this tick; null when absent. */
	public static Double investPerSecond;
	/** Own server rank (canonical, see {@link Parse#serverRank}) and premium pass from the tab list. */
	public static String rank;
	public static boolean premiumPass;
	public static final GemInterval GEM_INTERVAL = new GemInterval();

	/** Invest money/s: sidebar value when shown, else the one from /invest. */
	public static Double effectiveInvest() {
		return investPerSecond != null ? investPerSecond : KarmaClient.config.investPerSecond;
	}

	/** Gems per payout: /gemslevel base × rank/pass multiplier; null when the base is unknown. */
	public static Double effectiveGemsPerPayout() {
		Double base = KarmaClient.config.gemsPer60s;
		return base == null ? null : base * Calc.gemsMultiplier(rank, premiumPass);
	}

	public static void onDisconnect() {
		investPerSecond = null;
		rank = null;
		premiumPass = false;
		GEM_INTERVAL.reset();
	}

	public static void tick(Minecraft mc) {
		if (mc.level == null || mc.player == null) return;
		readTab(mc);
		Scoreboard sb = mc.level.getScoreboard();
		Objective obj = null;
		PlayerTeam own = sb.getPlayersTeam(mc.player.getScoreboardName());
		if (own != null) obj = own.getColor().map(color -> sb.getDisplayObjective(color.displaySlot())).orElse(null);
		if (obj == null) obj = sb.getDisplayObjective(DisplaySlot.SIDEBAR);
		Double invest = null;
		if (obj != null) {
			NumberFormat fmt = obj.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);
			for (PlayerScoreEntry e : sb.listPlayerScores(obj)) {
				if (e.isHidden()) continue;
				// Servers usually put the text in team prefix/suffix; the score itself may be the number or blank.
				String name = MenuReader.strip(PlayerTeam.formatNameForTeam(sb.getPlayersTeam(e.owner()), e.ownerName()));
				String line = name + " " + MenuReader.strip(e.formatValue(fmt));
				Double a = Parse.scoreboardAura(line);
				if (a != null) aura = a;
				Double i = Parse.scoreboardInvest(line);
				if (i != null) invest = i;
				Integer countdown = Parse.scoreboardGemsCountdown(line);
				if (countdown != null) GEM_INTERVAL.update(countdown);
			}
		}
		investPerSecond = invest;
	}

	private static void readTab(Minecraft mc) {
		if (mc.getConnection() == null) return;
		PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
		if (info == null) return;
		Component c = info.getTabListDisplayName();
		if (c == null) c = PlayerTeam.formatNameForTeam(info.getTeam(), mc.player.getName());
		String text = MenuReader.strip(c);
		rank = Parse.serverRank(text);
		premiumPass = Parse.hasPremiumPass(text);
	}
}

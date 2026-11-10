package cl.karmamc.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class KarmaClient implements ClientModInitializer {
	public static final String MOD_ID = "karmaclient";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static KarmaConfig config;
	/** Set by commands/keys; opened next tick because chat closes after running the command. */
	private static Screen pendingScreen;

	@Override
	public void onInitializeClient() {
		config = KarmaConfig.load();
		KeyMapping hudKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.karmaclient.hud", -1,
				KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"))));
		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "hud"),
				(g, delta) -> KarmaHud.extract(g, Minecraft.getInstance()));
		InvestTooltip.register();
		GemsTooltip.register();
		GemShopTooltip.register();
		ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
			String line = MenuReader.strip(message);
			if (Parse.investUpgradeBought(line)) onInvestUpgradeBought();
			if (Parse.claim(line)) AfkSession.onClaim();
		});
		ClientTickEvents.END_CLIENT_TICK.register(mc -> {
			MenuReader.tick(mc);
			SidebarReader.tick(mc);
			AfkSession.tick(mc);
			while (hudKey.consumeClick()) pendingScreen = new HudEditScreen();
			if (pendingScreen != null) {
				mc.gui.setScreen(pendingScreen);
				pendingScreen = null;
			}
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, mc) -> AfkSession.onDisconnect(mc));

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			// All mod commands live under /fish.
			dispatcher.register(literal("fish")
						.then(literal("hud").executes(ctx -> open(new HudEditScreen())))
						.then(literal("aura")
								.then(literal("on").executes(ctx -> setAura(ctx, true)))
								.then(literal("off").executes(ctx -> setAura(ctx, false))))
						.then(literal("spawnersetup").executes(ctx -> open(new SpawnerSetupScreen())))
						.then(literal("tokenprices").executes(ctx -> open(new TokenPricesScreen())))
						.then(literal("afk")
								.then(literal("start").executes(ctx -> reply(ctx, AfkSession.start(ctx.getSource().getClient()))))
								.then(literal("stop").executes(ctx -> reply(ctx, AfkSession.stop(ctx.getSource().getClient()))))
								.then(literal("calcular")
										.executes(ctx -> afk(ctx, null))
										.then(argument("tiempo", StringArgumentType.greedyString())
												.executes(ctx -> afk(ctx, StringArgumentType.getString(ctx, "tiempo")))))));
		});
	}

	private static int reply(CommandContext<FabricClientCommandSource> ctx, String message) {
		if (message != null) ctx.getSource().sendFeedback(Component.literal(message));
		return 1;
	}

	private static int open(Screen screen) {
		pendingScreen = screen;
		return 1;
	}

	/** Each Aurashop purchase raises the invest limit by one step (capped), so Invest Left stays current without reopening /invest. */
	private static void onInvestUpgradeBought() {
		if (config.investLimit == null) return;
		double step = config.auraShopStep != null ? config.auraShopStep : 1_000_000;
		double next = config.investLimit + step;
		config.investLimit = config.auraShopCap != null ? Math.min(next, config.auraShopCap) : next;
		config.save();
	}

	private static int afk(CommandContext<FabricClientCommandSource> ctx, String input) {
		Double seconds = Parse.duration(input);
		if (seconds == null) {
			ctx.getSource().sendFeedback(Component.literal(AfkCalc.USAGE));
			return 0;
		}
		KarmaConfig c = config;
		for (String line : AfkCalc.lines(input.trim().toLowerCase(Locale.ROOT), seconds, c.investPerSecond, c.minionsPerSecond,
				c.gemsPer60s, c.showAura, c.auraPer3h, c.spawners, c.totalSold))
			ctx.getSource().sendFeedback(Component.literal(line));
		return 1;
	}

	private static int setAura(CommandContext<FabricClientCommandSource> ctx, boolean on) {
		config.showAura = on;
		config.save();
		ctx.getSource().sendFeedback(Component.literal("Aura/h en HUD: " + (on ? "activado" : "desactivado")));
		return 1;
	}
}

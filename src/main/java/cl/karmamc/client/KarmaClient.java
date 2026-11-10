package cl.karmamc.client;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
			if (Parse.investUpgradeBought(MenuReader.strip(message))) onInvestUpgradeBought();
		});
		ClientTickEvents.END_CLIENT_TICK.register(mc -> {
			MenuReader.tick(mc);
			SidebarReader.tick(mc);
			while (hudKey.consumeClick()) pendingScreen = new HudEditScreen();
			if (pendingScreen != null) {
				mc.gui.setScreen(pendingScreen);
				pendingScreen = null;
			}
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(literal("spawnersetup").executes(ctx -> open(new SpawnerSetupScreen())));
			dispatcher.register(literal("tokenprices").executes(ctx -> open(new TokenPricesScreen())));
			dispatcher.register(literal("karmaclient")
						.then(literal("aura")
								.then(literal("on").executes(ctx -> setAura(ctx, true)))
								.then(literal("off").executes(ctx -> setAura(ctx, false))))
						.then(literal("hud").executes(ctx -> open(new HudEditScreen()))));
		});
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

	private static int setAura(CommandContext<FabricClientCommandSource> ctx, boolean on) {
		config.showAura = on;
		config.save();
		ctx.getSource().sendFeedback(Component.literal("Aura/h en HUD: " + (on ? "activado" : "desactivado")));
		return 1;
	}
}

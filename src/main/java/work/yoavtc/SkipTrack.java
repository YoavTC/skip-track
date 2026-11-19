package work.yoavtc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.network.chat.Component;

public class SkipTrack implements ClientModInitializer {
	public static final String MOD_ID = "skip-track";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static KeyMapping skipTrackKeybind;
	private static MusicManager musicManager;

	@Override
	public void onInitializeClient() {
		musicManager = Minecraft.getInstance().getMusicManager();
		
		SkipTrackConfig.load();
		registerKeybind();
		registerPauseButton();
		SkipTrackConfigScreen.foo(skipTrackKeybind);
	}
	
	// Main function that actually does the track skipping
	private void SkipCurrentTrack() {
		if (musicManager != null) {
			musicManager.stopPlaying();
			Music music = Minecraft.getInstance().getSituationalMusic();
			if (music != null) {
				musicManager.startPlaying(music);
			}
		} else { // MusicManager sometimes gets either reassigned or removed or something, so just reassign it if null
			LOGGER.error("Music Manager is null, trying to reassign...");
			musicManager = Minecraft.getInstance().getMusicManager();
			LOGGER.info("Music Manager is now " + (musicManager == null ? "null" : "not null! Recalling function.."));
			if (musicManager != null) {
				SkipCurrentTrack();
			}
		}
	}
	
	private void registerKeybind() {
		skipTrackKeybind = KeyMappingHelper.registerKeyMapping(new KeyMapping("Skip Track", InputConstants.Type.KEYBOARD, 0, KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "skip"))));
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (skipTrackKeybind.consumeClick()) {
				SkipCurrentTrack();
			}
		});
	}
	
	private void registerPauseButton() {
		ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
			if (SkipTrackConfig.drawButton && screen instanceof PauseScreen) {
				Screens.getWidgets(screen).add(Button.builder(Component.literal("Skip Track"), _ -> SkipCurrentTrack())
					.bounds(5, h - 25, 75, 20)
					.build());
			}
		});
	}

	public static Identifier id(@NonNull String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}

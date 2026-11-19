package work.yoavtc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;

import net.fabricmc.loader.api.FabricLoader;

public final class SkipTrackConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final String drawButtonConfigKey = "draw_button";
    public static boolean drawButton = true; // true by default, gets reassigned below if specified in config file

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("skip_track.json");
    }

    public static void load() {
        Path path = path();
        try {
			if (Files.exists(path)) {
				JsonObject json = GSON.fromJson(Files.readString(path), JsonObject.class);
				if (json != null && json.has(drawButtonConfigKey)) {
					drawButton = json.get(drawButtonConfigKey).getAsBoolean();
				}
				if (json == null || !json.has(drawButtonConfigKey)) {
					writeDefault(path);
				}
			} else {
				writeDefault(path);
			}
		} catch (IOException | RuntimeException e) {
			SkipTrack.LOGGER.warn("Could not read config file. Try deleting it and restarting the game. Error: " + e);
		}
    }

    private static void writeDefault(Path path) throws IOException {
		write(path); // drawButton still holds its default here
	}

    private static void write(Path path) throws IOException {
		JsonObject json = new JsonObject();
		json.addProperty(drawButtonConfigKey, drawButton);
		Files.writeString(path, GSON.toJson(json));
	}

    public static void save() {
		try {
			write(path());
		} catch (IOException e) {
			SkipTrack.LOGGER.warn("Could not save config file. Error: " + e);
		}
	}
}

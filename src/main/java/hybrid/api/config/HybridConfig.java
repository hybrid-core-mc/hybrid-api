package hybrid.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import hybrid.api.HybridApi;
import hybrid.api.mods.HybridMod;
import hybrid.api.mods.HybridMods;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HybridConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static File CLIENT_FOLDER = FabricLoader.getInstance().getGameDir().resolve(HybridApi.MOD_ID).toFile();
    public static File THEME_FOLDER = new File(CLIENT_FOLDER + File.separator + "themes");

    public static void init() {

        createFolders(CLIENT_FOLDER);

        for (HybridMod hybridMod : HybridMods.mods) {
            File modFolder = new File(CLIENT_FOLDER, hybridMod.getName());
            hybridMod.setFolder(modFolder);
            createFolders(modFolder);

            loadModConfig(hybridMod);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(HybridConfig::onShutdown));
    }


    private static void loadModConfig(HybridMod mod) {
        File modConfig = new File(mod.getFolder(), mod.getName() + "-config.json");

        if (!modConfig.exists()) return;

        try (FileReader reader = new FileReader(modConfig)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            if (json != null) {
                mod.loadJson(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void onShutdown() {

        for (HybridMod mod : HybridMods.mods) {
            File modFolder = mod.getFolder();

            File modConfig = new File(modFolder, mod.getName() + "-config.json");

            try {
                if (!modConfig.exists()) {
                    modFolder.mkdirs();
                    modConfig.createNewFile();
                }

                JsonObject object = mod.getJson();

                try (FileWriter writer = new FileWriter(modConfig)) {
                    GSON.toJson(object, writer);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createFolders(File... folders) {
        for (File folder : folders) {
            if (folder != null && !folder.exists()) {
                folder.mkdirs();
            }
        }
    }
}
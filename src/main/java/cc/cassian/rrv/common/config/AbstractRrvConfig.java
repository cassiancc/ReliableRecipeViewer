package cc.cassian.rrv.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

public abstract class AbstractRrvConfig {

    private final String fileName;
    private JsonObject data;

    protected AbstractRrvConfig(String fileName) {
        this.fileName = fileName;
        this.data = new JsonObject();
    }

    public JsonObject data() {
        return this.data;
    }


    protected abstract void loadData();

    protected abstract void saveData();

    public void load() {
        try {
            File file = ReliableRecipeViewer.CONFIG_PATH.resolve(this.fileName + ".json").toFile();

            if (!file.exists()) {
                ReliableRecipeViewer.LOGGER.info("Config file: {}.json not present, creating a new one...", this.fileName);
                this.save();
                return;
            }

            String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            this.data = ReliableRecipeViewer.GSON.fromJson(fileContent, JsonObject.class);

            ReliableRecipeViewer.LOGGER.info("Loading config file: {}.json", this.fileName);
            this.loadData();
        } catch (Exception e) {
            ReliableRecipeViewer.LOGGER.error("Failed to load config file: {}.json", this.fileName, e);
        }

    }


    public void save() {
        try {
            this.saveData();
            File configDirectory = ReliableRecipeViewer.CONFIG_PATH.toFile();
            File saveFile = ReliableRecipeViewer.CONFIG_PATH.resolve(this.fileName + ".json").toFile();

            if(configDirectory.mkdirs())
                ReliableRecipeViewer.LOGGER.debug("Couldn't find config directory, creating new one...");

            if (saveFile.createNewFile())
                ReliableRecipeViewer.LOGGER.info("Created new config file: {}.json", this.fileName);

            String encoded = ReliableRecipeViewer.GSON.toJson(this.data);

            FileUtils.writeStringToFile(saveFile, encoded, StandardCharsets.UTF_8);
            ReliableRecipeViewer.LOGGER.info("Saved config file: {}.json", this.fileName);

        } catch (Exception e) {
            ReliableRecipeViewer.LOGGER.error("Failed to save config file: {}.json", this.fileName, e);
        }

    }

    protected <T> void save(String key, T newValue, Codec<T> codec) {
        this.data().add(key, codec.encodeStart(JsonOps.INSTANCE, newValue).getOrThrow());
    }

    protected void save(String key, boolean newValue) {
        this.data().addProperty(key, newValue);
    }

    protected <T> T load(String key, T defaultValue, Codec<T> codec) {
        if (this.data().has(key))
            return codec.decode(JsonOps.INSTANCE, this.data().get(key)).mapOrElse(Pair::getFirst, (e)->defaultValue);
        return defaultValue;
    }

    protected boolean load(String key, boolean defaultValue) {
        if (this.data().has(key))
            return this.data().get(key).getAsBoolean();
        return defaultValue;
    }

}

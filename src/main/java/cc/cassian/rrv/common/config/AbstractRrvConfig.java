package cc.cassian.rrv.common.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import cc.cassian.rrv.common.CommonRRV;
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
            File file = new File(CommonRRV.CONFIG_PATH + this.fileName + ".json");

            if (!file.exists()) {
                CommonRRV.LOGGER.info("Config file: {}.json not present, creating a new one...", this.fileName);
                this.save();
                return;
            }

            String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            this.data = new GsonBuilder().create().fromJson(fileContent, JsonObject.class);

            CommonRRV.LOGGER.info("Loading config file: {}.json", this.fileName);
            this.loadData();
        } catch (Exception e) {
            CommonRRV.LOGGER.error("Failed to load config file: {}.json", this.fileName, e);
        }

    }


    public void save() {
        try {

            this.saveData();
            File configDirectory = new File(CommonRRV.CONFIG_PATH);
            File saveFile = new File(CommonRRV.CONFIG_PATH + this.fileName + ".json");

            if(configDirectory.mkdirs())
                CommonRRV.LOGGER.info("Couldn't find config directory, creating new one...");


            if (saveFile.createNewFile())
                CommonRRV.LOGGER.info("Created new config file: {}.json", this.fileName);

            String encoded = new GsonBuilder().setPrettyPrinting().create().toJson(this.data);

            FileUtils.writeStringToFile(saveFile, encoded, StandardCharsets.UTF_8);
            CommonRRV.LOGGER.info("Saved config file: {}.json", this.fileName);

        } catch (Exception e) {
            CommonRRV.LOGGER.error("Failed to save config file: {}.json", this.fileName, e);
        }

    }

}

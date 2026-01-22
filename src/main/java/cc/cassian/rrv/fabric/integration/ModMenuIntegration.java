//? fabric {
package cc.cassian.rrv.fabric.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import cc.cassian.rrv.common.gui.RrvClientSettingsScreen;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (RrvClientSettingsScreen::new);
    }
}
//?}
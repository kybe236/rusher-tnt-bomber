package org.tntNuker.modules;

import org.rusherhack.client.api.events.client.EventUpdate;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.setting.BooleanSetting;
import org.tntNuker.TntNuker;

public class TntNukerModule extends ToggleableModule {
    /**
     * Settings
     */
    private final BooleanSetting leftTnt = new BooleanSetting("Left", "Place tnt left", true);
    private final BooleanSetting rightTnt = new BooleanSetting("Right", "Place tnt right", true);
    private final BooleanSetting bellowTnt = new BooleanSetting("Bellow", "Place tnt bellow", true);
    private final BooleanSetting frontTnt = new BooleanSetting("Front", "Place tnt front", false);
    private final BooleanSetting backTnt = new BooleanSetting("Back", "Place tnt back", false);
    private final BooleanSetting placeRedstone = new BooleanSetting("Redstone", "Place redstone", true);

    /**
     * Constructor
     */
    public TntNukerModule() {
        super("TntNuker", "Uses tnt as nukes(scaffold)", ModuleCategory.MISC);

        this.registerSettings(
                leftTnt,
                rightTnt,
                bellowTnt,
                frontTnt,
                backTnt,
                placeRedstone
        );
    }

    @Subscribe
    public void onTick(EventUpdate event) {
        if (mc.level != null) {
            TntNuker.onTick(leftTnt.getValue(), rightTnt.getValue(), bellowTnt.getValue(), placeRedstone.getValue(), frontTnt.getValue(), backTnt.getValue());
        }
    }
    @Override
    public void onEnable() {
        if (mc.level != null) {
            ChatUtils.print("TntNuker enabled!");
        }
    }

}

package net.favouriteless.tfc_trotting_wagons;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TfcTrottingWagons.MOD_ID)
public class TfcTrottingWagons {

    public static final String MOD_ID = "tfc_trotting_wagons";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TfcTrottingWagons(IEventBus bus, ModContainer container) {
        LOGGER.debug("TerraFirmaCraft livestock is added to trotting_wagons:draft_animals");
    }

}

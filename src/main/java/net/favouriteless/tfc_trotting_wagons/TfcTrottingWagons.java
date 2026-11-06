package net.favouriteless.tfc_trotting_wagons;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.config.TFCConfig;
import net.favouriteless.trotting_wagons.common.entities.base.AbstractWagon;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TfcTrottingWagons.MOD_ID)
public class TfcTrottingWagons {

    public static final String MOD_ID = "tfc_trotting_wagons";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TfcTrottingWagons(IEventBus bus, ModContainer container) {
        AbstractWagon.addDraftCheck(TfcTrottingWagons::hasOutlinedHeart);
        LOGGER.debug("TerraFirmaCraft livestock is added to trotting_wagons:draft_animals");
    }

    /** White or red outlined familiarity heart. Below the decay limit the heart has not settled. */
    private static boolean hasOutlinedHeart(Entity entity) {
        if(!(entity instanceof TFCAnimalProperties animal))
            return true;
        return animal.getFamiliarity() >= TFCConfig.SERVER.familiarityDecayLimit.get();
    }

}

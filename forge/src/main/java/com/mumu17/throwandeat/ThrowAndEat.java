package com.mumu17.throwandeat;

import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ThrowAndEat {

    public ThrowAndEat() {

        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Constants.LOG.info("Hello Forge world!");
        CommonClass.init();

    }
}
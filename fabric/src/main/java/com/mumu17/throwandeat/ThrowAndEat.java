package com.mumu17.throwandeat;

import net.fabricmc.api.ModInitializer;

public class ThrowAndEat implements ModInitializer {
    
    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        com.mumu17.throwandeat.Constants.LOG.info("Hello Fabric world!");
        com.mumu17.throwandeat.CommonClass.init();
    }
}

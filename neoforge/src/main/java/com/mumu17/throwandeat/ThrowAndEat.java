package com.mumu17.throwandeat;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ThrowAndEat {

    public ThrowAndEat(IEventBus eventBus) {
        CommonClass.init();

    }
}
package com.moulberry.flashback.mixin;

import net.minecraft.client.gui.components.toasts.SystemToast;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(SystemToast.SystemToastIds.class)
@Unique
public class MixinSystemToastIds {

    @Shadow
    @Final
    @Mutable
    private static SystemToast.SystemToastIds[] $VALUES;

    static {
        ArrayList<SystemToast.SystemToastIds> variants = new ArrayList<>(Arrays.asList(MixinSystemToastIds.$VALUES));
        SystemToast.SystemToastIds instrument = systemToastIds$invokeInit("RECORDING_TOAST", variants.get(variants.size() - 1).ordinal() + 1);
        variants.add(instrument);
        MixinSystemToastIds.$VALUES = variants.toArray(new SystemToast.SystemToastIds[0]);
    }

    @Invoker("<init>")
    public static SystemToast.SystemToastIds systemToastIds$invokeInit(String internalName, int internalId) {
        throw new AssertionError();
    }

}

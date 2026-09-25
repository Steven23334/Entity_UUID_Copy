package com.github.steven23334.entity_uuid_copy;

import com.github.steven23334.entity_uuid_copy.init.InitItems;
import com.github.steven23334.entity_uuid_copy.network.PlayerListPacket;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(EntityUuidCopy.MOD_ID)
public class EntityUuidCopy {
    public static final String MOD_ID = "entity_uuid_copy";

    public EntityUuidCopy(IEventBus modBus) {
        InitItems.ITEMS.register(modBus);
        modBus.addListener(this::registerPayloads);
        modBus.addListener(this::addCreative);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                PlayerListPacket.TYPE,
                PlayerListPacket.CODEC,
                PlayerListPacket::handle
        );
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(InitItems.ENTITY_UUID_COPY);
            event.accept(InitItems.PLAYER_UUID_COPY);
        }
    }
}
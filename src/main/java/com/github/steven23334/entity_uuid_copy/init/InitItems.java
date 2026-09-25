package com.github.steven23334.entity_uuid_copy.init;

import com.github.steven23334.entity_uuid_copy.EntityUuidCopy;
import com.github.steven23334.entity_uuid_copy.item.ItemEntityUuidCopy;
import com.github.steven23334.entity_uuid_copy.item.ItemPlayerUuidCopy;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public class InitItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(EntityUuidCopy.MOD_ID);

    public static final DeferredItem<Item> ENTITY_UUID_COPY =
            ITEMS.registerItem("entity_uuid_copy",
                    properties -> new ItemEntityUuidCopy(properties.stacksTo(1)));

    public static final DeferredItem<Item> PLAYER_UUID_COPY =
            ITEMS.registerItem("player_uuid_copy",
                    properties -> new ItemPlayerUuidCopy(properties.stacksTo(1)));
}
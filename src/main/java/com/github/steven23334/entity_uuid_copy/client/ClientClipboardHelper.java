package com.github.steven23334.entity_uuid_copy.client;

import net.minecraft.client.Minecraft;

public class ClientClipboardHelper {
    public static void copy(String text) {
        Minecraft.getInstance().keyboardHandler.setClipboard(text);
    }
}
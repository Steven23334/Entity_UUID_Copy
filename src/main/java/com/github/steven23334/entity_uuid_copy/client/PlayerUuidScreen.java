package com.github.steven23334.entity_uuid_copy.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerUuidScreen extends Screen {
    private final List<Map.Entry<String, UUID>> allEntries;
    private List<Map.Entry<String, UUID>> filteredEntries;

    /** 玩家名搜索框 */
    private EditBox nameSearchBox;
    /** UUID 搜索框 */
    private EditBox uuidSearchBox;

    private String lastKeywordName = "";
    private String lastKeywordUuid = "";

    private int scrollOffset = 0;
    private static final int ROW_HEIGHT = 14;

    private static final int HEAD_SIZE = 8;
    private static final int HEAD_X = 5;
    private static final int TEXT_X = 18;

    public PlayerUuidScreen(Map<String, UUID> players) {
        super(Component.translatable("gui.entity_uuid_copy.player_list.title"));
        this.allEntries = new ArrayList<>(players.entrySet());
        this.allEntries.sort(Map.Entry.comparingByKey());
        this.filteredEntries = new ArrayList<>(this.allEntries);
    }

    public static void open(Map<String, UUID> players) {
        Minecraft.getInstance().setScreen(new PlayerUuidScreen(players));
    }

    @Override
    protected void init() {
        int midX = this.width / 2;

        // 玩家名搜索框（左半）
        this.nameSearchBox = new EditBox(
                this.font,
                midX - 200, 30,
                190, 18,
                Component.translatable("gui.entity_uuid_copy.search.name")
        );
        this.nameSearchBox.setMaxLength(64);
        this.nameSearchBox.setHint(
                Component.translatable("gui.entity_uuid_copy.search.name.hint")
                        .withStyle(ChatFormatting.DARK_GRAY)
        );
        this.nameSearchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(this.nameSearchBox);

        // UUID 搜索框（右半）
        this.uuidSearchBox = new EditBox(
                this.font,
                midX + 10, 30,
                190, 18,
                Component.translatable("gui.entity_uuid_copy.search.uuid")
        );
        this.uuidSearchBox.setMaxLength(64);
        this.uuidSearchBox.setHint(
                Component.translatable("gui.entity_uuid_copy.search.uuid.hint")
                        .withStyle(ChatFormatting.DARK_GRAY)
        );
        this.uuidSearchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(this.uuidSearchBox);

        // 关闭按钮
        this.addRenderableWidget(Button.builder(
                Component.translatable("gui.entity_uuid_copy.close"),
                button -> this.onClose()
        ).bounds(this.width - 80, this.height - 30, 70, 20).build());

    }

    /** 两个输入框任一变化都重新过滤 */
    private void onSearchChanged(String ignored) {
        String nameKeyword = nameSearchBox.getValue().trim().toLowerCase();
        String uuidKeyword = uuidSearchBox.getValue().trim().toLowerCase();

        if (nameKeyword.equals(lastKeywordName) && uuidKeyword.equals(lastKeywordUuid)) {
            return;
        }
        lastKeywordName = nameKeyword;
        lastKeywordUuid = uuidKeyword;
        scrollOffset = 0;

        String uuidKeywordNoDash = uuidKeyword.replace("-", "");

        filteredEntries = new ArrayList<>();
        for (Map.Entry<String, UUID> entry : allEntries) {
            String name = entry.getKey().toLowerCase();
            String uuid = entry.getValue().toString().toLowerCase();
            String uuidNoDash = uuid.replace("-", "");

            // 玩家名条件：为空 或 名字包含关键字
            boolean nameMatch = nameKeyword.isEmpty() || name.contains(nameKeyword);

            // UUID 条件：为空 或 包含关键字（带/不带连字符）
            boolean uuidMatch = uuidKeyword.isEmpty()
                    || uuid.contains(uuidKeyword)
                    || (!uuidKeywordNoDash.isEmpty() && uuidNoDash.contains(uuidKeywordNoDash));

            if (nameMatch && uuidMatch) {
                filteredEntries.add(entry);
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);

        // 标题
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);

        // 两个搜索框的标签
        graphics.drawString(this.font,
                Component.translatable("gui.entity_uuid_copy.search.name.label"),
                this.width / 2 - 200, 20, 0xAAAAAA);
        graphics.drawString(this.font,
                Component.translatable("gui.entity_uuid_copy.search.uuid.label"),
                this.width / 2 + 10, 20, 0xAAAAAA);

        if (filteredEntries.isEmpty()) {
            graphics.drawCenteredString(this.font,
                    Component.translatable("gui.entity_uuid_copy.player_list.empty"),
                    this.width / 2, this.height / 2, 0xAAAAAA);
            return;
        }

        int y = 60;
        for (int i = scrollOffset; i < filteredEntries.size(); i++) {
            if (y > this.height - 45) break;

            Map.Entry<String, UUID> entry = filteredEntries.get(i);

            // 头像
            int headY = y - 2;
            ResourceLocation skin = DefaultPlayerSkin.get(entry.getValue()).texture();
            graphics.blit(skin, HEAD_X, headY, 8, 8, HEAD_SIZE, HEAD_SIZE, 64, 64);

            // 文字
            String text = entry.getKey() + " → " + entry.getValue();
            boolean hovered = mouseX >= TEXT_X
                    && mouseX <= TEXT_X + this.font.width(text) + 80
                    && mouseY >= y - 2 && mouseY <= y + 10;

            graphics.drawString(this.font, text, TEXT_X, y, hovered ? 0xFFFF00 : 0xFFFFFF);

            if (hovered) {
                graphics.drawString(this.font,
                        Component.translatable("gui.entity_uuid_copy.click_to_copy"),
                        TEXT_X + this.font.width(text) + 8, y, 0xFFFF00);
            }
            y += ROW_HEIGHT;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 两个搜索框都优先处理
        if (this.nameSearchBox.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.uuidSearchBox.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        int y = 60;
        for (int i = scrollOffset; i < filteredEntries.size(); i++) {
            if (y > this.height - 45) break;

            Map.Entry<String, UUID> entry = filteredEntries.get(i);
            String text = entry.getKey() + " → " + entry.getValue();

            if (mouseX >= HEAD_X && mouseX <= TEXT_X + this.font.width(text) + 80
                    && mouseY >= y - 2 && mouseY <= y + 10) {
                ClientClipboardHelper.copy(entry.getValue().toString());
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.displayClientMessage(
                            Component.translatable("message.entity_uuid_copy.player_copied",
                                    entry.getKey(), entry.getValue().toString()), true);
                }
                return true;
            }
            y += ROW_HEIGHT;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int visibleRows = (this.height - 105) / ROW_HEIGHT;
        int maxOffset = Math.max(0, filteredEntries.size() - visibleRows);
        if (scrollY < 0) scrollOffset = Math.min(scrollOffset + 1, maxOffset);
        if (scrollY > 0) scrollOffset = Math.max(scrollOffset - 1, 0);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 两个输入框依次处理键盘输入
        if (this.nameSearchBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.uuidSearchBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.nameSearchBox.charTyped(codePoint, modifiers)) {
            return true;
        }
        if (this.uuidSearchBox.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
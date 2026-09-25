package com.github.steven23334.entity_uuid_copy.item;

import com.github.steven23334.entity_uuid_copy.client.ClientClipboardHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ItemEntityUuidCopy extends Item {
    public ItemEntityUuidCopy(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player,
                                                           LivingEntity target, @NotNull InteractionHand hand) {
        UUID uuid = target.getUUID();

        if (player.level().isClientSide) {
            // 客户端：复制到系统剪贴板
            if (FMLEnvironment.dist == Dist.CLIENT) {
                ClientClipboardHelper.copy(uuid.toString());
            }
        } else {
            // 服务端：发一条提示消息
            player.displayClientMessage(
                    Component.translatable("message.entity_uuid_copy.copied", uuid.toString())
                            .withStyle(ChatFormatting.GREEN),
                    true);
        }

        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                List<Component> components, @NotNull TooltipFlag flag) {
        components.add(Component.translatable("tooltips.entity_uuid_copy.desc")
                .withStyle(ChatFormatting.GRAY));
    }
}
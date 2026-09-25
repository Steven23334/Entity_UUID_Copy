package com.github.steven23334.entity_uuid_copy.item;

import com.github.steven23334.entity_uuid_copy.network.PlayerListPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemPlayerUuidCopy extends Item {
    public ItemPlayerUuidCopy(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            Map<String, UUID> players = new HashMap<>();
            serverPlayer.server.getPlayerList().getPlayers().forEach(p ->
                    players.put(p.getGameProfile().getName(), p.getUUID()));
            PacketDistributor.sendToPlayer(serverPlayer, new PlayerListPacket(players));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                List<Component> components, @NotNull TooltipFlag flag) {
        components.add(Component.translatable("tooltips.entity_uuid_copy.player_uuid_copy.desc")
                .withStyle(ChatFormatting.GRAY));
    }
}
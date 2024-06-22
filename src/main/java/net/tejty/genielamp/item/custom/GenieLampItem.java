package net.tejty.genielamp.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tejty.genielamp.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class GenieLampItem extends Item {
    private final Random random = new Random();

    public GenieLampItem(Properties properties){
        super(properties);
    }
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        if (Screen.hasShiftDown()){
            // If shift is pressed
            if (pStack.getItem() instanceof GenieLampItem item) {
                // Text: "Uncharged"
                pTooltip.add(Component.translatable("item.genie_lamp.magic_lamp.tooltip.uncharged").withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_PURPLE));
                // New line
                pTooltip.add(Component.empty());
                // Text: "Experience:"
                pTooltip.add(Component.translatable("item.genie_lamp.magic_lamp.tooltip.experience").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_GREEN));

                // Calculating experience bar
                int barLength = 10;
                int chargedLength = (int)((float)getExperience(pStack) / (float)getMaxExperience() * (float)barLength);

                // Generating experience bar's string
                String chargedBar = "█".repeat(Math.max(0, chargedLength));
                String unchargedBar = "▒".repeat(Math.max(0, barLength - chargedLength));

                // Rendering experience bar
                pTooltip.add(
                        Component.literal(chargedBar).withStyle(ChatFormatting.GREEN)
                                .append(Component.literal(unchargedBar).withStyle(ChatFormatting.GRAY))
                );
                // Rendering experience ratio
                pTooltip.add(Component.literal(getExperience(pStack) + "/" + getMaxExperience()));
                // New line
                pTooltip.add(Component.empty());
                // Rendering tutorial
                pTooltip.add(Component.translatable("item.genie_lamp.magic_lamp.tooltip.tutorial").withStyle(ChatFormatting.GRAY));
            }
        }
        else {
            // Text: "Press SHIFT for details "
            pTooltip.add(
                    Component.translatable("item.genie_lamp.magic_lamp.tooltip.press").withStyle(ChatFormatting.GRAY)
                            .append(Component.literal("SHIFT").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW))
                            .append(Component.translatable("item.genie_lamp.magic_lamp.tooltip.for_details").withStyle(ChatFormatting.GRAY))
            );
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        // Getting used item
        ItemStack item = pPlayer.getItemInHand(pUsedHand);

        // Checking if it's on client
        if (!pLevel.isClientSide()) {
            // Getting lamp class
            if (item.getItem() instanceof GenieLampItem lamp) {
                // Making sure the tag is there
                if (!item.hasTag()) {
                    item.setTag(new CompoundTag());
                }

                // Getting experience amount
                int experience = lamp.getExperience(item);
                // Checking if player has enough xp levels
                if (pPlayer.experienceLevel < 1 || (experience >= getMaxExperience())) {
                    return InteractionResultHolder.fail(item);
                }

                // Removing xp from player if is in survival
                if (!pPlayer.canUseGameMasterBlocks()) {
                    pPlayer.giveExperienceLevels(-1);
                }

                // Adding experience
                experience += 1;

                // Setting the xp to the lamp
                lamp.setExperience(item, experience);

                // If is lamp full
                if (experience >= getMaxExperience()) {
                    // Changing magic lamp to charged magic lamp
                    pPlayer.setItemInHand(pUsedHand, new ItemStack(ModItems.CHARGED_MAGIC_LAMP.get()));

                    // Adding cool down for charged magic lamps, to prevent instant place
                    pPlayer.getCooldowns().addCooldown(pPlayer.getItemInHand(pUsedHand).getItem(), 60);

                    // Playing sound
                    Vec3 playerPos = pPlayer.getPosition(0);
                    pLevel.playSound(null, playerPos.x, playerPos.y, playerPos.z, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.PLAYERS, 2f, 1f);

                    // Showing full charged xp ratio
                    pPlayer.displayClientMessage(Component.literal(getMaxExperience() + "/" + getMaxExperience()).withStyle(ChatFormatting.DARK_GREEN), true);

                    // Rendering totem-like animation for lamp
                    Minecraft.getInstance().gameRenderer.displayItemActivation(new ItemStack(ModItems.MAGIC_LAMP.get()));

                    // Spawning particles
                    for (int i = 0; i < 50; i++) {
                        // Calculating position
                        Vec3 pos = new Vec3(
                                playerPos.x + random.nextDouble() - 0.5,
                                playerPos.y + random.nextDouble(),
                                playerPos.z + random.nextDouble() - 0.5
                        );
                        // Spawning particles
                        ((ServerLevel) pLevel).sendParticles(ParticleTypes.GLOW, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0.1);
                    }
                }
                else {
                    // Playing charging sound
                    pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1f, random.nextFloat(0.5f, 0.8f));
                }
            }
        }

        // If didn't worked, it returns failed
        return InteractionResultHolder.fail(item);
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 100;
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        // Ticking super
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);

        // Checking if it's in hand
        if (player.getItemInHand(InteractionHand.MAIN_HAND) == stack || player.getItemInHand(InteractionHand.OFF_HAND) == stack){
            // If it is magic lamp
            if (stack.getItem() instanceof GenieLampItem lamp) {
                // Showing experience ratio
                player.displayClientMessage(Component.literal(lamp.getExperience(stack) + "/" + getMaxExperience()).withStyle(ChatFormatting.DARK_GREEN), true);
            }
        }
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.SPYGLASS;
    }

    public void setExperience(ItemStack stack, int experience) {
        if (!stack.hasTag()){
            stack.setTag(new CompoundTag());
        }
        CompoundTag nbtData = stack.getTag();
        assert nbtData != null;
        nbtData.putInt("genie_lamp.experience", experience);
        stack.setTag(nbtData);
    }

    public int getExperience(ItemStack stack){
        if (!stack.hasTag()){
            return 0;
        }
        else {
            return stack.getTag().getInt("genie_lamp.experience");
        }
    }
    public static int getMaxExperience(){
        return 100;
    }
}

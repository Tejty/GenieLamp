package net.tejty.genielamp.client.gui.screens;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class WishingScreen extends Screen {
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("genie_lamp:textures/gui/wishing_screen.png");
    private static final int FILE_WIDTH = 512;
    private static final int FILE_HEIGHT = 512;
    private static final int ROWS = 7;
    private static final int COLUMNS = 9;
    private static final int SLOT_SIZE = 18;
    private static final int ITEM_SIZE = 16;
    private static final int SLOT_SPACING = 0;
    private static final int PADDING_LEFT = 9;
    private static final int PADDING_RIGHT = 26;
    private static final int PADDING_TOP = 18;
    private static final int PADDING_BOTTOM = 8;


    public static Player player;
    private static Collection<Item> items;
    private int getCenterX(){
        return width / 2;
    }
    private int getCenterY() {
        return height / 2;
    }
    private int getContentWidth(){
        return COLUMNS * SLOT_SIZE;
    }
    private int getContentHeight(){
        return ROWS * SLOT_SIZE;
    }
    private int getContentCornerX(){
        return getCenterX() - getBackgroundWidth() / 2 + PADDING_LEFT;
    }
    private int getContentCornerY(){
        return getCenterY() - getBackgroundHeight() / 2 + PADDING_TOP;
    }
    private int getBackgroundCornerX(){return getContentCornerX() - PADDING_LEFT;}
    private int getBackgroundCornerY(){return getContentCornerY() - PADDING_TOP;}
    private int getBackgroundWidth(){return getContentWidth() + PADDING_LEFT + PADDING_RIGHT;}
    private int getBackgroundHeight(){return getContentHeight() + PADDING_TOP + PADDING_BOTTOM;}
    private int getSlotPadding(){return (SLOT_SIZE - ITEM_SIZE) / 2;}

    public WishingScreen(Player pPlayer) {
        super(Component.literal("Item Wishing"));
        player = pPlayer;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected void init() {
        this.createScreen();
    }

    private void createScreen() {
        items = ForgeRegistries.ITEMS.getValues();
        player.displayClientMessage(Component.literal("creating screen yay"), false);
        CreativeModeTab tab = CreativeModeTabRegistry.getDefaultTabs().get(0);
        player.displayClientMessage(Component.literal(tab.toString()), false);
        player.displayClientMessage(Component.literal(tab.hasAnyItems() + ""), false);
        player.displayClientMessage(Component.literal("Size:" + items.size()), false);


        Collection<ItemStack> items = tab.getDisplayItems();
        player.displayClientMessage(Component.literal(items.toString()), false);
        for (ItemStack itemStack : items) {
            player.displayClientMessage(Component.literal("iterating"), false);
            ItemStack item = itemStack;
            player.displayClientMessage(Component.literal(item.toString()), false);
        }
    }

    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(BACKGROUND_LOCATION, getBackgroundCornerX(), getBackgroundCornerY(), 0, 0, 0, getBackgroundWidth(), getBackgroundHeight(), 256, 256);
        int itemValue = 0;
        while (itemValue < COLUMNS * ROWS) {
            graphics.renderItem(
                    new ItemStack((Item) items.toArray()[itemValue], 1),
                    (itemValue % COLUMNS) * SLOT_SIZE + getContentCornerX() + getSlotPadding(),
                    (itemValue - itemValue % COLUMNS) / COLUMNS * SLOT_SIZE + getContentCornerY() + getSlotPadding()
            );

            itemValue++;
        }
    }
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {

    }

    private Button openScreenButton(Component component, Supplier<Screen> screenSupplier) {
        return Button.builder(component, (p_280817_) -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(screenSupplier.get());
        }).width(98).build();
    }

    private Button openLinkButton(Component component, String uri) {
        return this.openScreenButton(component, () -> new ConfirmLinkScreen((p_280813_) -> {
            if (p_280813_) {
                Util.getPlatform().openUri(uri);
            }

            assert this.minecraft != null;
            this.minecraft.setScreen(this);
        }, uri, true));
    }
}



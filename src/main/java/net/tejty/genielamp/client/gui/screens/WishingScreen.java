package net.tejty.genielamp.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.CreativeModeTabSearchRegistry;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.common.ForgeI18n.stripControlCodes;

@OnlyIn(Dist.CLIENT)
public class WishingScreen extends Screen {
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("genie_lamp:textures/gui/wishing_screen.png");
    private static final int FILE_WIDTH = 256;
    private static final int FILE_HEIGHT = 256;
    private static final int ROWS = 7;
    private static final int COLUMNS = 9;
    private static final int SLOT_SIZE = 18;
    private static final int ITEM_SIZE = 16;
    private static final int CONTENT_PADDING_LEFT = 9;
    private static final int CONTENT_PADDING_RIGHT = 26;
    private static final int CONTENT_PADDING_TOP = 18;
    private static final int CONTENT_PADDING_BOTTOM = 8;
    private static final int SLIDER_PADDING_LEFT = 177;
    private static final int SLIDER_WIDTH = 12;
    private static final int SLIDER_HEIGHT = 15;
    private static final int SLIDER_BAR_HEIGHT = 126;
    private static final int SLIDER_X = 197;
    private static final int SLIDER_Y = 0;
    private static final int SEARCH_BOX_PADDING_LEFT = 84;
    private static final int SEARCH_BOX_PADDING_TOP = 6;
    private static final int SEARCH_BOX_WIDTH = 88;
    private static final int SEARCH_BOX_HEIGHT = 10;

    public static Player player;
    private static Collection<ItemStack> items;
    private static Collection<Item> unsortedItems;
    private static Collection<ItemStack> unsortedItemsFromTab;
    private int scrollOff;
    private boolean isDragging;
    private EditBox searchBox;

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
        return getCenterX() - getBackgroundWidth() / 2 + CONTENT_PADDING_LEFT;
    }
    private int getContentCornerY(){
        return getCenterY() - getBackgroundHeight() / 2 + CONTENT_PADDING_TOP;
    }
    private int getBackgroundCornerX(){return getContentCornerX() - CONTENT_PADDING_LEFT;}
    private int getBackgroundCornerY(){return getContentCornerY() - CONTENT_PADDING_TOP;}
    private int getBackgroundWidth(){return getContentWidth() + CONTENT_PADDING_LEFT + CONTENT_PADDING_RIGHT;}
    private int getBackgroundHeight(){return getContentHeight() + CONTENT_PADDING_TOP + CONTENT_PADDING_BOTTOM;}
    private int getSlotPadding(){return (SLOT_SIZE - ITEM_SIZE) / 2;}
    private int getSliderRange(){return SLIDER_BAR_HEIGHT - SLIDER_HEIGHT;}

    public WishingScreen(Player pPlayer) {
        super(Component.literal("Item Wishing"));
        player = pPlayer;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected void init() {
        CreativeModeTabs.tryRebuildTabContents(FeatureFlags.DEFAULT_FLAGS, true, player.level().registryAccess());
        this.createScreen();
    }

    private void createScreen() {
        unsortedItems = ForgeRegistries.ITEMS.getValues();
        CreativeModeTab tab = CreativeModeTabRegistry.getTab(new ResourceLocation("search"));
        unsortedItemsFromTab = tab.getDisplayItems();
        player.displayClientMessage(Component.literal("name: " + tab.getDisplayName()), false);
        player.displayClientMessage(Component.literal("size: " + unsortedItemsFromTab.size()), false);
        player.displayClientMessage(Component.literal("item: " + tab), false);
        items = unsortedItemsFromTab;
        this.searchBox = new EditBox(this.font, getBackgroundCornerX() + SEARCH_BOX_PADDING_LEFT, getBackgroundCornerY() + SEARCH_BOX_PADDING_TOP, SEARCH_BOX_WIDTH, SEARCH_BOX_HEIGHT, Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(16777215);
        this.searchBox.setCanLoseFocus(false);
        this.searchBox.setFocused(true);
        this.addWidget(this.searchBox);
    }

    @Override
    public void tick() {
        super.tick();
        searchBox.tick();
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        String s = this.searchBox.getValue();
        if (this.searchBox.charTyped(pCodePoint, pModifiers)) {
            if (!Objects.equals(s, this.searchBox.getValue())) {
                this.refreshSearchResults();
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        String s = this.searchBox.getValue();
        if (this.searchBox.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            if (!Objects.equals(s, this.searchBox.getValue())) {
                this.refreshSearchResults();
            }

            return true;
        } else {
            return this.searchBox.isFocused() && this.searchBox.isVisible() && pKeyCode != 256 || super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
    }

    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(BACKGROUND_LOCATION, getBackgroundCornerX(), getBackgroundCornerY(), 0, 0, 0, getBackgroundWidth(), getBackgroundHeight(), FILE_WIDTH, FILE_HEIGHT);
        int itemValue = 0;
        while (itemValue < COLUMNS * ROWS) {
            try {
                int x = (itemValue % COLUMNS) * SLOT_SIZE + getContentCornerX();
                int y = (itemValue - itemValue % COLUMNS) / COLUMNS * SLOT_SIZE + getContentCornerY();
                ItemStack stack = (ItemStack)items.toArray()[itemValue + scrollOff * COLUMNS];
                graphics.renderItem(
                        stack,
                        x + getSlotPadding(),
                        y + getSlotPadding()
                );
                if (mouseX >= x + getSlotPadding() && mouseX <= x + ITEM_SIZE + getSlotPadding() && mouseY >= y + getSlotPadding() && mouseY <= y + ITEM_SIZE + getSlotPadding()) {
                    graphics.renderTooltip(this.font, stack, mouseX, mouseY);
                    graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, FastColor.ARGB32.color(100, 200, 200, 200));
                    //graphics.renderOutline(x, y, SLOT_SIZE, SLOT_SIZE, 255255255);
                }
            } catch (Exception ignored){}
            itemValue++;
        }
        graphics.drawString(
                this.font,
                Component.translatable("wishing_screen.title"),
                getBackgroundCornerX() + CONTENT_PADDING_LEFT,
                getBackgroundCornerY() + SEARCH_BOX_PADDING_TOP,
                FastColor.ARGB32.color(255, 63, 63, 63),
                false
        );
        this.searchBox.render(graphics, mouseX, mouseY, partialTicks);
        renderSlider(graphics);
    }
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {

    }
    private void renderSlider(GuiGraphics pGuiGraphics) {
        int sliderPoints = (int)Math.ceil((double) items.size() / COLUMNS) - ROWS;

        if (sliderPoints > 1) {
            int sliderShift = getSliderRange() * scrollOff / sliderPoints;
            int sliderPosY = getBackgroundCornerY() + CONTENT_PADDING_TOP + sliderShift;

            pGuiGraphics.blit(BACKGROUND_LOCATION, getBackgroundCornerX() + SLIDER_PADDING_LEFT, sliderPosY, 0, SLIDER_X, SLIDER_Y, SLIDER_WIDTH, SLIDER_HEIGHT, FILE_WIDTH, FILE_HEIGHT);
        } else {
            pGuiGraphics.blit(BACKGROUND_LOCATION, getBackgroundCornerX() + SLIDER_PADDING_LEFT, getBackgroundCornerY() + CONTENT_PADDING_TOP, 0, SLIDER_X + SLIDER_WIDTH, SLIDER_Y, SLIDER_WIDTH, SLIDER_HEIGHT, FILE_WIDTH, FILE_HEIGHT);
        }
    }
    private boolean canScroll(int pNumRows) {
        return pNumRows > ROWS;
    }
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        int i = (int)Math.ceil((double) items.size() / COLUMNS);
        if (this.canScroll(i)) {
            int j = i - ROWS;
            this.scrollOff = Mth.clamp((int)((double)this.scrollOff - pDelta), 0, j);
        }

        return true;
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        int rows = (int)Math.ceil((double) items.size() / COLUMNS);
        if (this.isDragging) {
            int top = getBackgroundCornerY() + CONTENT_PADDING_TOP;
            int bottom = top + SLIDER_BAR_HEIGHT; //getSliderRange();
            int sliderPoints = rows - ROWS;
            float f = ((float)pMouseY - (float)top - (float) SLIDER_HEIGHT / 2) / ((float)(bottom - top) - SLIDER_HEIGHT);
            f = f * (float)sliderPoints + 0.5F;
            this.scrollOff = Mth.clamp((int)f, 0, sliderPoints);
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.isDragging = false;
        int left = getBackgroundCornerX() + SLIDER_PADDING_LEFT;
        int top = getBackgroundCornerY() + CONTENT_PADDING_TOP;
        int right = left + SLIDER_WIDTH;
        int bottom = top + SLIDER_BAR_HEIGHT;
        int rows = (int)Math.ceil((double) items.size() / COLUMNS);
        if (this.canScroll(rows) && pMouseX > left && pMouseX < right && pMouseY > top && pMouseY <= bottom) {
            this.isDragging = true;
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    private void refreshSearchResults() {
        items = unsortedItemsFromTab.stream().filter((ItemStack item) -> StringUtils.toLowerCase(stripControlCodes(item.getItem().getName(item).getString())).contains(StringUtils.toLowerCase(searchBox.getValue()))).collect(Collectors.toList());
        this.scrollOff = 0;
        /*
        items.clear();
        String s = this.searchBox.getValue();
        if (s.isEmpty()) {
            items.addAll(ForgeRegistries.ITEMS.getValues());
        } else {

            items.addAll(searchtree.search(s.toLowerCase(Locale.ROOT)));
        }

        this.scrollOff = 0;
        ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).scrollTo(0.0F);
        */
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



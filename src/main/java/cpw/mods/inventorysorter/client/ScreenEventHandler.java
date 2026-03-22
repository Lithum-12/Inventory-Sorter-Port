package cpw.mods.inventorysorter.client;

import cpw.mods.inventorysorter.Action;
import cpw.mods.inventorysorter.Config;
import cpw.mods.inventorysorter.InventorySorter;
import cpw.mods.inventorysorter.Network;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = InventorySorter.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ScreenEventHandler {

    public static void init() {}

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();

        if (!Config.ClientConfig.CONFIG.showSortButton.get()) return;
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) return;

        if (screen instanceof InventoryScreen) {
            // 场景1：纯玩家背包界面 —— 一个整理背包的按钮
            addPlayerInventoryButton(event, containerScreen);
        } else {
            // 场景2：容器界面（箱子等）—— 整理容器 + 整理背包区 两个按钮
            addContainerButton(event, containerScreen);
            addPlayerInventoryInContainerButton(event, containerScreen);
        }
    }

    private static void addPlayerInventoryButton(ScreenEvent.Init.Post event, AbstractContainerScreen<?> screen) {
        Slot anchor = findFirstPlayerSlot(screen);
        int x, y;
        if (anchor != null) {
            x = screen.getGuiLeft() + anchor.x + 18;
            y = screen.getGuiTop() + anchor.y - 2;
        } else {
            x = screen.getGuiLeft() + screen.getXSize() - 18;
            y = screen.getGuiTop() + 5;
        }

        event.addListener(new SortButton(x, y, btn -> {
            if (Minecraft.getInstance().player != null) {
                Network.channel.sendToServer(
                        Network.ActionMessage.fromActionAndSlot(Action.SORT, findFirstPlayerSlotIndex(screen))
                );
            }
        }));
    }

    private static void addContainerButton(ScreenEvent.Init.Post event, AbstractContainerScreen<?> screen) {
        int x = screen.getGuiLeft() + screen.getXSize() - 18;
        int y = screen.getGuiTop() + 5;

        event.addListener(new SortButton(x, y, btn -> {
            if (Minecraft.getInstance().player != null) {
                Network.channel.sendToServer(
                        Network.ActionMessage.fromActionAndSlot(Action.SORT, findFirstContainerSlotIndex(screen))
                );
            }
        }));
    }

    private static void addPlayerInventoryInContainerButton(ScreenEvent.Init.Post event, AbstractContainerScreen<?> screen) {
        Slot anchor = findFirstPlayerSlot(screen);
        if (anchor == null) return;

        int x = screen.getGuiLeft() + anchor.x + 18;
        int y = screen.getGuiTop() + anchor.y - 2;

        event.addListener(new SortButton(x, y, btn -> {
            if (Minecraft.getInstance().player != null) {
                Network.channel.sendToServer(
                        Network.ActionMessage.fromActionAndSlot(Action.SORT, findFirstPlayerSlotIndex(screen))
                );
            }
        }));
    }

    // -----------------------------------------------------------------------
    // Texture handling and button rendering are in SortButton.java
    // -----------------------------------------------------------------------

    private static Slot findFirstPlayerSlot(AbstractContainerScreen<?> screen) {
        for (Slot slot : screen.getMenu().slots) {
            if (slot.container instanceof Inventory && slot.getSlotIndex() >= 9) {
                return slot;
            }
        }
        return null;
    }

    /** 返回玩家背包区第一个 slot 的 menu index（供网络包使用） */
    private static int findFirstPlayerSlotIndex(AbstractContainerScreen<?> screen) {
        Slot slot = findFirstPlayerSlot(screen);
        return slot != null ? slot.index : 0;
    }

    /** 返回容器（非玩家背包）的第一个 slot 的 menu index */
    private static int findFirstContainerSlotIndex(AbstractContainerScreen<?> screen) {
        for (Slot slot : screen.getMenu().slots) {
            if (!(slot.container instanceof Inventory)) {
                return slot.index;
            }
        }
        return 0;
    }
}
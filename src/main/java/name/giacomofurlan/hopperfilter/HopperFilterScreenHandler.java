package name.giacomofurlan.hopperfilter;

import java.util.ArrayList;
import java.util.List;

import name.giacomofurlan.hopperfilter.component.HopperFilterComponentTypes;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class HopperFilterScreenHandler extends ScreenHandler {
    private final Inventory filterInventory;
    private final HopperBlockEntity hopper;

    protected HopperFilterScreenHandler(int syncId, PlayerInventory playerInventory, HopperBlockEntity hopper) {
        super(ScreenHandlerType.GENERIC_9X3, syncId);

        this.filterInventory = new SimpleInventory(27);
        this.hopper = hopper;

        // Filter slots initialization
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(filterInventory, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }
        // Filter slots filling
        List<ItemStack> filterStacks = HopperFilterStorage.getFilter(hopper);
        for (int i = 0; i < filterStacks.size(); i++) {
            ItemStack stack = filterStacks.get(i);
            filterInventory.setStack(i, stack.copy());
        }

        // Player slots initialization
        int m;
        for (m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }

        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasStack()) return ItemStack.EMPTY;

        ItemStack original = slot.getStack();

        // Add the item only once in the filter
        if (filterInventory.count(original.getItem()) > 0) {
            return ItemStack.EMPTY;
        }

        if (slot.inventory == player.getInventory()) {
            ItemStack ghostCopy = original.copy();
            ghostCopy.setCount(1); // Always only one

            // Add the item to the first empty slot of the filter
            for (int i = 0; i < filterInventory.size(); i++) {
                if (filterInventory.getStack(i).isEmpty()) {
                    filterInventory.setStack(i, ghostCopy);
                    break;
                }
            }
        }

        return ItemStack.EMPTY; // Do not transfer the real item
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);

        if (!player.getWorld().isClient()) {
            List<ItemStack> filter = new ArrayList<>();
            for (int i = 0; i < filterInventory.size(); i++) {
                ItemStack stack = filterInventory.getStack(i);
                if (!stack.isEmpty()) filter.add(stack.copy());
            }

            HopperFilterStorage.saveFilter(hopper, filter);
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        Slot clickedSlot = slotIndex >= 0 && slotIndex < this.slots.size() ? this.slots.get(slotIndex) : null;

        // Managing the filter's inventory
        if (clickedSlot != null && clickedSlot.inventory == filterInventory) {
            ItemStack heldStack = this.getCursorStack();

            if (!heldStack.isEmpty()) {
                ItemStack ghost = heldStack.copy();
                ghost.setCount(1);
                clickedSlot.setStack(ghost);
            } else {
                // Avoid the user to get the item (item dupe)
                clickedSlot.setStack(ItemStack.EMPTY);
            }

            return;
        }

        super.onSlotClick(slotIndex, button, actionType, player);
    }

    public Inventory getFilterInventory() {
        return this.filterInventory;
    }
}

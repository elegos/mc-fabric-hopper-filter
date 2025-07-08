package name.giacomofurlan.hopperfilter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class HopperFilterScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final HopperBlockEntity hopper;

    protected HopperFilterScreenHandler(int syncId, PlayerInventory playerInventory, HopperBlockEntity hopper) {
        super(ScreenHandlerType.GENERIC_9X3, syncId);

        this.inventory = new SimpleInventory(27);
        this.hopper = hopper;

        // Virtual slots initialization
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }

        // Player slots
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

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            ItemStack singleStack = originalStack.copy();
            singleStack.setCount(1);

            return singleStack;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);

        if (!player.getWorld().isClient()) {
            // Salva i contenuti dell’inventario come filtro
            List<ItemStack> filter = new ArrayList<>();
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.isEmpty()) filter.add(stack.copy());
            }

            HopperFilterStorage.saveFilter(hopper, filter);
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}

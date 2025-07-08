package name.giacomofurlan.hopperfilter.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.giacomofurlan.hopperfilter.HopperFilterStorage;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
    @Inject(
        method = "extract(Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/inventory/Inventory;ILnet/minecraft/util/math/Direction;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void onExtract(Hopper hopper, Inventory inventory, int slot, Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (!(hopper instanceof HopperBlockEntity hopperEntity)) {
            return;
        }

        List<ItemStack> filter = HopperFilterStorage.getFilter(hopperEntity);

        if (filter == null || filter.isEmpty()) {
            // Vanilla behaviour
            return;
        }

        ItemStack stack = inventory.getStack(slot);
        if (stack.isEmpty()) {
            return;
        }

        boolean allowed = filter.stream().anyMatch(f -> ItemStack.areItemsEqual(f, stack));
        if (!allowed) {
            cir.setReturnValue(false);
        }
    }
}

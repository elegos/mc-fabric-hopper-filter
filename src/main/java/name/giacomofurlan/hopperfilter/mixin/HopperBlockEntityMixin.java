package name.giacomofurlan.hopperfilter.mixin;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.giacomofurlan.hopperfilter.HopperFilterStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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

    @Inject(method = "insert", at = @At("HEAD"), cancellable = true)
    private static void onInsert(World world, BlockPos pos, HopperBlockEntity blockEntity, CallbackInfoReturnable<Boolean> cir) {
        // If the block below is a filtered hopper and the first non-empty stack of the inventory is in the filter list,
        // stop the insertion
        BlockEntity belowBlock = world.getBlockEntity(pos.down());
        if (belowBlock instanceof HopperBlockEntity belowHopper) {
            List<ItemStack> filter = HopperFilterStorage.getFilter(belowHopper);
            if (filter == null || filter.isEmpty()) {
                return;
            }

            Optional<ItemStack> nextStackToInsertOpt = IntStream.range(0, blockEntity.size())
                .mapToObj(blockEntity::getStack)
                .filter(stack -> !stack.isEmpty())
                .findFirst();
            
            if (!nextStackToInsertOpt.isPresent()) {
                return;
            }

            ItemStack nextStackToInsert = nextStackToInsertOpt.get();

            boolean filterMatch = filter.stream().anyMatch(stack -> ItemStack.areItemsEqual(stack, nextStackToInsert));
            if (filterMatch) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}

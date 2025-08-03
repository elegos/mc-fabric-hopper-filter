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

        if (filter.isEmpty()) {
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
        // if the block below is a hopper filter, and it can accept one item from the current hopper's inventory, block
        // the insert
        BlockEntity belowBlock = world.getBlockEntity(pos.down());
        if (belowBlock instanceof HopperBlockEntity belowHopper) {
            List<ItemStack> belowNotFullStacks = IntStream.range(0, blockEntity.size())
                    .mapToObj(belowHopper::getStack)
                    .filter(stack ->
                        stack.isEmpty() || stack.getCount() < stack.getMaxCount()
                    )
                    .toList();

            if (belowNotFullStacks.isEmpty()) {
                // the below filter has no free spot, don't block insert
                return;
            }

            List<ItemStack> filter;
            if (belowNotFullStacks.stream().anyMatch(ItemStack::isEmpty)) {
                // if the below filter has at least an empty spot, then it can accept any item from its filters
                filter = HopperFilterStorage.getFilter(belowHopper);
            }
            else {
                // otherwise, it can only accept stacks from its filters for which it has at least one non-full stack in inventory
                filter = HopperFilterStorage.getFilter(belowHopper)
                        .stream()
                        .filter(filterItem -> belowNotFullStacks
                                .stream()
                                .anyMatch(notFullStack -> ItemStack.areItemsEqual(notFullStack, filterItem))
                        ).toList();
                if (filter.isEmpty()) {
                    return;
                }
            }

            Optional<ItemStack> nextStackToInsertOpt = IntStream.range(0, blockEntity.size())
                .mapToObj(blockEntity::getStack)
                .filter(stack -> !stack.isEmpty())
                .findFirst();
            
            if (nextStackToInsertOpt.isEmpty()) {
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

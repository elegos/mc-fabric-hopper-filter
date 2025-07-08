package name.giacomofurlan.hopperfilter.mixin;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;

import name.giacomofurlan.hopperfilter.DataComponentHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.component.ComponentType;
import net.minecraft.util.math.BlockPos;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin extends BlockEntity implements DataComponentHolder {
    private final Map<ComponentType<?>, Object> dataComponents = new HashMap<>();

    protected HopperBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(ComponentType<T> type) {
        Object data = dataComponents.get(type);
        if (data != null && type.getClass().isInstance(data)) {
            return (T) data;
        }
        return null;
    }

    @Override
    public <T> void set(ComponentType<T> type, T value) {
        dataComponents.put(type, value);
        markDirty();
    }

    @Override
    public boolean contains(ComponentType<?> type) {
        return dataComponents.containsKey(type);
    }
}

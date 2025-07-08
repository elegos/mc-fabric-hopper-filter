package name.giacomofurlan.hopperfilter;

import java.util.List;

import name.giacomofurlan.hopperfilter.component.HopperFilterComponentTypes;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.ItemStack;

public class HopperFilterStorage {
    public static void saveFilter(HopperBlockEntity hopper, List<ItemStack> filter) {
        DataComponentHolder dataHolder = (DataComponentHolder) hopper;

        dataHolder.set(HopperFilterComponentTypes.HOPPER_FILTER, filter);
    }

    public static List<ItemStack> getFilter(HopperBlockEntity hopper) {
        DataComponentHolder dataHolder = (DataComponentHolder) hopper;

        if (dataHolder.contains(HopperFilterComponentTypes.HOPPER_FILTER)) {
            return dataHolder.get(HopperFilterComponentTypes.HOPPER_FILTER);
        }

        return null;
    }
}

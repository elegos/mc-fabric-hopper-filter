package name.giacomofurlan.hopperfilter;

import java.util.ArrayList;
import java.util.List;

import name.giacomofurlan.hopperfilter.component.HopperFilterComponentTypes;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;

public class HopperFilterStorage {
    public static void saveFilter(HopperBlockEntity hopper, List<ItemStack> filter) {
        ComponentMap components = hopper.getComponents();
        ComponentMap newComponents = ComponentMap.builder()
            .addAll(components)
            .add(HopperFilterComponentTypes.HOPPER_FILTER, filter)
            .build();

        hopper.setComponents(newComponents);
        hopper.markDirty();
    }

    public static List<ItemStack> getFilter(HopperBlockEntity hopper) {
        ComponentMap components = hopper.getComponents();
        if (components.contains(HopperFilterComponentTypes.HOPPER_FILTER)) {
            return components.get(HopperFilterComponentTypes.HOPPER_FILTER);
        }

        return new ArrayList<>();
    }
}

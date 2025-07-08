package name.giacomofurlan.hopperfilter.component;

import java.util.List;
import java.util.function.UnaryOperator;

import com.mojang.serialization.Codec;

import name.giacomofurlan.hopperfilter.HopperFilter;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class HopperFilterComponentTypes {
    public static final ComponentType<List<ItemStack>> HOPPER_FILTER = register("hopper_filter", builder -> builder.codec(Codec.list(ItemStack.CODEC)));

    public static void registerComponentTypes() {
        HopperFilter.LOGGER.info("Registering component types");
    }

    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(HopperFilter.MOD_ID, name),
            builderOperator.apply(ComponentType.builder()).build()
        ); 
    }
}

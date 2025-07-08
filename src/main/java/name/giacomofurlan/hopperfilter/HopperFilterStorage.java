package name.giacomofurlan.hopperfilter;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

public class HopperFilterStorage {
    private static final Codec<List<ItemStack>> codec = Codec.list(ItemStack.CODEC);

    public static void saveFilter(HopperBlockEntity hopper, List<ItemStack> filter) {
        ComponentMap components = hopper.getComponents();
        NbtComponent customComponent = components.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound customCompound = customComponent != null ? customComponent.copyNbt() : new NbtCompound();

        DataResult<NbtElement> result = codec.encodeStart(NbtOps.INSTANCE, filter);
        result.result().ifPresent(nbt -> customCompound.put("hopper_filter", nbt));

        ComponentMap newComponents = ComponentMap.builder()
            .addAll(components)
            .add(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(customCompound))
            .build();

        hopper.setComponents(newComponents);
        hopper.markDirty();
    }

    public static List<ItemStack> getFilter(HopperBlockEntity hopper) {
        ComponentMap components = hopper.getComponents();
        List<ItemStack> result = new ArrayList<>();

        NbtComponent customComponent = components.get(DataComponentTypes.CUSTOM_DATA);
        if (customComponent == null) {
            return result;
        }

        NbtCompound customCompound = customComponent != null ? customComponent.copyNbt() : new NbtCompound();
        NbtElement nbt = customCompound.get("hopper_filter");
        if (nbt == null) {
            return result;
        }

        DataResult<List<ItemStack>> parseResult = codec.parse(NbtOps.INSTANCE, nbt);

        parseResult.result().ifPresent(list -> result.addAll(list));

        return result;
    }
}

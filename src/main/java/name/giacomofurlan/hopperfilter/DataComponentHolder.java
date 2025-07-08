package name.giacomofurlan.hopperfilter;

import net.minecraft.component.ComponentType;

public interface DataComponentHolder {
    <T> T get(ComponentType<T> type);
    <T> void set(ComponentType<T> type, T value);
    boolean contains(ComponentType<?> type);
}

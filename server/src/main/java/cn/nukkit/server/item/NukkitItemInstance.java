package cn.nukkit.server.item;

import cn.nukkit.api.enchantment.EnchantmentInstance;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemInstanceBuilder;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.server.metadata.MetadataSerializers;
import cn.nukkit.server.nbt.CompoundTagBuilder;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.nbt.tag.StringTag;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.*;

@ToString
@Immutable
public class NukkitItemInstance implements ItemInstance {
    private final ItemType type;
    private final int amount;
    private final Metadata data;
    private final String itemName;
    private final List<String> itemLore;
    private final Set<EnchantmentInstance> enchantments;

    public NukkitItemInstance(ItemType type) {
        this(type, 1, null, null, null, null);
    }

    public NukkitItemInstance(ItemType type, int amount, Metadata data) {
        this(type, amount, data, null, null, null);
    }

    public NukkitItemInstance(ItemType type, int amount, Metadata data, String itemName, List<String> itemLore, Collection<EnchantmentInstance> enchantments) {
        this.type = type;
        this.amount = amount;
        this.data = data;
        this.itemName = itemName;
        this.itemLore = itemLore;
        this.enchantments = enchantments == null ? ImmutableSet.of() : ImmutableSet.copyOf(enchantments);
    }

    @Override
    public ItemType getItemType() {
        return type;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public Optional<Metadata> getItemData() {
        return Optional.ofNullable(data);
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(itemName);
    }

    @Override
    public List<String> getLore() {
        return ImmutableList.copyOf(itemLore);
    }

    @Override
    public Collection<EnchantmentInstance> getEnchantments() {
        return ImmutableList.copyOf(enchantments);
    }

    @Override
    public ItemInstanceBuilder toBuilder() {
        return new NukkitItemInstanceBuilder().itemType(type).amount(amount).itemData(data).name(itemName);
    }

    @Override
    public boolean isMergeable(@Nonnull ItemInstance other) {
        return equals(other, false, true, true, true, true);
    }

    @Override
    public boolean isSimilar(@Nonnull ItemInstance other) {
        return equals(other, false, false, false, true, false);
    }

    private boolean equals(ItemInstance other, boolean checkAmount, boolean checkName, boolean checkEnchantments,
                           boolean checkMeta, boolean checkCustom) {
        if (this == other) return true;
        if (other == null || this.getClass() != other.getClass()) return false;
        NukkitItemInstance that = (NukkitItemInstance) other;
        return this.type == that.type && (!checkAmount || this.amount == that.amount) &&
                (!checkName || Objects.equals(this.itemName, that.itemName)) &&
                (!checkEnchantments || Objects.equals(this.enchantments, that.enchantments)) &&
                (!checkMeta || Objects.equals(this.data, that.data)); // TODO: Custom data.
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ItemInstance)) return false;
        return equals((ItemInstance) o, true, true, true, true, true);
    }

    public int hashCode() {
        return Objects.hash(type, amount, data, itemName, itemLore, enchantments);
    }

    public CompoundTag toFullNBT() {
        return CompoundTagBuilder.builder()
                .byteTag("Count", (byte) amount)
                .shortTag("Damage", MetadataSerializers.serializeMetadata(this))
                .shortTag("id", (short) type.getId())
                .tag(toSpecificNBT())
                .buildRootTag();
    }

    public CompoundTag toSpecificNBT() {
        CompoundTagBuilder display = CompoundTagBuilder.builder();
        // Custom Name
        if (itemName != null) {
            display.stringTag("Name", itemName);
        }

        // Lore
        if (itemLore != null) {
            List<StringTag> loreLines = new ArrayList<>();
            itemLore.forEach(s -> loreLines.add(new StringTag("", s)));
            display.listTag("Lore", StringTag.class, loreLines);
        }
        return display.build("display");
    }
}

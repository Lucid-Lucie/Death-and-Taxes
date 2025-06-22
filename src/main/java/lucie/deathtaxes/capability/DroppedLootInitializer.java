package lucie.deathtaxes.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class DroppedLootInitializer implements INBTSerializable<CompoundTag>
{
    public List<ItemStack> droppedLoot = new ArrayList<>();

    public void copyFrom(DroppedLootInitializer other)
    {
        this.droppedLoot = other.droppedLoot;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        ListTag dropsList = new ListTag();
        droppedLoot.forEach(stack -> ItemStack.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, stack).result().ifPresent(dropsList::add));
        tag.put("DroppedItems", dropsList);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        if (nbt.contains("DroppedItems", Tag.TAG_LIST))
        {
            ListTag dropsList = nbt.getList("DroppedItems", Tag.TAG_COMPOUND);
            List<ItemStack> drops = new ArrayList<>();
            dropsList.forEach(tag -> ItemStack.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, tag).result().ifPresent(drops::add));
            droppedLoot.addAll(drops);
        }
    }
}
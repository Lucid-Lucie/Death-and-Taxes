package lucie.deathtaxes.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

public interface DroppedItems extends INBTSerializable<CompoundTag>
{
    Player getPlayer();

    static LazyOptional<DroppedItems> get(Player player)
    {
        return player.getCapability(DroppedItemsCapability.CAPABILITY);
    }

    void setItems(List<ItemStack> items);

    List<ItemStack> getItems();
}

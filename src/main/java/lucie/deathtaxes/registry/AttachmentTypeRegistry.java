package lucie.deathtaxes.registry;

import lucie.deathtaxes.DeathTaxes;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachmentTypeRegistry
{
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DeathTaxes.MODID);

    public static final Supplier<AttachmentType<ItemContainerContents>> PLAYER_INVENTORY_DROPS = ATTACHMENT_TYPES.register("player_inventory_drops", () ->
            AttachmentType.builder(() -> ItemContainerContents.EMPTY).serialize(ItemContainerContents.CODEC).build());
}

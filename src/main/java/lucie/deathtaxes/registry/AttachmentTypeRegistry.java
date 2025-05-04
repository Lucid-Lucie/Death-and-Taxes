package lucie.deathtaxes.registry;

import lucie.deathtaxes.DeathTaxes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

public class AttachmentTypeRegistry
{
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DeathTaxes.MODID);

    public static final Supplier<AttachmentType<ItemContainerContents>> DEATH_LOOT = ATTACHMENT_TYPES.register("death_loot", () ->
            AttachmentType.builder(() -> ItemContainerContents.EMPTY).serialize(ItemContainerContents.CODEC).build());

    public static final Supplier<AttachmentType<Optional<BlockPos>>> HOME_POS = ATTACHMENT_TYPES.register("home_pos", () ->
            AttachmentType.builder(Optional::<BlockPos>empty).serialize(ExtraCodecs.optionalEmptyMap(BlockPos.CODEC)).build());

    public static final Supplier<AttachmentType<MerchantOffers>> SCAVENGED_GOODS = ATTACHMENT_TYPES.register("scavenged_goods", () ->
            AttachmentType.builder(MerchantOffers::new).serialize(MerchantOffers.CODEC).build());
}

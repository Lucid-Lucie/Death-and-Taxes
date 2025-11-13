package lucie.deathtaxes.utility;

import lucie.deathtaxes.DeathTaxes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ItemEvaluation
{
    private static final ResourceKey<LootTable> LOOT_TABLE_KEY = ResourceKey.create(Registries.LOOT_TABLE, DeathTaxes.withModNamespace("gameplay/scavenger_pricing"));

    private static final LootContextParamSet EVALUATION_KEY = new LootContextParamSet.Builder()
            .required(LootContextParams.TOOL)
            .optional(LootContextParams.ORIGIN)
            .optional(LootContextParams.THIS_ENTITY)
            .build();

    public static MerchantOffers evaluateItems(ServerPlayer serverPlayer, ServerLevel serverLevel, ItemContainerContents contents)
    {
        LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(ItemEvaluation.LOOT_TABLE_KEY);
        MerchantOffers merchantOffers = new MerchantOffers();

        for (ItemStack itemStack : contents.stream().toList())
        {
            // Add current itemstack as tool parameter.
            LootParams lootParams = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.TOOL, itemStack)
                    .withParameter(LootContextParams.ORIGIN, serverPlayer.position())
                    .withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
                    .create(ItemEvaluation.EVALUATION_KEY);

            // Create cost for itemstack.
            lootTable.getRandomItems(lootParams).stream()
                    .findAny()
                    .map(itemCost -> new MerchantOffer(new ItemCost(itemCost.getItem(), itemCost.getCount()), itemStack, 1, itemCost.getCount() * 4, 1.0F))
                    .ifPresent(merchantOffers::add);
        }
        
        if (!merchantOffers.isEmpty())
        {
            // Sort items highest to lowest.
            merchantOffers.sort((a, b) -> Integer.compare(b.getBaseCostA().getCount(), a.getBaseCostA().getCount()));
        }

        return merchantOffers;
    }
}

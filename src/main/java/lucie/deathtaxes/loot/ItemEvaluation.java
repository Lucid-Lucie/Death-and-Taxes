package lucie.deathtaxes.loot;

import lucie.deathtaxes.DeathTaxes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public class ItemEvaluation
{
    private static final LootContextParamSet EVALUATION_KEY = new LootContextParamSet.Builder()
            .required(LootContextParams.TOOL)
            .optional(LootContextParams.ORIGIN)
            .optional(LootContextParams.THIS_ENTITY)
            .build();

    public static MerchantOffers evaluateItems(ServerPlayer serverPlayer, ServerLevel serverLevel, List<ItemStack> content)
    {
        LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(DeathTaxes.withModNamespace("gameplay/scavenger_pricing"));
        MerchantOffers merchantOffers = new MerchantOffers();

        for (ItemStack itemStack : content)
        {
            // Implement corresponding context parameters.
            LootParams lootParams = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.TOOL, itemStack)
                    .withParameter(LootContextParams.ORIGIN, serverPlayer.position())
                    .withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
                    .create(ItemEvaluation.EVALUATION_KEY);

            // Get a random evaluated item from the loot table.
            lootTable.getRandomItems(lootParams).stream()
                    .findAny()
                    .map(itemCost -> new MerchantOffer(itemCost, ItemStack.EMPTY, itemStack, 1, itemCost.getCount() * 4, 1.0F))
                    .ifPresent(merchantOffers::add);
        }

        if (!merchantOffers.isEmpty())
        {
            // Sort items from highest to lowest.
            merchantOffers.sort((a, b) -> Integer.compare(b.getBaseCostA().getCount(), a.getBaseCostA().getCount()));
        }

        return merchantOffers;
    }
}

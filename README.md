# Death & Taxes - Forge (1.20.1)
Death comes at a price.

**About**

Instead of losing your items to the void, lava, or a distant cave thousands of blocks from your base, they will now be stolen by a new mob called the Scavenger. The Scavenger is a grave robber that seeks out dropped player loot and is willing to return it, for a price. But be quick, the Scavenger leaves one day after being summoned.

**Technical**

When a player dies, their entire inventory is collected by the Scavenger, who is summoned once the player respawns. The player can buy back their inventory from the Scavenger, with each item being evaluated using the `deathtaxes:gameplay/scavenger_pricing` loot table. The Scavenger may pocket some of your items like Emeralds and Emerald Blocks, this can be changed in the `deathtaxes:blacklisted_loot` item tag.

**Forge Specifics**

This build is a backport of version **1.21.6** and is missing a few key components that have been changed in the current version. Most notably, data components, which are exclusive to **1.21** and above.

To maintain consistency with the latest version of the mod, two new loot conditions have been added for item evaluation done in the `deathtaxes:gameplay/scavenger_pricing` loot table:
* `deathtaxes:has_rarity`
* `deathtaxes:has_enchantmetns`
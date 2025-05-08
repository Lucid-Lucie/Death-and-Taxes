# Death & Taxes

Death comes at a price.

**About**

Instead of losing your items to the void, lava, or a distant cave thousands of blocks from your base, they will now be stolen by a new mob called the Scavenger. The Scavenger is a grave robber that seeks out dropped player loot and is willing to return it, for a price. But be quick, the Scavenger leaves one day after being summoned.

**Technical**

When a player dies, their entire inventory is collected by the Scavenger, who is summoned once the player respawns. The player can buy back their inventory from the Scavenger, with each item being evaluated using the `deathtaxes:gameplay/scavenger_pricing` loot table. The Scavenger may pocket some of your items like Emeralds and Emerald Blocks, this can be changed in the `deathtaxes:blacklisted_loot` item tag.

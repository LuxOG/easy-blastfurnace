package com.toofifty.easyblastfurnace.utils;


import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;

public enum BarsOres
{
    COPPER_ORE(Varbits.BLAST_FURNACE_COPPER_ORE, ItemID.COPPER_ORE, 2.267),
    TIN_ORE(Varbits.BLAST_FURNACE_TIN_ORE, ItemID.TIN_ORE, 2.267),
    IRON_ORE(Varbits.BLAST_FURNACE_IRON_ORE, ItemID.IRON_ORE, 2.267),
    COAL(Varbits.BLAST_FURNACE_COAL, ItemID.COAL, 2.267),
    MITHRIL_ORE(Varbits.BLAST_FURNACE_MITHRIL_ORE, ItemID.MITHRIL_ORE, 1.814),
    ADAMANTITE_ORE(Varbits.BLAST_FURNACE_ADAMANTITE_ORE, ItemID.ADAMANTITE_ORE, 2.271),
    RUNITE_ORE(Varbits.BLAST_FURNACE_RUNITE_ORE, ItemID.RUNITE_ORE, 2.267),
    SILVER_ORE(Varbits.BLAST_FURNACE_SILVER_ORE, ItemID.SILVER_ORE, 2.267),
    GOLD_ORE(Varbits.BLAST_FURNACE_GOLD_ORE, ItemID.GOLD_ORE, 2.267),
    BRONZE_BAR(Varbits.BLAST_FURNACE_BRONZE_BAR, ItemID.BRONZE_BAR, 1.814),
    IRON_BAR(Varbits.BLAST_FURNACE_IRON_BAR, ItemID.IRON_BAR, 1.814),
    STEEL_BAR(Varbits.BLAST_FURNACE_STEEL_BAR, ItemID.STEEL_BAR, 1.814),
    MITHRIL_BAR(Varbits.BLAST_FURNACE_MITHRIL_BAR, ItemID.MITHRIL_BAR, 1.587),
    ADAMANTITE_BAR(Varbits.BLAST_FURNACE_ADAMANTITE_BAR, ItemID.ADAMANTITE_BAR, 2.041),
    RUNITE_BAR(Varbits.BLAST_FURNACE_RUNITE_BAR, ItemID.RUNITE_BAR, 1.814),
    SILVER_BAR(Varbits.BLAST_FURNACE_SILVER_BAR, ItemID.SILVER_BAR, 1.814),
    GOLD_BAR(Varbits.BLAST_FURNACE_GOLD_BAR, ItemID.GOLD_BAR, 1.814);

    private static final Map<Integer, BarsOres> VARBIT;

    static
    {
        ImmutableMap.Builder<Integer, BarsOres> builder = new ImmutableMap.Builder<>();

        for (BarsOres s : values())
        {
            builder.put(s.getVarbit(), s);
        }

        VARBIT = builder.build();
    }

    @Getter
    private final int varbit;
    @Getter
    private final int itemID;
    @Getter
    private final double weight;

    BarsOres(int varbit, int itemID, double weight)
    {
        this.varbit = varbit;
        this.itemID = itemID;
        this.weight = weight;
    }

    public static BarsOres getVarbit(int varbit)
    {
        return VARBIT.get(varbit);
    }
}

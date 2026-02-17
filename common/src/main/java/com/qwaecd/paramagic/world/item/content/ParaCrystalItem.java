package com.qwaecd.paramagic.world.item.content;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.data.para.util.ParaComponentBuilder;
import com.qwaecd.paramagic.thaumaturgy.ParaCrystalComponent;
import com.qwaecd.paramagic.tools.nbt.CrystalComponentUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class ParaCrystalItem extends Item {
    public ParaCrystalItem() {
        super(new Properties().rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide()) {
            return super.use(level, player, usedHand);
        }

        ItemStack crystal = player.getItemInHand(usedHand);
        this.test(crystal);
        ParaCrystalComponent paraCrystal = CrystalComponentUtils.getComponentFromItemStack(crystal);

        return super.use(level, player, usedHand);
    }

    private void test(ItemStack crystal) {
        ParaComponentBuilder builder = new ParaComponentBuilder()
                .beginChild()
                    .beginChild()
                        .beginChild()
                        .endChild()
                    .endChild()
                .endChild()

                .beginChild()
                    .beginChild()
                        .beginChild()
                        .endChild()
                    .endChild()

                    .beginChild()
                .   endChild()
                .endChild()

                .beginChild()
                .endChild()

                .beginChild()
                    .beginChild()
                        .beginChild()
                            .beginChild()
                                .beginChild()
                                .endChild()
                            .endChild()
                        .endChild()
                    .endChild()
                .endChild();
        for (int i = 0; i < 4; i++) {
            var builder1 = builder.beginChild();
            for (int j = 0; j < 6; j++) {
                builder1.beginChild().endChild();
            }
            builder1.endChild();
        }
        ParaData paraData = new ParaData(builder.build());
        ParaCrystalComponent crystalComponent = new ParaCrystalComponent(paraData);
        CrystalComponentUtils.writeComponentToItemStack(crystal, crystalComponent);
    }
}

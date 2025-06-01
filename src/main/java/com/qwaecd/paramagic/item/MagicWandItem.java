package com.qwaecd.paramagic.item;

import com.qwaecd.paramagic.api.ManaContext;
import com.qwaecd.paramagic.capability.ManaCapability;
import com.qwaecd.paramagic.feature.ManaNode;
import com.qwaecd.paramagic.feature.SpellExecutor;
import com.qwaecd.paramagic.magic.TestMagic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MagicWandItem extends Item {
    private static final Logger LOGGER = LogManager.getLogger();
    private final int maxMana;

    public MagicWandItem(Properties properties, int maxMana) {
        super(properties.durability(maxMana));
        this.maxMana = maxMana;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new ManaCapability.ManaCapabilityProvider(maxMana);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {

            // Open spell selection GUI or execute spell
            // TODO: Implement spell selection and execution
//            ManaNode manaNode = new ManaNode(
//                    new TestMagic(),
//                    200
//            );
//
//            ManaContext manaContext = new ManaContext(
//                    level,
//                    player,
//                    new BlockPos(player.getBlockX(),player.getBlockY(),player.getBlockZ())
//            );
//            SpellExecutor.executeSpell(manaNode, manaContext);
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack stack = player.getItemInHand(context.getHand());
        Level level = context.getLevel();
        if (!level.isClientSide) {
            // Open spell selection GUI or execute spell
            // TODO: Implement spell selection and execution

            ManaContext manaContext = new ManaContext(
                    level,
                    player,
                    context.getClickedPos(),
                    stack
            );

            ManaNode rootNode = new ManaNode(
                    new TestMagic(),
                    20
            );
            ManaNode node_1 = new ManaNode(new TestMagic(), 40);
            node_1.addChild(new ManaNode(new TestMagic(), 60));
            rootNode.addChild(node_1);

            SpellExecutor.executeSpell(rootNode, manaContext);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
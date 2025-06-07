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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand){
        ItemStack itemInHand = player.getItemInHand(usedHand);
        setMana(itemInHand,Integer.MAX_VALUE);
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(itemInHand);
    }
    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStacktack, int remainingUseDuration) {
        // TODO: Implement spell selection and execution
    }

    public int getUseDuration(ItemStack pStack) {
        return Integer.MAX_VALUE;
    }

    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int timeCharged) {
        setMana(itemStack,0);
//        System.out.println(getMana(itemStack));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStacktack) {
        return UseAnim.BOW;
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack stack = player.getItemInHand(context.getHand());
        Level level = context.getLevel();
        if (!level.isClientSide) {
            // Open spell selection GUI or execute spell
            // TODO: Implement spell selection and execution
            setMana(stack,Integer.MAX_VALUE);

            ManaContext manaContext = new ManaContext(
                    level,
                    player,
                    context.getClickLocation(),
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

    private void setMana(ItemStack itemStack, int mana){
        LazyOptional<ManaCapability.IManaStorage> capability = itemStack.getCapability(ManaCapability.MANA_STORAGE);
        if (capability.resolve().isPresent()) {
            ManaCapability.IManaStorage manaStorage = capability.resolve().get();
            manaStorage.setMana(mana);
        }
    }

    private int getMana(ItemStack itemStack){
        LazyOptional<ManaCapability.IManaStorage> capability = itemStack.getCapability(ManaCapability.MANA_STORAGE);
        if (capability.resolve().isPresent()) {
            ManaCapability.IManaStorage manaStorage = capability.resolve().get();
            return manaStorage.getMana();
        }
        return 0;
    }
}
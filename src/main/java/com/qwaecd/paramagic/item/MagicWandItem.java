package com.qwaecd.paramagic.item;

import com.qwaecd.paramagic.api.ManaContext;
import com.qwaecd.paramagic.capability.ManaCapability;
import com.qwaecd.paramagic.client.renderer.MagicCircleManager;
import com.qwaecd.paramagic.feature.ManaNode;
import com.qwaecd.paramagic.feature.SpellExecutor;
import com.qwaecd.paramagic.feature.dynamic.texture.DynamicTestTexture;
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

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand){
        ItemStack itemInHand = player.getItemInHand(usedHand);
        setMana(itemInHand,Integer.MAX_VALUE);
        player.startUsingItem(usedHand);
        if (level.isClientSide){
            DynamicTestTexture testTexture = new DynamicTestTexture();
            MagicCircleManager.getInstance().getDynamicTextures().add(testTexture);
        }

        return InteractionResultHolder.success(itemInHand);
    }
    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStacktack, int remainingUseDuration) {
        // TODO: Implement spell selection and execution
        if (livingEntity instanceof Player player && level.isClientSide) {
            for (DynamicTestTexture dynamicTexture : MagicCircleManager.getInstance().getDynamicTextures()) {
                if (dynamicTexture.isActive()) {
                    // Update the texture with some example data
                    dynamicTexture.updateTexture(
                            (int) (Math.random() * 16),
                            (int) (Math.random() * 16),
                            new java.awt.Color((int) (Math.random() * 0xFFFFFF))
                    );
                }else {
                    // If the texture is not active, remove it from the list
                    MagicCircleManager.getInstance().getDynamicTextures().remove(dynamicTexture);
                }
            }

        }
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 32767;
    }

    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int timeCharged) {
//        System.out.println(getMana(itemStack));
        MagicCircleManager.getInstance().getDynamicTextures().clear();
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
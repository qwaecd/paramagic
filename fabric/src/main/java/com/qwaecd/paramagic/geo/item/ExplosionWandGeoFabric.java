package com.qwaecd.paramagic.geo.item;

//import com.qwaecd.paramagic.geo.renderer.ExplosionWandRendererFabric;
//import com.qwaecd.paramagic.world.item.feat.ExplosionWand;
//import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
//import software.bernie.geckolib.animatable.GeoItem;
//import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
//import software.bernie.geckolib.animatable.client.RenderProvider;
//import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
//import software.bernie.geckolib.core.animation.AnimatableManager;
//import software.bernie.geckolib.core.animation.AnimationController;
//import software.bernie.geckolib.core.animation.RawAnimation;
//import software.bernie.geckolib.core.object.PlayState;
//import software.bernie.geckolib.util.GeckoLibUtil;
//
//import java.util.function.Consumer;
//import java.util.function.Supplier;
//
//public final class ExplosionWandGeoFabric extends ExplosionWand implements GeoItem {
//    private static final RawAnimation ACTIVATE_ANIM = RawAnimation.begin().thenPlay("use.activate");
//    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
//    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
//
//    public ExplosionWandGeoFabric() {
//        super();
//        SingletonGeoAnimatable.registerSyncedAnimatable(this);
//    }
//
//    @Override
//    public void createRenderer(Consumer<Object> consumer) {
//        consumer.accept(new RenderProvider() {
//            private ExplosionWandRendererFabric renderer;
//
//            @Override
//            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
//                if (this.renderer == null)
//                    this.renderer = new ExplosionWandRendererFabric();
//
//                return this.renderer;
//            }
//        });
//    }
//
//    @Override
//    public Supplier<Object> getRenderProvider() {
//        return this.renderProvider;
//    }
//
//    @Override
//    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
//        controllers.add(new AnimationController<>(this, "Activation", 0, state -> {
//            if (!state.isCurrentAnimation(ACTIVATE_ANIM)) {
//                return state.setAndContinue(ACTIVATE_ANIM);
//            }
//            return PlayState.CONTINUE;
//        })
//                .triggerableAnim("activate", ACTIVATE_ANIM));
//    }
//
//    @Override
//    public AnimatableInstanceCache getAnimatableInstanceCache() {
//        return this.cache;
//    }
//}

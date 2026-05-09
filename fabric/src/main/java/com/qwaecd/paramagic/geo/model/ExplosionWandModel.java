package com.qwaecd.paramagic.geo.model;

import com.qwaecd.paramagic.geo.item.ExplosionWandGeoFabric;
import com.qwaecd.paramagic.tools.ModRL;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ExplosionWandModel extends GeoModel<ExplosionWandGeoFabric> {
    private static final ResourceLocation MODEL = ModRL.inModSpace("geo/explosion_wand.geo.json");
    private static final ResourceLocation TEXTURE = ModRL.inModSpace("textures/item/feat/explosion_wand_geo.png");
    private static final ResourceLocation ANIMATION = ModRL.inModSpace("animations/explosion_wand.animation.json");

    @Override
    public ResourceLocation getModelResource(ExplosionWandGeoFabric explosionWand) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ExplosionWandGeoFabric explosionWand) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ExplosionWandGeoFabric explosionWand) {
        return ANIMATION;
    }
}

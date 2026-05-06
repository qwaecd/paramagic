package com.qwaecd.paramagic.client.obj.laser;

import com.qwaecd.paramagic.client.material.EnergyFlowMaterial;
import com.qwaecd.paramagic.client.renderbase.RenderableObject;
import com.qwaecd.paramagic.client.renderbase.SharedMeshes;
import org.joml.Vector3f;

public class EnergyCoreSphere extends RenderableObject {
    public EnergyCoreSphere(EnergyFlowMaterial material) {
        super(SharedMeshes.laserSphere(), material);
    }

    public static EnergyCoreSphere createDefault() {
        return new EnergyCoreSphere(new EnergyFlowMaterial());
    }

    public EnergyCoreSphere setSphere(Vector3f center, float radius) {
        this.transform.setPosition(center).setScale(radius);
        return this;
    }
}

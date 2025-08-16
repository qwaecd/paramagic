package com.qwaecd.paramagic.client.renderbase;

import com.qwaecd.paramagic.client.renderbase.prototype.SpherePrototype;
import com.qwaecd.paramagic.client.renderbase.prototype.UnitQuadPrototype;

public class BaseObjectManager {

    public static void init() {
        SpherePrototype.init();
        UnitQuadPrototype.init();
    }

}

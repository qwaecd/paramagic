package com.qwaecd.paramagic;

import com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties;
import com.qwaecd.paramagic.data.para.AllParaComponentData;
import com.qwaecd.paramagic.datagen.Lang;
import com.qwaecd.paramagic.network.codec.codable.CodableTypeRegistry;
import com.qwaecd.paramagic.particle.server.ServerEffectManager;
import com.qwaecd.paramagic.platform.Services;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpellRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Paramagic {
	public static final String MOD_ID = "paramagic";
	public static final String MOD_NAME = "Paramagic";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static void init() {
		Paramagic.LOG.debug("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
		if (Services.PLATFORM.isModLoaded(Paramagic.MOD_ID)) {
			Paramagic.LOG.debug("Hello to paramagic");
		}
		Lang.init();
		CodableTypeRegistry.init();
		AllParaComponentData.registerAll();
		AllEmitterProperties.registerAll();
		ServerEffectManager.init();
		BuiltinSpellRegistry.init();
	}
}
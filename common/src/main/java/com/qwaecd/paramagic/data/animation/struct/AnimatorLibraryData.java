package com.qwaecd.paramagic.data.animation.struct;

import java.util.Map;
import java.util.Optional;

public class AnimatorLibraryData {
    /**
     * A map of named, reusable AnimatorData templates.<p>
     * Key: The unique name of the animator (e.g., "fastSpin", "slowPulse").<p>
     * Value: The AnimatorData definition.
     */
    private final Map<String, AnimatorData> templates;

    public AnimatorLibraryData(Map<String, AnimatorData> templates) {
        this.templates = templates;
    }

    public Optional<AnimatorData> getTemplateByName(String name) {
        return Optional.ofNullable(templates.get(name));
    }
}

package com.qwaecd.paramagic.client.input;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

import java.util.Objects;

/**
 * A loader-neutral description of a client key binding.
 *
 * <p>The loader modules turn this definition into their respective
 * {@code KeyMapping} and invoke {@link #press()} after consuming a click.</p>
 */
@PlatformScope(PlatformScopeType.CLIENT)
public record KeyBindingDefinition(String id, int defaultKeyCode, String categoryKey, Runnable onPress) {
    public KeyBindingDefinition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Key binding id must not be blank");
        }
        if (categoryKey == null || categoryKey.isBlank()) {
            throw new IllegalArgumentException("Key binding category must not be blank");
        }
        Objects.requireNonNull(onPress, "Key binding action must not be null");
    }

    public String translationKey() {
        return "key." + Paramagic.MOD_ID + "." + id;
    }

    public void press() {
        onPress.run();
    }
}

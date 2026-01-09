package com.qwaecd.paramagic.spell.builtin;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.SpellIdentifier;
import com.qwaecd.paramagic.spell.core.Spell;
import com.qwaecd.paramagic.spell.core.SpellDefinition;
import com.qwaecd.paramagic.spell.logic.ExecutionContext;

@PlatformScope(PlatformScopeType.COMMON)
public interface BuiltinSpell {
    SpellIdentifier getSpellId();
    SpellDefinition definition();
    default Spell create() {
        return new Spell(this.definition(), true);
    }

    /**
     * 该法术在 服务端 执行时被调用.<br>
     * 不要使用仅客户端的类, 该方法不会在客户端执行.
     */
    @PlatformScope(PlatformScopeType.SERVER)
    void execute(ExecutionContext context);
}

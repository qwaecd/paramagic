package com.qwaecd.paramagic.world.entity;

import com.qwaecd.paramagic.network.serializer.AllEntityDataSerializers;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.BuiltinSpellId;
import com.qwaecd.paramagic.spell.SpellSpawnerClient;
import com.qwaecd.paramagic.spell.SpellUnion;
import com.qwaecd.paramagic.spell.session.SessionManagers;
import com.qwaecd.paramagic.spell.session.client.ClientSession;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Optional;

public class SpellAnchorEntity extends Entity {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpellAnchorEntity.class);

    public static final String IDENTIFIER = "spell_anchor";
    private static final int MAX_LIFETIME_TICKS = 20 * 3; // 3s
    /**
     * 仅应该在服务端, 用于定时清除不存在 spell 的实体.
     */
    @PlatformScope(PlatformScopeType.SERVER)
    private int lifetimeTicks = 0;
    private static final EntityDataAccessor<Optional<SpellUnion>> OPTIONAL_SPELL_UNION = SynchedEntityData.defineId(SpellAnchorEntity.class, AllEntityDataSerializers.OPTIONAL_SPELL_UNION);

    public SpellAnchorEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        this.setInvulnerable(true);
    }

    public SpellAnchorEntity(Level level) {
        this(ModEntityTypes.SPELL_ANCHOR_ENTITY, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            return;
        }
        if (this.isNoSpell()) {
            this.lifetimeTicks++;
        }
        if (this.lifetimeTicks > MAX_LIFETIME_TICKS) {
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OPTIONAL_SPELL_UNION, Optional.empty());
    }

    @Override
    public void onSyncedDataUpdated(@Nonnull EntityDataAccessor<?> key) {
        if (this.level().isClientSide() && OPTIONAL_SPELL_UNION.equals(key)) {
            this.createSessionOnClient();
        }
    }

    @PlatformScope(PlatformScopeType.CLIENT)
    private void createSessionOnClient() {
        SpellUnion spellUnion = this.entityData.get(OPTIONAL_SPELL_UNION).orElseThrow(() -> new RuntimeException("SpellUnion is empty on client when creating session"));

        if (spellUnion.isBuiltinSpell()) {
            BuiltinSpellId builtinId = spellUnion.getBuiltinId();
            if (builtinId == null) {
                LOGGER.error("BuiltinSpellId is null for builtin spell in SpellUnion");
                return;
            }
            SpellSpawnerClient.spawnInternalOnClient(this.level(), spellUnion.getSessionRef(), builtinId, this);
        } else {
            // Para spell
        }
    }

    @Override
    public void onClientRemoval() {
        Optional<SpellUnion> spellUnion = this.entityData.get(OPTIONAL_SPELL_UNION);
        if (spellUnion.isEmpty()) {
            return;
        }
        ClientSession session = (ClientSession) SessionManagers.getForClient().getSession(spellUnion.get().getSessionRef().getServerSessionId());
        if (session != null) {
            session.interrupt();
        }
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundTag compound) {
    }

    @SuppressWarnings("RedundantMethodOverride")
    @Override
    public boolean isPushable() {
        return false;
    }

    @PlatformScope(PlatformScopeType.SERVER)
    public void attachSessionData(@Nonnull ServerSession session, SpellUnion spellUnion) {
        this.entityData.set(OPTIONAL_SPELL_UNION, Optional.of(spellUnion), true);
    }

    public boolean isNoSpell() {
        return this.entityData.get(OPTIONAL_SPELL_UNION).isEmpty();
    }
}

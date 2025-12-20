package com.qwaecd.paramagic.entity;

import com.qwaecd.paramagic.network.serializer.AllEntityDataSerializers;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.SpellSpawnerClient;
import com.qwaecd.paramagic.spell.core.Spell;
import com.qwaecd.paramagic.spell.core.SpellDefinition;
import com.qwaecd.paramagic.spell.listener.SpellPhaseListener;
import com.qwaecd.paramagic.spell.listener.ListenerFactoryClient;
import com.qwaecd.paramagic.spell.session.SessionManagers;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.spell.session.SpellSessionRef;
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
import java.util.List;
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

    private static final EntityDataAccessor<Optional<SpellDefinition>> OPTIONAL_SPELL_DATA = SynchedEntityData.defineId(SpellAnchorEntity.class, AllEntityDataSerializers.OPTIONAL_SPELL_DEF);
    private static final EntityDataAccessor<Optional<SpellSessionRef>> OPTIONAL_SESSION_REF = SynchedEntityData.defineId(SpellAnchorEntity.class, AllEntityDataSerializers.SPELL_SESSION_REF);

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
//        System.out.println("session ref: " + this.entityData.get(OPTIONAL_SESSION_REF).isEmpty());
//        System.out.println("spell: " + this.entityData.get(OPTIONAL_SPELL_DATA).isEmpty());
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
        this.entityData.define(OPTIONAL_SPELL_DATA, Optional.empty());
        this.entityData.define(OPTIONAL_SESSION_REF, Optional.empty());
    }

    @Override
    public void onSyncedDataUpdated(@Nonnull EntityDataAccessor<?> key) {
        if (OPTIONAL_SPELL_DATA.equals(key)) {
            Optional<SpellDefinition> optionalSpell = this.entityData.get(OPTIONAL_SPELL_DATA);
            if (!this.level().isClientSide()) {
                return;
            }
            if (optionalSpell.isEmpty()) {
                return;
            }
            this.onUpdateEntityData();
            return;
        }
        if (OPTIONAL_SESSION_REF.equals(key)) {
            Optional<SpellSessionRef> optionalSessionRef = this.entityData.get(OPTIONAL_SESSION_REF);
            if (!this.level().isClientSide()) {
                return;
            }
            if (optionalSessionRef.isEmpty()) {
                return;
            }
            this.onUpdateEntityData();
        }
    }

    @PlatformScope(PlatformScopeType.CLIENT)
    private void onUpdateEntityData() {
        if (!this.tryEnsureClientSession()) {
            return;
        }
        // assert not null
        SpellDefinition spellDefinition = this.entityData.get(OPTIONAL_SPELL_DATA).orElseThrow();
        SpellSessionRef sessionRef = this.entityData.get(OPTIONAL_SESSION_REF).orElseThrow();

        SpellSession existSession = SessionManagers.getForClient().getSession(sessionRef.serverSessionId);
        if (existSession != null) {
            return;
        }

        ClientSession clientSession = SpellSpawnerClient.spawnOnClient(this.level(), sessionRef, Spell.create(spellDefinition), this);
        if (clientSession != null) {
            List<SpellPhaseListener> listeners = ListenerFactoryClient.createListenersFromConfig(spellDefinition);
            listeners.forEach(clientSession::registerListener);
        }
    }

    @Override
    public void onClientRemoval() {
        Optional<SpellSessionRef> spellSessionRef = this.entityData.get(OPTIONAL_SESSION_REF);
        if (spellSessionRef.isEmpty()) {
            return;
        }
        ClientSession session = (ClientSession) SessionManagers.getForClient().getSession(spellSessionRef.get().serverSessionId);
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
    public void attachSession(@Nonnull ServerSession session) {
        this.entityData.set(OPTIONAL_SPELL_DATA, Optional.of(session.getSpell().definition), true);
        this.entityData.set(OPTIONAL_SESSION_REF, Optional.of(SpellSessionRef.fromSession(session)), true);
    }

    public boolean isNoSpell() {
        return this.entityData.get(OPTIONAL_SPELL_DATA).isEmpty();
    }

    public boolean hasSessionRef() {
        return this.entityData.get(OPTIONAL_SESSION_REF).isPresent();
    }


    @PlatformScope(PlatformScopeType.CLIENT)
    private boolean tryEnsureClientSession() {
        // 存在 spell 且 存在 sessionRef
        return !this.isNoSpell() && this.hasSessionRef();
    }
}

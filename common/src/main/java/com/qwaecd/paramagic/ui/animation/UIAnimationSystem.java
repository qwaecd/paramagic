package com.qwaecd.paramagic.ui.animation;

import com.qwaecd.paramagic.ui.core.UINode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class UIAnimationSystem {
    private final List<Entry> entries = new ArrayList<>();

    @Nonnull
    private final IdentityHashMap<BaseUIAnimator<?>, Entry> byAnimator = new IdentityHashMap<>();

    @Nonnull
    private final IdentityHashMap<UINode, Set<Entry>> byOwner = new IdentityHashMap<>();

    @Nonnull
    private final Map<AnimationSlot, Entry> bySlot = new HashMap<>();

    private final Queue<Entry> pendingAdd = new ConcurrentLinkedQueue<>();
    private final Queue<Entry> pendingRemove = new ConcurrentLinkedQueue<>();

    public UIAnimationSystem() {
    }

    public void updateAll(float deltaTime) {
        this.flushPendingRemove();
        this.flushPendingAdd();

        var iterator = this.entries.iterator();
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            if (entry.removed) {
                continue;
            }
            BaseUIAnimator<?> animator = entry.animator;
            animator.update(deltaTime);
            if (animator.isFinished()) {
                iterator.remove();
                this.detachEntry(entry);
                animator.remove();
            }
        }

        this.flushPendingRemove();
    }

    public void addAnimator(@Nullable UINode owner, @Nonnull BaseUIAnimator<?> animator) {
        this.addAnimator(owner, null, animator);
    }

    public void addAnimator(@Nullable UINode owner, @Nullable String key, @Nonnull BaseUIAnimator<?> animator) {
        if (this.byAnimator.containsKey(animator)) {
            return;
        }

        if (owner != null && key != null) {
            this.removeAnimator(this.getAnimator(owner, key));
        }

        Entry entry = new Entry(owner, key, animator);
        this.pendingAdd.offer(entry);
        this.byAnimator.put(animator, entry);
        if (owner != null) {
            this.byOwner.computeIfAbsent(owner, ignored -> new HashSet<>()).add(entry);
        }
        if (entry.slot != null) {
            this.bySlot.put(entry.slot, entry);
        }
    }

    public void addAnimator(@Nonnull BaseUIAnimator<?> animator) {
        this.addAnimator(null, null, animator);
    }

    public void removeAnimator(@Nullable BaseUIAnimator<?> animator) {
        if (animator == null) {
            return;
        }
        Entry entry = this.byAnimator.get(animator);
        if (entry == null || entry.removed || entry.queuedForRemoval) {
            return;
        }
        entry.queuedForRemoval = true;
        this.pendingRemove.offer(entry);
        animator.cancel();
    }

    @Nullable
    public BaseUIAnimator<?> getAnimator(@Nonnull UINode owner, @Nonnull String key) {
        Entry entry = this.bySlot.get(new AnimationSlot(owner, key));
        return entry == null ? null : entry.animator;
    }

    public void replaceAnimator(@Nonnull UINode owner, @Nonnull String key, @Nonnull BaseUIAnimator<?> animator) {
        this.removeAnimator(this.getAnimator(owner, key));
        this.addAnimator(owner, key, animator);
    }

    public void removeNodeAnimators(@Nonnull UINode owner) {
        Set<Entry> entries = this.byOwner.get(owner);
        if (entries == null || entries.isEmpty()) {
            return;
        }

        for (Entry entry : List.copyOf(entries)) {
            this.removeAnimator(entry.animator);
        }
    }

    public void removeAnimatorsInSubtree(@Nonnull UINode root) {
        for (UINode owner : List.copyOf(this.byOwner.keySet())) {
            if (root.containsInSubtree(owner)) {
                this.removeNodeAnimators(owner);
            }
        }
    }

    public void close() {
        for (Entry entry : List.copyOf(this.byAnimator.values())) {
            BaseUIAnimator<?> animator = entry.animator;
            if (!entry.removed) {
                if (!animator.isFinished()) {
                    animator.cancel();
                }
                this.detachEntry(entry);
                animator.remove();
            }
        }
        this.entries.clear();
        this.byAnimator.clear();
        this.byOwner.clear();
        this.bySlot.clear();
        this.pendingAdd.clear();
        this.pendingRemove.clear();
    }

    private void flushPendingAdd() {
        while (this.pendingAdd.peek() != null) {
            Entry entry = this.pendingAdd.poll();
            if (!entry.removed) {
                this.entries.add(entry);
            }
        }
    }

    private void flushPendingRemove() {
        while (this.pendingRemove.peek() != null) {
            Entry entry = this.pendingRemove.poll();
            this.removeEntryNow(entry);
        }
    }

    private void removeEntryNow(@Nonnull Entry entry) {
        if (entry.removed) {
            return;
        }
        this.entries.remove(entry);
        this.detachEntry(entry);
        entry.animator.remove();
    }

    private void detachEntry(@Nonnull Entry entry) {
        if (entry.removed) {
            return;
        }

        entry.removed = true;
        this.byAnimator.remove(entry.animator);
        if (entry.owner != null) {
            Set<Entry> ownerEntries = this.byOwner.get(entry.owner);
            if (ownerEntries != null) {
                ownerEntries.remove(entry);
                if (ownerEntries.isEmpty()) {
                    this.byOwner.remove(entry.owner);
                }
            }
        }
        if (entry.slot != null) {
            if (this.bySlot.get(entry.slot) == entry) {
                this.bySlot.remove(entry.slot);
            }
        }
    }

    private static final class Entry {
        @Nullable
        private final UINode owner;
        @Nullable
        private final AnimationSlot slot;
        @Nonnull
        private final BaseUIAnimator<?> animator;
        private boolean queuedForRemoval = false;
        private boolean removed = false;

        private Entry(@Nullable UINode owner, @Nullable String key, @Nonnull BaseUIAnimator<?> animator) {
            this.owner = owner;
            this.slot = owner == null || key == null ? null : new AnimationSlot(owner, key);
            this.animator = animator;
        }
    }

    private static final class AnimationSlot {
        @Nonnull
        private final UINode owner;
        @Nonnull
        private final String key;

        private AnimationSlot(@Nonnull UINode owner, @Nonnull String key) {
            this.owner = owner;
            this.key = key;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof AnimationSlot other
                    && this.owner == other.owner
                    && this.key.equals(other.key);
        }

        @Override
        public int hashCode() {
            return 31 * System.identityHashCode(this.owner) + this.key.hashCode();
        }
    }
}

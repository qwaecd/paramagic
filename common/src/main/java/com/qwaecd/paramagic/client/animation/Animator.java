package com.qwaecd.paramagic.client.animation;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Animator {
    @Getter
    private final List<AnimationTrack> tracks;
    float currentTime;
    float speed;
    boolean isPlaying;
    boolean isLooping;

    public Animator() {
        this.tracks = new ArrayList<>();
    }

    public void update(float deltaTime) {
    }

    public void addTrack(AnimationTrack track) {
        this.tracks.add(track);
    }
}

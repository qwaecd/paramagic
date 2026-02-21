package com.qwaecd.paramagic.client.animation;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Animator {
    @Getter
    protected final List<AnimationTrack> tracks;
    protected float currentTime = 0.0f;
    @Setter
    protected float speed = 1.0f;
    @Setter
    protected boolean isPlaying = true;
    protected float duration = 0.0f;

    public Animator() {
        this.tracks = new ArrayList<>();
    }

    /**
     * @param deltaTime 自上一帧过去的时间，单位秒。
     */
    public void update(float deltaTime) {
        if (!isPlaying || tracks.isEmpty()) {
            return;
        }

        this.currentTime += deltaTime * speed;

        boolean allTracksFinished = true;
        for (AnimationTrack track : tracks) {
            if (!track.isFinished(this.currentTime)) {
                allTracksFinished = false;
                break;
            }
        }

        if (allTracksFinished) {
            this.isPlaying = false;
        }

        for (AnimationTrack track : tracks) {
            track.apply(this.currentTime);
        }
    }

    public void addTrack(AnimationTrack track) {
        this.tracks.add(track);

        if (track.getDuration() > this.duration) {
            this.duration = track.getDuration();
        }
    }

    public void play() {
        this.isPlaying = true;
    }

    public void pause() {
        this.isPlaying = false;
    }

    public void reset() {
        this.isPlaying = false;
        this.currentTime = 0.0f;
        for (AnimationTrack track : tracks) {
            track.apply(0.0f);
        }
    }
}

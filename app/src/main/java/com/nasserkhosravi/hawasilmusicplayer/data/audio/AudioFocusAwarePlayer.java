package com.nasserkhosravi.hawasilmusicplayer.data.audio;

/**
 * {@code AudioFocusAwarePlayer} defines an interface for players
 * to respond to audio focus changes.
 */
public interface AudioFocusAwarePlayer {
    boolean isPlaying();

    void play();

    void pause();

    void stop();

    void setVolume(float volume);
}
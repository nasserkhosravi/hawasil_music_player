package com.nasserkhosravi.hawasilmusicplayer.data.model

import com.nasserkhosravi.hawasilmusicplayer.data.DataFakeFactory
import org.junit.Assert.*
import org.junit.Test

class SongModelTest {
    val song = DataFakeFactory.getPureSong()

    @Test
    fun reversePlayStatus() {
        assertFalse(song.isPlaying())

        song.reversePlayStatus()
        assertTrue(song.isPlaying())

        song.reversePlayStatus()
        assertFalse(song.isPlaying())
    }

    @Test
    fun isPlaying() {
        assertFalse(song.isPlaying())

        song.status = SongStatus.PAUSE
        assertFalse(song.isPlaying())

        song.status = SongStatus.PLAYING
        assertTrue(song.isPlaying())
    }

    @Test
    fun resetToPassiveState() {
        song.passedDuration = 10
        song.status = SongStatus.PLAYING

        assertEquals(10, song.passedDuration)
        assertEquals(SongStatus.PLAYING, song.status)

        song.resetToPassiveState()
        assertEquals(SongStatus.PAUSE, song.status)

    }
}
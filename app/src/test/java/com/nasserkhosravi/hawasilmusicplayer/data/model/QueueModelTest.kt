package com.nasserkhosravi.hawasilmusicplayer.data.model

import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class QueueModelTest {

    @Test
    fun isNewSong() {
        val queue = QueueModel()
        val song = mock(SongModel::class.java)
        val song2 = mock(SongModel::class.java)

        queue.items.add(song)
        queue.items.add(song2)
        queue.active(0)

        `when`(song.id).thenReturn(1)
        `when`(song2.id).thenReturn(2)

        assertFalse(queue.isNewSong(song.id))
        assertTrue(queue.isNewSong(song2.id))

        queue.active(1)
        assertTrue(queue.isNewSong(song.id))
        assertFalse(queue.isNewSong(song2.id))
    }

    @Test
    fun isNewQueue() {
        val queue1 = QueueModel()
        queue1.queueId = "queue one"

        val queue2 = QueueModel()
        queue2.queueId = "queue two"

        assertFalse(queue1.isNewQueue(queue1.queueId))
        assertTrue(queue1.isNewQueue(queue2.queueId))
    }

    @Test
    fun isOnFirstItem() {
        val queue = QueueModel()
        val song1 = mock(SongModel::class.java)
        val song2 = mock(SongModel::class.java)

        assertFalse(queue.isOnFirstItem())
        queue.items.add(song1)
        queue.items.add(song2)
        assertFalse(queue.isOnFirstItem())

        queue.active(0)
        assertTrue(queue.isOnFirstItem())

        queue.active(1)
        assertFalse(queue.isOnFirstItem())
    }

    @Test
    fun isOnLastItem() {
        val queue = QueueModel()
        val song1 = mock(SongModel::class.java)
        val song2 = mock(SongModel::class.java)

        assertFalse(queue.isOnLastItem())
        queue.items.add(song1)
        queue.items.add(song2)
        assertFalse(queue.isOnLastItem())

        queue.active(0)
        assertFalse(queue.isOnLastItem())

        queue.active(1)
        assertTrue(queue.isOnLastItem())
    }

    @Test
    fun isSingular() {
        val queue = QueueModel()
        assertFalse(queue.isSingular())

        val song = mock(SongModel::class.java)
        queue.items.add(song)
        assertTrue(queue.isSingular())

        queue.items.add(song)
        assertFalse(queue.isSingular())

        queue.items.add(song)
        assertFalse(queue.isSingular())
    }

    @Test
    fun active() {
        val queue = QueueModel()
        val song1 = mock(SongModel::class.java)
        val song2 = mock(SongModel::class.java)

        assertEquals(queue.selectedIndex, -1)
        assertEquals(queue.selected, null)

        queue.items.add(song1)
        queue.items.add(song2)
        assertEquals(queue.selectedIndex, -1)
        assertEquals(queue.selected, null)

        queue.active(0)
        assertEquals(queue.selectedIndex, 0)
        assertEquals(queue.selected, song1)

        queue.active(1)
        assertEquals(queue.selectedIndex, 1)
        assertEquals(queue.selected, song2)
    }

    @Test
    fun shouldStartFromFirst() {
        val queue = QueueModel()
        assertFalse(queue.shouldStartFromFirst())

        queue.items.add(mock(SongModel::class.java))
        queue.active(0)
        assertFalse(queue.shouldStartFromFirst())

        queue.isEnableRepeat = true
        //when we have one item and repeat is enable
        assertTrue(queue.shouldStartFromFirst())

        queue.items.add(mock(SongModel::class.java))
        queue.items.add(mock(SongModel::class.java))
        queue.items.add(mock(SongModel::class.java))

        queue.active(2)
        assertFalse(queue.shouldStartFromFirst())

        queue.active(queue.items.lastIndex)
        //when item is one last and repeat is enable
        assertTrue(queue.shouldStartFromFirst())
    }

    @Test
    fun toggleShuffle() {
        val queue = QueueModel()
        val orderItems = ArrayList<SongModel>()
        for (i in 0..5) {
            val song = mock(SongModel::class.java)
            `when`(song.title).thenReturn(i.toString())
            orderItems.add(song)
        }
        queue.items.addAll(orderItems)

        assertFalse(queue.isShuffled)
        assertEquals(queue.items, orderItems)

        queue.setShuffle(true)
        assertTrue(queue.isShuffled)
        assertNotEquals(queue.items, orderItems)

        queue.setShuffle(false)
        assertFalse(queue.isShuffled)
        assertEquals(queue.items, orderItems)
    }

    @Test
    fun toggleRepeat() {
        val queue = QueueModel()
        assertFalse(queue.isEnableRepeat)
        queue.setRepeat(true)
        assertTrue(queue.isEnableRepeat)
        queue.setRepeat(false)
        assertFalse(queue.isEnableRepeat)
    }

    @Test
    fun hasNextItem() {
        val queue = QueueModel()
        val song1 = mock(SongModel::class.java)
        val song2 = mock(SongModel::class.java)

        assertFalse(queue.hasNextItem())
        queue.items.add(song1)
        assertFalse(queue.hasNextItem())

        queue.items.add(song2)
        assertFalse(queue.hasNextItem())

        queue.active(0)
        assertTrue(queue.hasNextItem())
        queue.active(1)
        assertFalse(queue.hasNextItem())
    }

    @Test
    fun reset() {
        val queue = QueueModel()
        with(queue) {
            queueId = "someGeneratedId"
            shouldLoad = true
            selectedIndex = 20
            toggleShuffle()
            setRepeat(true)
            val song = mock(SongModel::class.java)
            items.add(song)
            active(0)
        }

        val newInstance = QueueModel()
        val beforeReset = (newInstance == queue)
        assertFalse(beforeReset)

        queue.reset()

        val afterReset = (newInstance == queue)
        assertTrue(afterReset)
    }

}
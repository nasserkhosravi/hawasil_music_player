package com.nasserkhosravi.hawasilmusicplayer.data

import com.nasserkhosravi.hawasilmusicplayer.data.model.QueueModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class UserPrefTest {
    @Throws(Exception::class)
    fun setUp() {
        val context = RuntimeEnvironment.application
        UserPref.build(context, "user_pref_test")
    }

    @Test
    fun hasQueue() {
        val shouldBeFalse = UserPref.hasQueue()
        val shouldBeFalse2 = UserPref.hasQueue()
        assertFalse(shouldBeFalse)
        assertFalse(shouldBeFalse2)

        val queueData = QueueModel()
        UserPref.saveQueueData(queueData)

        val shouldBeTrue = UserPref.hasQueue()
        assertTrue(shouldBeTrue)
    }

    /**
     * both test depend on each other
     * so I decide to do them in one test
     */
    @Test
    fun save_retrieve_QueueData() {
        val queue = ArrayList<SongModel>()
        val selectedSong = DataFakeFactory.getSong()

        val queueDataSave = QueueModel()
        queueDataSave.queueId = "someGeneratedId"
        queueDataSave.setShuffle(true)
        queueDataSave.isEnableRepeat = false
        queueDataSave.selectedIndex = 10
        queueDataSave.items.addAll(queue)
        queueDataSave.selected = selectedSong
        UserPref.saveQueueData(queueDataSave)

        val queueDataRetrieve = UserPref.retrieveQueueData()!!

        assertEquals(queueDataSave.queueId, queueDataRetrieve.queueId)
        assertEquals(queueDataSave.isShuffled, queueDataRetrieve.isShuffled)
        assertEquals(queueDataSave.isEnableRepeat, queueDataRetrieve.isEnableRepeat)
        assertEquals(queueDataSave.selectedIndex, queueDataRetrieve.selectedIndex)
        assertEquals(queueDataSave.selected, queueDataRetrieve.selected)
        assertEquals(queueDataSave.items, queueDataRetrieve.items)
    }

    @Test
    fun clear() {
        UserPref.saveQueueData(QueueModel())
        assertTrue(UserPref.hasQueue())

        UserPref.clear()

        assertFalse(UserPref.hasQueue())
        val queueDataRetrieve = UserPref.retrieveQueueData()
        assertNull(queueDataRetrieve)

    }

    @Before
    fun tearDown() {
        UserPref.clear()
    }

}
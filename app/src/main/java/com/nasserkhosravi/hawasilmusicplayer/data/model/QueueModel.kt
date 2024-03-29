package com.nasserkhosravi.hawasilmusicplayer.data.model

import com.google.gson.annotations.Expose
import com.nasserkhosravi.hawasilmusicplayer.app.App

class QueueModel {
    var shouldLoad = false

    @Expose
    var selected: SongModel? = null
    @Expose
    val items = ArrayList<SongModel>()
    @Expose
    var selectedIndex = -1
    @Expose
    var queueId = ""
    @Expose
    var isEnableRepeat = false

    @Expose
    var isShuffled = false
        private set

    fun toJson(): String {
        return App.get().jsonAdapter.toJson(this)
    }

    fun isNewSong(newSongId: Long) = selected?.id != newSongId

    fun isNewQueue(queueId: String): Boolean {
        return this.queueId != queueId
    }

    fun isOnFirstItem() = selectedIndex == 0

    fun isOnLastItem(): Boolean {
        if (selectedIndex == -1) {
            return false
        }
        return selectedIndex == items.lastIndex
    }

    fun isSingular(): Boolean {
        return items.size == 1
    }

    fun active(position: Int) {
        selected = items[position]
        selectedIndex = position
        selected!!.status = SongStatus.PLAYING
    }

    fun hasNextItem(): Boolean {
        if (selectedIndex == -1) {
            return false
        }
        return selectedIndex < items.lastIndex
    }

    fun shouldStartFromFirst(): Boolean {
        return if (isEnableRepeat) {
            isSingular() || isOnLastItem()
        } else {
            false
        }
    }

    fun setRepeat(isEnable: Boolean) {
        isEnableRepeat = isEnable
    }

    fun setShuffle(isEnable: Boolean) {
        if (isEnable) {
            shuffleItems()
        } else {
            unShuffleItems()
        }
        this.isShuffled = isEnable
    }

    private fun shuffleItems() {
        items.shuffle()
    }

    private fun unShuffleItems() {
        items.sortBy { it.title }
    }

    fun reset() {
        shouldLoad = false
        items.clear()
        selectedIndex = -1
        queueId = ""
        isShuffled = false
        isEnableRepeat = false
        selected = null
    }

    override fun hashCode(): Int {
        var result = shouldLoad.hashCode()
        result = 31 * result + (selected?.hashCode() ?: 0)
        result = 31 * result + items.hashCode()
        result = 31 * result + selectedIndex
        result = 31 * result + queueId.hashCode()
        result = 31 * result + isEnableRepeat.hashCode()
        result = 31 * result + isShuffled.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QueueModel

        if (shouldLoad != other.shouldLoad) return false
        if (selected != other.selected) return false
        if (items != other.items) return false
        if (selectedIndex != other.selectedIndex) return false
        if (queueId != other.queueId) return false
        if (isEnableRepeat != other.isEnableRepeat) return false
        if (isShuffled != other.isShuffled) return false

        return true
    }

    fun deActiveSelected() {
        items[selectedIndex].resetToPassiveState()
    }

    companion object {
        fun fromJson(json: String): QueueModel {
            return App.get().jsonAdapter.fromJson(json, QueueModel::class.java)
        }
    }

}
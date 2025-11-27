package com.example.mushits.data;

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val data: String,
    val uri: String,
    val artUri: String?
) {
    val durationReadable: String
        get() = formatDuration(duration)

    private fun formatDuration(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0)
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        else
            String.format("%02d:%02d", minutes, seconds)
    }
}
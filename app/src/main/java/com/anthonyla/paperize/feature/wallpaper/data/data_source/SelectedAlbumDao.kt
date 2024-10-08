package com.anthonyla.paperize.feature.wallpaper.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.anthonyla.paperize.feature.wallpaper.domain.model.Album
import com.anthonyla.paperize.feature.wallpaper.domain.model.SelectedAlbum
import com.anthonyla.paperize.feature.wallpaper.domain.model.Wallpaper
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for selected album for application to keep track of which album is currently selected for changing wallpapers from
 */
@Dao
interface SelectedAlbumDao {

    @Transaction
    @Query("SELECT * FROM album")
    fun getSelectedAlbum(): Flow<List<SelectedAlbum>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAlbum(album: Album)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWallpaper(wallpaper: Wallpaper)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWallpaperList(wallpapers: List<Wallpaper>)

    @Query("DELETE FROM album WHERE initialAlbumName = :initialAlbumName")
    suspend fun deleteAlbum(initialAlbumName: String)

    @Delete
    suspend fun deleteWallpaper(wallpaper: Wallpaper)

    @Query("DELETE FROM wallpaper WHERE initialAlbumName = :initialAlbumName")
    suspend fun cascadeDeleteWallpaper(initialAlbumName: String)

    @Query("DELETE FROM album")
    suspend fun deleteAllAlbums()

    @Query("DELETE FROM wallpaper")
    suspend fun deleteAllWallpapers()

    @Transaction
    suspend fun deleteAll() {
        deleteAllAlbums()
        deleteAllWallpapers()
    }

    @Transaction
    suspend fun upsertSelectedAlbum(selectedAlbum: SelectedAlbum) {
        upsertAlbum(selectedAlbum.album)
        upsertWallpaperList(selectedAlbum.wallpapers)
    }
}
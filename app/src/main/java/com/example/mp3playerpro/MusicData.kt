package com.example.mp3playerpro

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import java.io.Serializable

// 여기서 음악에서 뭐를 가져올지 선택하는 부분
class MusicData(id: String, title: String?, artists: String?, albumId: String?, duration: Long,like: Int) :
    Serializable {
    var id: String = ""
    var title: String?
    var artists: String?
    var albumId: String?
    var duration: Long?
    var like: Int?

    init {
        this.id = id
        this.title = title
        this.artists = artists
        this.albumId = albumId
        this.duration = duration
        this.like = like
    }

    // 음악 id를 통해서 음악 파일 URI(음악 파일의 디스크립트)를 가져오는 함수가 필요함.
    // ex) id = naver URI= htt:://naver.com.. 여기를 중간중간 교체하는 것
    // 음악 파일의 경로 = Uri
    fun getMusicUri(): Uri? =
        Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.id)

    //음악 Uri 가져오는 함수
    fun getAlbumUri(): Uri? = Uri.parse("content://media/external/audio/albumart/+ ${this.albumId}")

    // 음악앨범을 Uri를 통해서 Bitmap 으로 가져오는 함수
    fun getAlbumBitmap(content: Context, albumImageSize: Int): Bitmap? {
        val contentResolver: ContentResolver = content.contentResolver
        val albumUri = getAlbumUri()
        val options = BitmapFactory.Options()
        var bitmap: Bitmap? = null
        var parcelDescriptor: ParcelFileDescriptor? = null
        try {
            if (albumUri != null) {
                // 음악 이미지를 가져와서 BitmapFactory 만들려면 ParcelDescriptor(파일의 주소) 필요

                parcelDescriptor = contentResolver.openFileDescriptor(albumUri, "r")
                bitmap = BitmapFactory.decodeFileDescriptor(
                    parcelDescriptor?.fileDescriptor,
                    null,
                    options
                )
                // Bitmap 사이즈를 결정함.
                if (bitmap != null) {
                    //화면에 보여줄 이미지 사이즈가 아닐 경우
                    if (options.outHeight !== albumImageSize || options.outWidth !== albumImageSize) {
                        // 화면에 보여줄 이미지가 맞지 않을 경우 강제로 사이즈를 정해버림
                        val tempBitmap =
                            Bitmap.createScaledBitmap(bitmap, albumImageSize, albumImageSize, true)
                        // 기존에 있던 bitmap으로 해제
                        bitmap.recycle()
                        bitmap = tempBitmap
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MusicData", e.toString())
        } finally {
            try {
                if (parcelDescriptor != null) {
                    parcelDescriptor.close()
                }
            } catch (e: Exception) {
                Log.e("MusicData", e.toString())
            }
        }
        return bitmap
    }
}
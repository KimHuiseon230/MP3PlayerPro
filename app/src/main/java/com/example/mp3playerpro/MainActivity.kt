package com.example.mp3playerpro

import android.content.pm.PackageManager
import android.os.Build.VERSION_CODES.P
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mp3playerpro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    val permission = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE, // 읽기
//        android.Manifest.permission.WRITE_EXTERNAL_STORAGE // 쓰기
    )
    val REQUEST_CODE = 100
    lateinit var musicDataList: MutableList<MusicData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 외장 메모리를 승인
        val flag =
            ContextCompat.checkSelfPermission(this, permission[0]) // 퍼미션 승인의 여부 확인 -> int 값으로 돌려줌
        if (flag == PackageManager.PERMISSION_GRANTED) {
            startProcess()
            // 원하는 것을 진행
        } //if
        else {
            // 승인 요청
            // requestCode  받아야함. 사용자가 승인 -> 콜백 (승인을 받음)
            ActivityCompat.requestPermissions(this, permission, REQUEST_CODE)
        }
    }


    // 승인 요청 -> 바로 결과값을 해당 함수로 넘김.
    override fun onRequestPermissionsResult(
        requestCode: Int, // else 있는 requestCode 코드
        permissions: Array<out String>, //
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 원하는 것을 실행
            } else {
                Toast.makeText(this, " 권한을 승인해야 앱을 사용할수 있습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        } //if (requestCode == REQUEST_CODE)
    }

    // 어댑터를 만들어서 넘김
    private fun startProcess() {
        // 음원 정보를 가져옴.-> 음원 정보 주소를 가져와서 넣어줌
        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,

        )
        // 레코드 셋과 같은 효과
        val cursor = contentResolver.query(musicUri, projection, null, null, null)
        musicDataList = mutableListOf<MusicData>()
       if (cursor!!.count <=0) {
           Toast.makeText(this, "해당 기기의 내장 메모리에 음악파일이 존재하지 않습니다. 음악을 다운 받아주세요.", Toast.LENGTH_SHORT).show()
           finish()
        }
        while (cursor.moveToNext()) {
            val id = cursor.getString(0)
            val title = cursor.getString(1)
            val artists = cursor.getString(2)
            val albumId = cursor.getString(3)
            val duration = cursor.getLong(4)
            val like = cursor.getInt(5)
            val musicData =MusicData(id,title,artists,albumId,duration,like)
            musicDataList.add(musicData)

        }
        // Adapter 와 recyclerView 연결
        val musicRecyclerAdapter = MusicRecyclerAdapter(this, musicDataList)
        binding.recyclerView.adapter = musicRecyclerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}

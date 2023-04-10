package com.example.mp3playerpro

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.example.mp3playerpro.databinding.ActivityMainBinding
import com.example.mp3playerpro.databinding.ActivityPlayBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class PlayActivity : AppCompatActivity(), View.OnClickListener {
    val binding: ActivityPlayBinding by lazy { ActivityPlayBinding.inflate(layoutInflater) }
    val ALBUM_IMAGE_SIZE = 90
    var mediaPlayer: MediaPlayer? = null// 음악을 플레이 관련
    lateinit var musicData: MusicData
    var mp3PlayerJob: Job? = null
    var pauseFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // 전달해온 인텐트 값을 가져옴 -> 노래를 쓸수 있음
        musicData = intent.getSerializableExtra("musicData") as MusicData

        // 화면에 바인딩을 진행한다.
        binding.albumTitle.text = musicData.title
        binding.albumArtist.text = musicData.artists
        binding.totalDuration.text = SimpleDateFormat("mm:ss").format(musicData.duration)
        binding.playDuration.text = "00:00"
        val bitmap = musicData.getAlbumBitmap(this, ALBUM_IMAGE_SIZE)
        if (bitmap != null) {
            binding.albumImage.setImageBitmap(bitmap)
        } else {
            binding.albumImage.setImageResource(R.drawable.music_24)
        }
        // 음악 파일객체를 가져오는 것
        mediaPlayer = MediaPlayer.create(this, musicData.getMusicUri())
        // 이벤트 처리  (실행, 돌아가기,정지(=일시정지), 싱크바 조절)
        binding.listButton.setOnClickListener(this)
        binding.playButton.setOnClickListener(this)
        binding.stopButton.setOnClickListener(this)
        binding.seekBar.max = mediaPlayer!!.duration

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            // 눌렀을 때 싱크에 맞춰서 곡에 맞춰서 움직임
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer!!.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        }) //setOnSeekBarChangeListener of end

    }

    // 여기서 미디어 플레이 설정
    override fun onClick(v: View?) {
        when (v?.id) {
            // 노래가 나오는 중이라면 죽이고, 메인 액티비티만 남음. 하지만 노래를 정지해야함.
            R.id.listButton -> {
                mediaPlayer!!.stop()
                mp3PlayerJob?.cancel()
                mediaPlayer!!.release()
                mediaPlayer = null
                finish()
            }

            R.id.playButton -> {
                // 만약 작동중이라면 멈춤
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer?.pause()
                    binding.playButton.setImageResource(R.drawable.play_24)
                    pauseFlag = true
                } else {
                    mediaPlayer?.start()
                    binding.playButton.setImageResource(R.drawable.pause_24)
                    pauseFlag = false
                    // 코루틴으로 음악을 재생
                    val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
                    mp3PlayerJob = backgroundScope.launch {
                        while (mediaPlayer!!.isPlaying) {
                            var currentPosition = mediaPlayer?.currentPosition!!
                            // 코루틴 속에서 화면의 값을 변경할때 사용됨
                            runOnUiThread {
                                binding.seekBar.progress = currentPosition
                                binding.playDuration.text =
                                    SimpleDateFormat("mm:ss").format(mediaPlayer?.currentPosition)
                            }
                            try {
                                delay(1000)

                            } catch(e: java.lang.Exception) {
                                Log.e("PlayActivity", "delay 오류발생 ${e.printStackTrace()}")
                            }
                        } //while
                        // pause 상태? ->
                        if (pauseFlag == false) {
                            runOnUiThread {
                                binding.seekBar.progress = 0
                                binding.playDuration.text = "00:00"
                                binding.playDuration.text =
                                    SimpleDateFormat("mm:ss").format(mediaPlayer?.currentPosition)
                                binding.playButton.setImageResource(R.drawable.play_24)
                            }
                        }
                    }//
                }
            }

            R.id.stopButton -> {
                mediaPlayer!!.stop()
                mp3PlayerJob?.cancel()
                mediaPlayer = MediaPlayer.create(this, musicData.getMusicUri()) // 다시 음원이 재생됨
                binding.seekBar.progress = 0
                binding.playDuration.text = "00:00"
                binding.seekBar.max = mediaPlayer!!.duration // 싱크바를 다시 세팅
                binding.totalDuration.text =
                    SimpleDateFormat("mm:ss").format(musicData.duration) // 해당줄은 꼭 할 필요는 없음.
                binding.playButton.setImageResource(R.drawable.play_24)
                mediaPlayer = null
            }

        }
    }

    override fun onBackPressed() {
        mediaPlayer!!.stop()
        mp3PlayerJob?.cancel()
        mediaPlayer!!.release()
        mediaPlayer = null
        finish()
    }
}
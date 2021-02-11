package com.iiitd.ucsf.activities

import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.iiitd.ucsf.R
import com.iiitd.ucsf.models.Audio
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_audio_details.*


class AudioDetails : AppCompatActivity() {
    var mMediaPlayer: MediaPlayer? = null
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    private var pause:Boolean = false
    val mapKey = "map"
    private var lastduration:String="0"
    private var count_play:Int= 0
    private val sharedPrefFile = "kotlinsharedpreference"

    var audioname:String=""
    var audio_desc:String=""
    var init_count: Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_details)

     /*   val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(this)
        val name = sharedPreferences.getString("TestKey", "default value")*/

       // Toast.makeText(this,name,Toast.LENGTH_LONG).show()
        var audio = intent.getSerializableExtra(getString(R.string.audioKey)) as Audio
        ivAudioDetailsId.setImageResource(audio.image)
        tvIdAudioName.text = audio.name + "\n" + audio.description
          audioname=audio.name;
        audio_desc=audio.description

    // Seek bar change listener
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaPlayer.seekTo(i * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

    }
    // 1. Plays the water sound
    fun playSound(view: View) {
        if(pause){
            mediaPlayer.seekTo(mediaPlayer.currentPosition)
            mediaPlayer.start()
            pause = false




            Toast.makeText(this,"media playing",Toast.LENGTH_SHORT).show()
        }else{
            mediaPlayer = MediaPlayer()

            val path = Environment.getExternalStorageDirectory().getPath()+"/All_Audios/"+audioname+"$"+audio_desc+".mp3";

         Log.v("path",path)
            val audio_val: Uri = Uri.parse( path)

            mediaPlayer.setDataSource(applicationContext,audio_val);
/*
if(audioname.equals("water"))
         mediaPlayer = MediaPlayer.create(applicationContext,R.raw.water)


            else if (audioname.equals("sample"))
    mediaPlayer = MediaPlayer.create(applicationContext,R.raw.sample)

            else
    mediaPlayer = MediaPlayer.create(applicationContext,R.raw.kalimba)
*/
           mediaPlayer.prepare()
            mediaPlayer.start()
            Toast.makeText(this,"media playing",Toast.LENGTH_SHORT).show()

        }
        initializeSeekBar()
        playButton.isEnabled = false
        pauseButton.isEnabled = true
        stopBtn.isEnabled = true

        mediaPlayer.setOnCompletionListener {
            playButton.isEnabled = true
            pauseButton.isEnabled = false
            stopBtn.isEnabled = false
            Toast.makeText(this,"end",Toast.LENGTH_SHORT).show()
            count_play++;
            init_count++
        }
    }
    // 2. Pause playback
    fun pauseSound(view: View) {
        if(mediaPlayer.isPlaying){
            mediaPlayer.pause()
            pause = true
            playButton.isEnabled = true
            pauseButton.isEnabled = false
            stopBtn.isEnabled = true
            Toast.makeText(this,"media pause",Toast.LENGTH_SHORT).show()
        }
    }

    // 3. {optional} Stops playback
    fun stopSound(view: View) {
        if(mediaPlayer.isPlaying || pause.equals(true)){
            pause = false
            seek_bar.setProgress(0)
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
            handler.removeCallbacks(runnable)

            playButton.isEnabled = true
            pauseButton.isEnabled = false
            stopBtn.isEnabled = false
            lastduration=tv_pass.text.toString()
            if(!tv_due.text.toString().equals("0"))
            {   count_play++
                init_count++}
            tv_pass.text = ""
            tv_due.text = ""
            Toast.makeText(this,"media stop",Toast.LENGTH_SHORT).show()
        }
    }

    // 4. Closes the MediaPlayer when the app is closed
    override fun onStop() {
        super.onStop()


        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if(keyCode==KeyEvent.KEYCODE_BACK){

          val bzkMap = HashMap<String, String>()
         //   bzkMap.put("name", audioname)

            var outputMap: HashMap<String, String>
            outputMap =getHashMap( "mymap")

            var count_val=0

               if(outputMap.isEmpty()){
                   outputMap.put(audioname+"_duration",lastduration)
                   outputMap.put(audioname+"_count","0")

               }

            try{
   count_val = Integer.parseInt(outputMap.get(audioname + "_count")) + count_play;}
            catch (e:Exception){
                Log.v("expc",e.message)
                outputMap.put(audioname+"_duration",lastduration)
                outputMap.put(audioname+"_count",init_count.toString())

                count_val=init_count
            }


    outputMap.replace(audioname+"_duration",lastduration)
    outputMap.replace(audioname + "_count", count_val.toString())

Log.v("duration",tv_pass.text.toString()+"..........................."+lastduration)
            saveHashMap( "mymap",outputMap)

            Toast.makeText(applicationContext, "Saved Locally!", Toast.LENGTH_SHORT).show()


//            Log.v("data",outputMap.get(audioname+"_duration"));
        //    Log.v("data",outputMap.get(audioname+"_count"));
         /* //  Toast.makeText(applicationContext, outputMap.toString(), Toast.LENGTH_SHORT).show()
            Log.v("data",outputMap.get(audioname+"_duration"));
            Log.v("data",outputMap.get(audioname+"_count"));
*/

        }
        return super.onKeyDown(keyCode, event)
    }


    fun saveHashMap(key: String?, obj: HashMap<String, String>) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        val gson = Gson()
        val json: String = gson.toJson(obj)
        editor.putString(key, json)
        editor.apply() // This line is IMPORTANT !!!
    }

    fun getHashMap(key: String?): HashMap<String, String> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val gson = Gson()
        val json = prefs.getString(key, "")
        val type =
            object : TypeToken<HashMap<String?, String?>?>() {}.type
        return gson.fromJson(json, type)
    }
// Method to initialize seek bar and audio stats
private fun initializeSeekBar() {
    seek_bar.max = mediaPlayer.seconds

    runnable = Runnable {
        seek_bar.progress = mediaPlayer.currentSeconds

        tv_pass.text = "${mediaPlayer.currentSeconds}  "
        val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
        tv_due.text = "Remaining: "+"$diff sec"
        lastduration=tv_pass.text.toString()

        handler.postDelayed(runnable, 1000)
    }
    handler.postDelayed(runnable, 1000)
}
}
// Creating an extension property to get the media player time duration in seconds
val MediaPlayer.seconds:Int
    get() {
        return this.duration / 1000
    }
// Creating an extension property to get media player current position in seconds
val MediaPlayer.currentSeconds:Int
    get() {
        return this.currentPosition/1000
    }

package com.iiitd.ucsf.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iiitd.ucsf.BuildConfig
import com.iiitd.ucsf.R
import com.iiitd.ucsf.adapters.AudioAdapter
import com.iiitd.ucsf.adapters.AudioAdapter.OnItemClickListener
import com.iiitd.ucsf.application.ucsf
import com.iiitd.ucsf.manager.DownloadManager
import com.iiitd.ucsf.models.Audio
import com.iiitd.ucsf.models.Data
import com.iiitd.ucsf.threads.LongThread
import com.iiitd.ucsf.utilities.Utilities
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.*
import java.lang.ref.WeakReference
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), OnItemClickListener,Handler.Callback {
    private val sharedPrefFile = "kotlinsharedpreference"
    val MyPREFERENCES = "MyPrefs"
    private val URL = "https://api.npoint.io/f3923cefb4a31792918c"

    //    var  currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
    var is_downloaded: Int = 0
    lateinit var recyclerView: RecyclerView
    lateinit var  androidversion: TextView
    lateinit var  updatemedia: TextView

    val   mapKey ="map"
    var duration="0sec"
    var outputMap=HashMap<String, String>()

    var REQUEST_CODE_WRITE_STORAGE_PERMISION = 105
    val arrayList_audios = ArrayList<String>(3)
    val arrayList_audio_names = ArrayList<String>(3)

    val arrayList_audios_link = ArrayList<String>(3)
    val arrayList_audio_names_name = ArrayList<String>(3)
    val arrayList_audio_desc = ArrayList<String>(3)


    //    var context: Context = applicationContext
    var PACKAGE_NAME: String? = null
    lateinit var app_upate: Button
    lateinit var refresh: Button

    val PREF_VERSION_CODE_KEY = "version_code"
    val DOESNT_EXIST = -1
    var  savedVersionCode: Int = 0
    var curCount = 0
    var totalCount = 50f
     var file: File? = null

     val arrayList_files = ArrayList<String>()
    // Get current version code
    var currentVersionCode = BuildConfig.VERSION_CODE

     lateinit var progressDialog :ProgressDialog
    var internetOnFlag = false
    val myDB = FirebaseFirestore.getInstance()
    val audio_data = myDB.collection("audio_data")

    var adapter: AudioAdapter? = null
    lateinit var app: ucsf
    lateinit  var db: FirebaseFirestore
    var brand = Build.BRAND // for getting BrandName

    var model = Build.MODEL // for getting Model of the device
    var version = Build.VERSION.SDK_INT
    var versionRelease = Build.VERSION.RELEASE
    lateinit var  secureId  :String
    var dialog: ProgressDialog? = null

    override fun onItemClick(audio: Audio) {
        var intent = Intent(this, AudioDetails::class.java)
        intent.putExtra(getString(R.string.audioKey), audio)
         startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DownloadManager.setContext(this)
        //        }
        if (Utilities.isInternetOn(applicationContext)) {
            internetOnFlag = true
        }
        app = ucsf.getInstance()
        db = app.getFirebaseDatabaseInstance()
          secureId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)

           Log.v("data", model + "_" + version + "_" + versionRelease);
//Secure ID

     /*

       val dataObj = Data(secureId, "time", "count")


        Utilities.saveDataToList(dataObj, applicationContext)
        val all_data: java.util.ArrayList<Data> = Utilities.getListOfdata(applicationContext)


        updatedataToFireBase(all_data)

*/
        progressDialog = ProgressDialog(this@MainActivity)
        dialog = ProgressDialog(this@MainActivity)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST)


      /*  arrayList_audios_link.add("https://www.dropbox.com/s/lcqi3n6hreyokmy/Kalimba.mp3?dl=1")
        arrayList_audios_link.add("https://www.dropbox.com/s/p6aka8bp7jqrrom/sample.mp3?dl=1")
        arrayList_audios_link.add("https://www.dropbox.com/s/d7q0v2ueg3fr2k4/water.mp3?dl=1")

*/
//https://www.dropbox.com/s/wqtrgbx234ajdc6/Kalimba%24This%20is%20a%20short%20description%20of%20the%20audio%20file.This%20audio%20can%20be%20played%20within%20the%20app%20and%20we%20can%20record%20data%20of%20its%20usage.mp3?dl=0
//https://www.dropbox.com/s/7mmcf65pyfmf8ke/sample%24This%20is%20a%20short%20description%20of%20the%20audio%20file.This%20audio%20can%20be%20played%20within%20the%20app%20and%20we%20can%20record%20data%20of%20its%20usage.mp3?dl=0
// https://www.dropbox.com/s/l2ahoma96xztupb/water%24This%20is%20a%20short%20description%20of%20the%20audio%20file.This%20audio%20can%20be%20played%20within%20the%20app%20and%20we%20can%20record%20data%20of%20its%20usage.mp3?dl=0


     /*   arrayList_audios_link.add("https://www.dropbox.com/s/wqtrgbx234ajdc6/Kalimba%24This%20is%20a%20short%20description%20of%20the%20audio%20file.This%20audio%20can%20be%20played%20within%20the%20app%20and%20we%20can%20record%20data%20of%20its%20usage.mp3?dl=1")
        arrayList_audios_link.add("https://www.dropbox.com/s/7mmcf65pyfmf8ke/sample%24This%20is%20a%20short%20description%20of%20the%20audio%20file.This%20audio%20can%20be%20played%20within%20the%20app%20and%20we%20can%20record%20data%20of%20its%20usage.mp3?dl=1")
        arrayList_audios_link.add("https://www.dropbox.com/s/l2ahoma96xztupb/water%24This%20is%20a%20short%20description%20of%20the%20audio%20file.This%20audio%20can%20be%20played%20within%20the%20app%20and%20we%20can%20record%20data%20of%20its%20usage.mp3?dl=1")


        arrayList_audio_names_name.add("Kalimba$ A short description of the audio file.This audio can be played within the app and we can record data of its usage")
        arrayList_audio_names_name.add("Sample $ A short description of the audio file.This audio can be played within the app and we can record data of its usage")
        arrayList_audio_names_name.add("Water $ A short description of the audio file.This audio can be played within the app and we can record data of its usage")*/
        /*// Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return;

        }

        else*/

        if (currentVersionCode == savedVersionCode) {

            Log.v(
                "swati",
                "11111111111111111111" + currentVersionCode + "__________" + savedVersionCode
            )

            get_stored_audio_files()

        }
        else
            if (savedVersionCode == DOESNT_EXIST) {
                Log.v("swati", "2222222222222222222")

                checkStoragePermissions(this)

            // TODO This is a new install (or the user cleared the shared preferences)

        } else if (currentVersionCode > savedVersionCode) {
                Log.v("swati", "3333333333333")

                checkStoragePermissions(this)
                showForceUpdateDialog()
                // TODO This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();

        arrayList_audios.clear()
        arrayList_audio_names.clear()
        arrayList_audio_desc.clear()

        for (i in 0 until arrayList_files.size) {

            arrayList_audios.add(arrayList_files.get(i))

            var delimiter = "/"


            val parts = arrayList_files.get(i).split(delimiter)

            val size=parts.size-1


            val temp_name= parts[size].split("$").get(0)
            val temp_name_desc= parts[size].split("$").get(1).replace(".mp3", "")


            Log.v("swati", temp_name + "_______" + temp_name_desc)


            //  arrayList_audio_names.add(parts[size].split(".").get(0).toString())
            arrayList_audio_names.add(temp_name.toString())
            arrayList_audio_desc.add(temp_name_desc.toString())

        }

        Log.v(
            "final",
            arrayList_audios.toString() + "\\\\\\\\\\" + arrayList_audio_names.toString() + "\n" + arrayList_audio_desc.toString()
        )



        PACKAGE_NAME = getApplicationContext().getPackageName();
         //duration of file last played and count of number of times heard
    try {
        outputMap = getHashMap("mymap") as HashMap<String, String>
        Log.v("myyyyyyyyy", arrayList_audio_names.get(0))
        Log.v("data", outputMap.get(arrayList_audio_names.get(0) + "_count").toString())
        Log.v("data", outputMap.get(arrayList_audio_names.get(0) + "_duration").toString())
        val dataObj = Data(model + "_" + secureId, "" + (version), versionRelease, outputMap)


        Utilities.saveDataToList(dataObj, applicationContext)
        val all_data: java.util.ArrayList<Data> = Utilities.getListOfdata(applicationContext)


        updatedataToFireBase(all_data)

/*

Log.v("data",outputMap.get("water"+"_duration").toString()+"__"+outputMap.get("water"+"_count").toString())
        Log.v("data",outputMap.get("kalimba"+"_duration").toString()+"__"+outputMap.get("kalimba"+"_count").toString())
        Log.v("data",outputMap.get("sample"+"_duration").toString()+"__"+outputMap.get("sample"+"_count").toString())
*/


    }catch (e: Exception)
    {

        val bzkMap = HashMap<String, String>()
        for (i in 0 until arrayList_audio_names.size) {

            bzkMap.put(arrayList_audio_names.get(i) + "_duration", "0")
            bzkMap.put(arrayList_audio_names.get(i) + "_count", "0")
        }
/*

        bzkMap.put("kalimba_duration","0")
        bzkMap.put(".kalimba_count","0")

        bzkMap.put("sample_duration","0")
        bzkMap.put("sample_count","0")

*/

        saveHashMap("mymap", bzkMap)

    }
            //Log.v("data", outputMap1.get("water" + "_duration"));
            //Log.v("data", outputMap1.get("water" + "_count"));
        val audio: Array<Audio?> = arrayOfNulls(arrayList_audio_names.size)

        for (i in 0 until arrayList_audio_names.size){
            var val1= arrayList_audio_names.get(i)+"_duration"
            var val2=arrayList_audio_names.get(i)+"_count"

            var value1=outputMap.get(val1).toString()
            var value2=outputMap.get(val2).toString()

            if ( ( value1.equals("null"))) {
                 value1="0"
            }
            if  ( value2.equals("null")) {
                value2="0"
            }
            audio[i]= Audio(
                arrayList_audio_names.get(i),
                R.drawable.audio,
                arrayList_audio_desc.get(i),
                value1,
                value2
            )
          }
      //  var data : List<Audio> = List()
var data = audio.asList()

/*

        var data : List<Audio> = arrayListOf(
            Audio("water",R.drawable.audio,"water sound  ",outputMap.get("water"+"_duration").toString(),outputMap.get("water"+"_count").toString()),
            Audio("kalimba", R.drawable.audio,"Kalimba music file",outputMap.get("kalimba"+"_duration").toString(),outputMap.get("kalimba"+"_count").toString()),
            Audio("sample",R.drawable.audio,"Sample Audio File ",outputMap.get("sample"+"_duration").toString(),outputMap.get("sample"+"_count").toString()
            )
        */
/*    Audio("water",R.drawable.audio,"Desc "+duration), Audio("Audio1", R.drawable.audio,"Desc"),
            Audio("Audio1",R.drawable.audio,"Desc"), Audio("Audio1", R.drawable.audio,"Desc"),
            Audio("Audio1", R.drawable.audio,"Desc"),
            Audio("Audio1", R.drawable.audio,"Desc"), Audio("Audio1", R.drawable.audio,"Desc"),
            Audio("Audio1", R.drawable.audio,"Desc"), Audio("Audio1", R.drawable.audio,"Desc"),
            Audio("Audio1", R.drawable.audio,"Desc"), Audio("Audio1",R.drawable.audio,"Desc"),
            Audio("Audio1", R.drawable.audio,"Desc"), Audio("Audio1",R.drawable.audio,"Desc"),
            Audio("Audio1",R.drawable.audio,"Desc"), Audio("Audio1",R.drawable.audio,"Desc"),
            Audio("Audio1",R.drawable.audio,"Desc")*//*

        )
*/
        androidversion=findViewById(R.id.version)
        androidversion.text=" Running on Android "+ versionRelease


        updatemedia=findViewById(R.id.update_data)

        updatemedia.setOnClickListener {

           // download_files();
            download_data_from_service()
        }

        recyclerView = findViewById(R.id.rvAudiosId)

        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        adapter  = AudioAdapter(this, data as List<Audio>, this)
        recyclerView.adapter = adapter

        app_upate=findViewById(R.id.appversion)
        app_upate.setOnClickListener{
           /* val task = GetVersionCode(this)
            task.execute()

            GetVersionCode(this)
            Toast.makeText(this, R.string.disabled, Toast.LENGTH_LONG).show()*/
           // FetchDataTask().execute(URL)

      download_data_from_service()

         }

        refresh=findViewById(R.id.refresh)
        refresh.setOnClickListener(View.OnClickListener {
            finish();
            startActivity(getIntent());
        })
    }

    fun getHashMap(key: String?): Map<String, String> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val gson = Gson()
        val json = prefs.getString(key, "")
        val type =
            object : TypeToken<HashMap<String?, String?>?>() {}.type
        return gson.fromJson(json, type)
    }
    fun saveHashMap(key: String?, obj: HashMap<String, String>) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        val gson = Gson()
        val json: String = gson.toJson(obj)
        editor.putString(key, json)
        editor.apply() // This line is IMPORTANT !!!

        Log.v("saved", "saved")




    }

//companion object{
    private class GetVersionCode(private var mainActivity: MainActivity?) :
        AsyncTask<Void?, String?, String?>() {

        private val activityReference: WeakReference<MainActivity?> = WeakReference(mainActivity)

        override fun onPostExecute(onlineVersion: String?) {
            super.onPostExecute(onlineVersion)


            val versionCode = BuildConfig.VERSION_CODE.toString()
            val versionName = BuildConfig.VERSION_NAME
            Log.d(
                "update",
                "Current version " + versionName + "playstore version " + onlineVersion
            )
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (java.lang.Float.valueOf(versionName) < java.lang.Float.valueOf(onlineVersion)) {
                    //show dialog
                    val builder = AlertDialog.Builder(mainActivity?.applicationContext)
                    builder.setTitle("App Update Alert")
                    builder.setMessage(
                        mainActivity?.getString(R.string.youAreNotUpdatedMessage)
                            .toString() + " " + mainActivity?.currentVersionCode + mainActivity?.getString(
                            R.string.youAreNotUpdatedMessage1
                        )
                    )
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        mainActivity?.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + mainActivity?.getPackageName())
                            )
                        )
                    }

                    builder.setNegativeButton(android.R.string.no) { dialog, which ->
                        Toast.makeText(
                            mainActivity?.applicationContext,
                            android.R.string.no, Toast.LENGTH_SHORT
                        ).show()
                    }


                    builder.show()
                }
            }
     //   }


    }

    override fun doInBackground(vararg p0: Void?): String? {

        Log.d("app", activityReference.get()?.PACKAGE_NAME.toString())
        var newVersion: String? = null
        return try {
            newVersion = Jsoup.connect(
                "https://play.google.com/store/apps/details?id=" + activityReference.get()?.PACKAGE_NAME.toString()
                    .toString() + "&hl=it"
            )
                .timeout(30000)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com")
                .get()
                .select(".hAyfc .htlgb")
                .get(7)
                .ownText()
            newVersion
        } catch (e: Exception) {
            newVersion
        }
    }
}

    private fun checkStoragePermissions(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_CODE_WRITE_STORAGE_PERMISION
                    )
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_CODE_WRITE_STORAGE_PERMISION
                    )
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )) {
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            REQUEST_CODE_WRITE_STORAGE_PERMISION
                        )
                    }
                }
            } else {
         // download_files()
                download_data_from_service()
            }
        }
    }

    override fun onResume() {
        super.onResume()


        adapter?.notifyDataSetChanged()
        recyclerView.adapter = adapter
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_WRITE_STORAGE_PERMISION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val folder = File(
                        Environment.getExternalStorageDirectory().toString() + "/" + "All_Audios"
                    )
                    if (!folder.exists()) {
                        folder.mkdirs()
                    }/*
                    val fileName = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Date()) + ".mp3"
                    val urlOfTheFile = "https://sample-videos.com/audio/mp3/wave.mp3"
                    DownloadManager.initDownload(this, urlOfTheFile, folder.absolutePath, fileName)*/


                    //   DownloadManager.initDownload(this,  arrayList_audios.get(0), folder.absolutePath, arrayList_audio_names.get(0))

                    // download_files()
                    download_data_from_service()
                }
            }

        }
    }

    override fun handleMessage(p0: Message?): Boolean {
           curCount++
              val per: Float = curCount / totalCount * 100
             // progressBar.setProgress(per.toInt())
           /*   if (per < 100){
                  progressDialog.show()
              }
              else {
                  if( progressDialog.isShowing)
                      progressDialog.cancel()
                  //All images are downloaded
                  }*/
    /*    {}   if(progressDialog.isShowing)
        progressDialog.cancel()}*/

        progressDialog.isShowing
        progressDialog.cancel()
        finish();
        startActivity(getIntent());
      Log.i("p0", "in handle" + p0)

    return true
    }

      fun download_files(){

       //   val task_audio =AsyncTaskAudio(this)
        //  task_audio.execute()

          Log.v("downlaoding", "in downlaod")
        val folder = File(Environment.getExternalStorageDirectory().toString() + "/" + "All_Audios")
        if (!folder.exists()) {
            folder.mkdirs()
        }

        val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
          Log.v("size", arrayList_audios_link.size.toString())
if(arrayList_audios_link.size>0){
        val executor = ThreadPoolExecutor(
            NUMBER_OF_CORES * arrayList_audios_link.size,
            NUMBER_OF_CORES * arrayList_audios_link.size,
            120L,
            TimeUnit.SECONDS,
            LinkedBlockingQueue<Runnable>()
        )

        progressDialog.setTitle("Downloading Audios")
        progressDialog.setMessage("Application is downloading, please wait")
        for (i in 0 until arrayList_audios_link.size) {

            if(!progressDialog.isShowing)
                progressDialog.show()


            executor.execute(
                LongThread(
                    i, arrayList_audios_link.get(i), Handler(this), arrayList_audio_names_name.get(
                        i
                    ) + ".mp3"
                )
            )
           // DownloadManager.initDownload( arrayList_audios.get(i), folder.absolutePath, arrayList_audio_names.get(i)+".mp3")

        }
    }}

    fun get_stored_audio_files(){

        val path =
            Environment.getExternalStorageDirectory().toString() + "/"+"All_Audios"
        val f = File(path)
        try{
        getListFiles(f)}
        catch (e: Exception){

        }
        Log.v("files", arrayList_files.toString())

        if(progressDialog.isShowing)
        {        progressDialog.cancel()

           finish();
           startActivity(getIntent());}
        }

    fun getListFiles(parentDir: File): List<File>? {
        val inFiles = ArrayList<File>()
        val files = parentDir.listFiles()
        for (file in files) {
            if (file.isDirectory) {
                inFiles.addAll(getListFiles(file)!!)
            } else {
                inFiles.add(file)

            }
        }

        for (i in 0 until inFiles.size) {
            arrayList_files.add(inFiles.get(i).toString())
        }

            return inFiles
    }
    fun showForceUpdateDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("App Update Alert")
        builder.setMessage(
            this.getString(R.string.youAreNotUpdatedMessage)
                .toString() + " " + currentVersionCode + this.getString(R.string.youAreNotUpdatedMessage1)
        )
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + applicationContext.getPackageName())
                )
            )
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(
                applicationContext,
                android.R.string.no, Toast.LENGTH_SHORT
            ).show()
        }


        builder.show()
    }

   fun updatedataToFireBase(meds: java.util.ArrayList<Data>) {
        if (internetOnFlag) {
            Log.d("firebase", "Internet present")
            for (m in meds) {
                val documentReference: DocumentReference = db.collection(
                    "data"
                ).document(m.id.toString())
                documentReference.set(m).addOnFailureListener {
                    Log.d(
                        "firebase",
                        "failure in adding " + m.id
                    )
                }.addOnSuccessListener {
                    Log.d(
                        "firebase",
                        "sucessss in adding main activity" + m.id
                    )
                }
            }
        }
    }/*

    private class AsyncTaskAudio(private var mainActivity: MainActivity?) :
        AsyncTask<String?, String?, Boolean>() {
        var p = ProgressDialog(mainActivity)

        override fun onPreExecute() {

            super.onPreExecute()
                 }

     *//*   private fun update_links_from_network() {
            TODO("Not yet implemented")
            val dialog
            dialog!!.setMessage("Loading....");
            dialog!!.show();

        }*//*


        override fun onPostExecute(bool: Boolean) {
            super.onPostExecute(bool)
            Log.v("downlaoding", "in downlaod")
            val folder = File(Environment.getExternalStorageDirectory().toString() + "/" + "All_Audios")
            if (!folder.exists()) {
                folder.mkdirs()
            }

            val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()

            val executor = ThreadPoolExecutor(
                NUMBER_OF_CORES * 3,
                NUMBER_OF_CORES * 3,
                120L,
                TimeUnit.SECONDS,
                LinkedBlockingQueue<Runnable>()
            )

            p.setTitle("Downloading Audios")
            p.setMessage("Application is downloading, please wait")
            for (i in 0 until 3) {

                if(!p.isShowing)
                    p.show()


                executor.execute(
                    LongThread(
                        i, mainActivity?.arrayList_audios_link?.get(i), Handler(mainActivity), mainActivity?.arrayList_audio_names_name?.get(
                            i
                        ) + ".mp3"
                    )
                )
                // DownloadManager.initDownload( arrayList_audios.get(i), folder.absolutePath, arrayList_audio_names.get(i)+".mp3")

            }
        }

        override fun doInBackground(vararg p0: String?): Boolean {
            Looper.prepare()
            val URL = "https://api.npoint.io/f3923cefb4a31792918c"

            p.setMessage("Please wait...It is downloading")
            p.setIndeterminate(false)
            p.setCancelable(false)
            p.show()

            return true
        }
    }*/


/*
    private class FetchDataTask :
        AsyncTask<String?, Void?, String?>() {

        override fun onPostExecute(dataFetched: String?) {
            //parse the JSON data and then display
            parseJSON(dataFetched)
        }

        @Throws(IOException::class)
        private fun convertInputStreamToString(inputStream: InputStream): String? {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = ""
            var result: String? = ""
            while (bufferedReader.readLine().also({ line = it }) != null) result += line
            inputStream.close()
            return result
        }

        private fun parseJSON(data: String?) {
            try {
                val jsonMainNode = JSONArray(data)
                val jsonArrLength = jsonMainNode.length()
                for (i in 0 until jsonArrLength) {
                    val jsonChildNode = jsonMainNode.getJSONObject(i)
                    val postTitle = jsonChildNode.getString("post_title")
                    //tutorialList.add(postTitle)
                }



            } catch (e: java.lang.Exception) {
                Log.i("App", "Error parsing data" + e.message)
            }
        }

        override fun doInBackground(vararg params: String?): String? {
             var inputStream: InputStream? = null
            var result: String? = null
            val client: DefaultHttpClient = DefaultHttpClient()
            val httpGet = HttpGet(params[0])
            try {
                val response: HttpResponse = client.execute()
                inputStream = response.getEntity().getContent()

                // convert inputstream to string
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream)
                    Log.i("App", "Data received:$result")
                } else result = "Failed to fetch data"
                return result
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }*/
    fun  download_data_from_service(){
    val request = StringRequest(URL, object : Response.Listener<String?> {
        override fun onResponse(string: String?) {


            parseJsonData(string)
            Thread.sleep(2000)
            download_files()
        }


        fun parseJsonData(jsonString: String?) {
            try {
                val `object` = JSONObject(jsonString)
                val audioArray = `object`.getJSONArray("audios")
                val al = ArrayList<Any>()




                for (i in 0 until audioArray.length()) {
                    val jsonChildNode: JSONObject = audioArray.getJSONObject(i)
                    val audio_name = jsonChildNode.getString("name")
                    val audio_url = jsonChildNode.getString("url")
                    val audio_desc = jsonChildNode.getString("description")
                    Log.v("audios", audio_name + "\n" + audio_url + "\n" + audio_desc)
                    Log.v("audios", audio_name + "\n" + audio_url + "\n" + audio_desc)

                    /*if(arrayList_audios_link.size<=audioArray.length()) {
                        for (j in 0 until arrayList_audios_link.size) {
                            if (!audio_url.toString().contains(arrayList_audios_link.get(i))) {
                                arrayList_audios_link.add(audio_url)
                                arrayList_audio_names_name.add(audio_name + "$" + audio_desc)
                            } else {
                                Log.i("val", audio_url.toString())
                                Log.i("val", arrayList_audio_names_name.get(i).toString())
                                // arrayList_audios_link.remove(audio_url)
                                //   arrayList_audio_names_name.remove(audio_name+"$"+audio_desc)

                            }
                        }
                    }
                    else*/

                        arrayList_audios_link.add(audio_url)
                        arrayList_audio_names_name.add(audio_name + "$" + audio_desc)
                    Log.v("audios", arrayList_audios_link.toString())

                    /*  for (current_val  in arrayList_audios_link) {
                          if (current_val.contains(audio_url)) {
                              arrayList_audios_link.add(audio_url)
                              arrayList_audio_names_name.add(audio_name + "$" + audio_desc)
                          }

                       else {
                          arrayList_audios_link.remove(audio_url)
                          arrayList_audio_names_name.remove(audio_name + "$" + audio_desc)
                      }}*/
                }
                Log.v("size", arrayList_audios_link.size.toString()+"____"+arrayList_audios_link.toString())

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            // dialog.dismiss()
        }


    }, object : Response.ErrorListener {
        override fun onErrorResponse(volleyError: VolleyError?) {
            /*  Toast.makeText(this, "Some error occurred!!", Toast.LENGTH_SHORT)
                  .show()*/

        }
    })

    val rQueue: RequestQueue = Volley.newRequestQueue(this)
    rQueue.add(request)
    }
    }


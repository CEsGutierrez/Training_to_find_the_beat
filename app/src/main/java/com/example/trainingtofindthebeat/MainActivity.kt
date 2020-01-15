package com.example.trainingtofindthebeat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //sets up connect_button listener which will effectively launch the entire app
        connect_button.setOnClickListener {
            showMainButtons()
            hideConnectButton()
        }

        //sets up all the other buttons' listeners which will determine the functional parts of the app
        setupListeners()
    }


    // function that actually launches the player activity
    fun launchPlayer () {
        SServicePlayer.connect(this) {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }
    }

    fun showMainButtons() {
        training_selection.visibility = View.VISIBLE
        salsa_train.visibility = View.VISIBLE
        bachata_train.visibility = View.VISIBLE
        tango_train.visibility = View.VISIBLE

        quiz_selection.visibility = View.VISIBLE
        salsa_quiz.visibility = View.VISIBLE
        bachata_quiz.visibility = View.VISIBLE
        tango_quiz.visibility = View.VISIBLE

        progress_button.visibility = View.VISIBLE
    }

    fun hideConnectButton() {
        connect_button.visibility = View.INVISIBLE
    }

    fun getSalsaScores() {
        //gts the salsa scores from sharedpreferences
        //assigns them to salsaScores
    }
    fun getBachataScores() {
        //gts the bachata scores from sharedpreferences
        //assigns them to bachataScores
    }
    fun getTangoMilScores() {
        //gts the tango and milonga scores from sharedpreferences
        //assigns them to tangoMilongaScores
    }

    val salsaScores = ArrayList<Any>()
    val bachataScores = ArrayList<Any>()
    val tangoMilongaScores = ArrayList<Any>()

    // populates an array of the salsa songs already trained on

    fun getSalsaSongs() {
        getSalsaScores()
        if (salsaScores.size == 0)
            salsaSongs.add("")
        else if (salsaScores.size == 1) {
            var placeholder = ArrayList<Any>(salsaScores)
            val onlySong = placeholder.get(0).toString()
            salsaSongs.add(onlySong)
        }
        else {
            var limit :Int = 3
            salsaScores.forEachIndexed { index, element ->
                if(index % limit == 0)
                    salsaSongs.add(element.toString())
            }
        }
    }

    fun getBachataSongs() {}
    fun getTangoMilongaSongs() {}


    var salsaSongs = ArrayList<String>()
    val bachataSongs = ArrayList<Any>()
    val tangoMilongaSongs = ArrayList<Any>()



    fun bufferAPI(){
        var finished = false;
        runBlocking {
//            val response = SManualAPI.getPlaylistId()
//            val response = SManualAPI.getTrackList()
//            val actualText = response.getPlaylistId()
            val response = SManualAPI.getTrackAA()
            progress_button.setText(response)
            finished = true
        }
        while (!finished) {
            Thread.sleep(500);
        }

    }



    private fun setupListeners() {

        salsa_train.setOnClickListener {
            bufferAPI()
//            getSalsaScores()
////            progress_button.setText("There's nothing here")
//            // salsaScores is now populated
//            getSalsaSongs()
//            //salsaSongs is now populated
////            getSalsaTrack()

        }



        bachata_train.setOnClickListener {}
        tango_train.setOnClickListener {}

        salsa_quiz.setOnClickListener {}
        bachata_quiz.setOnClickListener {}
        tango_quiz.setOnClickListener {}

        progress_button.setOnClickListener {}
    }

}

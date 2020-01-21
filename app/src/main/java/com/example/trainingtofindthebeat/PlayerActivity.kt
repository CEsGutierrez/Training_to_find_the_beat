package com.example.trainingtofindthebeat

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.trainingtofindthebeat.SManualAPI.CURRENT_BACHATA_TRACK
import com.example.trainingtofindthebeat.SManualAPI.CURRENT_SALSA_TRACK
import com.example.trainingtofindthebeat.SManualAPI.TEMPO
import kotlinx.android.synthetic.main.activity_player.*


class PlayerActivity : AppCompatActivity() {

    var TIME_STAMP_LIST= arrayListOf<Int>() // slew of variables needed across the program
    var START_TIME = 0
    var END_TIME:Long = 0
    var BETTER_START_TIME:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        Toast.makeText(this, "The blue note will help you and the song get settled.", Toast.LENGTH_LONG).show()
        Toast.makeText(this, "Tap the grey button to the beat.", Toast.LENGTH_LONG).show()
        Toast.makeText(this, "When you stop, we'll tell you how you did!", Toast.LENGTH_LONG).show()
        setupListeners()
        trackAssigner()
    }

    fun trackAssigner() { // depending on the genre that has been chosen, this ensures that the
        // right track is assigned, additionally that it's audio features are gotten and the beat
        // for the song assigned
        if (SManualAPI.GENRE == "SALSA") {
            // Changes the view appearance to red background, trombone, and text "Salsa"
            instrument_1.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.trombone))
            activity_announcer.setBackgroundColor(Color.parseColor("#CB0D0F"))
            activity_ann.setText("SALSA")

            CURRENT_SALSA_TRACK = SManualAPI.SALSA_TRACKS[0]
            SManualAPI.getTrackTempo(CURRENT_SALSA_TRACK)
        }
        else if (SManualAPI.GENRE == "BACHATA") {
            // Changes the view appearance to yellow background, congas, and text "Bachata"
            instrument_1.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.congas))
            activity_announcer.setBackgroundColor(Color.parseColor("#F8D541"))
            activity_ann.setText("BACHATA")

            CURRENT_BACHATA_TRACK = SManualAPI.BACHATA_TRACKS[0]
            SManualAPI.getTrackTempo(CURRENT_BACHATA_TRACK)
        }
    }


    fun animationScaler() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 2f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 2f)

        val animator = ObjectAnimator.ofPropertyValuesHolder(animation, scaleX, scaleY)

        animator.repeatCount = 80
        val durationValue = TEMPO / 2 // accounts for expanding and contracting action in the
        // annimation
        animator.setDuration(durationValue)
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.start()
    }

    fun genreExtender(): String {
        if(SManualAPI.GENRE == "SALSA") {
            return "spotify:track:${CURRENT_SALSA_TRACK}"
        }
        else {
            return "spotify:track:${CURRENT_BACHATA_TRACK}"
        }
    }


    fun setupListeners() {
        start_button.setOnClickListener {
            recordSTART_TIME()
            recordBETTER_START_TIME()
            SServicePlayer.play(genreExtender())
            animationScaler()
        }

        stop_button.setOnClickListener {
            recordEND_TIME()
            val score = calculateScore()

            displayStars(score)
            SServicePlayer.disconnect()
        }

        tap.setOnClickListener {
            recordTime()
        }
    }

    fun displayStars(score: Double) {
        score_area.visibility = View.VISIBLE
        if (score < 20) {
            star_1.visibility = View.VISIBLE
            star_2.visibility = View.VISIBLE
            star_3.visibility = View.VISIBLE
            star_4.visibility = View.VISIBLE
            star_5.visibility = View.VISIBLE
        }
        else if (score < 50) {
            star_1.visibility = View.VISIBLE
            star_2.visibility = View.VISIBLE
            star_3.visibility = View.VISIBLE
            star_4.visibility = View.VISIBLE
        }
        else if (score < 100) {
            star_1.visibility = View.VISIBLE
            star_2.visibility = View.VISIBLE
            star_3.visibility = View.VISIBLE
        }
        else if (score < 200) {
            star_1.visibility = View.VISIBLE
            star_2.visibility = View.VISIBLE
        }
        else {
            star_1.visibility = View.VISIBLE
        }
    }

    fun recordTime() {
        var time = System.currentTimeMillis()
        var now = time.toLong().toInt()
        addTimeToArray(now)
        }

    fun addTimeToArray(time: Int ) {
        var length = TIME_STAMP_LIST.size
        if (length == 0)
            TIME_STAMP_LIST.add(0, time)
        else
            TIME_STAMP_LIST.add(length, time)
    }

    fun recordSTART_TIME() {
        val time = System.currentTimeMillis()
        START_TIME = time.toInt()
    }

    fun recordEND_TIME() {
        END_TIME = System.currentTimeMillis()
    }

    fun recordBETTER_START_TIME() {
        BETTER_START_TIME = System.currentTimeMillis()
    }

    fun calculateScore():Double {
        // gets the AA for the track in the appropriate genre, then converts its values to integers
        // in MS
        var AA:ArrayList<String>

        if (SManualAPI.GENRE == "SALSA") {
            AA = SManualAPI.getTrackAA(CURRENT_SALSA_TRACK)
        }
        else {
            AA = SManualAPI.getTrackAA(CURRENT_BACHATA_TRACK)
        }


        val AATimesMS = QuizActivity.aaTimecCnverter(AA, stopTime = END_TIME,
            startTime = BETTER_START_TIME)

//         calculates the tap times in integers in MS
        val TapTimesMS = QuizActivity.tapTimeConverter(TIME_STAMP_LIST, START_TIME)

        // removes introductory part of the song for both AA and Tap data(30 seconds)
        var functionalAA = QuizActivity.removeIntro(AATimesMS)
        var functionalTap = QuizActivity.removeIntro(TapTimesMS)

        // clusters the times into arrays that hold 10 seconds worth of information
        val clusteredAA = QuizActivity.groupTimes(functionalAA)
        val clusteredTap = QuizActivity.groupTimes(functionalTap)

        // calculates the average differences in times of clustered times
        val averagedifferenceAA = QuizActivity.averageDiffInTimes(clusteredAA)
        val averageddifferenceTap = QuizActivity.averageDiffInTimes(clusteredTap)

        // compares the averages in times to compose a final score
        val finalScore = QuizActivity.compareAverages(averagedifferenceAA, averageddifferenceTap)

        return finalScore
    }
}



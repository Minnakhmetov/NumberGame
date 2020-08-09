package com.example.numbersgame

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.findNavController
import com.example.numbersgame.storage.ThemeKeeper
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val themeKeeper by lazy {
        ThemeKeeper.getInstance(this)
    }

    override fun getTheme(): Resources.Theme {
        return super.getTheme().apply {
            applyStyle(themeKeeper.themeId, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val backButton = findViewById<ImageView>(R.id.back_button)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainMenuFragment -> {
                    if (backButton.visibility == View.VISIBLE) {
                        backButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left))
                        backButton.visibility = View.INVISIBLE
                    }
                }
                else -> {
                    if (backButton.visibility == View.INVISIBLE) {
                        backButton.visibility = View.VISIBLE
                        backButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.silde_in_left))
                    }
                }
            }
        }

        val themeKeeperListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            recreate()
        }

        themeKeeper.subscribe(this)

        backButton.setOnClickListener {
            navController.navigateUp()
        }
    }

    override fun onStart() {
        super.onStart()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}
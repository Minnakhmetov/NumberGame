package com.blueberrybeegames.numbersgame

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.navigation.findNavController
import com.blueberrybeegames.numbersgame.theme.ThemeKeeper

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
                R.id.chapterChoiceFragment -> {
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
}
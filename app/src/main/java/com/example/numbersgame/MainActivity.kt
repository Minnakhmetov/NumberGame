package com.example.numbersgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {

    var hideBackButtonInGameFragment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val backButton = findViewById<ImageView>(R.id.back_button)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            backButton.visibility = when (destination.id) {
                R.id.mainMenuFragment -> View.INVISIBLE
                R.id.resultsFragment -> View.INVISIBLE
                R.id.gameFragment -> {
                    if (hideBackButtonInGameFragment)
                        View.INVISIBLE
                    else
                        View.VISIBLE
                }
                else -> View.VISIBLE
            }
        }

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
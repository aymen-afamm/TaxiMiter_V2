package com.example.moltaxi

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.moltaxi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimations()
        setupClickListeners()
    }

    private fun setupAnimations() {
        // Animate background
        val animDrawable = binding.layoutMain.background as? AnimationDrawable
        animDrawable?.apply {
            setEnterFadeDuration(1000)
            setExitFadeDuration(2000)
            start()
        }

        // Fade in for logo
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.taxiLogo.startAnimation(fadeIn)

        // Slide up for button
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        binding.btnInscrire.startAnimation(slideUp)

        // App title animation
        binding.tvAppTitle.alpha = 0f
        binding.tvAppTitle.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(300)
            .start()
    }

    private fun setupClickListeners() {
        binding.btnInscrire.setOnClickListener {
            animateButton()
            startActivity(Intent(this, MeterActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            // TODO: Open settings
        }
    }

    private fun animateButton() {
        binding.btnInscrire.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                binding.btnInscrire.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }
}

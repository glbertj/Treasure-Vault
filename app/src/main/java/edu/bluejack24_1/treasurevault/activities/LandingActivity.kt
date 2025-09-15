package edu.bluejack24_1.treasurevault.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import edu.bluejack24_1.treasurevault.databinding.ActivityLandingBinding
import edu.bluejack24_1.treasurevault.models.Setting
import edu.bluejack24_1.treasurevault.models.User
import edu.bluejack24_1.treasurevault.utilities.CacheUtility
import edu.bluejack24_1.treasurevault.ai.CategoryClassifier

class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        Setting.init(this)
        User.init(this)
        CategoryClassifier.init(this)

        CacheUtility.clearExpiredCache()

        startAnimationAndProceed(MainActivity::class.java)
    }

    private fun startAnimationAndProceed(nextActivity: Class<*>) {
        val titleText = binding.titleText
        val sloganText = binding.sloganText

        val blinkAnimation = AlphaAnimation(0.0f, 1.0f).apply {
            duration = 1000
            startOffset = 50
            repeatMode = AlphaAnimation.REVERSE
            repeatCount = 1
        }

        blinkAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                val intent = Intent(this@LandingActivity, nextActivity)
                startActivity(intent)
                finish()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        titleText.startAnimation(blinkAnimation)
        sloganText.startAnimation(blinkAnimation)

        ViewCompat.setOnApplyWindowInsetsListener(binding.landingLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
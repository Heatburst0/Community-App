package com.kv.ablecommunity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.kv.ablecommunity.databinding.ActivityIntroBinding


class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signup.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding.signin.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
        }
    }

}
package com.kv.ablecommunity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_intro.*


class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        signup.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        signin.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
        }
    }

}
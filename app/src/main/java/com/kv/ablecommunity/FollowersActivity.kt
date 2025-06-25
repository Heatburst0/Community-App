package com.kv.ablecommunity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kv.ablecommunity.databinding.ActivityFollowersBinding
import com.kv.ablecommunity.models.User
import com.kv.ablecommunity.utils.ViewPagerAdapter

class FollowersActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFollowersBinding
    lateinit var currentUser : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFollowersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupActionBar()
        if(intent.hasExtra("user")){
            currentUser = intent.getParcelableExtra("user")!!
        }
        binding.followTab.setTabGravity(TabLayout.GRAVITY_FILL)

        val adapter = ViewPagerAdapter(this)
        binding.vPager.adapter = adapter
        val tabTitles = listOf("Followers", "Following")
        TabLayoutMediator(binding.followTab, binding.vPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()


    }
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarFollowersActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = "My Followers"
        }

        binding.toolbarFollowersActivity.setNavigationOnClickListener { onBackPressed() }
    }
}
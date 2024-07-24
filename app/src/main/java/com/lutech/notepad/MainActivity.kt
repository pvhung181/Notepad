package com.lutech.notepad

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.lutech.notepad.databinding.ActivityMainBinding
import com.lutech.notepad.ui.add.AddActivity

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navController: NavController
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
        setSupportActionBar(toolbar)
        setMenuItemsClick()
        setupDrawer()

        navView.setupWithNavController(navController)
    }

    private fun init() {
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        toolbar = binding.appBarMain.toolbar
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        val newDrawerArrowDrawable = DrawerArrowDrawable(this)
        newDrawerArrowDrawable.color = ContextCompat.getColor(this, R.color.white)
        toggle.drawerArrowDrawable = newDrawerArrowDrawable

        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
    }

    private fun setListeners() {
        binding.appBarMain.fab.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
    }

    private fun setMenuItemsClick(): Unit {
        navView.menu.findItem(R.id.nav_setting).setOnMenuItemClickListener {
            startActivity(Intent(this, AddActivity::class.java))
            true
        }

        navView.menu.findItem(R.id.nav_home).setOnMenuItemClickListener {
            navController.navigate(R.id.nav_home)
            drawerLayout.close()

            true
        }

        navView.menu.findItem(R.id.nav_backup).setOnMenuItemClickListener {
            navController.navigate(R.id.nav_backup)
            drawerLayout.close()

            true
        }
        navView.menu.findItem(R.id.nav_slideshow).setOnMenuItemClickListener {
            navController.navigate(R.id.nav_slideshow)
            drawerLayout.close()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
package com.lutech.notepad

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.lutech.notepad.databinding.ActivityMainBinding
import com.lutech.notepad.ui.add.AddActivity
import com.lutech.notepad.ui.backup.BackupActivity
import com.lutech.notepad.ui.help.HelpActivity
import com.lutech.notepad.ui.privacy_policy.PrivacyPolicyActivity
import com.lutech.notepad.ui.setting.SettingActivity

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
        setDrawerItemClick()
        setupDrawer()
        setSearchToolbarListener()
        //registerForContextMenu(binding.appBarMain.moreBtn)
        navView.setupWithNavController(navController)
    }

    private fun setSearchToolbarListener() {
        binding.appBarMain.backBtn.setOnClickListener {
            binding.appBarMain.toolbar.visibility = View.VISIBLE
            binding.appBarMain.searchToolbar.visibility = View.GONE
        }

//        binding.appBarMain.moreBtn.setOnClickListener {
//            Toast.makeText(this, "active", Toast.LENGTH_SHORT).show()
//        }

    }

    private fun init() {
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        toolbar = binding.appBarMain.toolbar
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
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

    private fun setDrawerItemClick() {
        navView.menu.findItem(R.id.nav_setting).setOnMenuItemClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
            drawerLayout.close()
            true
        }

        navView.menu.findItem(R.id.nav_home).setOnMenuItemClickListener {
            navController.navigate(R.id.nav_home)
            drawerLayout.close()
            true
        }

        navView.menu.findItem(R.id.nav_backup).setOnMenuItemClickListener {
            startActivity(Intent(this, BackupActivity::class.java))
            drawerLayout.close()
            true
        }

        navView.menu.findItem(R.id.nav_trash).setOnMenuItemClickListener {
            navController.navigate(R.id.nav_trash)
            drawerLayout.close()
            true
        }

        navView.menu.findItem(R.id.nav_categories).setOnMenuItemClickListener {
            navController.navigate(R.id.nav_categories)
            drawerLayout.close()
            true
        }

        navView.menu.findItem(R.id.nav_help).setOnMenuItemClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
            drawerLayout.close()
            true
        }

        navView.menu.findItem(R.id.nav_privacy_policy).setOnMenuItemClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
            drawerLayout.close()
            true
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_sort) {
            showSortDialog()
        } else if (item.itemId == R.id.action_search) {
            binding.appBarMain.toolbar.visibility = View.GONE
            binding.appBarMain.searchToolbar.visibility = View.VISIBLE
            binding.appBarMain.searchField.requestFocus()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSortDialog() {
        val builder = AlertDialog.Builder(this)
        var checkedItem = 0
        builder.setTitle("Sort")
        val listItems = arrayOf(
            "edit date: from newest",
            "edit date: from oldest",
            "title: A to Z",
            "title: Z to A",
            )

        val dialog = builder.setSingleChoiceItems(listItems, checkedItem) { dlg, which ->
            checkedItem = which
        }.setNegativeButton("Cancel") { dlg, _ -> dlg.dismiss() }
            .setPositiveButton("Sort") { dlg, _ -> dlg.dismiss() } //Todo
            .create()
        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

//    override fun onCreateContextMenu(
//        menu: ContextMenu?,
//        v: View?,
//        menuInfo: ContextMenu.ContextMenuInfo?
//    ) {
//       menuInflater.inflate(R.menu.activity_main_search_menu, menu)
//        super.onCreateContextMenu(menu, v, menuInfo)
//    }
//
//    override fun onContextItemSelected(item: MenuItem): Boolean {
//        return super.onContextItemSelected(item)
//    }
}
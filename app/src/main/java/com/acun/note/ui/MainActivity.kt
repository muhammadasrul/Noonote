package com.acun.note.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.acun.note.R
import com.acun.note.databinding.ActivityMainBinding
import com.acun.note.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        navController = findNavController(R.id.fragmentContainerView)
        binding.bottomNav.setupWithNavController(navController)
        setSupportActionBar(binding.toolBar)

        val appBarConfig = AppBarConfiguration.Builder(
            setOf(
                R.id.noteFragment,
                R.id.homeFragment,
                R.id.projectFragment
            )
        ).build()
        setupActionBarWithNavController(navController, appBarConfig)

        val themePref =
            this.getSharedPreferences(Constants.THEME_PREFERENCE_NAME, Context.MODE_PRIVATE)
        if (themePref.getBoolean(Constants.IS_DARK_MODE, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.addNoteFragment) {
                binding.bottomNavConntainer.visibility = View.GONE
            } else {
                binding.bottomNavConntainer.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }
}
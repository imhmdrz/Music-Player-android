package com.example.musicplayer



import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.musicplayer.dataStore.dataStore
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.viewModel.Injection
import com.example.musicplayer.viewModel.SongViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                ViewModelProvider(
                    this,
                    Injection.provideSongViewModelFactory(this, this.dataStore)
                ).get(SongViewModel::class.java).refreshSong(this)
            }
        }
        if(requestCode == 1){
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                ViewModelProvider(
                    this,
                    Injection.provideSongViewModelFactory(this, this.dataStore)
                ).get(SongViewModel::class.java).refreshSong(this)
            }
        }
    }
}
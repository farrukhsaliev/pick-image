package uz.mobisoft.pickimage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import uz.mobisoft.pickimage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            pickImage()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun pickImage() {
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) openGallery()
        else
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2000)
    }

    private fun openGallery() {
        val gallery: Intent
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            gallery = Intent(Intent.ACTION_OPEN_DOCUMENT)
            gallery.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        } else {
            gallery = Intent(Intent.ACTION_GET_CONTENT)
        }
        gallery.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        gallery.type = "image/*"
        startActivityForResult(
                Intent.createChooser(
                        gallery,
                        resources.getString(R.string.app_name)
                ), 2000
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 2000) {
            data?.data?.let {
                val takeFlags = data.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                val resolver = this.contentResolver
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    resolver.takePersistableUriPermission(it, takeFlags)
                }
                Glide
                        .with(this)
                        .load(it)
                        .centerCrop()
                        .into(binding.imageView)
            }
        }
    }
}
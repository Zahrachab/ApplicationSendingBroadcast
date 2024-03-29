package com.example.rencontreapp

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private var bitmap : Bitmap ?= null
    private val GALLERY = 1
    private val CAMERA = 2
    private val CUSTOM_BROADCAST_ACTION = "MARIAGE_BROADCAST";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        image!!.setOnClickListener{ showPictureDialog() }
        submit!!.setOnClickListener{
            val intent = Intent(CUSTOM_BROADCAST_ACTION)
            intent.putExtra("nom", nom.text.toString())
            intent.putExtra("bibliographie", bibliographie.text.toString())
            intent.putExtra("photo", bitmap )
            sendBroadcast(intent) }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select image from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> chooseImageFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun chooseImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    saveImage(bitmap!!)
                    image!!.setImageBitmap(bitmap)
                }
                catch (e: IOException)
                {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if (requestCode == CAMERA)
        {
            bitmap = data!!.extras!!.get("data") as Bitmap
            image!!.setImageBitmap(bitmap)
            saveImage(bitmap!!)
            Toast.makeText(this@MainActivity, "Photo Show!", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
        val wallpaperDirectory = File (
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {
            wallpaperDirectory.mkdirs()
        }
        try
        {
            Log.d("heel", wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .getTimeInMillis()).toString() + ".png"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this, arrayOf(f.getPath()), arrayOf("image/png"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException){
            e1.printStackTrace()
        }
        return ""
    }

    companion object {
        private val IMAGE_DIRECTORY = "/rencontres"
    }
}
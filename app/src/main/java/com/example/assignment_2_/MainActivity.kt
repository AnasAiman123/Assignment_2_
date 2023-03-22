package com.example.assignment_2_

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    val reqCode:Int = 100
    lateinit var pdfPath: Uri
    lateinit var dialog: ProgressDialog
    lateinit var download: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val storage = Firebase.storage
        val ref = storage.reference


        ChoosePDF.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "application/pdf"
            startActivityForResult(intent,reqCode)
        }

        Upload.setOnClickListener {
            if(pdfPath.toString().isNotEmpty()) {
                dialog = ProgressDialog(this)
                dialog.setTitle("Upload")
                dialog.setCancelable(false)
                dialog.show()
                val ref = ref.child("filesPdf").child(UUID.randomUUID().toString())
                ref.putFile(pdfPath)
                    .addOnSuccessListener { task ->
                        ref.downloadUrl.addOnCompleteListener {uri ->
                            download = "${uri.result}"
                            Toast.makeText(applicationContext, "Upload Success", Toast.LENGTH_SHORT).show()
                            dialog.cancel()
                        }

                    }

                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "Please, Upload Image", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        Download.setOnClickListener {
            if(download.isNotEmpty()) {
                val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val uri = Uri.parse(download)
                val request = DownloadManager.Request(uri)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalFilesDir(this, "pdf", "${pdfPath}.pdf")
                downloadManager.enqueue(request)
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == reqCode){
            pdfPath = data!!.data!!
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,pdfPath)
//            img.setImageBitmap(bitmap)
        }
    }
}
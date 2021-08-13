package com.usman.qr_scaner_and_generator

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

private val sharedPrefFile = "kotlinsharedpreference"


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scane_qrcode.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, ScanQRCode::class.java))
        })
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )
        val qrcodestring = sharedPreferences.getString("qrString", "defaultname")
        val imageBytes = Base64.decode(qrcodestring, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        qrcodeImageview.setImageBitmap(image)
        genbtn.setOnClickListener(View.OnClickListener {


            val data = qrtext.text.toString()
            if (data.isEmpty()) {
                Toast.makeText(this, "Enter any text to generate qr code", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val write = QRCodeWriter()

                try {
                    val bitMatrix = write.encode(data, BarcodeFormat.QR_CODE, 512, 512)
                    val width = bitMatrix.width
                    val height = bitMatrix.height
                    val bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.RGB_565)

                    for (x in 0 until width) {
                        for (y in 0 until height) {
                            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                        }
                    }

                    qrcodeImageview.setImageBitmap(bitmap)
                    val sharedPreferences: SharedPreferences = this.getSharedPreferences(
                        sharedPrefFile,
                        Context.MODE_PRIVATE
                    )

                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString("qrString", BitMapToString(bitmap))
                    editor.apply()
                    editor.commit()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun BitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }
}

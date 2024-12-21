package com.example.appaei.ui

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appaei.data.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import com.example.appaei.R

class ImageActivity : AppCompatActivity() {

    val imageResources = arrayOf(
        R.drawable.animal_chancho_dos,
        R.drawable.animal_gato,
        R.drawable.animal_leon,
        R.drawable.animal_mono,
        R.drawable.animal_perro,
        R.drawable.imagen_letra_a,
        R.drawable.imagen_letra_e,
        R.drawable.imagen_letra_i,
        R.drawable.imagen_letra_o,
        R.drawable.imagen_letra_u
    )
    var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proyectar_imagen)

        // Obtener IP del servidor desde SharedPreferences
        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val serverIp = sharedPreferences.getString("server_ip", null)

        if (serverIp == null) {
            Toast.makeText(this, "Por favor, configura la dirección IP en los ajustes", Toast.LENGTH_LONG).show()
            finish() // Cierra la actividad si no hay IP configurada
            return
        }

        val imageView: ImageView = findViewById(R.id.imageView)
        val nextButton: Button = findViewById(R.id.nextButton)

        imageView.setImageResource(imageResources[currentIndex])

        nextButton.setOnClickListener {
            currentIndex = (currentIndex + 1) % imageResources.size
            imageView.setImageResource(imageResources[currentIndex])

            sendImageToServer(serverIp, imageResources[currentIndex])
        }
    }

    fun sendImageToServer(serverIp: String, imageRes: Int) {
        val bitmap = BitmapFactory.decodeResource(resources, imageRes)
        val file = File(applicationContext.cacheDir, "image.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("PROYECTANDO..")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$serverIp:5000/") // Usar la IP configurada
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        apiService.uploadImage(body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    Toast.makeText(this@ImageActivity, "Imagen Proyectada correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ImageActivity, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@ImageActivity, "Error en el envío: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
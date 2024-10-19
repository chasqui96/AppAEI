package com.example.appaei.ui

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
        R.drawable.vocal_a,
        R.drawable.vocal_e,
        R.drawable.vocal_i,
        R.drawable.vocal_o,
        R.drawable.vocal_u
    )
    var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proyectar_imagen)

        val imageView: ImageView = findViewById(R.id.imageView)
        val nextButton: Button = findViewById(R.id.nextButton)

        // Mostrar la primera imagen
        imageView.setImageResource(imageResources[currentIndex])

        nextButton.setOnClickListener {
            // Cambiar a la siguiente imagen
            currentIndex = (currentIndex + 1) % imageResources.size
            imageView.setImageResource(imageResources[currentIndex])

            // Enviar la imagen al servidor
            sendImageToServer(imageResources[currentIndex])
        }
    }

    fun sendImageToServer(imageRes: Int) {
        val bitmap = BitmapFactory.decodeResource(resources, imageRes)
        val file = File(applicationContext.cacheDir, "image.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.31.214:5000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        apiService.uploadImage(body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Muestra un mensaje si la imagen se envió correctamente
                    Toast.makeText(this@ImageActivity, "Imagen Proyectada correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    // Muestra un mensaje si hubo algún error en la respuesta
                    Toast.makeText(this@ImageActivity, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Error en el envío (problema de red o servidor)
                Toast.makeText(this@ImageActivity, "Error en el envío: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }
}
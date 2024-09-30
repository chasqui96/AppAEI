package com.example.appaei.viewmodel // Asegúrate de que este sea el paquete correcto

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appaei.data.model.LoginpageModel
import org.koin.core.component.KoinComponent
import com.google.firebase.auth.FirebaseAuth

class LoginpageVM : ViewModel(), KoinComponent {
  val loginpageModel: MutableLiveData<LoginpageModel> = MutableLiveData(LoginpageModel())
  private lateinit var appContext: Application
  var navArguments: Bundle? = null
  val loginSuccess: MutableLiveData<Boolean> = MutableLiveData()
  fun setAppContext(application: Application) {
    appContext = application
  }

  fun onLoginClicked() {
    val username = loginpageModel.value?.etEdittextValue
    val password = loginpageModel.value?.etEdittextOneValue

    if (username.isNullOrBlank() || password.isNullOrBlank()) {
      showToast("Por favor, ingrese todos los campos.")
    } else {
      authenticateUser(username, password)
    }
  }

  fun showToast(message: String) {
    Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
  }

  private fun authenticateUser(username: String, password: String) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          showToast("Login exitoso")
          loginSuccess.value = true // Aquí notificamos que el login fue exitoso
        } else {
          showToast("Error de autenticación: ${task.exception?.message}")
        }
      }
  }
}

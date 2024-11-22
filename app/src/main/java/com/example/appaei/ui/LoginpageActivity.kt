package com.example.appaei.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.example.appaei.R
import com.example.appaei.databinding.ActivityLoginpageBinding
import com.example.appaei.viewmodel.LoginpageVM
import com.example.appaei.ui.MenuActivity // Asegúrate de importar tu HomeActivity
import com.loginpage.app.appcomponents.base.BaseActivity

class LoginpageActivity : BaseActivity<ActivityLoginpageBinding>(R.layout.activity_loginpage) {

  private val viewModel: LoginpageVM by viewModels()

  override fun onInitialized() {
    super.onInitialized()
    viewModel.navArguments = intent.extras?.getBundle("bundle")

    // Inicializa el appContext en el ViewModel
    viewModel.setAppContext(application)

    // Asigna el ViewModel a la variable binding
    binding.loginpageVM = viewModel

    // Configura los elementos de la UI aquí
    setupUI()

    // Observa el LiveData `loginSuccess` para redirigir si el login es exitoso
    viewModel.loginSuccess.observe(this, { success ->
      if (success) {
        // Redirige a la actividad de Home si el login es exitoso
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish() // Termina la actividad de login para que no pueda volver atrás
      }
    })
  }

  override fun setUpClicks() {
    binding.btnIngresar.setOnClickListener {
      viewModel.onLoginClicked()
    }
    binding.btnCrearUsuario.setOnClickListener {
      // Inicia la nueva Activity
      val intent = Intent(this, RegisterActivity::class.java)
      startActivity(intent)
    }

  }

  private fun setupUI() {
    // Configura los valores iniciales
    viewModel.loginpageModel.value?.let { model ->
      binding.txtUsuario.setText(model.etEdittextValue)
      binding.txtPassword.setText(model.etEdittextOneValue)
    }

    // Configura el observador para los cambios en LiveData
    viewModel.loginpageModel.observe(this, { model ->
      // Actualiza los campos de texto con los nuevos valores
      binding.txtUsuario.setText(model.etEdittextValue)
      binding.txtPassword.setText(model.etEdittextOneValue)
    })
  }

  companion object {
    const val TAG: String = "LOGINPAGE_ACTIVITY"
  }
}

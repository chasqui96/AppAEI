package com.example.appaei.data.model

import com.example.appaei.R
import com.example.appaei.di.MyApp
import kotlin.String

data class LoginpageModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtIngresaraaei: String? =
      MyApp.getInstance().resources.getString(R.string.lbl_ingresar_a_aei)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtTunombre: String? = MyApp.getInstance().resources.getString(R.string.lbl_tu_nombre)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtTucontrasea: String? = MyApp.getInstance().resources.getString(R.string.lbl_tu_contrase_a)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txt: String? = MyApp.getInstance().resources.getString(R.string.lbl)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txt1: String? = MyApp.getInstance().resources.getString(R.string.lbl2)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtOne: String? = MyApp.getInstance().resources.getString(R.string.lbl3)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtTwo: String? = MyApp.getInstance().resources.getString(R.string.lbl4)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var etEdittextValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdittextOneValue: String? = null
)

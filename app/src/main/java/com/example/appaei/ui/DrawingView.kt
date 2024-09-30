package com.example.appaei.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var bitmap: Bitmap? = null // Imagen de fondo
    private val paint: Paint = Paint().apply {
        color = Color.RED // Color del dibujo
        style = Paint.Style.STROKE // Estilo de trazo
        strokeWidth = 10f // Grosor del trazo
    }
    private val path = android.graphics.Path() // Trayectoria del dibujo

    // Método para establecer la imagen de la letra
    fun setLetterResource(resId: Int) {
        bitmap = BitmapFactory.decodeResource(resources, resId) // Cambia la imagen según el recurso pasado
        invalidate() // Redibujar
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Dibujar la imagen de fondo
        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        // Dibujar la trayectoria
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y) // Mover la trayectoria al punto donde se toca
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y) // Dibujar una línea hasta el nuevo punto
            }
            MotionEvent.ACTION_UP -> {
                // No hacer nada cuando se levanta el dedo
            }
        }
        invalidate() // Redibujar la vista
        return true
    }

    // Método para limpiar el dibujo
    fun clearDrawing() {
        path.reset() // Reiniciar la trayectoria
        invalidate() // Redibujar
    }

}

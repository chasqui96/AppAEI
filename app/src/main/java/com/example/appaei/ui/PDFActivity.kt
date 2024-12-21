import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import android.widget.Toast
import com.example.appaei.R

class PDFActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_slideshow)

        // Llama al método para abrir el PDF desde assets
        openPDFFromAssets("ayuda_interactiva.pdf")
    }

    private fun openPDFFromAssets(assetFileName: String) {
        try {
            // Copiar el archivo desde assets a una ubicación accesible
            val inputStream: InputStream = assets.open(assetFileName)
            val outputFile = File(getExternalFilesDir(null), assetFileName)
            val outputStream = FileOutputStream(outputFile)

            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            // Abrir el archivo PDF con un visor de PDF instalado en el dispositivo
            val uri = Uri.fromFile(outputFile)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            // Inicia la actividad para abrir el PDF
            startActivity(Intent.createChooser(intent, "Abrir PDF con"))

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al abrir el PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

package mx.edu.ittepic.unidad4p3

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var recibirSMS = android.Manifest.permission.RECEIVE_SMS
        var enviarSMS = android.Manifest.permission.SEND_SMS
        var permisoDenegado = PackageManager.PERMISSION_DENIED
        if(ActivityCompat.checkSelfPermission(this, recibirSMS) == permisoDenegado)
            ActivityCompat.requestPermissions(this, arrayOf(recibirSMS), 1)
        if(ActivityCompat.checkSelfPermission(this, enviarSMS) == permisoDenegado)
            ActivityCompat.requestPermissions(this, arrayOf(enviarSMS), 2)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1)
            Toast.makeText(this, "PERMISO DE RECIBIR SMS OTORGADOS", Toast.LENGTH_LONG).show()
        if(requestCode == 2)
            Toast.makeText(this, "PERMISO DE ENVIAR SMS OTORGADOS", Toast.LENGTH_LONG).show()
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}

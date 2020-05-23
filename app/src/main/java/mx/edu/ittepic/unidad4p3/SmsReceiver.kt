package mx.edu.ittepic.unidad4p3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SmsMessage
import com.google.firebase.firestore.FirebaseFirestore

class SmsReceiver:BroadcastReceiver() {
    var baseRemota = FirebaseFirestore.getInstance()

    override fun onReceive(context: Context?, intent: Intent?) {
        var extras = intent!!.extras
        if (extras != null) {
            var sms = extras.get("pdus") as Array<Any>
            for (indice in sms.indices) {
                var formato= extras.getString("format")
                var smsMensaje = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    SmsMessage.createFromPdu(sms[indice] as ByteArray, formato)
                else
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                var numeroOrigen =smsMensaje.originatingAddress.toString()
                var smsMensajeString = smsMensaje.messageBody.toString()
                if(smsMensajeString.length>=7 && smsMensajeString.substring(0, 7) == "COVID19") {
                    analizarMensaje(smsMensajeString, numeroOrigen)
                }
            }
        }
    }

    private fun analizarMensaje(mensaje:String, numero:String) {
        var separarPorEspacio = mensaje.split(" ")
        if(separarPorEspacio.size <3 || separarPorEspacio.size >3){
            mostrarMensajeError(numero, 1)
            return
        }
        consultarFirebase(separarPorEspacio[1], separarPorEspacio[2], numero)
    }

    private fun mostrarMensajeError(numero:String, tipoError:Int) {
        if(tipoError == 1){
            SmsManager.getDefault().sendTextMessage(numero, null, "SINTAXIS INCORRECTA, LA SINTAXIS CORRECTA ES LA SIGUIENTE:\n" +
                    "COVID19 NombreDelMunicipio TipoConsulta\nREVISE LA DOCUMENTACION DE USO DE LA APP PARA MAS INFO",
                    null, null)
        }
        if(tipoError ==2 ){
            SmsManager.getDefault().sendTextMessage(numero, null, "EL MUNICIPIO QUE USTED INGRESO NO SE ENCUENTRA EN"+
                    " LA LISTA. REVISE LA DOCUMENTACION DE USO DE LA APP",
                null, null)
        }
        if(tipoError == 3){
            SmsManager.getDefault().sendTextMessage(numero, null, "SINTAXIS INCORRECTA:\n" +
                    "SE DESCONOCE EL TIPO DE CONSULTA SOLICITADO\nREVISE LA DOCUMENTACION DE USO DE LA APP PAR MAS INFO",
                    null, null)
        }
    }

    private fun consultarFirebase(municipio: String, tipoConsulta:String, numero:String) {
        baseRemota.collection("Nayarit")
            .document(municipio)
            .get()
            .addOnSuccessListener {
                if (it.data==null) {
                    mostrarMensajeError(numero, 2)
                    return@addOnSuccessListener
                }
                if(!(tipoConsulta == "CONF" || tipoConsulta == "SOSP" ||
                   tipoConsulta== "NEG" || tipoConsulta =="INFO")){
                    mostrarMensajeError(numero, 3)
                    return@addOnSuccessListener
                }

                if(tipoConsulta == "CONF"){
                    SmsManager.getDefault().sendTextMessage(numero, null, "En el municipio de ${it.getString("nombre")}"+
                    " actualmente hay ${it.getString("confirmado")} casos confrimados de COVID19", null, null)
                }
                if(tipoConsulta == "SOSP"){
                    SmsManager.getDefault().sendTextMessage(numero, null, "En el municipio de ${it.getString("nombre")}"+
                            " actualmente hay ${it.getString("sospechoso")} casos sospechosos de COVID19", null, null)
                }
                if(tipoConsulta == "NEG"){
                    SmsManager.getDefault().sendTextMessage(numero, null, "En el municipio de ${it.getString("nombre")}"+
                            " actualmente hay ${it.getString("negativo")} casos negativos de COVID19", null, null)
                }
                if(tipoConsulta == "INFO"){
                    SmsManager.getDefault().sendTextMessage(numero, null, "En el municipio de ${it.getString("nombre")}"+
                                                            " con respecto al COVID 19, tenemos:\n" +
                                                            "CASOS CONFIRMADOS: ${it.getString("confirmado")}\n" +
                                                            "CASOS SOSPECHOSOS: ${it.getString("sospechoso")}\n" +
                                                            "CASOS NEGATIVOS: ${it.getString("negativo")}",
                        null, null)
                }
            }

    }
}
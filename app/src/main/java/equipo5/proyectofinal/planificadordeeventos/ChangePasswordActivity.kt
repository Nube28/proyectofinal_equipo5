package equipo5.proyectofinal.planificadordeeventos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Actividad que permite al usuario cambiar su contraseña.
 */
class ChangePasswordActivity : AppCompatActivity() {

    private var email = ""
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_password)

        auth = FirebaseAuth.getInstance()

        val btn_send_newpassword: Button = findViewById(R.id.btn_send_newpassword) as Button
        val et_email_newpassword: EditText = findViewById(R.id.et_email_newpassword) as EditText

        btn_send_newpassword.setOnClickListener{

            email = et_email_newpassword.text.toString().trim()

            if(email.isNotEmpty() || email.isNotBlank()){
                resetPassword()
            } else {
                Toast.makeText(this, "Debe ingresar el email", Toast.LENGTH_SHORT).show()
            }

        }

    }

    /**
     * Funcion que envia un email para cambiar la contraseña del usuario.
     */
    private fun resetPassword(){
        auth.setLanguageCode("it")
        auth.setLanguageCode("it")
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@ChangePasswordActivity, "Se ha enviado su correo", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ChangePasswordActivity, "No se pudo enviar el correo para restablecer su contraseña", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
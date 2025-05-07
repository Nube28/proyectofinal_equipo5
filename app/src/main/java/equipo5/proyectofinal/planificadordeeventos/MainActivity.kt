package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

/**
 * Pantalla principal de autenticación para el usuario.
 *
 * Permite al usuario iniciar sesión con su correo y contraseña utilizando Firebase Authentication.
 * Si el usuario ya tiene una sesión activa, se redirige automáticamente a la pantalla principal del sistema.
 * También permite redirigir al formulario de registro si no se tiene una cuenta.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth

        val et_email: EditText = findViewById(R.id.et_email)
        val et_password: EditText = findViewById(R.id.et_password)

        val btn_sign_in: Button = findViewById(R.id.btn_sign_in)
        btn_sign_in.setOnClickListener {
            val email = et_email.text.toString().trim()
            val password = et_password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(email, password)
        }

        val btn_not_account_register: TextView = findViewById(R.id.btn_not_account_register)
        btn_not_account_register.setOnClickListener{
            val intent: Intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

    /**
     * Verifica si hay una sesión de usuario activa al iniciar la actividad.
     * Si existe, redirige automáticamente a la pantalla de selección de evento.
     */
    public override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            Toast.makeText(this, "Ya tienes una sesion iniciada", Toast.LENGTH_SHORT).show()
            goToMain(currentUser)
        }
    }

    /**
     * Redirige a la pantalla principal del sistema, pasando el correo electrónico del usuario.
     *
     * @param user Usuario autenticado de Firebase.
     */
    fun goToMain(user: FirebaseUser){
        val intent = Intent(this, ChoseEvent::class.java)
        intent.putExtra("user", user.email)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP 
        startActivity(intent)
    }

    /**
     * Intenta iniciar sesión con los datos proporcionados por el usuario.
     * Si la autenticación es exitosa, redirige a la pantalla principal.
     * Si falla, muestra un mensaje de error.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     */
    fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    goToMain(user!!)
                } else {
                    Toast.makeText(this, "Email o Contraseña no coincide", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
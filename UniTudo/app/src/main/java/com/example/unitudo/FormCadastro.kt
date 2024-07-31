package com.example.unitudo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class FormCadastro : AppCompatActivity() {


    private lateinit var edit_nome: EditText
    private lateinit var edit_email: EditText
    private lateinit var edit_confirmaremail: EditText
    private lateinit var edit_telefone: EditText
    private lateinit var edit_senha: EditText
    private lateinit var edit_confirmarsenha: EditText
    private lateinit var bt_cadastrar: Button


    private val mensagens = arrayOf("Preencha todos os campos", "Cadastro realizado com sucesso")
    private var usuarioID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form_cadastro)
        supportActionBar?.hide()


        iniciarComponentes()


        bt_cadastrar.setOnClickListener {
            val nome = edit_nome.text.toString()
            val email = edit_email.text.toString()
            val confirmaremail = edit_confirmaremail.text.toString()
            val telefone = edit_telefone.text.toString()
            val senha = edit_senha.text.toString()
            val confirmarsenha = edit_confirmarsenha.text.toString()


            if (nome.isEmpty() || email.isEmpty() || confirmaremail.isEmpty() || telefone.isEmpty() || senha.isEmpty() || confirmarsenha.isEmpty()) {
                val snackbar = Snackbar.make(it, mensagens[0], Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.WHITE)
                snackbar.setTextColor(Color.BLACK)
                snackbar.show()
            } else {
                cadastrarUsuario(it, email, senha)
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun iniciarComponentes() {
        edit_nome = findViewById(R.id.edit_nome)
        edit_email = findViewById(R.id.edit_email)
        edit_confirmaremail = findViewById(R.id.edit_confirmaremail)
        edit_telefone = findViewById(R.id.edit_telefone)
        edit_senha = findViewById(R.id.edit_senha)
        edit_confirmarsenha = findViewById(R.id.edit_confirmarsenha)
        bt_cadastrar = findViewById(R.id.bt_cadastrar)
    }


    private fun cadastrarUsuario(view: View, email: String, senha: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snackbar = Snackbar.make(view, mensagens[1], Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()


                    salvarDadosUsuario()
                } else {
                    var erro = "Erro ao cadastrar usuário"
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        erro = "Digite uma senha com no mínimo 6 caracteres"
                    } catch (e: FirebaseAuthUserCollisionException) {
                        erro = "Essa conta já foi cadastrada"
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        erro = "E-mail inválido"
                    } catch (e: Exception) {
                        erro = "Erro ao cadastrar usuário"
                    }
                    val snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()
                }
            }
    }


    private fun salvarDadosUsuario() {
        val nome = edit_nome.text.toString()
        val telefone = edit_telefone.text.toString()
        val db = FirebaseFirestore.getInstance()


        val usuario = hashMapOf(
            "nome" to nome,
            "telefone" to telefone
        )


        usuarioID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val documentReference: DocumentReference = db.collection("Usuarios").document(usuarioID)
        documentReference.set(usuario)
            .addOnSuccessListener {
                Log.d("db", "Sucesso ao salvar os dados")
            }
            .addOnFailureListener { e ->
                Log.d("db_erro", "Erro ao salvar os dados: ${e.message}")
            }
    }
}

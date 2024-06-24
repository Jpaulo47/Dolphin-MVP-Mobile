package com.joaorodrigues.dolphinmvp

import Notificador.Companion.showToast
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.joaorodrigues.dolphinmvp.databinding.ActivityLoginBinding
import java.util.Objects


class LoginActivity : AppCompatActivity() {

    private lateinit var usuario: Usuario
    private lateinit var autenticacao: FirebaseAuth
    private lateinit var binding : ActivityLoginBinding
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        autenticacao = FirebaseAuth.getInstance()
        installSplashScreen()
        verificarUsuarioLogado()
        binding.textCadastrar.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }

        binding.progressLogin.visibility = View.INVISIBLE
        binding.buttonEntrar.setOnClickListener {
            val textoEmail = binding.editEmail.text.toString()
            val textoSenha = Objects.requireNonNull(binding.textSenha.text).toString()
            if (textoEmail.isNotEmpty()) {
                if (textoSenha.isNotEmpty()) {
                    usuario = Usuario()
                    usuario.email = textoEmail
                    usuario.senha = textoSenha
                    validarLogin(usuario)
                } else {
                    showToast(this, "Preencha a senha!")
                }
            } else {
                showToast(this, "Preencha o email!")
            }
        }

        binding.buttonLigarSamu.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.modal_samu)

            val positiveButton = dialog.findViewById<Button>(R.id.btnLigarParaSamu)
            val negativeButton = dialog.findViewById<Button>(R.id.btnCancelar)

            val dialogWindow = dialog.window
            if (dialogWindow != null) {
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(dialogWindow.attributes)
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                dialogWindow.setLayout(layoutParams.width, layoutParams.height)
            }

            positiveButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = android.net.Uri.parse("tel:192")
                startActivity(intent)
                dialog.dismiss()
            }

            negativeButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

    }

    //Método para validar o login do usuário
    private fun validarLogin(usuario: Usuario) {
        binding.progressLogin.visibility = View.VISIBLE
        autenticacao.signInWithEmailAndPassword(usuario.email.toString(), usuario.senha.toString())
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    binding.progressLogin.visibility = View.VISIBLE
                    Toast.makeText(
                        this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()

                } else {
                    binding.progressLogin.visibility = View.INVISIBLE
                    val erroExcecao = try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidUserException) {
                        "Usuário não cadastrado."
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        "E-mail e/ou senha não correspondem a um usuário cadastrado."
                    } catch (e: Exception) {
                        "Erro ao efetuar login: ${e.message}"
                    }
                    showToast(this, erroExcecao)
                }
            }
    }

    //Método para verificar se o usuário está logado
    private fun verificarUsuarioLogado() {
        if (autenticacao.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
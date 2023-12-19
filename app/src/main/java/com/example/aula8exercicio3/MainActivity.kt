package com.example.aula8exercicio3

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.aula8exercicio3.ApiResponse
import com.example.aula8exercicio3.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var btnChamarApi: Button
    private lateinit var txtResultado: TextView
    private lateinit var imgFoto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val service = Retrofit.Builder()
            .baseUrl("https://randomuser.me/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(UserService::class.java)

        btnChamarApi = findViewById(R.id.btnChamarApi)
        txtResultado = findViewById(R.id.txtResultado)
        imgFoto = findViewById(R.id.imgFoto)
        btnChamarApi.setOnClickListener {
            // Desative o botão e mostre o ProgressBar durante a chamada da API
            btnChamarApi.isEnabled = false

            service.getUsers(1).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    // Restaure o estado do botão e esconda o ProgressBar
                    btnChamarApi.isEnabled = true

                    if (response.isSuccessful) {
                        val dadosRecebidos = response.body()?.results
                        if (!dadosRecebidos.isNullOrEmpty()) {
                            // Filtra apenas os usuários que têm nomes e URL de imagem
                            val usuariosComNomesEImagens = dadosRecebidos.filter {
                                it.name != null && it.picture?.large != null
                            }

                            if (usuariosComNomesEImagens.isNotEmpty()) {
                                // Leva apenas os primeiros 5 usuários com nomes e URLs de imagem
                                val usuarios = usuariosComNomesEImagens.take(1).map {
                                    it.name?.fullName to it.picture?.large
                                }

                                // Exiba os nomes e carregue as imagens
                                txtResultado.text = "Dados recebidos:\n${usuarios.joinToString("\n")}"

                                // Carregue a primeira imagem usando Glide
                                val imageUrl = usuarios[0].second
                                if (!imageUrl.isNullOrEmpty()) {
                                    Glide.with(this@MainActivity)
                                        .load(imageUrl)
                                        .apply(RequestOptions.centerCropTransform())
                                        .into(imgFoto)
                                }
                            } else {
                                txtResultado.text = "Nenhum usuário com nome e imagem encontrados"
                            }
                        } else {
                            txtResultado.text = "Lista de usuários vazia"
                        }
                    } else {
                        txtResultado.text = "Erro na chamada da API: ${response.message()}"
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    btnChamarApi.isEnabled = true
                    txtResultado.text = "Erro na chamada da API"
                }
            })
        }
    }
}
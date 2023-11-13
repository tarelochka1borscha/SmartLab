package com.example.smartlab

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listener()
    }
    private fun listener(){
        binding.buttonNext.setOnClickListener()
        {
            sendEmail()
            val intent = Intent(this@RegistrationActivity, Code::class.java)
            startActivity(intent)
        }
    }
    fun sendEmail(){
        //interceptor - Перехватчик OkHttp, который регистрирует данные HTTP-запроса и ответа.
        //Создаем объект перехватчика
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        //Добавляем перехватчика в клиента
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()


        val gsonBuilder = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())//Конвертер строк, обоих примитивов и их коробочных типов в text/plain тела.
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
            .baseUrl("https://iis.ngknn.ru/NGKNN/МамшеваЮС/MedicMadlab/")
            .client(httpClient) //добавление http клиента в retrofit
            .build()
        val requestApi = retrofit.create(ApiRequest::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                requestApi.postEmail(binding.emailUser.text.toString())
                    .awaitResponse() //Запуск метода из ApiRequest с переданым параметром в Header (заголовок)
                Log.d("Response", "Success send Email") // Вывод информации на консоль
            } catch (e: Exception) {
                Log.d(ContentValues.TAG, e.toString()) // Вывод информации на консоль
            }
        }
    }
}
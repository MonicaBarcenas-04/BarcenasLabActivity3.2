package com.example.barcenaslabactivity32

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private val apiKey = "07d023a27a370e1548d07b72cb5bd5b2"
    private val city = "New York"
    private val baseUrl = "https://api.openweathermap.org/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "New York Weather"

        val tvCity = findViewById<TextView>(R.id.tvCity)
        val tvMain = findViewById<TextView>(R.id.tvMain)
        val tvDescription = findViewById<TextView>(R.id.tvDescription)
        val ivIcon = findViewById<ImageView>(R.id.ivIcon)

        // Create Moshi instance with KotlinJsonAdapterFactory
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        // Create Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val weatherService = retrofit.create(WeatherService::class.java)

        // Call the API with enqueue()
        weatherService.getWeather(city, apiKey).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    if (weatherResponse != null) {
                        tvCity.text = weatherResponse.name

                        val weather = weatherResponse.weather.firstOrNull()
                        if (weather != null) {
                            tvMain.text = weather.main
                            tvDescription.text = weather.description

                            val iconUrl = "https://openweathermap.org/img/wn/${weather.icon}@2x.png"
                            Glide.with(this@MainActivity)
                                .load(iconUrl)
                                .into(ivIcon)
                        }
                    } else {
                        val errorMsg = "Response body is empty"
                        Log.e("MainActivity", errorMsg)
                        Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMsg = "HTTP Error ${response.code()}: ${response.message()}"
                    Log.e("MainActivity", errorMsg)
                    Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                val errorMsg = "Request failed: ${t.localizedMessage}"
                Log.e("MainActivity", errorMsg, t)
                Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }
}

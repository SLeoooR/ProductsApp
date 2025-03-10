package com.scottreganit.productsapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.scottreganit.productsapp.databinding.ActivityStartUpBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class StartUpActivity : AppCompatActivity(), CoroutineScope {
    private val binding: ActivityStartUpBinding by lazy {
        ActivityStartUpBinding.inflate(layoutInflater)
    }

    private lateinit var job: Job
    private lateinit var productList: List<Product>
    private lateinit var productGateway: ProductGateway
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        job = Job()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        productGateway = provideProductRepository(applicationContext)
    }

    override fun onStart() {
        super.onStart()

        launch {
            delay(1000)
            try {
                startNavigation()
            } catch (e: Exception) {
                Log.d("error", e.localizedMessage?: "")
            }
        }
    }

    private suspend fun startNavigation() {
        try {
            productList = productGateway.fetchProducts(5, 30).products
            productList.forEach {
                productGateway.insertProduct(it)
            }
            Log.d("Products", productGateway.getProducts().toString())
        } catch (e: Exception) {
            Log.d("error", e.localizedMessage?: "")
        }

        startActivity(Intent(this@StartUpActivity, MainActivity::class.java))
    }
}
package com.scottreganit.productsapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.scottreganit.productsapp.databinding.FragmentProductBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.util.Locale

class ProductFragment : Fragment() {

    private val binding: FragmentProductBinding by lazy {
        FragmentProductBinding.inflate(layoutInflater)
    }

    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val productService = activity?.let { provideProductRepository(it.applicationContext) }

        lifecycleScope.launch {
            product = productService?.getProductWhere(arguments?.getLong("productId")?.toInt() ?: 0) ?: Product(0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val circularProgressDrawable = CircularProgressDrawable(view.context).apply {
            strokeWidth = 5F
            centerRadius = 15F
            setColorSchemeColors(ContextCompat.getColor(view.context, R.color.white))
            start()
        }

        Picasso.get().load(product.thumbnail).error(R.drawable.ic_broken_image).placeholder(circularProgressDrawable).into(binding.ivProductIcon)
        binding.tvTitle.text = product.title
        binding.tvDescription.text = product.description
        val priceToString = "₱" + product.price.toString()
        binding.tvPrice.text = priceToString
        val categoryStr = "Category: " + product.category?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        binding.tvCategory.text = categoryStr
        val brandStr = "Brand: " + (product.brand ?: "None")
        binding.tvBrand.text = brandStr
        try {
            Picasso.get().load(product.images?.get(0)).error(R.drawable.ic_broken_image).placeholder(circularProgressDrawable).into(binding.ivProductOne)
        } catch (e: Exception) {
            Log.d("error", e.localizedMessage?: "")
            Picasso.get().load(R.drawable.ic_broken_image).into(binding.ivProductOne)
        }
        try {
            Picasso.get().load(product.images?.get(1)).error(R.drawable.ic_broken_image).placeholder(circularProgressDrawable).into(binding.ivProductTwo)
        } catch (e: Exception) {
            Log.d("error", e.localizedMessage?: "")
            Picasso.get().load(R.drawable.ic_broken_image).into(binding.ivProductTwo)
        }
        try {
            Picasso.get().load(product.images?.get(2)).error(R.drawable.ic_broken_image).placeholder(circularProgressDrawable).into(binding.ivProductThree)
        } catch (e: Exception) {
            Log.d("error", e.localizedMessage?: "")
            Picasso.get().load(R.drawable.ic_broken_image).into(binding.ivProductThree)
        }
    }
}
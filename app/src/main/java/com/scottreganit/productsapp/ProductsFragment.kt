package com.scottreganit.productsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.scottreganit.productsapp.databinding.FragmentProductsBinding
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class ProductsFragment : Fragment() {

    private val binding: FragmentProductsBinding by lazy {
        FragmentProductsBinding.inflate(layoutInflater)
    }

    private lateinit var productsList: List<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val productService = activity?.let { provideProductRepository(it.applicationContext) }

        lifecycleScope.launch {
            productsList = productService?.getProducts() ?: emptyList()
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

        binding.rvProducts.layoutManager = LinearLayoutManager(this.context)
        binding.rvProducts.adapter = ProductsListAdapter(productsList) {

        }
    }
}
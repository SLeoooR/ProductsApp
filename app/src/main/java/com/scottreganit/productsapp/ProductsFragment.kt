package com.scottreganit.productsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
    private val itemsPerPage = 10
    private var itemsRemaining = 0
    private var lastPage = 0
    private var currentPage = 0
    private var totalPages = 0
    private lateinit var trimmedProducts: List<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val productService = activity?.let { provideProductRepository(it.applicationContext) }

        lifecycleScope.launch {
            productsList = productService?.getProducts() ?: emptyList()
        }

        itemsRemaining = productsList.size % itemsPerPage
        lastPage = productsList.size / itemsPerPage
        totalPages = lastPage

        trimmedProducts = if (productsList.size >= itemsPerPage) {
            productsList.subList(0, itemsPerPage)
        } else {
            productsList
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
        binding.rvProducts.adapter = ProductsListAdapter(trimmedProducts) {
            val bundle = bundleOf("productId" to it)
            findNavController().navigate(R.id.action_productsFragment_to_productFragment, bundle)
        }

        toggleButtons()
        setupBtnNext()
        setupBtnPrev()
    }

    private fun toggleButtons() {
        if (totalPages == 0) {
            binding.btnNext.visibility = View.INVISIBLE
            binding.btnNext.isEnabled = false

            binding.btnPrev.visibility = View.INVISIBLE
            binding.btnPrev.isEnabled = false
        } else {
            when (currentPage) {
                totalPages - 1 -> {
                    binding.btnNext.visibility = View.INVISIBLE
                    binding.btnNext.isEnabled = false

                    binding.btnPrev.visibility = View.VISIBLE
                    binding.btnPrev.isEnabled = true
                }
                0 -> {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnNext.isEnabled = true

                    binding.btnPrev.visibility = View.INVISIBLE
                    binding.btnPrev.isEnabled = false
                }
                in 1..totalPages -> {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnNext.isEnabled = true

                    binding.btnPrev.visibility = View.VISIBLE
                    binding.btnPrev.isEnabled = true
                }
            }
        }
    }

    private fun updateRv() {
        binding.rvProducts.layoutManager = LinearLayoutManager(this.context)
        binding.rvProducts.adapter = ProductsListAdapter(trimmedProducts) {
            val bundle = bundleOf("productId" to it)
            findNavController().navigate(R.id.action_productsFragment_to_productFragment, bundle)
        }
    }

    private fun setupBtnNext() {
        binding.btnNext.setOnClickListener {
            currentPage++
            generateProducts(currentPage)
            updateRv()
            toggleButtons()
        }
    }

    private fun setupBtnPrev() {
        binding.btnPrev.setOnClickListener {
            currentPage--
            generateProducts(currentPage)
            updateRv()
            toggleButtons()
        }
    }

    private fun generateProducts(currentPage: Int) {
        val startItem = (currentPage * itemsPerPage) + 1
        val numOfData = itemsPerPage

        trimmedProducts = if (currentPage == lastPage && itemsRemaining > 0) {
            productsList.subList(startItem - 1, (startItem + itemsRemaining) - 1)
        } else {
            productsList.subList(startItem - 1, (startItem + numOfData) - 1)
        }
    }
}
package com.scottreganit.productsapp

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.scottreganit.productsapp.databinding.ProductListItemBinding
import com.squareup.picasso.Picasso

class ProductsListAdapter(
    private val productsList: List<Product>,
    private val onProductClick: (id: Long) -> Unit
) : RecyclerView.Adapter<ProductsListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ProductListItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val mPosition = layoutPosition
            val productId: Long = productsList[mPosition].id

            onProductClick.invoke(productId)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val productListItemBinding = ProductListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return ViewHolder(productListItemBinding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (this.productsList.isNotEmpty()) {
            val product: Product = productsList[position]

            val circularProgressDrawable = CircularProgressDrawable(viewHolder.itemView.context).apply {
                strokeWidth = 5F
                centerRadius = 15F
                setColorSchemeColors(ContextCompat.getColor(viewHolder.itemView.context, R.color.white))
                start()
            }

            Picasso.get().load(product.thumbnail).error(R.drawable.ic_broken_image).placeholder(circularProgressDrawable).into(viewHolder.binding.ivProductIcon)
            viewHolder.binding.tvTitle.text = product.title
            viewHolder.binding.tvDescription.text = product.description
            val priceToString = "₱" + product.price.toString()
            viewHolder.binding.tvPrice.text = priceToString
        }
    }

    override fun getItemCount(): Int = productsList.size
}
package com.scottreganit.productsapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Products(
    @SerializedName("products")
    val products: List<Product>
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: Long,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String? = "",

    @ColumnInfo(name = "description")
    @SerializedName("description")
    val description: String? = "",

    @ColumnInfo(name = "category")
    @SerializedName("category")
    val category: String? = "",

    @ColumnInfo(name = "price")
    @SerializedName("price")
    val price: Double? = 0.0,

    @ColumnInfo(name = "brand")
    @SerializedName("brand")
    val brand: String? = "",

    @ColumnInfo(name = "images")
    @SerializedName("images")
    val images: List<String>? = emptyList(),

    @ColumnInfo(name = "thumbnail")
    @SerializedName("thumbnail")
    val thumbnail: String? = "",
)
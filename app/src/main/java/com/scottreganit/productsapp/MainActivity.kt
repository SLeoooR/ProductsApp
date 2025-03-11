package com.scottreganit.productsapp

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.scottreganit.productsapp.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {
    private val bindingMainActivity: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(bindingMainActivity.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        navController.graph = graph
    }
}

interface ProductEndpoint {
    @Headers("Content-Type: application/json")
    @GET("products")
    suspend fun fetchProducts(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int,
    ): Response<Products>
}

fun createProductService(): ProductEndpoint {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY

    val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

    val retrofit = Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://dummyjson.com")
        .build()

    return retrofit.create(ProductEndpoint::class.java)
}

class ProductRepository(
    private val productDao: ProductDao,
    private val productService: ProductEndpoint
) : ProductGateway {
    override suspend fun fetchProducts(skip: Int, limit: Int): Products {
        return try {
            Products(productService.fetchProducts(skip, limit).body()!!.products)
        } catch (e: Exception) {
            e.printStackTrace()
            Products(emptyList())
        }
    }

    override fun insertProduct(product: Product) {
        productDao.insertProduct(
            product
        )
    }

    override fun deleteProducts() {
        productDao.deleteProducts()
    }

    override fun getProducts(): List<Product> = productDao.getProducts()
    override fun countProducts(): Int = productDao.countProducts()
    override fun getProductWhere(id: Int): Product = productDao.getProductWhere(id)
}

interface ProductGateway {
    suspend fun fetchProducts(skip: Int, limit: Int): Products

    fun insertProduct(product: Product)
    fun deleteProducts()
    fun getProducts(): List<Product>
    fun countProducts(): Int
    fun getProductWhere(id: Int): Product
}

fun provideProductRepository(appContext: Context) : ProductRepository {
    val db = ProductDatabase.getDatabase(appContext)
    return ProductRepository(
        db.getProductDao(),
        createProductService()
    )
}

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: Product)

    @androidx.room.Query("DELETE FROM products")
    fun deleteProducts()

    @androidx.room.Query("SELECT * FROM products")
    fun getProducts(): List<Product>

    @androidx.room.Query("SELECT COUNT(*) FROM products")
    fun countProducts(): Int

    @androidx.room.Query("SELECT * FROM products WHERE id = :id")
    fun getProductWhere(id: Int): Product
}

@Database(
    entities = [
        Product::class,
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ProductDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: ProductDatabase? = null

        fun getDatabase(context: Context): ProductDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java,
                    "product_database"
                ).fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    abstract fun getProductDao(): ProductDao
}

object Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType: Type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}
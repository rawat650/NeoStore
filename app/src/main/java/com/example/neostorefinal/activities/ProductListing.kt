package com.example.neostorefinal.activities

import android.app.SearchManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neostorefinal.R
import com.example.neostorefinal.adapter.ProductListAdapter
import com.example.neostorefinal.api.RetrofitClient
import com.example.neostorefinal.modelClass.ProductData
import com.example.neostorefinal.modelClass.ProductList
import kotlinx.android.synthetic.main.activity_product_detail_screen.*
import kotlinx.android.synthetic.main.activity_product_listing.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ProductListing : AppCompatActivity() {
    lateinit var list: MutableList<ProductData>
    lateinit var temporayList: MutableList<ProductData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_listing)
//        setSupportActionBar(productCategoriesToolBar)
        /*supportActionBar?.setDisplayShowTitleEnabled(false)*/
        supportActionBar?.hide()

        btnProductTollbarBack.setOnClickListener() {
            onBackPressed()
        }

        val getIntent = intent.getStringExtra("CATEGORY_VALUE")

        when (getIntent) {
            "1" -> productToolbarTitle.text = "Tables"
            "2" -> productToolbarTitle.text = "Chairs"
            "3" -> productToolbarTitle.text = "Sofas"
            "4" -> productToolbarTitle.text = "Cupboards"
        }

        temporayList = mutableListOf<ProductData>()
        RetrofitClient.getClient.productList(getIntent!!).enqueue(object : Callback<ProductList?> {
            override fun onResponse(call: Call<ProductList?>, response: Response<ProductList?>) {

                list = mutableListOf<ProductData>()
                for (item in response.body()?.data!!) {
                    list.add(item)
                }
                temporayList.addAll(list)

                val productAdapter = ProductListAdapter(temporayList)
                productRecyclerView.adapter = productAdapter
                productRecyclerView.layoutManager = LinearLayoutManager(
                    this@ProductListing,
                    LinearLayoutManager.VERTICAL, false
                )
                productAdapter.setOnItemClickListerner(object :
                    ProductListAdapter.onItemClickListerner {
                    override fun onItemClick(position: Int) {
                        val id = position.toString()
                        val prodcutItemIntent =
                            Intent(this@ProductListing, ProductDetailScreen::class.java)
                        prodcutItemIntent.putExtra("PRODUCT_ID", id)
                        startActivity(prodcutItemIntent)
                    }
                })

            }

            override fun onFailure(call: Call<ProductList?>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu , menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchitem = menu?.findItem(R.id.action_search)
        val searchView = searchitem?.actionView as androidx.appcompat.widget.SearchView
        searchView?.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.queryHint = Html.fromHtml("<font color = #ffffff>" + "Search ..")


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@ProductListing,"No Data Found", Toast.LENGTH_LONG).show()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                temporayList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if(searchText.isNotEmpty()){
                    list.forEach {
                        if(it.name.toLowerCase(Locale.getDefault()).contains(searchText)){
                            temporayList.add(it)
                        }
                    }
                    productRecyclerView.adapter!!.notifyDataSetChanged()
                }else{
                    temporayList.clear()
                    temporayList.addAll(list)
                    productRecyclerView.adapter!!.notifyDataSetChanged()
                }


                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

}

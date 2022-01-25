package com.example.neostorefinal.activities

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide

import kotlinx.android.synthetic.main.activity_product_detail_screen.*
import kotlinx.android.synthetic.main.product_detail_images_item_list.*
import kotlinx.android.synthetic.main.product_detail_images_item_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neostorefinal.R
import com.example.neostorefinal.adapter.ProductDetailImageAdapter
import com.example.neostorefinal.api.RetrofitClient
import com.example.neostorefinal.fragment.AddToCardtFragment
import com.example.neostorefinal.fragment.RatingPopUpScreen
import com.example.neostorefinal.modelClass.ProductDetail
import com.example.neostorefinal.modelClass.ProductImage
import com.example.neostorefinal.storage.SharedPreferenceManager
import kotlinx.android.synthetic.main.activity_home.*


class ProductDetailScreen : AppCompatActivity() {
    lateinit var getId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail_screen)
        /*supportActionBar?.hide()*/
        setSupportActionBar(toolBar)


        getProductDetailData()

        btnProductDetailTollbarBack .setOnClickListener(){
            onBackPressed()
        }
       /* btnAddToCart.setOnClickListener {
            startActivity(Intent(this,MyCartScreen::class.java))
        }
*/


    }

    private fun getProductDetailData() {
        getId = intent.getStringExtra("PRODUCT_ID").toString()
        val data: Uri? = intent?.data
        Log.d(TAG, "getProductDetailData: $data")
        if (data != null) {
            getId = data.getQueryParameter("id").toString()
            Log.d(TAG, "getProductDetailData: $getId")
        }
        RetrofitClient.getClient.productDetail(getId!!).enqueue(object : Callback<ProductDetail?> {
            override fun onResponse(
                call: Call<ProductDetail?>,
                response: Response<ProductDetail?>
            ) {
                if (response.body() != null) {
                    val productResponse = response.body()
                    productDetailtoolbarTitle.setText(productResponse?.data?.name)
                    val productName = productResponse?.data?.name
                    ProductDetailName.text = productName
                    val categoryId = productResponse?.data?.product_category_id
                    if (categoryId == 1)
                        productDetailCategory.text = "Category - Tables"
                    else if (categoryId == 2)
                        productDetailCategory.text = "Category - Chairs"
                    else if (categoryId == 3)
                        productDetailCategory.text = "Category - Sofas"
                    else if (categoryId == 4)
                        productDetailCategory.text = "Category - Cupboards"
                    else
                        productDetailCategory.text = ""

                    productDetailProducer.text = productResponse?.data?.producer
                    productDetailRatingBar.rating = productResponse?.data?.rating!!.toFloat()


                    val nf = NumberFormat.getCurrencyInstance()
                    val pattern = (nf as DecimalFormat).toPattern()
                    val newPattern = pattern.replace("\u00A4", "").trim { it <= ' ' }
                    val newFormat: NumberFormat = DecimalFormat(newPattern)
                    val answer = newFormat.format(productResponse?.data?.cost).trim().dropLast(3)
                    productDetailPrice.text = "Rs. " + answer

                    productDetailDescription.text = productResponse.data.description

                    val imageUrl = productResponse.data.product_images[0].image
                    Glide.with(this@ProductDetailScreen).load(imageUrl).into(productDetailImage)

                    val imageList = mutableListOf<ProductImage>()
                    for (image in productResponse.data.product_images) {
                        imageList.add(image)
                    }
                    Log.d(TAG, "onResponse: ${productResponse.data.product_images[0].image}")


                    val prodcutDetailAdapter = ProductDetailImageAdapter(imageList)
                    productDetailRecyclerview.adapter = prodcutDetailAdapter
                    productDetailRecyclerview.layoutManager =
                        LinearLayoutManager(this@ProductDetailScreen,LinearLayoutManager.HORIZONTAL,false)


                    ProductDetailRatingPopUp.setOnClickListener(){

                        val fragmentPopUp: DialogFragment = RatingPopUpScreen()
                        val bundle = Bundle()
                        bundle.putString("productId",getId)
                        bundle.putString("productName" , productName)
                        bundle.putString("ImageURl",imageUrl)
                        fragmentPopUp.arguments = bundle
                        fragmentPopUp.show(supportFragmentManager,"RatingPOPUpScreen")
                    }

                    btnProductDetailBuyNow.setOnClickListener(){
                        if(!SharedPreferenceManager.getInstance(this@ProductDetailScreen).loggedIn){
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }else{
                            val buyNowPopUp :DialogFragment = AddToCardtFragment()
                            val bundle = Bundle()
                            bundle.putString("productId",getId)
                            bundle.putString("productName" , productName)
                            bundle.putString("ImageURl",imageUrl)
                            buyNowPopUp.arguments = bundle
                            buyNowPopUp.show(supportFragmentManager,"AddToCartScreen")
                        }


                    }


                    productDetailSharing.setOnClickListener(){
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        //http://staging.php-dev.in:8844/trainingapp/api/products/getDetail?product_id=$getId
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "www.neostore.com/product_id?id=$getId")
                        startActivity(Intent.createChooser(shareIntent, "Share link using"))
                    }


                    prodcutDetailAdapter.setOnClickListerner(object : ProductDetailImageAdapter.onImageClickListernet {




                        override fun onClickListerner(position: String) {

                            Glide.with(this@ProductDetailScreen).load(position).into(productDetailImage)
                        }
                    })

                }else{
                    Toast.makeText(this@ProductDetailScreen,"${response.errorBody()}",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductDetail?>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun myCart(view: android.view.View) {
        val intent = Intent(this,MyCartScreen::class.java)
        startActivity(intent)
    }


}
package com.martbangunan.app.ui.activity.selller

import android.app.Activity
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.martbangunan.app.R
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.ProductModel
import com.martbangunan.app.network.model.SliderItem
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UploadProductActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper

    val back: ImageView by lazy { findViewById(R.id.imgBack) }
    val parentView: ConstraintLayout by lazy { findViewById(R.id.parentAddProduct) }
    val productName: TextInputEditText by lazy { findViewById(R.id.inputProductName) }
    val productCategory: AutoCompleteTextView by lazy { findViewById(R.id.inputProductCategory) }
    val productQuantity: TextInputEditText by lazy { findViewById(R.id.inputProductQuantity) }
    val productPrice: TextInputEditText by lazy { findViewById(R.id.inputProductPrice) }
    val productDescription: TextInputEditText by lazy { findViewById(R.id.inputProductDescription) }
    val btnAddProduct: MaterialButton by lazy { findViewById(R.id.btnAddProduct) }

    val img1: ImageView by lazy { findViewById(R.id.img1) }
    val img2: ImageView by lazy { findViewById(R.id.img2) }
    val img3: ImageView by lazy { findViewById(R.id.img3) }

    var cek = false
    private val category = listOf("Material bangunan alami", "Material bangunan pabrik")

    private var reqBody: RequestBody? = null
    private var parts: ArrayList<MultipartBody.Part> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_product)
        sharedPref = PreferencesHelper(this)
        val type = sharedPref.getString(Constant.PREF_TYPE)
        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

        cek = intent.getBooleanExtra("cek", false)
        val idProduct = intent.getIntExtra("id", 0).toString()
        val nameProduct = intent.getStringExtra("name")
        val categoryProduct = intent.getStringExtra("category")
        val quantityProduct = intent.getIntExtra("quantity", 0).toString()
        val priceProduct = intent.getIntExtra("price", 0).toString()
        val descriptionProduct = intent.getStringExtra("description")

        if (cek) {
            productName.setText(nameProduct)
            productCategory.setText(categoryProduct)
            productQuantity.setText(quantityProduct)
            productPrice.setText(priceProduct)
            productDescription.setText(descriptionProduct)
            btnAddProduct.text = "Edit"

            getImage(token!!, idProduct)

        } else {
            btnAddProduct.text = "Tambah produk"
            img1.setOnClickListener {
                ImagePicker.with(this)
                    .cropSquare()
                    .compress(1024)         //Final image size will be less than 1 MB(Optional)
                    .createIntent { intent ->
                        startForProfileImageResult1.launch(intent)
                    }
            }
            img2.setOnClickListener {
                ImagePicker.with(this)
                    .cropSquare()
                    .compress(1024)         //Final image size will be less than 1 MB(Optional)
                    .createIntent { intent ->
                        startForProfileImageResult2.launch(intent)
                    }
            }
            img3.setOnClickListener {
                ImagePicker.with(this)
                    .cropSquare()
                    .compress(1024)         //Final image size will be less than 1 MB(Optional)
                    .createIntent { intent ->
                        startForProfileImageResult3.launch(intent)
                    }
            }
        }

        val adapterCategory =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, category)
        productCategory.setAdapter(adapterCategory)

        back.setOnClickListener { finish() }

        btnAddProduct.setOnClickListener {
            val name = productName.text.toString()
            val category = productCategory.text.toString()
            val quantity = productQuantity.text.toString()
            val price = productPrice.text.toString()
            val description = productDescription.text.toString()

            if (cek) {
                if (name.isEmpty() || category.isEmpty() || quantity.isEmpty() || price.isEmpty() || description.isEmpty()) {
                    Snackbar.make(
                        parentView,
                        "Lengkapi data", Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    var idCategory = 0
                    if (category == "Material bangunan alami") {
                        idCategory = 1
                    } else if (category == "Material bangunan pabrik") {
                        idCategory = 2
                    }

                    editProduct(token, name, idCategory, quantity, price, description, idProduct)
                }
            } else {
                if (name.isEmpty() || category.isEmpty() || quantity.isEmpty() || price.isEmpty() || description.isEmpty() || parts.isNullOrEmpty()) {
                    Snackbar.make(
                        parentView,
                        "Lengkapi data", Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    var idCategory = 0
                    if (category == "Material bangunan alami") {
                        idCategory = 1
                    } else if (category == "Material bangunan pabrik") {
                        idCategory = 2
                    }

                    uploadProduct(token, name, idCategory, quantity, price, description)
                }
            }

        }

    }

    private fun uploadProduct(
        token: String?,
        name: String,
        idCategory: Int,
        quantity: String,
        price: String,
        description: String
    ) {
        val partName: RequestBody =
            name.toRequestBody("text/plain".toMediaTypeOrNull())
        val partIdCategory: RequestBody =
            idCategory.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val partQuantity: RequestBody =
            quantity.toRequestBody("text/plain".toMediaTypeOrNull())
        val partPrice: RequestBody =
            price.toRequestBody("text/plain".toMediaTypeOrNull())
        val partDescription: RequestBody =
            description.toRequestBody("text/plain".toMediaTypeOrNull())

        ApiClient.instances.uploadProduct(
            "Bearer $token",
            partName,
            partIdCategory,
            partQuantity,
            partPrice,
            parts,
            partDescription
        )
            .enqueue(object : Callback<ProductModel> {
                override fun onResponse(
                    call: Call<ProductModel>,
                    response: Response<ProductModel>
                ) {
                    if (response.isSuccessful) {
                        Log.e(this.toString(), "success: " + response.body())
                        Snackbar.make(
                            parentView,
                            "Berhasil tambah produk", Snackbar.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Snackbar.make(
                            parentView,
                            "Gagal", Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                    Snackbar.make(
                        parentView,
                        "Gagal", Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun editProduct(
        token: String?,
        name: String,
        idCategory: Int,
        quantity: String,
        price: String,
        description: String,
        idProduct: String
    ) {

        ApiClient.instances.editProduct(
            "Bearer $token",
            name,
            idCategory,
            quantity.toInt(),
            price.toInt(),
            idProduct.toInt(), description
        )
            .enqueue(object : Callback<ProductModel> {
                override fun onResponse(
                    call: Call<ProductModel>,
                    response: Response<ProductModel>
                ) {
                    if (response.isSuccessful) {
                        Log.e(this.toString(), "success: " + response.body())

                        if (parts.isNullOrEmpty()) {
                            Snackbar.make(
                                parentView,
                                "Berhasil edit produk", Snackbar.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            addImage(token, idProduct)
                        }

                    } else {
                        Snackbar.make(
                            parentView,
                            "Gagal", Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                    Snackbar.make(
                        parentView,
                        "Gagal", Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
    }


    private val startForProfileImageResult1 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
//                    imageView.setImageURI(fileUri)

                val image: File = File(fileUri.path!!)
                img1.setImageBitmap(BitmapFactory.decodeFile(image.absolutePath))

                Log.e("image format: ", "uri = $fileUri")
                Log.e("image format: ", "file path = $image")
                Log.e("image format: ", "file absolute path = ${image.absolutePath}")

                reqBody = image.asRequestBody("image/*".toMediaTypeOrNull())

                val partImage: MultipartBody.Part =
                    MultipartBody.Part.createFormData("image[]", image.name, reqBody!!)

                parts.add(partImage)

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    private val startForProfileImageResult2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
//                    imageView.setImageURI(fileUri)

                val image: File = File(fileUri.path!!)
                img2.setImageBitmap(BitmapFactory.decodeFile(image.absolutePath))

                Log.e("image format: ", "uri = $fileUri")
                Log.e("image format: ", "file path = $image")
                Log.e("image format: ", "file absolute path = ${image.absolutePath}")

                reqBody = image.asRequestBody("image/*".toMediaTypeOrNull())

                val partImage: MultipartBody.Part =
                    MultipartBody.Part.createFormData("image[]", image.name, reqBody!!)

                parts.add(partImage)

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    private val startForProfileImageResult3 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
//                    imageView.setImageURI(fileUri)

                val image: File = File(fileUri.path!!)
                img3.setImageBitmap(BitmapFactory.decodeFile(image.absolutePath))

                Log.e("image format: ", "uri = $fileUri")
                Log.e("image format: ", "file path = $image")
                Log.e("image format: ", "file absolute path = ${image.absolutePath}")

                reqBody = image.asRequestBody("image/*".toMediaTypeOrNull())

                val partImage: MultipartBody.Part =
                    MultipartBody.Part.createFormData("image[]", image.name, reqBody!!)

                parts.add(partImage)

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private fun optionAlert(token: String, id: Int, imgInt: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Aksi")

        val options = arrayOf("Ganti gambar ini", "Hapus")
        builder.setItems(
            options
        ) { _, which ->
            when (which) {
                0 -> {
                    when (imgInt) {
                        1 -> {
                            ImagePicker.with(this)
                                .cropSquare()
                                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                                .createIntent { intent ->
                                    startForProfileImageResult1.launch(intent)
                                }
                            deleteImage(token, id, 1)
//                            Toast.makeText(this, id.toString(), Toast.LENGTH_SHORT).show()
                        }
                        2 -> {
                            ImagePicker.with(this)
                                .cropSquare()
                                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                                .createIntent { intent ->
                                    startForProfileImageResult2.launch(intent)
                                }
                            if (id != 0) {
                                deleteImage(token, id, 2)
                            }
                        }
                        3 -> {
                            ImagePicker.with(this)
                                .cropSquare()
                                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                                .createIntent { intent ->
                                    startForProfileImageResult3.launch(intent)
                                }
                            if (id != 0) {
                                deleteImage(token, id, 3)
                            }
                        }
                    }
                }
                1 -> {
                    if (id != 0) {
                        deleteAlert(token, id, imgInt)
                    }
                }
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteAlert(token: String, id: Int, imgInt: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Hapus")
        builder.setMessage("Hapus foto ini ?")

        builder.setPositiveButton("Ya") { _, _ ->
            deleteImage(token, id, imgInt)
        }

        builder.setNegativeButton("Tidak") { _, _ ->
            // cancel
        }
        builder.show()
    }

    private fun deleteImage(token: String?, id: Int, imgInt: Int) {
        ApiClient.instances.deleteImageProduct("Bearer $token", id)
            .enqueue(object : Callback<ProductModel> {
                override fun onResponse(
                    call: Call<ProductModel>,
                    response: Response<ProductModel>
                ) {
                    if (response.isSuccessful) {
                        Log.e(this.toString(), "success: " + response.body())
                        when (imgInt) {
                            1 -> {
                                img1.setImageResource(R.drawable.ic_plus)
                            }
                            2 -> {
                                img2.setImageResource(R.drawable.ic_plus)
                            }
                            3 -> {
                                img3.setImageResource(R.drawable.ic_plus)
                            }
                        }
                    } else {
                        Snackbar.make(
                            parentView,
                            "Gagal", Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                    Snackbar.make(
                        parentView,
                        "Gagal", Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun getImage(token: String, idProduct: String) {
        ApiClient.instances.imageProduct("Bearer $token", idProduct)
            .enqueue(object : Callback<SliderItem> {
                override fun onResponse(call: Call<SliderItem>, response: Response<SliderItem>) {
                    val image = response.body()?.image

                    if (response.isSuccessful) {
                        if (image?.isNotEmpty() == true) {

                            var position = 0
                            for (i in image) {
                                var linkImage =
                                    "${Constant.URL_IMAGE_PRODUCT}${image[position].image}"
                                if (position == 0) {
                                    img1.load(linkImage)
                                } else if (position == 1) {
                                    img2.load(linkImage)
                                } else if (position == 2) {
                                    img3.load(linkImage)
                                }
                                position += 1
                            }

                            img1.setOnClickListener {
//                                if (image.size == 1) {
                                if (image.size >= 0)
                                    optionAlert(token, image[0].id, 1)
//                                } else {
//                                    optionAlert(token, 0, 1)
//                                }
                            }
                            img2.setOnClickListener {
//                                if (image.size == 2) {
                                if (image.size >= 2) {
                                    optionAlert(token, image[1].id, 2)
                                } else {
                                    optionAlert(token, 0, 2)
                                }
                            }
                            img3.setOnClickListener {
//                                if (image.size == 3) {
                                if (image.size >= 3) {
                                    optionAlert(token, image[2].id, 3)
                                } else {
                                    optionAlert(token, 0, 3)
                                }
                            }


                        } else {
                            Snackbar.make(
                                parentView,
                                "Gogal memuat informasi", Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Snackbar.make(
                            parentView,
                            "Gogal memuat informasi", Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<SliderItem>, t: Throwable) {
                    Snackbar.make(
                        parentView,
                        "Gogal memuat informasi", Snackbar.LENGTH_SHORT
                    ).show()
                }

            })
    }

    private fun addImage(token: String?, idProduct: String) {

        val partId: RequestBody =
            idProduct.toRequestBody("text/plain".toMediaTypeOrNull())

        ApiClient.instances.addImageProduct("Bearer $token", partId, parts)
            .enqueue(object : Callback<ProductModel> {
                override fun onResponse(
                    call: Call<ProductModel>,
                    response: Response<ProductModel>
                ) {
                    if (response.isSuccessful) {
                        finish()
                        Snackbar.make(
                            parentView,
                            "Berhasil edit produk", Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        Snackbar.make(
                            parentView,
                            "gagal edit produk", Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                    Snackbar.make(
                        parentView,
                        "gagal edit produk : ${t.message}", Snackbar.LENGTH_SHORT
                    ).show()
                }

            })
    }
}

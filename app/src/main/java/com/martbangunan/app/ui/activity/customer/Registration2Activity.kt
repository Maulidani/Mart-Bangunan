package com.martbangunan.app.ui.activity.customer

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.martbangunan.app.R
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.*
import com.martbangunan.app.ui.activity.LoginActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class Registration2Activity : AppCompatActivity() {
    private val parentView: ConstraintLayout by lazy { findViewById(R.id.parentRegistration2) }

    private val btnRegistration: MaterialButton by lazy { findViewById(R.id.btnRegistration) }
    private val address: TextInputEditText by lazy { findViewById(R.id.inputAddress) }
    private val province: AutoCompleteTextView by lazy { findViewById(R.id.inputProvince) }
    private val city: AutoCompleteTextView by lazy { findViewById(R.id.inputCity) }
    private val districts: AutoCompleteTextView by lazy { findViewById(R.id.inputDistrincts) }

    private val imgView: ImageView by lazy { findViewById(R.id.imgProfie) }
    private val tvPilihFoto: TextView by lazy { findViewById(R.id.tvPilihFoto) }

    private var reqBody: RequestBody? = null
    private var partImage: MultipartBody.Part? = null

    private var itemProvince: MutableList<String> = ArrayList()
    private var itemCity: MutableList<String> = ArrayList()
    private var itemDistrict: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration2)

        getProvince()

        val fullName = intent.getStringExtra("fullname")
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")
        val phone = intent.getStringExtra("phone")

        tvPilihFoto.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        btnRegistration.setOnClickListener {
            if (address.text.toString().isEmpty() || province.text.toString()
                    .isEmpty() || city.text.toString().isEmpty() || districts.text.toString()
                    .isEmpty()
            ) {
                Snackbar.make(
                    parentView,
                    "Lengkapi data", Snackbar.LENGTH_SHORT
                ).show()
            } else if (partImage == null) {
                Snackbar.make(
                    parentView,
                    "Pilih gambar terlebih dahulu", Snackbar.LENGTH_SHORT
                ).show()
            } else {

                daftar(
                    email.toString(),
                    password.toString(),
                    address.text.toString(),
                    province.text.toString(),
                    city.text.toString(),
                    districts.text.toString(),
                    fullName.toString(),
                    phone.toString()
                )
            }
        }
    }

    private fun daftar(
        email: String,
        password: String,
        address: String,
        province: String,
        city: String,
        districts: String,
        fullName: String,
        phone: String
    ) {
        val type = "customer"
        val partEmail: RequestBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val partPassword: RequestBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
        val partAddress: RequestBody = address.toRequestBody("text/plain".toMediaTypeOrNull())
        val partProvince: RequestBody = province.toRequestBody("text/plain".toMediaTypeOrNull())
        val partCity: RequestBody = city.toRequestBody("text/plain".toMediaTypeOrNull())
        val partDistricts: RequestBody = districts.toRequestBody("text/plain".toMediaTypeOrNull())
        val partFullName: RequestBody = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
        val partPhone: RequestBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
        val partType: RequestBody = type.toRequestBody("text/plain".toMediaTypeOrNull())

        ApiClient.instances.registration(
            partEmail,
            partPassword,
            partAddress,
            partProvince,
            partCity,
            partDistricts,
            partFullName,
            partPhone,
            partImage!!,
            partType
        ).enqueue(object : Callback<RegisterModel> {
            override fun onResponse(call: Call<RegisterModel>, response: Response<RegisterModel>) {

                val message = response.body()?.message
                if (response.isSuccessful) {
                    if (message == "Success") {
                        Toast.makeText(
                            applicationContext,
                            message.toString() + " silahkan masuk",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(applicationContext, LoginActivity::class.java)
                                .putExtra("type", "customer")
                        )
                    } else {
                        Snackbar.make(
                            parentView,
                            "Gagal: periksa kembali data anda", Snackbar.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Snackbar.make(
                        parentView,
                        "Gagal: periksa kembali data anda", Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RegisterModel>, t: Throwable) {
                Snackbar.make(
                    parentView,
                    t.message.toString(), Snackbar.LENGTH_SHORT
                ).show()

                Log.e(this.toString(), "onFailure: " + t.message.toString())
            }

        })

    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
//                    imageView.setImageURI(fileUri)

                val image: File = File(fileUri.path!!)
                imgView.setImageBitmap(BitmapFactory.decodeFile(image.absolutePath))

                Log.e("image format: ", "uri = $fileUri")
                Log.e("image format: ", "file path = $image")
                Log.e("image format: ", "file absolute path = ${image.absolutePath}")

                reqBody = image.asRequestBody("image/*".toMediaTypeOrNull())

                partImage = MultipartBody.Part.createFormData("image", image.name, reqBody!!)

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private fun getProvince() {

        ApiClient.instanceLocation.getProvince().enqueue(object : Callback<LocationModel> {
            override fun onResponse(call: Call<LocationModel>, response: Response<LocationModel>) {

                if (response.isSuccessful) {
                    val provinsi = response.body()?.provinsi
                    if (provinsi != null) {
                        for (i in provinsi) {
                            itemProvince.add(i.nama)
                        }
                        val adapterList = ArrayAdapter(
                            applicationContext,
                            R.layout.support_simple_spinner_dropdown_item,
                            itemProvince
                        )
                        province.setAdapter(adapterList)

                        city.setOnClickListener {

                            if (!province.text.isNullOrEmpty()) {

                                var selectedProvince: ProvinsiList =
                                    provinsi.filter { s -> s.nama == province.text.toString() }
                                        .single()

                                getCity(selectedProvince.id.toString())
                            }

                        }
                    }

                } else {
                    Snackbar.make(
                        parentView,
                        "Gagal: periksa kembali koneksi anda", Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LocationModel>, t: Throwable) {
                Snackbar.make(
                    parentView,
                    t.message.toString(), Snackbar.LENGTH_SHORT
                ).show()

                Log.e(this.toString(), "onFailure: " + t.message.toString())
            }

        })
    }

    private fun getCity(idCity: String) {

        ApiClient.instanceLocation.getCity(idCity).enqueue(object : Callback<LocationModel> {
            override fun onResponse(call: Call<LocationModel>, response: Response<LocationModel>) {

                if (response.isSuccessful) {
                    itemCity.clear()
                    val kota = response.body()?.kota_kabupaten
                    if (kota != null) {
                        for (i in kota) {
                            itemCity.add(i.nama)
                        }
                        val adapterList = ArrayAdapter(
                            applicationContext,
                            R.layout.support_simple_spinner_dropdown_item,
                            itemCity
                        )
                        city.setAdapter(adapterList)

                        districts.setOnClickListener {

                            if (!city.text.isNullOrEmpty()) {
                                var selectedDistrict: KotaList =
                                    kota.filter { s -> s.nama == city.text.toString() }.single()

                                getDistrict(selectedDistrict.id.toString())

                            }
                        }
                    }

                } else {
                    Snackbar.make(
                        parentView,
                        "Gagal: periksa kembali koneksi anda", Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LocationModel>, t: Throwable) {
                Snackbar.make(
                    parentView,
                    t.message.toString(), Snackbar.LENGTH_SHORT
                ).show()

                Log.e(this.toString(), "onFailure: " + t.message.toString())
            }

        })
    }

    private fun getDistrict(idDistrict: String) {

        ApiClient.instanceLocation.getDistrict(idDistrict).enqueue(object :
            Callback<LocationModel> {
            override fun onResponse(call: Call<LocationModel>, response: Response<LocationModel>) {

                if (response.isSuccessful) {
                    itemDistrict.clear()
                    val kecamatan = response.body()?.kecamatan
                    if (kecamatan != null) {
                        for (i in kecamatan) {
                            itemDistrict.add(i.nama)
                        }
                        val adapterList = ArrayAdapter(
                            applicationContext,
                            R.layout.support_simple_spinner_dropdown_item,
                            itemDistrict
                        )
                        districts.setAdapter(adapterList)
                    }

                } else {
                    Snackbar.make(
                        parentView,
                        "Gagal: periksa kembali koneksi anda", Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LocationModel>, t: Throwable) {
                Snackbar.make(
                    parentView,
                    t.message.toString(), Snackbar.LENGTH_SHORT
                ).show()

                Log.e(this.toString(), "onFailure: " + t.message.toString())
            }

        })
    }
}
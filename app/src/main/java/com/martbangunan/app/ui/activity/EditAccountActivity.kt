package com.martbangunan.app.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.martbangunan.app.R
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.RegisterModel
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

class EditAccountActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper

    private val parentView: ConstraintLayout by lazy { findViewById(R.id.parentEditAccount) }

    private val imgView: ImageView by lazy { findViewById(R.id.imgProfie) }
    private val back: ImageView by lazy { findViewById(R.id.imgBack) }

    private val textFielFullname: TextInputLayout by lazy { findViewById(R.id.fullNameTextField) }

    private val fullName: TextInputEditText by lazy { findViewById(R.id.inputFullName) }
    private val email: TextInputEditText by lazy { findViewById(R.id.inputEmail) }
    private val phone: TextInputEditText by lazy { findViewById(R.id.inputPhone) }
    private val address: TextInputEditText by lazy { findViewById(R.id.inputAddress) }
    private val province: TextInputEditText by lazy { findViewById(R.id.inputProvince) }
    private val city: TextInputEditText by lazy { findViewById(R.id.inputCity) }
    private val districts: TextInputEditText by lazy { findViewById(R.id.inputDistrincts) }

    private val btnRegistration: MaterialButton by lazy { findViewById(R.id.btnRegistration) }

    private var reqBody: RequestBody? = null
    private var partImage: MultipartBody.Part? = null

   private lateinit var userIDIntent:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)
        sharedPref = PreferencesHelper(this)
        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

        back.setOnClickListener { finish() }

        val emailIntent = intent.getStringExtra("email")
        val addressIntent = intent.getStringExtra("address")
        val provinceIntent = intent.getStringExtra("province")
        val cityIntent = intent.getStringExtra("city")
        val districtIntent = intent.getStringExtra("districts")
        val nameIntent = intent.getStringExtra("name")
        val phoneIntent = intent.getStringExtra("phone")
        val imageIntent = intent.getStringExtra("image")
        val typeIntent = intent.getStringExtra("type")
        userIDIntent = intent.getStringExtra("user_id").toString()
        val addressIdIntent = intent.getIntExtra("address_id",0)

        imgView.load(Constant.URL_IMAGE_USER + imageIntent)

        if (typeIntent == "seller") {
            textFielFullname.hint = "Nama Toko"
        }

        fullName.setText(nameIntent)
        email.setText(emailIntent)
        phone.setText(phoneIntent.toString())
        address.setText(addressIntent)
        province.setText(provinceIntent)
        city.setText(cityIntent)
        districts.setText(districtIntent)

        imgView.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        btnRegistration.setOnClickListener {
            if (fullName.toString().isEmpty() || email.toString().isEmpty() || phone.toString()
                    .isEmpty() || address.toString().isEmpty() || province.toString()
                    .isEmpty() || city.toString().isEmpty() || districts.toString().isEmpty()
            ) {
                Snackbar.make(
                    parentView,
                    "Lengkapi data", Snackbar.LENGTH_SHORT
                ).show()
            } else {
                editAccount(
                    token!!,
                    fullName,
                    email,
                    phone,
                    address,
                    province,
                    city,
                    districts,
                    typeIntent,
                    userIDIntent.toInt(),
                    addressIdIntent
                )
            }
        }
    }

    private fun editAccount(
        token:String,
        fullName: TextInputEditText,
        email: TextInputEditText,
        phone: TextInputEditText,
        address: TextInputEditText,
        province: TextInputEditText,
        city: TextInputEditText,
        districts: TextInputEditText,
        typeIntent: String?,
        userIDIntent: Int?,
        addressIdIntent: Int?
    ) {
        Log.e( "editAccount: ",    email.text.toString()+
            address.text.toString()+
            province.text.toString()+
            city.text.toString()+
            districts.text.toString()+
            fullName.text.toString()+
            phone.text.toString().toInt()+
            typeIntent.toString()+
            userIDIntent!!.toInt()+
            addressIdIntent!!.toInt())

        ApiClient.instances.edit(
            "Bearer $token",
            email.text.toString(),
            address.text.toString(),
            province.text.toString(),
            city.text.toString(),
            districts.text.toString(),
            fullName.text.toString(),
            phone.text.toString().toInt(),
            typeIntent.toString(),
            userIDIntent!!.toInt(),
            addressIdIntent!!.toInt()
        ).enqueue(object : Callback<RegisterModel> {
            override fun onResponse(
                call: Call<RegisterModel>,
                response: Response<RegisterModel>
            ) {
                val message = response.body()?.message
                if (response.isSuccessful) {
                    if (message == "Success") {
                       finish()
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

                val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

                val partUserId: RequestBody =
                    userIDIntent.toRequestBody("text/plain".toMediaTypeOrNull())

                Log.e("editImage: ", userIDIntent)
                ApiClient.instances.editImage("Bearer $token", partImage!!, partUserId).enqueue(object : Callback<RegisterModel> {
                    override fun onResponse(
                        call: Call<RegisterModel>,
                        response: Response<RegisterModel>
                    ) {

                        val message = response.body()?.message
                        if (response.isSuccessful) {
                            if (message == "Success") {

                            } else {
                                Snackbar.make(
                                    parentView,
                                    "Gagal: ganti foto", Snackbar.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Snackbar.make(
                                parentView,
                                "Gagal: ganti foto", Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<RegisterModel>, t: Throwable) {
                        Snackbar.make(
                            parentView,
                            "Gagal: ganti foto", Snackbar.LENGTH_SHORT
                        ).show()

                        Log.e(this.toString(), "onFailure: " + t.message.toString())
                    }
                })
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
}


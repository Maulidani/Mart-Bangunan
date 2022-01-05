package com.martbangunan.app.network.model

data class LocationModel(
    val provinsi: ArrayList<ProvinsiList>,
    val kota_kabupaten: ArrayList<KotaList>,
    val kecamatan: ArrayList<KecList>
)

data class ProvinsiList(
    val id: Int,
    val nama: String,
)

data class KotaList(
    val id: Int,
    val id_provinsi: String,
    val nama: String
)

data class KecList(
    val id: Int,
    val id_kota: String,
    val nama: String,
)
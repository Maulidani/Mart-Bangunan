package com.martbangunan.app.network.model

data class LocationModel(
    val provinsi: List<ProvinsiList>,
    val kota_kabupaten: List<KotaList>,
    val kecematan: List<KecList>
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
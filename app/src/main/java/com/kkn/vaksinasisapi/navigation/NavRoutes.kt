package com.kkn.vaksinasisapi.navigation

sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login")
    object RoleSelection : NavRoutes("role_selection")
    object DashboardPeternak : NavRoutes("dashboard_peternak")
    object DashboardDokter : NavRoutes("dashboard_dokter")

    // Rute persiapan untuk fitur selanjutnya
    object TambahSapi : NavRoutes("tambah_sapi")
}
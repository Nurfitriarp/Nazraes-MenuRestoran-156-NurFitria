package com.nurfitria.resmenu.model

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class MenuItem(
    val id: Long,
    val name: String,
    val price: String,
    val description: String,
    val imageUrl: String,
    val category: String
)

object MenuRepository {
    private const val MENU_KEY = "restaurant_menu_data"
    private val gson = Gson()

    private val defaultMenu = listOf(
        MenuItem(
            1,
            "Nasi Goreng Nusantara",
            "Rp 45.000",
            "Nasi goreng premium yang dimasak menggunakan beras pilihan dengan bumbu rempah otentik khas Nusantara. Menyajikan aroma smoky wajan yang menggugah selera.\n\nHidangan mewah ini disajikan sangat lengkap, mulai dari suwiran daging ayam yang gurih, dua tusuk sate ayam bumbu kacang tradisional, telur mata sapi dengan tingkat kematangan sempurna, serta kerupuk udang renyah sebagai pelengkap tekstur. Ditambah dengan acar segar untuk penyeimbang rasa.\n\nPilihan tepat bagi Anda yang menginginkan cita rasa lokal legendaris dengan porsi yang sangat mengenyangkan dan penyajian standar bintang lima.",
            "https://images.unsplash.com/photo-1626804475315-76940df1f3be?w=500&q=80",
            "Makanan"
        ),
        MenuItem(
            2,
            "Ribeye Steak",
            "Rp 125.000",
            "Potongan daging sapi impor premium bagian ribeye seberat 200 gram yang dipanggang dengan teknik khusus untuk menjaga kelembutan serat dan kejuisian alami daging.\n\nDisajikan di atas piring hangat bersama mashed potato buatan sendiri yang bertekstur sangat lembut, gurih, dan creamy berkat campuran mentega berkualitas tinggi. Hidangan ini disempurnakan dengan siraman saus jamur kental yang kaya rasa serta tumisan sayuran segar sebagai pendamping.\n\nSangat direkomendasikan bagi para pencinta daging yang mendambakan pengalaman bersantap steak barat otentik dengan kombinasi rasa yang harmonis dan tekstur daging yang lumer di mulut.",
            "https://images.unsplash.com/photo-1544025162-d76694265947?w=500&q=80",
            "Makanan"
        ),
        MenuItem(
            3,
            "Ayam Bakar Taliwang",
            "Rp 55.000",
            "Ayam bakar tradisional khas Lombok yang diolah menggunakan ayam muda pilihan agar dagingnya tetap empuk. Dibalut dengan bumbu cabai dan rempah Taliwang yang terkenal pedas, berani, dan meresap hingga ke tulang.\n\nProses pembakaran di atas arang memberikan lapisan aroma smoky yang khas pada kulit ayam yang karamelisasi. Hidangan ini disajikan lengkap bersama sepiring nasi putih hangat yang pulen dan plecing kangkung segar yang disiram sambal tomat-terasi segar berhasiat jeruk limau.\n\nMenu ini menyajikan perpaduan rasa pedas, gurih, dan asam segar yang eksotis. Sangat cocok bagi pencinta kuliner pedas Nusantara yang merindukan masakan otentik Bali dan Lombok.",
            "https://images.unsplash.com/photo-1532550907401-a500c9a57435?w=500&q=80",
            "Makanan"
        ),
        MenuItem(
            4,
            "Matcha Lava Cake",
            "Rp 35.000",
            "Kue cokelat premium dengan sentuhan modern yang memadukan kelembutan bolu cokelat manis dengan lelehan lava bubuk matcha (teh hijau Jepang) asli berkualitas tinggi di bagian dalamnya.\n\nSaat kue dipotong, lelehan lava matcha yang hangat, sedikit pahit, dan creamy akan mengalir keluar secara dramatis. Kontras rasa ini diseimbangkan dengan sempurna oleh kehadiran satu scoop besar es krim vanilla yang dingin, lembut, dan manis di sampingnya.\n\nHidangan penutup (*dessert*) mewah ini sangat disukai karena memberikan kombinasi tekstur hangat-dingin serta perpaduan rasa cokelat dan matcha yang elegan dalam setiap suapan.",
            "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500&q=80",
            "Makanan"
        ),
        MenuItem(
            5,
            "Panna Cotta Strawberry",
            "Rp 30.000",
            "Puding susu tradisional khas Italia yang dibuat menggunakan krim segar dan susu berkualitas tinggi, menghasilkan tekstur puding yang sangat lembut, halus, dan bergoyang saat disentuh.\n\nBagian atas panna cotta disiram dengan saus buah beri strawberry segar buatan dapur kami sendiri yang menyajikan keseimbangan rasa manis dan asam alami yang kuat. Dihiasi dengan potongan buah strawberry segar sebagai sentuhan akhir yang mempercantik tampilan.\n\nDessert pencuci mulut yang ringan, dingin, dan menyegarkan ini sangat cocok dinikmati setelah menyantap hidangan utama yang berat untuk membersihkan langit-langit lidah Anda.",
            "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=500&q=80",
            "Makanan"
        ),
        MenuItem(
            6,
            "Cappuccino (Hot/Ice)",
            "Rp 28.000",
            "Minuman kopi klasik berbasis espresso yang diekstrak dari biji kopi house-blend Arabika dan Robusta pilihan. Diracik dengan keseimbangan sempurna antara espresso, susu panas (steamed milk), dan lapisan busa susu yang tebal (milk foam).\n\nBisa disajikan panas (*Hot*) dengan hiasan latte art yang cantik untuk menemani pagi Anda yang santai, atau disajikan dengan es batu kristal (*Ice*) untuk kesegaran maksimal di siang hari yang terik.\n\nMemiliki profil rasa kopi yang kuat namun tetap ramah di lambung berkat kelembutan busa susunya. Pendamping yang sangat pas untuk dipadukan dengan Matcha Lava Cake atau dinikmati saat sedang nongkrong.",
            "https://images.unsplash.com/photo-1534778101976-62847782c213?w=500&q=80",
            "Minuman"
        ),
        MenuItem(
            7,
            "Fresh Orange Juice",
            "Rp 22.000",
            "Minuman jus murni yang diperas langsung dari buah jeruk sunkist pilihan yang segar dan matang di pohon tanpa tambahan pemanis buatan maupun bahan pengawet.\n\nMenyajikan kandungan vitamin C yang tinggi dengan rasa manis dan asam buah alami yang sangat murni. Disajikan dingin dengan es batu kristal untuk memberikan sensasi kesegaran instan yang melegakan tenggorokan.\n\nMinuman sehat yang sangat direkomendasikan sebagai penetral rasa setelah Anda menikmati hidangan utama yang kaya bumbu seperti Ayam Bakar Taliwang atau Sate.",
            "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=500&q=80",
            "Minuman"
        )
    )

    fun getMenu(prefs: SharedPreferences): List<MenuItem> {
        val json = prefs.getString(MENU_KEY, null)
        return if (json == null) {
            saveMenu(prefs, defaultMenu)
            defaultMenu
        } else {
            val type = object : TypeToken<List<MenuItem>>() {}.type
            gson.fromJson(json, type)
        }
    }

    fun saveMenu(prefs: SharedPreferences, menu: List<MenuItem>) {
        val json = gson.toJson(menu)
        prefs.edit().putString(MENU_KEY, json).apply()
    }

    fun addMenuItem(prefs: SharedPreferences, item: MenuItem) {
        val currentMenu = getMenu(prefs).toMutableList()
        currentMenu.add(item)
        saveMenu(prefs, currentMenu)
    }

    fun deleteMenuItem(prefs: SharedPreferences, id: Long) {
        val currentMenu = getMenu(prefs).toMutableList()
        currentMenu.removeAll { it.id == id }
        saveMenu(prefs, currentMenu)
    }

    fun updateMenuItem(prefs: SharedPreferences, updatedItem: MenuItem) {
        val currentMenu = getMenu(prefs).toMutableList()
        val index = currentMenu.indexOfFirst { it.id == updatedItem.id }
        if (index != -1) {
            currentMenu[index] = updatedItem
            saveMenu(prefs, currentMenu)
        }
    }
}
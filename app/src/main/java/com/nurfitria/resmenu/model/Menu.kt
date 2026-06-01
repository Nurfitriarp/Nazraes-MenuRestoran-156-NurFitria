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
            "Nasi goreng bumbu rempah pilihan, disajikan lengkap dengan suwiran ayam, sate ayam bumbu kacang, telur mata sapi wangi, dan kerupuk renyah.",
            "https://images.unsplash.com/photo-1626804475315-76940df1f3be?w=500&q=80",
            "Makanan"
        ),
        MenuItem(
            2,
            "Ribeye Steak",
            "Rp 125.000",
            "Daging sapi impor premium (200gr) panggang yang juicy, disajikan dengan mashed potato lembut yang creamy serta siraman saus jamur yang kaya rasa.",
            "https://images.unsplash.com/photo-1544025162-d76694265947?w=500&q=80",
            "Makanan"
        ),
        MenuItem(
            3,
            "Ayam Bakar Taliwang",
            "Rp 55.000",
            "Ayam bakar pedas bumbu otentik khas Lombok dengan aroma smoky yang meresap, dihidangkan bersama nasi putih hangat dan plecing kangkung segar.",
            "https://images.unsplash.com/photo-1532550907401-a500c9a57435?w=500&q=80",
            "Makanan"
        ),
        MenuItem(
            4,
            "Matcha Lava Cake",
            "Rp 35.000",
            "Kue cokelat premium dengan lelehan lava matcha (teh hijau Jepang) yang lumer di dalam, disajikan sempurna dengan satu scoop es krim vanilla dingin.",
            "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500&q=80",
            "Makanan"
        ),
        MenuItem(
            5,
            "Panna Cotta Strawberry",
            "Rp 30.000",
            "Puding susu lembut khas Italia bertekstur halus, disiram dengan saus strawberry segar buatan sendiri yang memberikan perpaduan rasa manis dan asam alami.",
            "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=500&q=80",
            "Makanan"
        ),
        MenuItem(
            6,
            "Cappuccino (Hot/Ice)",
            "Rp 28.000",
            "Perpaduan espresso house-blend premium dengan steamed milk lembut dan lapisan milk foam tebal, memberikan cita rasa kopi klasik yang seimbang.",
            "https://images.unsplash.com/photo-1534778101976-62847782c213?w=500&q=80",
            "Minuman"
        ),
        MenuItem(
            7,
            "Fresh Orange Juice",
            "Rp 22.000",
            "Jus murni dari perasan jeruk sunkist segar pilihan yang kaya vitamin C, disajikan dingin tanpa pemanis buatan untuk kesegaran alami yang maksimal.",
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
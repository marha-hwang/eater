package com.example.myapplication.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Item(val icon: String, val firstName: String, val lastName: String)

class HomeViewModel : ViewModel() {
    val itemsListData = MutableLiveData<ArrayList<Item>>()
    val items = ArrayList<Item>()

    init {
        items.add(Item("person", "Yuh-jung", "Youn"))
        items.add(Item("person", "Steven", "Yeun"))
        items.add(Item("person", "Alan", "Kim"))
        items.add(Item("person outline", "Ye-ri", "Han"))
        items.add(Item("person outline", "Noel", "Cho"))
        items.add(Item("person pin", "Lee Issac", "Chung"))
        items.add(Item("person", "Yuh-jung", "Youn"))
        items.add(Item("person", "Steven", "Yeun"))
        items.add(Item("person", "Alan", "Kim"))
        items.add(Item("person outline", "Ye-ri", "Han"))
        items.add(Item("person outline", "Noel", "Cho"))
        items.add(Item("person pin", "Lee Issac", "Chung"))
        itemsListData.value = items
    }
}
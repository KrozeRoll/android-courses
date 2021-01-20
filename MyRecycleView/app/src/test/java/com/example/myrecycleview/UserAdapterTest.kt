package com.example.myrecycleview

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class UserAdapterTest {
    private val list1 = mutableListOf(Contact("a", "1"))
    private val list2 = mutableListOf(
        Contact("a", "1"),
        Contact("b", "2")
    )
    private val list3 = mutableListOf(
        Contact("a", "1"),
        Contact("b", "2"),
        Contact("aa", "3")
    )

    private lateinit var userAdapter : UserAdapter

    private fun getItemCountTest(list: MutableList<Contact>, result : Int) {
        userAdapter = UserAdapter(list) {}
        assertEquals(result, userAdapter.itemCount)
    }

    private fun filterAndTest(text: String, list: MutableList<Contact>, resultList: List<Contact>) {
        val newList = list.toMutableList()
        userAdapter = UserAdapter(newList) {}
        userAdapter.filter(text)
        assertEquals(resultList, userAdapter.contacts)
    }

    @Test
    fun testFilter() {
        filterAndTest("a", list1, mutableListOf(Contact("a", "1")))
        filterAndTest("b", list1, mutableListOf())

        filterAndTest("b", list2, mutableListOf(Contact("b", "2")))
        filterAndTest("a", list2, mutableListOf(Contact("a", "1")))
        filterAndTest("2", list2, mutableListOf(Contact("b", "2")))
        filterAndTest("1", list2, mutableListOf(Contact("a", "1")))
        filterAndTest("A", list2, mutableListOf(Contact("a", "1")))
        filterAndTest("B", list2, mutableListOf(Contact("b", "2")))

        filterAndTest("a", list3, mutableListOf(
            Contact("a", "1"),
            Contact("aa", "3")))
    }

    @Test
    fun getItemCount() {
        getItemCountTest(mutableListOf(), 0)

        getItemCountTest(list1, 1)
        getItemCountTest(list2, 2)
        getItemCountTest(list3, 3)
    }

}
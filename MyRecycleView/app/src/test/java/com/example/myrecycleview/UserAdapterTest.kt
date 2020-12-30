package com.example.myrecycleview

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UserAdapterTest {
    val list1 : List<Contact> = listOf(Contact("a", "1"))
    val list2 = listOf(
        Contact("a", "1"),
        Contact("b", "2"))

    lateinit var userAdapter : UserAdapter

    @Before
    fun setUpList1() {
        userAdapter = UserAdapter(list1, {})
    }
    @Test
    fun getItemCount1() {
        assertEquals(1, userAdapter.itemCount)
    }


    fun setUpList2() {
        userAdapter = UserAdapter(list2, {})
    }
    @Test
    fun getItemCount2() {
        val userAdapter = UserAdapter(list2, {})
        assertEquals(2, userAdapter.itemCount)
    }

}
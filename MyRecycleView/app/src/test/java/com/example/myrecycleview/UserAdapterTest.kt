package com.example.myrecycleview

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UserAdapterTest {
    @Mock val list1 : List<Contact> = listOf(Contact("a", "1"))
    @Mock val list2 : List<Contact> = listOf(
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

    @Before
    fun setUpList2() {
        userAdapter = UserAdapter(list2, {})
    }
    @Test
    fun getItemCount2() {
        assertEquals(2, userAdapter.itemCount)
    }

}
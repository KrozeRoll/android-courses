package com.example.myrecycleview

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

class ContactTest {
    lateinit var contact : Contact

    @Before
    fun setUp() {
        contact = Contact("abc", "123")
    }

    @Test
    fun GetName() {
        assertEquals("abc", contact.name)
    }

    @Test
    fun GetPhone() {
        assertEquals("123", contact.phone)
    }
}

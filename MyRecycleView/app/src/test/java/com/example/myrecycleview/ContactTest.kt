package com.example.myrecycleview

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ContactTest {
    private lateinit var contact : Contact
    private lateinit var contact1 : Contact

    @Before
    fun setUp() {
        contact = Contact("abc", "123")
        contact1 = Contact("abcd", "45-12")
    }

    @Test
    fun getName() {
        assertEquals("abc", contact.name)
        assertEquals("abcd", contact1.name)
    }

    @Test
    fun getPhone() {
        assertEquals("123", contact.phone)
        assertEquals("45-12", contact1.phone)
    }
}

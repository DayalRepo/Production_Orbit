package com.orbitai.erp.core.model

import kotlin.test.Test
import kotlin.test.assertEquals

class UserTest {

    private fun user(name: String) = User(
        id = "u1",
        fullName = name,
        email = "u@example.com",
        role = UserRole.SiteEngineer,
    )

    @Test
    fun initialsUseFirstAndLastName() {
        assertEquals("VS", user("Vikram Shah").initials)
        assertEquals("AR", user("Ananya  Rao").initials)
        assertEquals("SK", user("Sneha Anand Kulkarni").initials)
    }

    @Test
    fun initialsFallBackForSingleAndBlankNames() {
        assertEquals("IM", user("Imran").initials)
        assertEquals("?", user("   ").initials)
    }
}

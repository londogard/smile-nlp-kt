package com.londogard

import org.junit.Assert.assertEquals
import org.junit.Test
// https://guides.gradle.org/building-kotlin-jvm-libraries/


class MyLibraryTest {
    @Test fun testTokenization() {
        assertEquals(listOf("hey", "there", "you", "are", "londogard"), "hey there you are londogard".words(filter=StopWordFilter.NONE))
        assertEquals(listOf("hey", "londogard"), "hey there you are londogard".words())
        // TODO add more test coverage
    }
}


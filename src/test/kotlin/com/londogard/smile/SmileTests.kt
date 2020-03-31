package com.londogard.smile

import com.londogard.smile.extensions.*
import org.junit.Assert.assertEquals
import org.junit.Test

class MyLibraryTest {
    @Test fun testTokenization() {
        assertEquals(listOf("hey", "there", "you", "are", "londogard"), "hey there you are londogard".words(filter= StopWordFilter.NONE))
        assertEquals(listOf("hey", "londogard"), "hey there you are londogard".words())
        assertEquals(listOf("Hej där!", "Du är en människa."), "Hej där! Du är en människa.".sentences())
    }
}


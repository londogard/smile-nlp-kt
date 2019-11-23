package com.londogard

import com.londogard.smilenlpkt.extensions.StopWordFilter
import com.londogard.smilenlpkt.extensions.words
import org.junit.Assert.assertEquals
import org.junit.Test

class MyLibraryTest {
    @Test fun testTokenization() {
        assertEquals(listOf("hey", "there", "you", "are", "londogard"), "hey there you are londogard".words(filter= StopWordFilter.NONE))
        assertEquals(listOf("hey", "londogard"), "hey there you are londogard".words())
        // TODO add more test coverage
    }
}


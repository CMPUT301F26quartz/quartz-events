package com.example.quartzevents

// Simple local tests for entrant data, dates, and campsite search.

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EntrantTest {
    private lateinit var entrant: Entrant
    private lateinit var pine: CampsiteInfo
    private lateinit var cedar: CampsiteInfo
    private lateinit var campsites: List<CampsiteInfo>

    @Before
    fun setUp() {
        entrant = Entrant("alex@example.com", "Alex Chen", "camping123")
        pine = CampsiteInfo("pine", "Pine Ridge Campground", "Hinton, Alberta", maxOccupancy = 8)
        cedar = CampsiteInfo("cedar", "Cedar Creek Campsite", "Edmonton, Alberta", maxOccupancy = 3)
        campsites = listOf(pine, cedar)
    }

    @Test
    fun userType_newEntrant_returnsEntrant() {
        assertEquals("entrant", entrant.userType())
    }

    @Test
    fun notificationsOn_newEntrant_isTrue() {
        assertTrue(entrant.notificationsOn)
    }

    @Test
    fun isValidDate_validLeapDay_returnsTrue() {
        val date = Date("2028-02-29")

        assertTrue(date.isValidDate())
    }

    @Test
    fun isValidDate_invalidLeapDay_returnsFalse() {
        val date = Date("2027-02-29")

        assertFalse(date.isValidDate())
    }

    @Test
    fun isBefore_earlierDate_returnsTrue() {
        val checkIn = Date("2027-07-10")
        val checkOut = Date("2027-07-12")

        assertTrue(checkIn.isBefore(checkOut))
    }

    @Test
    fun filterCampsites_nameHasCapitalLettersAndSpaces_returnsMatchingCampsite() {
        val results = filterCampsites(campsites, "  PINE  ", CampsiteFilters())

        assertEquals(listOf(pine), results)
    }

    @Test
    fun filterCampsites_matchingLocation_returnsMatchingCampsite() {
        val filters = CampsiteFilters(location = "edmonton")

        val results = filterCampsites(campsites, "", filters)

        assertEquals(listOf(cedar), results)
    }

    @Test
    fun filterCampsites_emptySearch_returnsAllCampsites() {
        val results = filterCampsites(campsites, "", CampsiteFilters())

        assertEquals(listOf(cedar, pine), results)
    }

    @Test
    fun filterCampsites_unknownName_returnsNoCampsites() {
        val results = filterCampsites(campsites, "Mountain Meadow", CampsiteFilters())

        assertTrue(results.isEmpty())
    }

    @Test
    fun filterCampsites_guestsEqualCapacity_returnsCampsite() {
        val filters = CampsiteFilters(guests = 8)

        val results = filterCampsites(campsites, "", filters)

        assertEquals(listOf(pine), results)
    }

    @Test
    fun filterCampsites_guestsExceedCapacity_returnsNoCampsites() {
        val filters = CampsiteFilters(guests = 9)

        val results = filterCampsites(campsites, "", filters)

        assertTrue(results.isEmpty())
    }

    @Test
    fun filterCampsites_combinedFilters_returnsMatchingCampsite() {
        val filters = CampsiteFilters(location = "Alberta", guests = 4)

        val results = filterCampsites(campsites, "pine", filters)

        assertEquals(listOf(pine), results)
    }

    @Test
    fun filterCampsites_nameMatchesButLocationDoesNot_returnsNoCampsites() {
        val filters = CampsiteFilters(location = "Edmonton")

        val results = filterCampsites(campsites, "pine", filters)

        assertTrue(results.isEmpty())
    }

    @Test
    fun filterCampsites_emptyList_returnsNoCampsites() {
        val results = filterCampsites(emptyList(), "", CampsiteFilters())

        assertTrue(results.isEmpty())
    }

    @Test
    fun readCampsite_savedFields_returnsCampsiteInformation() {
        val fields = mapOf(
            "name" to "Pine Ridge",
            "location" to "Alberta",
            "capacity" to 8L,
            "rating" to 4.5,
            "price" to 25.0,
            "tags" to listOf("Lake", "Hiking"),
            "availableStart" to "2027-07-01",
            "availableEnd" to "2027-07-31",
            "imageUrl" to "https://example.com/pine.jpg"
        )

        val campsite = readCampsite("pine", fields)

        assertEquals("pine", campsite.id)
        assertEquals("Pine Ridge", campsite.name)
        assertEquals("Alberta", campsite.location)
        assertEquals(8, campsite.maxOccupancy)
        assertEquals(4.5, campsite.rating!!, 0.0)
        assertEquals(25.0, campsite.price!!, 0.0)
        assertEquals("Lake · Hiking", campsite.tags)
        assertEquals("2027-07-01", campsite.availableStart)
        assertEquals("2027-07-31", campsite.availableEnd)
        assertEquals("https://example.com/pine.jpg", campsite.imageUrl)
    }

    @Test
    fun readCampsite_capacity_takesPriorityOverOldField() {
        val campsite = readCampsite("pine", mapOf("capacity" to 8L, "maxOccupancy" to 4L))

        assertEquals(8, campsite.maxOccupancy)
    }

    @Test
    fun readCampsite_oldCapacityField_isSupported() {
        val campsite = readCampsite("pine", mapOf("maxOccupancy" to 4L))

        assertEquals(4, campsite.maxOccupancy)
    }

    @Test
    fun filterCampsites_stayWithinAvailableDates_returnsCampsite() {
        val site = pine.copy(availableStart = "2027-07-01", availableEnd = "2027-07-31")
        val filters = CampsiteFilters(location = "Alberta", checkIn = "2027-07-01", checkOut = "2027-07-31", guests = 4)

        val results = filterCampsites(listOf(site), "Pine", filters)

        assertEquals(listOf(site), results)
    }

    @Test
    fun filterCampsites_stayOutsideAvailableDates_returnsNoCampsites() {
        val site = pine.copy(availableStart = "2027-07-10", availableEnd = "2027-07-20")

        val earlyStay = filterCampsites(listOf(site), "", CampsiteFilters(checkIn = "2027-07-09", checkOut = "2027-07-12"))
        val lateStay = filterCampsites(listOf(site), "", CampsiteFilters(checkIn = "2027-07-19", checkOut = "2027-07-21"))

        assertTrue(earlyStay.isEmpty())
        assertTrue(lateStay.isEmpty())
    }

    @Test
    fun filterCampsites_missingAvailableDates_returnsNoCampsitesForSelectedStay() {
        val filters = CampsiteFilters(checkIn = "2027-07-01", checkOut = "2027-07-03")

        val results = filterCampsites(campsites, "", filters)

        assertTrue(results.isEmpty())
    }

    @Test
    fun validStayDates_emptyDates_returnsTrue() {
        assertTrue(validStayDates("", "", "2027-07-01"))
    }

    @Test
    fun validStayDates_validRange_returnsTrue() {
        assertTrue(validStayDates("2027-07-01", "2027-07-03", "2027-07-01"))
    }

    @Test
    fun validStayDates_invalidCalendarDate_returnsFalse() {
        assertFalse(validStayDates("2027-02-30", "2027-03-03", "2027-02-01"))
    }

    @Test
    fun validStayDates_checkOutBeforeCheckIn_returnsFalse() {
        assertFalse(validStayDates("2027-07-03", "2027-07-01", "2027-07-01"))
    }

    @Test
    fun validStayDates_onlyOneDate_returnsFalse() {
        assertFalse(validStayDates("2027-07-01", "", "2027-07-01"))
    }

    @Test
    fun registrationIsOpen_boundaryDates_areIncluded() {
        assertTrue(registrationIsOpen("2027-07-01", "2027-07-03", "2027-07-01"))
        assertTrue(registrationIsOpen("2027-07-01", "2027-07-03", "2027-07-03"))
        assertFalse(registrationIsOpen("2027-07-01", "2027-07-03", "2027-07-04"))
    }
}

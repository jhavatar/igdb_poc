package io.chthonic.igdb.poc.utils

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created by jhavatar on 9/11/2018.
 */
@RunWith(RobolectricTestRunner::class)
//@Config(constants = BuildConfig::class)
class PagingUtilsTest {
    @Before
    fun before() {
        ShadowLog.stream = System.out
    }

    @Test
    fun testShouldShowLoading() {
        // forceDisplayLoadingis true
        assertTrue(PagingUtils.shouldShowLoading(true, 0, 0, true))
        assertTrue(PagingUtils.shouldShowLoading(true, 0, 0, false))
        assertTrue(PagingUtils.shouldShowLoading(true, 1, 0, true))
        assertTrue(PagingUtils.shouldShowLoading(true, 1, 0, false))

        // forceDisplayLoading is false
        assertFalse(PagingUtils.shouldShowLoading(false, 0, 0, true))
        assertFalse(PagingUtils.shouldShowLoading(false, 0, 0, false))
        assertFalse(PagingUtils.shouldShowLoading(false, 1, 0, true))
        assertFalse(PagingUtils.shouldShowLoading(false, 1, 0, false))

        // forceDisplayLoading is false is null
        assertTrue(PagingUtils.shouldShowLoading(null, 0, 0, true))
        assertFalse(PagingUtils.shouldShowLoading(null, 0, 0, false))
        assertTrue(PagingUtils.shouldShowLoading(null, 1, 0, true))
        assertTrue(PagingUtils.shouldShowLoading(null, 1, 0,false))
    }

    @Test
    fun testCanLoadMorePages() {

        // page is <= than firstPage && < lastPage
        assertTrue(PagingUtils.canLoadMorePages(-1, 0, 2, 3, 0))
        assertTrue(PagingUtils.canLoadMorePages(0, 0, 2, 3, 0))

        // page is >= lastPage
        assertFalse(PagingUtils.canLoadMorePages(-1, 0, -1, 3, 0))
        assertFalse(PagingUtils.canLoadMorePages(0, 0, -1, 3, 0))
        assertFalse(PagingUtils.canLoadMorePages(0, 0, 0, 3, 0))
        assertFalse(PagingUtils.canLoadMorePages(1, 0, 0, 3, 0))
        assertFalse(PagingUtils.canLoadMorePages(1, 0, 1, 3, 0))
        assertFalse(PagingUtils.canLoadMorePages(2, 0, 1, 3, 0))
        assertFalse(PagingUtils.canLoadMorePages(2, 0, 2, 3,3))

        // typical initial state
        assertTrue(PagingUtils.canLoadMorePages(0, 0, 2, 3, 0))

        // page < lastPage is not complete
        assertFalse(PagingUtils.canLoadMorePages(1, 0, 2, 3, 0))
        assertFalse(PagingUtils.canLoadMorePages(1, 0, 2, 3, 1))
        assertFalse(PagingUtils.canLoadMorePages(1, 0, 2, 3, 2))

        // page < lastPage and is complete
        assertTrue(PagingUtils.canLoadMorePages(1, 0, 2, 3, 3))
    }
}
package io.chthonic.igdb.poc.utils

/**
 * Created by jhavatar on 9/11/2018.
 */
object PagingUtils {

    fun shouldShowLoading(forceDisplayLoading: Boolean?,
                          page: Int,
                          firstPage: Int,
                          resultsAreEmpty: Boolean): Boolean {
        return forceDisplayLoading ?: ((page > firstPage) || ((page == firstPage) && resultsAreEmpty))
    }

    fun canLoadMorePages(page: Int, firstPage: Int, lastPage: Int, pageSize: Int, lastPageFetchedSize: Int): Boolean {
        return (page < lastPage) && // stop at lastPage
                ((page <= firstPage) || (lastPageFetchedSize >= pageSize)) // if not first page then the last page should have had full results
    }
}
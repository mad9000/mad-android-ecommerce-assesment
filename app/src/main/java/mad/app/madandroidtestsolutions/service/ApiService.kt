package mad.app.madandroidtestsolutions.service

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import mad.app.madandroidtestsolutions.service.catalog.CatalogApiService
import mad.app.madandroidtestsolutions.service.catalog.ICatalogApiService
import okhttp3.OkHttpClient

class ApiService {

    fun getApolloClient(): ApolloClient {
        val okHttpClient = OkHttpClient.Builder().build()
        return ApolloClient.Builder()
            .addHttpHeader("store", "app_en_za")
            .serverUrl("https://apiprd.omni.mrpg.com/graphql")
            .okHttpClient(okHttpClient)
            .build()
    }
}
package mad.app.madandroidtestsolutions.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import mad.app.madandroidtestsolutions.service.ApiService
import mad.app.madandroidtestsolutions.service.catalog.CatalogApiService
import mad.app.madandroidtestsolutions.service.catalog.ICatalogApiService
import javax.inject.Singleton


@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {

    @Binds
    @ViewModelScoped
   abstract fun bindApiService(catalogApiService: CatalogApiService) : ICatalogApiService

}
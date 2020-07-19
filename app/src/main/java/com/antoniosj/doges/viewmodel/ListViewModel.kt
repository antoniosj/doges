package com.antoniosj.doges.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antoniosj.doges.model.DogBreed
import com.antoniosj.doges.model.DogDatabase
import com.antoniosj.doges.model.DogsApiService
import com.antoniosj.doges.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class ListViewModel(private val app: Application): ViewModel() {

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    private val disposable = CompositeDisposable()
    private val dogsService = DogsApiService()

    private var prefHelper = SharedPreferencesHelper(app)
    private var refreshTime =  5 * 60 * 1000 * 1000 * 1000L // 5min -- nanoTime

    fun refresh() {
        val updateTime = prefHelper.getUpdateTime()
        if (shouldFetchFromDB(updateTime)) {
            fetchFromDataBase()
        } else {
            fetchFromRemote()
        }
    }

    private fun fetchFromDataBase() {
        loading.value = true
        viewModelScope.launch {
            val dogs = DogDatabase(app).dogDao().getAllDogs()
            dogsRetrieved(dogs)
            Toast.makeText(app, "Dogs retrieve from db", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shouldFetchFromDB(time: Long?): Boolean =
        time != null && time != 0L && System.nanoTime() - time < refreshTime

    private fun fetchFromRemote() {
        loading.value = true
        disposable.add(
            dogsService.getDogs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<List<DogBreed>>(){
                    override fun onSuccess(dogList: List<DogBreed>) {
                        viewModelScope.launch {
                            storeDogsLocally(dogList)
                            Toast.makeText(app, "Dogs retrieve from remote",
                                Toast.LENGTH_SHORT).show()
                        }

                    }

                    override fun onError(e: Throwable) {
                        dogsLoadError.value = true
                        loading.value = false
                        e.printStackTrace()
                    }

                })
        )
    }

    private fun dogsRetrieved(dogList: List<DogBreed>) {
        dogs.value = dogList
        dogsLoadError.value = false
        loading.value = false
    }

    private fun storeDogsLocally(dogList: List<DogBreed>) {
        viewModelScope.launch {
            val dao = DogDatabase(app.applicationContext).dogDao()
            dao.deleteAllDogs()
            val result = dao.insertAll(*dogList.toTypedArray()) // look at this later
            for (i in dogList.indices /* mesmo que 0..doglist.size - 1*/) {
                dogList[i].uuid = result[i].toInt()
            }
            dogsRetrieved(dogList)
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }

    fun refreshBypassCache() {
        fetchFromRemote()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.forager.ui.viewmodel

import androidx.lifecycle.*
import com.example.forager.data.ForageableDao
import com.example.forager.model.Forageable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// a ForageableDao value as a parameter to the view model constructor
class ForageableViewModel(
    private val fDao: ForageableDao
) : ViewModel() {

    // a property to set to a list of all forageables from the DAO
    private val _forageables: LiveData<List<Forageable>> = fDao.getAllForageables().asLiveData()
    val forageables get() = _forageables
    // method that takes id: Long as a parameter and retrieve a Forageable from the database by id via the DAO.

    fun getForageable(id: Long): LiveData<Forageable> {
        return fDao.getForageableById(id).asLiveData()
    }

    fun addForageable(
        name: String,
        address: String,
        inSeason: Boolean,
        notes: String
    ) {
        val forageable = Forageable(
            name = name,
            address = address,
            inSeason = inSeason,
            notes = notes
        )

        // a coroutine and call the DAO method to add a Forageable to the database within it
        viewModelScope.launch(Dispatchers.IO) {
            fDao.insertForageable(forageable)
        }

    }

    fun updateForageable(
        id: Long,
        name: String,
        address: String,
        inSeason: Boolean,
        notes: String
    ) {
        val forageable = Forageable(
            id = id,
            name = name,
            address = address,
            inSeason = inSeason,
            notes = notes
        )
        viewModelScope.launch(Dispatchers.IO) {
            fDao.updateForageable(forageable)
        }
    }

    fun deleteForageable(forageable: Forageable) {
        viewModelScope.launch(Dispatchers.IO) {
            fDao.deleteForageable(forageable)
        }
    }

    fun isValidEntry(name: String, address: String): Boolean {
        return name.isNotBlank() && address.isNotBlank()
    }
}

// a view model factory that takes a ForageableDao as a property and creates a ForageableViewModel
class ForageInventoryViewModelFactory(private val fDao: ForageableDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForageableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForageableViewModel(fDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.withContext

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var datasource: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

    private var shouldReturnError = false


    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }
    override suspend fun getReminders(): Result<List<ReminderDTO>>{
        if (shouldReturnError)        return Result.Error("shouldReturnError")
        datasource?.let { return Result.Success(it) }
        return Result.Error("Reminders not found")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        datasource?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        try {
            val reminder = datasource?.find {
                it.id==id
            }
            if (reminder != null) {
                return Result.Success(reminder)
            } else {
                return Result.Error("Reminder not found!")
            }
        } catch (e: Exception) {
            return Result.Error(e.localizedMessage)
        }
    }

    override suspend fun deleteAllReminders() {
        datasource?.clear()
    }


}
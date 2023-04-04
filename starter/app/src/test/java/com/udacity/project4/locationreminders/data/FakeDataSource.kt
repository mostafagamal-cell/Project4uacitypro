package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var datasource: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

    private var shouldReturnError = false


    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error(
                "Error getting reminders"
            )
        }
            if (datasource==null)return Result.Error(
                "Error List empty"
            )
        return Result.Success(datasource as List<ReminderDTO>)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        datasource?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (datasource==null||shouldReturnError)
        {
            return Result.Error("Reminder not found")
        }
        var reminderDTO=ReminderDTO("","","",1.0,2.0)
        var found=false
        for ( i in datasource!!)
        {
            if (i.id==id)
            {
                found=true
                reminderDTO=i
                 break
            }
        }
        if (found) return Result.Success(reminderDTO)
        return Result.Error("Reminder not found")
    }

    override suspend fun deleteAllReminders() {
        datasource?.clear()
    }


}
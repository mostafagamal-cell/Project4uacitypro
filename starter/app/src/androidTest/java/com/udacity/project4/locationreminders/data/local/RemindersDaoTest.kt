package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers

import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {


    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase
    private lateinit var Repo:RemindersLocalRepository

    @Before
    fun setup() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        Repo= RemindersLocalRepository(database.reminderDao(), Dispatchers.Unconfined)
    }
    val task = ReminderDTO("title", "description","",2.0,3.0,"id");
    @Test fun insertanddeletfindinemptylist() = runBlockingTest{
        database.reminderDao().saveReminder(task)

        // WHEN - Get the task by id from the database.
        val loaded2 = database.reminderDao().getReminderById(task.id)
        val loaded = database.reminderDao().getReminderById(task.id)

        // THEN - The loaded data contains the expected values.
        Assert.assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        Assert.assertThat(loaded.description, `is`(task.description))
        Assert.assertThat(loaded.latitude, `is`(task.latitude))
        Assert.assertThat(loaded.longitude, `is`(task.longitude))
        Assert.assertThat(loaded.id, `is`(task.id))
        Assert.assertThat(loaded.title, `is`(task.title))

        Repo.deleteAllReminders()
        val loaded3 = Repo.getReminders()
        assertThat(loaded3 is Result.Success, `is`(true))
        loaded3 as Result.Success
        assertThat(loaded3.data, `is` (emptyList()))
    }
    @After
    fun closeDb() = database.close()
    @Test fun insertReminderAndGetById() = runBlockingTest {
        val task = ReminderDTO("title", "description","",2.0,3.0,"id");
        database.reminderDao().saveReminder(task)

        // WHEN - Get the task by id from the database.
        val loaded = database.reminderDao().getReminderById(task.id)

        // THEN - The loaded data contains the expected values.
        Assert.assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        Assert.assertThat(loaded.id, `is`(task.id))
        Assert.assertThat(loaded.title, `is`(task.title))
        Assert.assertThat(loaded.description, `is`(task.description))
        Assert.assertThat(loaded.latitude, `is`(task.latitude))
        Assert.assertThat(loaded.longitude, `is`(task.longitude))
    }
    @Test fun findNotInDatabase() = runBlockingTest{
        val loaded = Repo.getReminder("sss")
        assertThat(loaded is Result.Error, `is`(true))
        loaded as Result.Error
        assertThat(loaded.message, `is`("Reminder not found!"))
    }
    @Test fun findinemptylist() = runBlockingTest{
        val loaded = Repo.getReminders()
        assertThat(loaded is Result.Success, `is`(true))
        loaded as Result.Success
        assertThat(loaded.data, `is` (emptyList()))
    }

}
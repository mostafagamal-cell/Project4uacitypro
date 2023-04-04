import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersLocalRepository: RemindersLocalRepository

    private lateinit var database: RemindersDatabase
    val reminder = ReminderDTO("home", "Home test", "cairo", 30.12, 31.48087)

    @Before
    fun setup() {

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        remindersLocalRepository = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Main
        )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun return_error_in_retrive() = runBlocking {
        remindersLocalRepository.saveReminder(reminder)
        remindersLocalRepository.deleteAllReminders()
        val result = remindersLocalRepository.getReminder(reminder.id)
        assertThat(result is Result.Error, `is`(true));result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }
    @Test
    fun save_and_get_by_id() = runBlocking {
        remindersLocalRepository.saveReminder(reminder)

        val list = remindersLocalRepository.getReminder(reminder.id) as? Result.Success

        assertThat(list is Result.Success, CoreMatchers.`is`(true)); list as Result.Success
        assertThat(list?.data?.title, CoreMatchers.`is`(reminder.title))
        assertThat(list?.data?.description, CoreMatchers.`is`(reminder.description))
        assertThat(list?.data?.latitude, CoreMatchers.`is`(reminder.latitude))
        assertThat(list?.data?.longitude, CoreMatchers.`is`(reminder.longitude))
        assertThat(list?.data?.location, CoreMatchers.`is`(reminder.location))
    }

    @Test
    fun delete_all()= runBlocking {
        remindersLocalRepository.saveReminder(reminder)

        remindersLocalRepository.deleteAllReminders()

        val list = remindersLocalRepository.getReminders()

        assertThat(list is Result.Success, `is`(true));list as Result.Success

        assertThat(list.data, `is`(emptyList()))
    }



}
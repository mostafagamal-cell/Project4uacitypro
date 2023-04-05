package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.RuleTestCustom
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = intArrayOf(Build.VERSION_CODES.P))

class RemindersListViewModelTest {
    @get:Rule
    var coroutineRule = RuleTestCustom()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private  var fakeDataSource: FakeDataSource?=null

    private  var viewModel: RemindersListViewModel?=null
    @Before
    fun setupTest()
    {
        val r1= ReminderDTO(null,null,null,null,null,"test1")
        val r2= ReminderDTO("samy",null,null,null,null,"test2")
        val r3= ReminderDTO("ahmed","for test","1234",33.0,222.0,"test3")
        fakeDataSource= FakeDataSource(mutableListOf<ReminderDTO>( r1,r2,r3))
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource!!)
    }
    @After
    fun tearDown() {
        viewModel=null
        fakeDataSource=null
        stopKoin()
    }
    @Test
    fun loadReminders()=coroutineRule.runBlockingTest{
        coroutineRule.pauseDispatcher()

        viewModel?.loadReminders()

        assertThat(viewModel?.showLoading?.getOrAwaitValue(),`is`(true))

        coroutineRule.resumeDispatcher()

        assertThat(viewModel?.showLoading?.getOrAwaitValue(),`is`(false))
    }
    @Test
    fun SnakeBarTest() {


        fakeDataSource?.setReturnError(true)

        viewModel?.loadReminders()


       assertThat(viewModel?.showSnackBar?.getOrAwaitValue(),`is`("Error getting reminders"))
    }
    @Test
    fun loademptylist()=coroutineRule.runBlockingTest{
        fakeDataSource!!.deleteAllReminders()
        fakeDataSource!!.getReminders()
        viewModel!!.loadReminders()
        assertThat(viewModel?.remindersList?.getOrAwaitValue(),`is`(emptyList()))

    }
    @Test
    fun loadnotemptylist()=coroutineRule.runBlockingTest{

        fakeDataSource!!.getReminders()
        viewModel!!.loadReminders()
        assertThat(viewModel?.remindersList?.getOrAwaitValue(), `not`(emptyList()))
    }
}
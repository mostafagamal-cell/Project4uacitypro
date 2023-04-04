package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth

import com.udacity.project4.R
import com.udacity.project4.locationreminders.RuleTestCustom
import com.udacity.project4.locationreminders.data.FakeDataSource

import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem


import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = intArrayOf(Build.VERSION_CODES.P))
class SaveReminderViewModelTest {


    @get:Rule
    var coroutineRule = RuleTestCustom()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private  var fakeDataSource: FakeDataSource?=null

    private  var viewModel: SaveReminderViewModel?=null
    @Before
    fun setupTest()
    {
        fakeDataSource= FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource!!)
    }
    @After
    fun tearDown() {
        viewModel=null
        fakeDataSource=null
        stopKoin()
    }
    @Test fun vailddatafalse()
    {
        val r = ReminderDataItem("", "descriptiontest", "locationtest", 1.32323, 1.54343)
        assertThat(viewModel?.validateEnteredData(r),`is`(false))
        Truth.assertThat(viewModel?.showSnackBarInt?.getOrAwaitValue()).isEqualTo(R.string.err_enter_title)
    }
    @Test fun vailddatatrue()
    {
        val r = ReminderDataItem("my name", "descriptiontest", "locationtest", 1.32323, 1.54343)
        assertThat(viewModel?.validateEnteredData(r),`is`(true))
    }
    @Test fun savetest()
    {
        val r = ReminderDataItem("test", "test", "test", 1.333, 2.5443433)
        coroutineRule.pauseDispatcher()
        viewModel?.saveReminder(r)
        assertThat(viewModel?.showLoading?.getOrAwaitValue(),`is`(true))
        coroutineRule.resumeDispatcher()
        assertThat(viewModel?.showLoading?.getOrAwaitValue(),`is`(false))
    }
    @Test fun errorinsave()
    {
        val r = ReminderDataItem("my name", "descriptiontest", "locationtest", null, null)
        assertThat(viewModel?.validateEnteredData(r),`is`(false))
        //error number
        Truth.assertThat(viewModel?.showSnackBarInt?.getOrAwaitValue()).isEqualTo(2131820602)
    }


}


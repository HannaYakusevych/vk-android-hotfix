package com.travels.searchtravels

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.api.services.vision.v1.model.LatLng
import com.preview.planner.prefs.AppPreferences
import com.travels.searchtravels.activity.MainActivity
import com.travels.searchtravels.api.OnVisionApiListener
import com.travels.searchtravels.api.VisionApi
import com.travels.searchtravels.utils.Constants
import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class VisionApiTests {

    /**
     * Тест отправляет на сервер пустую картинку
     * Проверяет, что приложение обрабатывает это как ошибку "Не удалось распознать картинку"
     */
    @Test
    fun wrongImageType_isError() {
        // Тут должен был быть кот, но получилась только пустая картинка =(
        val bitmap = getBitmapFromTestAssets("sad-cat-luhu.jpg")!!

        // Used to await for async task
        val signal = CountDownLatch(1);

        InstrumentationRegistry.getInstrumentation().runOnMainSync(Runnable() {
            VisionApi.findLocation(bitmap,
                AppPreferences.getToken(getApplicationContext()),
                object :
                    OnVisionApiListener {

                    override fun onSuccess(latLng: LatLng) {
                        fail("API call shouldn't be successful in this case")
                        signal.countDown()
                    }

                    override fun onErrorPlace(category: String) {
                        fail("Photo category shouldn't be distinguished in this case")
                        signal.countDown()
                    }

                    override fun onError() {
                        // Мы должны оказаться здесь - кот не является распознаваемым приложением объектом
                        signal.countDown()
                    }
                })
        })

        signal.await(10, TimeUnit.SECONDS)
    }

    /**
     * Проверяем, что для моря появляется нужная рекомендация
     */
    @Test
    fun seaCategoryTest() {
        val expectedCityEN = "Rimini"
        val expectedCityRU = "Римини"

        val scenario = launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onActivity { activity ->
            activity.loadByCategory("sea")
        }
        Assert.assertEquals(Constants.PICKED_CITY_EN, expectedCityEN)
        Assert.assertEquals(Constants.PICKED_CITY_RU, expectedCityRU)
    }

    /**
     * Проверяем, что для океана появляется нужная рекомендация
     */
    @Test
    fun oceanCategoryTest() {
        val expectedCityEN = "Rimini"
        val expectedCityRU = "Римини"

        val scenario = launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onActivity { activity ->
            activity.loadByCategory("ocean")
        }
        Assert.assertEquals(Constants.PICKED_CITY_EN, expectedCityEN)
        Assert.assertEquals(Constants.PICKED_CITY_RU, expectedCityRU)
    }

    /**
     * Проверяем, что для пляжа появляется нужная рекомендация
     */
    @Test
    fun beachCategoryTest() {
        val expectedCityEN = "Rimini"
        val expectedCityRU = "Римини"

        val scenario = launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onActivity { activity ->
            activity.loadByCategory("beach")
        }
        Assert.assertEquals(Constants.PICKED_CITY_EN, expectedCityEN)
        Assert.assertEquals(Constants.PICKED_CITY_RU, expectedCityRU)
    }

    /**
     * Проверяем, что для гор появляется нужная рекомендация
     */
    @Test
    fun mountainCategoryTest() {
        val expectedCityEN = "Sochi"
        val expectedCityRU = "Сочи"

        val scenario = launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onActivity { activity ->
            activity.loadByCategory("mountain")
        }
        Assert.assertEquals(Constants.PICKED_CITY_EN, expectedCityEN)
        Assert.assertEquals(Constants.PICKED_CITY_RU, expectedCityRU)
    }

    /**
     * Проверяем, что для снега появляется нужная рекомендация
     */
    @Test
    fun snowCategoryTest() {
        val expectedCityEN = "Helsinki"
        val expectedCityRU = "Хельсинки"

        val scenario = launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onActivity { activity ->
            activity.loadByCategory("snow")
        }
        Assert.assertEquals(Constants.PICKED_CITY_EN, expectedCityEN)
        Assert.assertEquals(Constants.PICKED_CITY_RU, expectedCityRU)
    }

    /**
     * Проверяем, что для непонятного фото появляется дефолтная рекомендация
     */
    @Test
    fun otherCategoryTest() {
        val expectedCityEN = "Rimini"
        val expectedCityRU = "Римини"

        val scenario = launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onActivity { activity ->
            activity.loadByCategory("bla-bla-bla")
        }
        Assert.assertEquals(Constants.PICKED_CITY_EN, expectedCityEN)
        Assert.assertEquals(Constants.PICKED_CITY_RU, expectedCityRU)
    }

    private fun getBitmapFromTestAssets(fileName: String?): Bitmap? {
        val context: Context = ApplicationProvider.getApplicationContext()
        val assetManager: AssetManager = context.getAssets()
        val istr: InputStream
        var bitmap: Bitmap? = null
        try {
            istr = assetManager.open(fileName!!)
            bitmap = BitmapFactory.decodeStream(istr)
        } catch (e: IOException) {

        }
        return bitmap
    }
}

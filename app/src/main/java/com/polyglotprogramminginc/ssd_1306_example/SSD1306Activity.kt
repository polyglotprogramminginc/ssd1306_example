package com.polyglotprogramminginc.ssd_1306_example

import android.app.Activity
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.text.DynamicLayout
import android.text.Layout
import android.text.TextPaint
import com.google.android.things.contrib.driver.ssd1306.BitmapHelper
import com.google.android.things.contrib.driver.ssd1306.Ssd1306
import android.util.Log
import java.io.IOException


/**
 * ssd1306 example -- with examples for displaying text and graphics
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class SSD1306Activity : Activity() {

    private lateinit var mScreen: Ssd1306
    private var mTick: Int = 0
    private val mHandler = Handler()
    private val TAG = "SSD1306 Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ssd1306)

        try {
            mScreen = Ssd1306(BoardDefaults.i2CPort)
        } catch (e : IOException){
            Log.e(TAG, "Error while opening screen", e)
        }

        Log.d(TAG, "OLED screen activity created")
        mHandler.post(mDrawRunnable)
    }

    /**
     * draws the screen with the specified resources
     *
     * @param text Text to display, leave blank if none and \n for line breaks.
     * @param fontSize Font size to use for the text. Needed if you pass in text.
     * @param imageResource The image resource id if you want to display an image.
     */
    fun drawScreen(text: String = "", fontSize: Float = 0f, imageResource: Int = 0) {

        mScreen.clearPixels()
        // base bitmap setup to draw into
        val conf = Bitmap.Config.ARGB_8888
        val mBitmap = Bitmap.createBitmap(128, 64, conf)

        // setting up the paint options
        val paint = TextPaint()
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        paint.textSize = fontSize

        val canvas = Canvas(mBitmap)

        if (imageResource > 0) {
            val imageBitmap = BitmapFactory.decodeResource(resources, imageResource)
            canvas.drawBitmap(imageBitmap, null, Rect(0, 0, 128, 64), paint)
        }

        // we set up any text to be displayed here and draw it
        val dynamicLayout = DynamicLayout(text,
                paint, 128, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
        dynamicLayout.draw(canvas)

        // paint to the screen
        BitmapHelper.setBmpData(mScreen, 0, 0, mBitmap, false)
        mScreen.show()

        /**
        this is optional, it allows you to make the display brighter
         comment out if you are user the pre 0.3 version of the SSD driver.*/
        mScreen.setContrast(255)
    }

    private val mDrawRunnable = object : Runnable {
        /**
         * Updates the display and tick counter.
         */
        override fun run() {
            // exit Runnable if the device is already closed
            if (mScreen == null) {
                return
            }

            try {
                when (mTick % 3) {
                    0 -> drawScreen("honing our craft together", 10f, R.drawable.purr_programming_full_white)
                    1 -> drawScreen("WEATHER\n\n77 F/29.1 IN", 15f)
                    else -> drawScreen("polyglotprogramminginc.com", 7.5f, R.drawable.t_shirt_cat_mono_white_on_black)
                }
                mScreen.show()
                mHandler.postDelayed(this, (5000).toLong())
            } catch (e: IOException) {
                Log.e(TAG, "Exception during screen update", e)
            }
            mTick++
        }
    }
}

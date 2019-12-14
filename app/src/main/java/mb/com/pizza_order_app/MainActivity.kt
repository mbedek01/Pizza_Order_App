package mb.com.pizza_order_app

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.squareup.okhttp.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.json.JSONObject
import java.io.IOException
import org.json.JSONException

import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /** Implementation for flashlight on/off */

        val switchOn: ImageButton
        val switchOff: ImageButton
        val camera: android.graphics.Camera

        switchOff = findViewById(R.id.switch_off)
        switchOn = findViewById(R.id.switch_on)

        // camera off
        switchOff.setOnClickListener(View.OnClickListener {
            switchOff.setVisibility(View.GONE)
            switchOn.setVisibility(View.VISIBLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                changeFlashLight(false)
            }
        })

        // camera on
        switchOn.setOnClickListener(View.OnClickListener {
            switchOff.setVisibility(View.VISIBLE)
            switchOn.setVisibility(View.GONE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                changeFlashLight(true)
            }
        })


        /** Get the list of all the pizzas using RESTful API */

        menuButton.setOnClickListener {

            val client: OkHttpClient = OkHttpClient()
            val request = Request.Builder()
                .url("http://192.168.1.3:4000/api/ListAll?username=neu&&password=neupass")
                .build()

            val call = client.newCall(request)

            /* asyn call */
            call.enqueue(object : Callback {
                override fun onFailure(request: Request?, e: IOException?) {
                    println(e?.message)
                }

                @Throws(IOException::class)
                override fun onResponse(response: Response?) {

                    if (response?.isSuccessful!!){

                        runOnUiThread {
                            val response = response.body().string()

                            try {
                                val jsonObject = JSONObject(response)
                                val message: String = jsonObject.get("message").toString()
                                runOnUiThread { displayView.text = message }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
            )}


        // Get the cost of pizza using RESTful API
        menuButton.setOnClickListener {

            val client: OkHttpClient = OkHttpClient()
            val request = Request.Builder()
                .url("http://192.168.1.3:4000/api/cost?option1=1&option2=1&option3=1&option4=2")
                .build()

            val call = client.newCall(request)

            /* asyn call */
            call.enqueue(object : Callback {
                override fun onFailure(request: Request?, e: IOException?) {
                    println(e?.message)
                }

                @Throws(IOException::class)
                override fun onResponse(response: Response?) {

                    if (response?.isSuccessful!!){

                        runOnUiThread {
                            val response = response.body().string()

                            try {
                                val jsonObject = JSONObject(response)
                                val message: String = jsonObject.get("message").toString()
                                runOnUiThread { displayView.text = message }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
            )}
    }

    // camera on or camera off code before Android API LOLLIPOP (7.0)
    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun changeFlashLight(openOrClose: Boolean) {
        // check if android API is later than LOLLIPOP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                // get CameraManager
                val mCameraManager =
                    applicationContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                // get all camera device ids of the phone
                val ids = mCameraManager.cameraIdList
                for (id in ids) {
                    val c = mCameraManager.getCameraCharacteristics(id)
                    // check whether the flashlight is available
                    val flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                    val lensFacing = c.get(CameraCharacteristics.LENS_FACING)
                    if (flashAvailable != null && flashAvailable
                        && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK
                    ) {
                        // turn on or turn off the flashlight
                        mCameraManager.setTorchMode(id, openOrClose)
                    }
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }

        }
    }



    fun onPlaceOrderButtonClicked(view: View){
        var pizzaSizePrice=0.0
        var toppingsTotal=0.0
        when{
            radioGroup.Margherita.isChecked -> pizzaSizePrice = 5.0
            radioGroup.Romana.isChecked -> pizzaSizePrice = 7.0
            radioGroup.Valtellina.isChecked -> pizzaSizePrice = 9.0
        }

        if (OnionsCheckBox.isChecked) { toppingsTotal += 1}
        if (OlivesCheckBox.isChecked) { toppingsTotal += 2}
        if (TomatoesCheckBox.isChecked) {toppingsTotal += 3}

        displayView.text="Total Order price = $" + (pizzaSizePrice + toppingsTotal)
    }

}











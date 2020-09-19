package com.example.qr

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.zxing.WriterException
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var imagenQr:ImageView;
    private lateinit var textoQr:EditText;
    private lateinit var botonCrear:Button;
    private lateinit var bitmap:Bitmap;
    private lateinit var inputValue:String;

    private lateinit var botonIntent:Button;

    override fun onCreate(savedInstanceState: Bundle?) {

        // Para ver SplashScreen
        Thread.sleep(1000);
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imagenQr = findViewById<ImageView>(R.id.imgQr);
        textoQr = findViewById<EditText>(R.id.txtQr);
        botonCrear = findViewById<Button>(R.id.btnQr);
        botonIntent = findViewById<Button>(R.id.btnIntent);

        botonCrear.setOnClickListener{
            inputValue = textoQr.getText().toString().trim();
            if(inputValue.length > 0){
                val displayMetrics = DisplayMetrics()

                val width:Int = imagenQr.width;
                val height:Int = imagenQr.height;
                var smallerdimension:Int = if (width < height)  width else height;
                smallerdimension = smallerdimension * 3/4;
                val newQr = QRGEncoder(inputValue,null,QRGContents.Type.TEXT,smallerdimension);

                try{
                    bitmap  = newQr.encodeAsBitmap();
                    imagenQr.setImageBitmap(bitmap);
                }catch (e:WriterException){
                    sendMessage("Por Favor, vuelva a intentar");
                }

            }else{
                sendMessage("Se require una palabra");
            }

        }

        botonIntent.setOnClickListener{
            val newIntent = Intent(this,camara::class.java);
            startActivity(newIntent);
        }

    }

    private fun sendMessage(message:String){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}
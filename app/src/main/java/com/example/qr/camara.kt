package com.example.qr

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class camara : AppCompatActivity() {

    val SOLICITUD_TOMAR_FOTO  = 1;
    val permisoCamera = android.Manifest.permission.CAMERA;
    val permisoWriteStorage = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    val permisoReadStorage = android.Manifest.permission.READ_EXTERNAL_STORAGE;
    private lateinit var boton:Button;
    var ivFoto:ImageView? = null;
    var urlFotoActual = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camara)

        boton = findViewById<Button>(R.id.bTomar)
        ivFoto = findViewById<ImageView>(R.id.ivFoto);

        boton.setOnClickListener{
            pedirPermisos();
        }

    }

    fun pedirPermisos(){
        val deboProveerContexto = ActivityCompat.shouldShowRequestPermissionRationale(this,permisoCamera);

        if(deboProveerContexto){
            solicitudPermisos();
        }else{
            solicitudPermisos();
        }
    }

    fun solicitudPermisos(){
        requestPermissions(arrayOf(permisoCamera,permisoWriteStorage,permisoReadStorage),SOLICITUD_TOMAR_FOTO);
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            SOLICITUD_TOMAR_FOTO -> {
                if(grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                ){
                    // tenemos permiso
                    dispararIntentTomarFoto();
                }else{
                    // no tenemos permiso
                    Toast.makeText(this,"No distes permiso para acceder a la camara",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    fun dispararIntentTomarFoto(){

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(packageManager) != null){
            var archivoFoto:File? = null;

            archivoFoto = crearArchivoImagen();

            if(archivoFoto != null){
                val urlFoto = FileProvider.getUriForFile(this,"com.example.qr.android.fileprovider",archivoFoto);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,urlFoto);
                startActivityForResult(intent,SOLICITUD_TOMAR_FOTO);
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            SOLICITUD_TOMAR_FOTO -> {
                if(resultCode == Activity.RESULT_OK){
                    // obtener la imagen

                    // val extras = data?.extras;
                    //val imageBitmap = extras!!.get("data") as Bitmap;

                    val uri = Uri.parse(urlFotoActual);
                    val stream = contentResolver.openInputStream(uri);
                    val imageBitmap = BitmapFactory.decodeStream(stream);
                    ivFoto!!.setImageBitmap(imageBitmap);
                    anadirImagenGaleria();
                }else{
                    // cancelo la captura
                }
            }
        }
    }

    fun crearArchivoImagen():File{
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date());
        val nombreArchivoImagen = "JPEG_" + timeStamp + "_";

        // val directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        val directorio = Environment.getExternalStorageDirectory();

        val directorioPictures = File(directorio.absolutePath + "/Pictures/");
        val imagen = File.createTempFile(nombreArchivoImagen,".jgp",directorioPictures);

        urlFotoActual = "file://" + imagen.absolutePath;

        return imagen;
    }

    fun anadirImagenGaleria(){
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // MediaScannerConnection.scanFile(this, arrayOf());

        val file = File(urlFotoActual);
        val uri = Uri.fromFile(file);
        intent.setData(uri);
        this.sendBroadcast(intent);
    }

}
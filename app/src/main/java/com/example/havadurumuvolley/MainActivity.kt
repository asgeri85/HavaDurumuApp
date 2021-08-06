package com.example.havadurumuvolley

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.havadurumuvolley.Classes.Weather
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {
   private lateinit var location:SimpleLocation
   private  var lat: String? =null
   private  var lot: String? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter=ArrayAdapter.createFromResource(this,R.array.turkey_city,android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCity.adapter=adapter
        spinnerCity.onItemSelectedListener=this
        spinnerCity.setSelection(1)
        havaGetir("Baku")

    }

    fun havaGetir(ad:String){
        val url="https://api.openweathermap.org/data/2.5/weather?q=$ad&appid=5ff5f6121eced2f3ad373070cbbb2040&lang=tr&units=metric"
        val istek = StringRequest(Request.Method.GET,url, { cevap->
            try {
                val json=JSONObject(cevap)

                val main=json.getJSONObject("main")
                val temp=main.getInt("temp")
                val weather=json.getJSONArray("weather")
                val hava=Weather(weather.getJSONObject(0).getInt("id"),weather.getJSONObject(0).getString("main"),
                weather.getJSONObject(0).getString("description"),weather.getJSONObject(0).getString("icon"))
                val city=json.getString("name")

                if (hava.icon.last() == 'd'){
                    rootLayout.background=getDrawable(R.drawable.bg)
                }else{
                    rootLayout.background=getDrawable(R.drawable.gece)
                }
                textViewLocation.text=city
                textViewTarih.text=tarihGetir()
                textViewDerece.text=temp.toString()
                textViewDurum.text=hava.aciklama

                val fotoAdi=resources.getIdentifier("icon_"+hava.icon.sonKarakter(),"drawable",packageName)
                imageViewLogo.setImageResource(fotoAdi)
            }catch (e:Exception ){
                e.printStackTrace()
            }
        }, { e-> e.printStackTrace() })

        Volley.newRequestQueue(this@MainActivity).add(istek)
    }

    fun tarihGetir():String{
        val calendar=Calendar.getInstance().time
        val format=SimpleDateFormat("dd.MM.yyyy", Locale("tr"))
        val date=format.format(calendar)
        return date
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p2==0){
           location= SimpleLocation(this)
            if (!location.hasLocationEnabled()){
                Toast.makeText(applicationContext,"Gps acmalisiniz",Toast.LENGTH_SHORT).show()
                SimpleLocation.openSettings(this)
            }else{
                if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),50)
                }else{
                    location= SimpleLocation(this)
                    lat= String.format("%.6f",location.latitude)
                    lot= String.format("%.6f",location.longitude)
                    Log.e("lat",lat+""+lot)
                    locationGetir(lat.toString(),lot.toString())
                }
            }
        }else{
            val sehir=p0?.getItemAtPosition(p2).toString()
            havaGetir(sehir)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode==50){
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                location= SimpleLocation(this)
                lat= String.format("%.6f",location.latitude)
                lot= String.format("%.6f",location.longitude)
                locationGetir(lat.toString(),lot.toString())
            }else{
                spinnerCity.setSelection(1)
                Toast.makeText(applicationContext,"İzin verseydiniz konuma ulaşabilirdik",Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    fun locationGetir(lat:String,lot:String){

        val url="https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lot&appid=5ff5f6121eced2f3ad373070cbbb2040&lang=tr&units=metric"
        val gpsistek=StringRequest(Request.Method.GET,url, { cevap->
            try {
                val json=JSONObject(cevap)

                val main=json.getJSONObject("main")
                val temp=main.getInt("temp")
                val weather=json.getJSONArray("weather")
                val hava=Weather(weather.getJSONObject(0).getInt("id"),weather.getJSONObject(0).getString("main"),
                    weather.getJSONObject(0).getString("description"),weather.getJSONObject(0).getString("icon"))
                val city=json.getString("name")

                if (hava.icon.last() == 'd'){
                    rootLayout.background=getDrawable(R.drawable.bg)
                }else{
                    rootLayout.background=getDrawable(R.drawable.gece)
                }
                textViewLocation.text=city
                textViewTarih.text=tarihGetir()
                textViewDerece.text=temp.toString()
                textViewDurum.text=hava.aciklama

                val fotoAdi=resources.getIdentifier("icon_"+hava.icon.sonKarakter(),"drawable",packageName)
                imageViewLogo.setImageResource(fotoAdi)
            }catch (e:Exception ){
                e.printStackTrace()
            }

        }, { e-> e.printStackTrace() })

        Volley.newRequestQueue(this@MainActivity).add(gpsistek)
    }

}

private fun String.sonKarakter(): Any? {
    return this.substring(0,this.length-1)
}

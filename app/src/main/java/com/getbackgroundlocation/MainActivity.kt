package com.getbackgroundlocation

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.getbackgroundlocation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val MY_FINE_LOCATION_REQUEST = 11
        private const val MY_BACKGROUND_LOCATION_REQUEST = 22
    }

    private lateinit var binding: ActivityMainBinding
    var mLocationService: LocationService = LocationService()
    lateinit var mServiceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.startServiceBtn.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.dialog_title))
                            setMessage(R.string.background_location_permission_message)
                            setPositiveButton(getString(R.string.dialog_okay_title)) { _, _ ->
                                startBackgroundService()
                            }
                            setNegativeButton(getString(R.string.dialog_negative_title)) { _, _ ->
                                requestBackgroundLocationPermission()
                            }
                        }.create().show()

                    } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        startBackgroundService()
                    }
                } else {
                    startBackgroundService()
                }

            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.access_fine_location))
                        .setMessage(getString(R.string.location_required))
                        .setPositiveButton(getString(R.string.ok)) { _, _ ->
                            requestFineLocationPermission()
                        }
                        .create()
                        .show()
                } else {
                    requestFineLocationPermission()
                }
            }
        }

        binding.stopServiceBtn.setOnClickListener {
            stopBackgroundService()
        }
    }

    private fun startBackgroundService() {
        mLocationService = LocationService()
        mServiceIntent = Intent(this, mLocationService.javaClass)
        if (!Utility.isLocationServiceRunning(mLocationService.javaClass, this)) {
            startService(mServiceIntent)
            Toast.makeText(this, getString(R.string.service_start_successfully), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.service_already_running), Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopBackgroundService() {
        mLocationService = LocationService()
        mServiceIntent = Intent(this, mLocationService.javaClass)
        if (Utility.isLocationServiceRunning(mLocationService.javaClass, this)) {
            stopService(mServiceIntent)
            Toast.makeText(this, getString(R.string.service_stopped), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.already_stopped), Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), MY_BACKGROUND_LOCATION_REQUEST)
        }
    }

    private fun requestFineLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_FINE_LOCATION_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Toast.makeText(this, requestCode.toString(), Toast.LENGTH_LONG).show()
        when (requestCode) {
            MY_FINE_LOCATION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        requestBackgroundLocationPermission()
                    }

                } else {
                    Toast.makeText(this, getString(R.string.fine_location_denied), Toast.LENGTH_LONG).show()
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", this.packageName, null)))
                    }
                }
                return
            }
            MY_BACKGROUND_LOCATION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(this, getString(R.string.location_permission_granted), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
}
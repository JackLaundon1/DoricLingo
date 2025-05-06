package com.example.doriclingo.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.doriclingo.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen() {
    val context = LocalContext.current

    //stores the location permission setting
    val hasLocationPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    //stores the users location
    val userLocation = remember { mutableStateOf<Location?>(null) }

    //stores the distance to Aberdeen city centre
    val distanceToAberdeen = remember { mutableStateOf<String?>(null) }

    //Aberdeen city center coordinates
    val aberdeenCenter = LatLng(57.1497, -2.0943)

    //sets the initial camera position on the map
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(aberdeenCenter, 12f)
    }

    //requests permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission.value = isGranted
        //if permission is granted
        if (isGranted) {
            //gets the user's last known location
            getLastKnownLocation(context, userLocation, distanceToAberdeen, aberdeenCenter)
        }
    }

    //gets location if permission is already granted
    LaunchedEffect(Unit) {
        if (hasLocationPermission.value) {
            getLastKnownLocation(context, userLocation, distanceToAberdeen, aberdeenCenter)
        }
    }

    //layout
    Column(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission.value
            )
        ) {
            //marker for Aberdeen city center
            Marker(
                state = MarkerState(position = aberdeenCenter),
                title = context.getString(R.string.aberdeen_city_center)
            )

            //draws line between user and Aberdeen if location available
            userLocation.value?.let { location ->
                val userLatLng = LatLng(location.latitude, location.longitude)
                Polyline(
                    points = listOf(userLatLng, aberdeenCenter),
                    //uses the same colour as the text link colour
                    color = colorResource(R.color.link)
                )
            }
        }

        //displays distance text
        Column(modifier = Modifier.padding(16.dp)) {
            //shows request permission button if permissions aren't granted
            if (!hasLocationPermission.value) {
                Button(onClick = {
                    //requests permission
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }) {
                    Text(context.getString(R.string.request_location_permission))
                }
            } else {
                when {
                    //shows loading text
                    userLocation.value == null -> Text(context.getString(R.string.getting_your_location))
                    distanceToAberdeen.value != null -> {
                        Text(
                            context.getString(
                                R.string.you_re_from_aberdeen_city_center,
                                distanceToAberdeen.value!!
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

//gets last known location
private fun getLastKnownLocation(
    context: Context,
    userLocation: MutableState<Location?>,
    distanceToAberdeen: MutableState<String?>,
    aberdeenCenter: LatLng
) {
    //if permission is granted
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        //references google's fused location provider
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        //fetches last known location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    //updates UI to draw line
                    userLocation.value = location
                    //calculates distance from Aberdeen using android location distance between
                    val results = FloatArray(1)
                    Location.distanceBetween(
                        location.latitude,
                        location.longitude,
                        aberdeenCenter.latitude,
                        aberdeenCenter.longitude,
                        results
                    )
                    //calculates distance in km
                    //location is measured in metres, so this is simply divided by 1000
                    val distanceInKm = results[0] / 1000
                    //converts km to miles
                    val distanceInMiles = distanceInKm * 0.621371
                    distanceToAberdeen.value = "%.1f miles (%.1f km)".format(distanceInMiles, distanceInKm)
                }
            }
    }
}

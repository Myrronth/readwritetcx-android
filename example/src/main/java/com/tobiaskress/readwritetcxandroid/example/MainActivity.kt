package com.tobiaskress.readwritetcxandroid.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tobiaskress.readwritetcxandroid.example.ui.theme.ReadWriteTcxAndroidTheme
import com.tobiaskress.readwritetcxandroid.parser.TcxParser
import com.tobiaskress.readwritetcxandroid.parser.types.TrainingCenterDatabase
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private const val LOG_TAG = "MainActivity"

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    private val tcx: TrainingCenterDatabase?
        get() {
            var tcx: TrainingCenterDatabase? = null

            assets.open("Morning_Ride.tcx").use {
                try {
                    Log.e(LOG_TAG, "Start parsing")
                    tcx = TcxParser().parse(it)
                    Log.e(LOG_TAG, "Finished parsing")
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: XmlPullParserException) {
                    e.printStackTrace()
                }
            }

            return tcx
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ReadWriteTcxAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val tcx by remember { mutableStateOf(tcx) }

                    Column(modifier = Modifier.fillMaxSize()) {
                        if (tcx != null) {
                            val activities = tcx!!.activities!!.activities.first()
                            val track = activities.laps.first().tracks.first()

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colors.background)
                            ) {
                                itemsIndexed(track.points) { index, waypoint ->
                                    Surface(color = MaterialTheme.colors.surface) {
                                        ListItem(
                                            text = {
                                                Text("Waypoint $index at ${waypoint.time.format(
                                                    DateTimeFormatter.ofLocalizedDateTime(
                                                        FormatStyle.MEDIUM,
                                                        FormatStyle.MEDIUM
                                                    )
                                                )}")
                                            },
                                            secondaryText = {
                                                Text("Latitude: ${waypoint.position?.latitude?.decimalDegrees}; " +
                                                        "Longitude: ${waypoint.position?.longitude?.decimalDegrees}")
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.verner.healthgateway.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.SleepSessionRecord
import com.verner.healthgateway.R
import com.verner.healthgateway.data.SleepSessionData
import com.verner.healthgateway.data.dateTimeWithOffsetOrDefault
import com.verner.healthgateway.data.formatHoursMinutes
import com.verner.healthgateway.formatDisplayTimeStartEnd
import com.verner.healthgateway.presentation.theme.HealthConnectTheme
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Creates a row to represent a [SleepSessionData], which encompasses data for both the sleep
 * session and any fine-grained sleep stages.
 */
@Composable
fun SleepSessionRow(
    sessionData: SleepSessionData,
    startExpanded: Boolean = false
) {
    var expanded by remember { mutableStateOf(startExpanded) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .clickable {
                expanded = !expanded
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val formatter = DateTimeFormatter.ofPattern("eee, d LLL")
        val startDateTime =
            dateTimeWithOffsetOrDefault(sessionData.startTime, sessionData.startZoneOffset)
        Text(
            modifier = Modifier
                .weight(0.4f),
            color = MaterialTheme.colors.primary,
            text = startDateTime.format(formatter)
        )
        if (!expanded) {
            Text(
                modifier = Modifier
                    .weight(0.4f),
                text = sessionData.duration?.formatHoursMinutes()
                    ?: stringResource(id = R.string.not_available_abbrev)
            )
        }
        IconButton(
            onClick = { expanded = !expanded }
        ) {
            val icon = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
            Icon(icon, stringResource(R.string.delete_button))
        }
    }
    if (expanded) {
        val startEndLabel = formatDisplayTimeStartEnd(
            sessionData.startTime,
            sessionData.startZoneOffset,
            sessionData.endTime,
            sessionData.endZoneOffset
        )
        SleepSessionDetailRow(labelId = R.string.sleep_time, item = startEndLabel)
        SleepSessionDetailRow(
            labelId = R.string.sleep_duration,
            item = sessionData.duration?.formatHoursMinutes()
        )
        SleepSessionDetailRow(labelId = R.string.sleep_notes, item = sessionData.notes)
        if (sessionData.stages.isNotEmpty()) {
            SleepSessionDetailRow(labelId = R.string.sleep_stages, item = "")
            SleepStagesDetail(sessionData.stages)
        }
    }
}

@Preview
@Composable
fun SleepSessionRowPreview() {
    HealthConnectTheme {
        val end = ZonedDateTime.now()
        val start = end.minusHours(1)
        Column {
            SleepSessionRow(
                SleepSessionData(
                    uid = "123",
                    title = "My sleep",
                    notes = "Slept well",
                    startTime = start.toInstant(),
                    startZoneOffset = start.offset,
                    endTime = end.toInstant(),
                    endZoneOffset = end.offset,
                    duration = Duration.between(start, end),
                    stages = listOf(
                        SleepSessionRecord.Stage(
                            stage = SleepSessionRecord.STAGE_TYPE_DEEP,
                            startTime = start.toInstant(),
                            endTime = end.toInstant(),
                        )
                    )
                ),
                startExpanded = true
            )
        }
    }
}

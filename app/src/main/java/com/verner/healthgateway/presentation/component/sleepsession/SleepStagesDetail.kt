package com.verner.healthgateway.presentation.component.sleepsession

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.SleepSessionRecord
import com.verner.healthgateway.formatDisplayTimeStartEnd
import com.verner.healthgateway.presentation.theme.HealthConnectTheme
import java.time.ZonedDateTime

@Composable
fun SleepStagesDetail(sleepStages: List<SleepSessionRecord.Stage>) {
    sleepStages.forEach { stage ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            val startEndLabel = formatDisplayTimeStartEnd(
                stage.startTime, null, stage.endTime, null
            )
            Text(
                modifier = Modifier.weight(0.5f),
                text = startEndLabel,
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Start
            )
            Text(
                modifier = Modifier
                    .weight(0.4f),
                text = SleepSessionRecord.STAGE_TYPE_INT_TO_STRING_MAP[stage.stage] ?: "unknown",
                textAlign = TextAlign.Start
            )
        }
    }
}

@Preview
@Composable
fun SleepStagesDetailPreview() {
    HealthConnectTheme {
        val end2 = ZonedDateTime.now()
        val start2 = end2.minusHours(1)
        val start1 = start2.minusHours(1)
        Column {
            SleepStagesDetail(
                sleepStages = listOf(
                    SleepSessionRecord.Stage(
                        stage = SleepSessionRecord.STAGE_TYPE_DEEP,
                        startTime = start2.toInstant(),
                        endTime = end2.toInstant()
                    ),
                    SleepSessionRecord.Stage(
                        stage = SleepSessionRecord.STAGE_TYPE_LIGHT,
                        startTime = start1.toInstant(),
                        endTime = start2.toInstant()
                    )
                )
            )
        }
    }
}

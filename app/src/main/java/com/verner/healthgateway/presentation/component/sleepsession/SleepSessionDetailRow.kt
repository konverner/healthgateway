package com.verner.healthgateway.presentation.component.sleepsession

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.verner.healthgateway.R
import com.verner.healthgateway.presentation.theme.HealthConnectTheme

@Composable
fun SleepSessionDetailRow(
    @StringRes labelId: Int,
    item: String?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(0.25f),
            text = stringResource(id = labelId),
            color = MaterialTheme.colors.secondary,
            textAlign = TextAlign.Start
        )
        Text(
            modifier = Modifier
                .weight(0.4f),
            text = item ?: stringResource(id = R.string.not_available_abbrev),
            textAlign = TextAlign.Start
        )
    }
}

@Preview
@Composable
fun SleepSessionDetailRowPreview() {
    HealthConnectTheme {
        Column {
            SleepSessionDetailRow(
                labelId = R.string.sleep_notes,
                item = "Slept well"
            )
        }
    }
}

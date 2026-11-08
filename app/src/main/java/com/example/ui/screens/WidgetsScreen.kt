package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.AddToHomeScreen
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DoomsdaySettings
import com.example.data.HorizontalAlignOption
import com.example.data.VerticalAlignOption
import com.example.data.WidgetLayoutType
import com.example.domain.CountdownSnapshot
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.SilverAccent
import com.example.ui.theme.SilverMuted
import com.example.ui.theme.SilverPrimary
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.widget.DoomsdaySystemUpdater

@Composable
fun WidgetsScreen(
    snapshot: CountdownSnapshot,
    settings: DoomsdaySettings,
    onSelectWidgetType: (WidgetLayoutType) -> Unit,
    onUpdateSettings: ((DoomsdaySettings) -> DoomsdaySettings) -> Unit,
    onRequestPinWidget: (WidgetLayoutType) -> Unit
) {
    val context = LocalContext.current
    val compactCount = DoomsdaySystemUpdater.getCompactWidgetCount(context)
    val largeCount = DoomsdaySystemUpdater.getLargeWidgetCount(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
        ) {
            Text(
                text = "HOME SCREEN WIDGETS",
                style = MaterialTheme.typography.headlineMedium.copy(
                    letterSpacing = 3.sp,
                    color = SilverPrimary,
                    fontSize = 20.sp
                )
            )
            Text(
                text = "Configure Compact (4x1) and Large (4x2) transparent glass widgets",
                style = MaterialTheme.typography.bodyMedium.copy(color = SilverMuted)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Widget Layout Type Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WidgetLayoutType.entries.forEach { type ->
                    val active = settings.selectedWidgetPreview == type
                    SelectablePillButton(
                        label = type.label,
                        active = active,
                        onClick = { onSelectWidgetType(type) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("widget_type_tab_${type.id.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Simulated Home Screen Launcher Canvas with Resizable Widget Preview
            SectionHeader("LIVE HOME-SCREEN PLACEMENT PREVIEW")
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(235.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1A202E),
                                Color(0xFF090B10)
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                // Subtle launcher grid dots
                val hAlign = when (settings.horizontalAlignment) {
                    HorizontalAlignOption.LEFT -> Alignment.Start
                    HorizontalAlignOption.CENTER -> Alignment.CenterHorizontally
                    HorizontalAlignOption.RIGHT -> Alignment.End
                }
                val vArr = when (settings.verticalAlignment) {
                    VerticalAlignOption.TOP -> Arrangement.Top
                    VerticalAlignOption.CENTER -> Arrangement.Center
                    VerticalAlignOption.BOTTOM -> Arrangement.Bottom
                }

                val presetSurface = Color(settings.preset.surfaceColorHex)
                val glassAlpha = (settings.glassOpacity * (1f - settings.backgroundTransparency * 0.5f))
                    .coerceIn(0.02f, 0.95f)
                val borderAlpha = settings.borderOpacity.coerceIn(0.05f, 0.9f)
                val textColor = Color(settings.textColorHex).copy(alpha = settings.textOpacity)

                Box(
                    modifier = Modifier
                        .width(settings.widgetWidthDp.coerceIn(220f, 360f).dp)
                        .height(
                            if (settings.selectedWidgetPreview == WidgetLayoutType.COMPACT)
                                (settings.widgetHeightDp * 0.68f).coerceIn(88f, 135f).dp
                            else
                                settings.widgetHeightDp.coerceIn(130f, 195f).dp
                        )
                        .clip(RoundedCornerShape(settings.cornerRadiusDp.dp))
                        .background(presetSurface.copy(alpha = glassAlpha))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = borderAlpha),
                            RoundedCornerShape(settings.cornerRadiusDp.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("widget_canvas_preview_box")
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = hAlign,
                        verticalArrangement = vArr
                    ) {
                        Text(
                            text = "DOOMSDAY",
                            style = TextStyle(
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 2.8.sp,
                                color = SilverAccent.copy(alpha = 0.85f)
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        if (settings.selectedWidgetPreview == WidgetLayoutType.COMPACT) {
                            Text(
                                text = snapshot.formattedColonString,
                                style = TextStyle(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (settings.fontSizeSp * 0.65f).coerceIn(14f, 22f).sp,
                                    color = textColor,
                                    letterSpacing = (settings.letterSpacingEm * 6f).sp
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "M  |  D  |  H  |  M  |  S",
                                style = TextStyle(
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 9.sp,
                                    letterSpacing = 1.8.sp,
                                    color = SilverMuted
                                )
                            )
                        } else {
                            // Large Widget Layout:
                            // DOOMSDAY
                            // 01 MONTH : 09 DAYS
                            // 12 HOURS : 59 MINUTES : 00 SECONDS
                            Text(
                                text = snapshot.formattedLargeRowOne,
                                style = TextStyle(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (settings.fontSizeSp * 0.62f).coerceIn(14f, 21f).sp,
                                    color = textColor
                                )
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = snapshot.formattedLargeRowTwo,
                                style = TextStyle(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = (settings.fontSizeSp * 0.44f).coerceIn(10f, 15f).sp,
                                    color = textColor.copy(alpha = 0.88f)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Native Pin to Home Screen Button + Active Status
            Button(
                onClick = { onRequestPinWidget(settings.selectedWidgetPreview) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SilverPrimary,
                    contentColor = Color(0xFF050507)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("pin_widget_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.AddToHomeScreen,
                    contentDescription = "Pin Widget",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add ${
                        if (settings.selectedWidgetPreview == WidgetLayoutType.COMPACT) "Compact" else "Large"
                    } Widget to Home Screen",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF050507)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Placed Widgets Counter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0D1017))
                    .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Placed Status",
                        tint = if (compactCount + largeCount > 0) EmeraldActive else SilverMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Active Widgets Placed",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SilverPrimary)
                    )
                }
                Text(
                    text = "$compactCount Compact • $largeCount Large",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        color = SilverAccent,
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Widget Layout & Dimension Controls
            SectionHeader("WIDGET POSITION, ALIGNMENT & DIMENSIONS")
            Spacer(modifier = Modifier.height(10.dp))

            StudioCardContainer {
                Text(
                    text = "Text Alignment Inside Widget",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SilverPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalAlignOption.entries.forEach { align ->
                        SelectablePillButton(
                            label = align.label,
                            active = settings.horizontalAlignment == align,
                            onClick = { onUpdateSettings { it.copy(horizontalAlignment = align) } },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                ParameterSliderRow(
                    label = "Widget Preview Width",
                    valueText = "${settings.widgetWidthDp.toInt()} dp",
                    value = settings.widgetWidthDp,
                    valueRange = 220f..360f,
                    onValueChange = { v -> onUpdateSettings { it.copy(widgetWidthDp = v) } }
                )

                ParameterSliderRow(
                    label = "Widget Preview Height",
                    valueText = "${settings.widgetHeightDp.toInt()} dp",
                    value = settings.widgetHeightDp,
                    valueRange = 110f..220f,
                    onValueChange = { v -> onUpdateSettings { it.copy(widgetHeightDp = v) } }
                )

                ParameterSliderRow(
                    label = "Widget Text Scale",
                    valueText = "${settings.fontSizeSp.toInt()} sp",
                    value = settings.fontSizeSp,
                    valueRange = 18f..36f,
                    onValueChange = { v -> onUpdateSettings { it.copy(fontSizeSp = v) } }
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 5-Step Manual Widget Tutorial
            SectionHeader("HOW TO PLACE & RESIZE ON HOME SCREEN")
            Spacer(modifier = Modifier.height(10.dp))

            StudioCardContainer {
                val steps = listOf(
                    "1. Long-press an empty area on your Android Home Screen.",
                    "2. Tap 'Widgets' from the popup launcher menu.",
                    "3. Scroll or search to find 'Doomsday'.",
                    "4. Choose either 'Doomsday Compact' (4×1) or 'Doomsday Large' (4×2).",
                    "5. Drag onto your home screen and drag the border handles to resize."
                )
                steps.forEach { step ->
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SilverPrimary.copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

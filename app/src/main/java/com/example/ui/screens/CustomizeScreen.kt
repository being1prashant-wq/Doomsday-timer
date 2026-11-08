package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DoomsdaySettings
import com.example.data.GlassPreset
import com.example.data.HorizontalAlignOption
import com.example.data.VerticalAlignOption
import com.example.domain.CountdownSnapshot
import com.example.ui.components.GlassmorphicCountdownCard
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.SilverAccent
import com.example.ui.theme.SilverMuted
import com.example.ui.theme.SilverPrimary
import java.util.Locale

private val CuratedTextColors = listOf(
    0xFFF8FAFC to "Pure White",
    0xFFE2E8F0 to "Platinum Silver",
    0xFF94A3B8 to "Titanium Slate",
    0xFF38BDF8 to "Ice Cyan",
    0xFFA7F3D0 to "Mint Vapor",
    0xFFFDE68A to "Champagne Gold"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomizeScreen(
    snapshot: CountdownSnapshot,
    settings: DoomsdaySettings,
    onSelectPreset: (GlassPreset) -> Unit,
    onUpdateSettings: ((DoomsdaySettings) -> DoomsdaySettings) -> Unit
) {
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
                text = "GLASSMORPHISM STUDIO",
                style = MaterialTheme.typography.headlineMedium.copy(
                    letterSpacing = 3.sp,
                    color = SilverPrimary,
                    fontSize = 20.sp
                )
            )
            Text(
                text = "Customize translucency, borders, typography, and spatial alignment",
                style = MaterialTheme.typography.bodyMedium.copy(color = SilverMuted)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pinned Live Preview Card
            GlassmorphicCountdownCard(
                snapshot = snapshot,
                settings = settings,
                compactMode = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customize_live_preview")
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Section 1: 6 Instant Glass Presets
            SectionHeader("1. GLASS PRESETS (FREE & UNLOCKED)")
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                maxItemsInEachRow = 2
            ) {
                GlassPreset.entries.forEach { preset ->
                    val isSelected = settings.preset == preset
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) Color.White.copy(alpha = 0.14f)
                                else Color(0xFF0E1017).copy(alpha = 0.85f)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.75.dp,
                                color = if (isSelected) SilverPrimary else Color.White.copy(alpha = 0.14f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectPreset(preset) }
                            .padding(12.dp)
                            .testTag("preset_chip_${preset.id.lowercase()}")
                    ) {
                        Column {
                            Text(
                                text = preset.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SilverPrimary,
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = preset.subtitle,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = SilverMuted,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: Surface & Glassmorphism Sliders
            SectionHeader("2. GLASS SURFACE & TRANSLUCENCY")
            Spacer(modifier = Modifier.height(10.dp))

            StudioCardContainer {
                ParameterSliderRow(
                    label = "Glass Background Opacity",
                    valueText = "${(settings.glassOpacity * 100).toInt()}%",
                    value = settings.glassOpacity,
                    valueRange = 0.02f..0.96f,
                    onValueChange = { v -> onUpdateSettings { it.copy(glassOpacity = v) } }
                )
                ParameterSliderRow(
                    label = "Background Transparency",
                    valueText = "${(settings.backgroundTransparency * 100).toInt()}%",
                    value = settings.backgroundTransparency,
                    valueRange = 0.0f..0.98f,
                    onValueChange = { v -> onUpdateSettings { it.copy(backgroundTransparency = v) } }
                )
                ParameterSliderRow(
                    label = "Border Opacity",
                    valueText = "${(settings.borderOpacity * 100).toInt()}%",
                    value = settings.borderOpacity,
                    valueRange = 0.0f..0.90f,
                    onValueChange = { v -> onUpdateSettings { it.copy(borderOpacity = v) } }
                )
                ParameterSliderRow(
                    label = "Blur Intensity",
                    valueText = "${settings.blurIntensity.toInt()} px",
                    value = settings.blurIntensity,
                    valueRange = 0f..40f,
                    onValueChange = { v -> onUpdateSettings { it.copy(blurIntensity = v) } }
                )
                ParameterSliderRow(
                    label = "Corner Radius",
                    valueText = "${settings.cornerRadiusDp.toInt()} dp",
                    value = settings.cornerRadiusDp,
                    valueRange = 0f..44f,
                    onValueChange = { v -> onUpdateSettings { it.copy(cornerRadiusDp = v) } }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Subtle Silver Rim Highlights",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SilverPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "Specular metallic top edge reflection",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SilverMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Switch(
                        checked = settings.showSilverHighlights,
                        onCheckedChange = { checked ->
                            onUpdateSettings { it.copy(showSilverHighlights = checked) }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = SilverPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 3: Typography & Spacing
            SectionHeader("3. TYPOGRAPHY & SPACING")
            Spacer(modifier = Modifier.height(10.dp))

            StudioCardContainer {
                ParameterSliderRow(
                    label = "Font Size",
                    valueText = "${settings.fontSizeSp.toInt()} sp",
                    value = settings.fontSizeSp,
                    valueRange = 16f..38f,
                    onValueChange = { v -> onUpdateSettings { it.copy(fontSizeSp = v) } }
                )
                ParameterSliderRow(
                    label = "Font Weight",
                    valueText = "${settings.fontWeightVal}",
                    value = settings.fontWeightVal.toFloat(),
                    valueRange = 300f..900f,
                    onValueChange = { v -> onUpdateSettings { it.copy(fontWeightVal = v.toInt()) } }
                )
                ParameterSliderRow(
                    label = "Text Opacity",
                    valueText = "${(settings.textOpacity * 100).toInt()}%",
                    value = settings.textOpacity,
                    valueRange = 0.25f..1.0f,
                    onValueChange = { v -> onUpdateSettings { it.copy(textOpacity = v) } }
                )
                ParameterSliderRow(
                    label = "Letter Spacing",
                    valueText = String.format(Locale.US, "%.2f em", settings.letterSpacingEm),
                    value = settings.letterSpacingEm,
                    valueRange = 0.0f..0.25f,
                    onValueChange = { v -> onUpdateSettings { it.copy(letterSpacingEm = v) } }
                )
                ParameterSliderRow(
                    label = "Number Unit Spacing",
                    valueText = "${settings.numberSpacingDp.toInt()} dp",
                    value = settings.numberSpacingDp,
                    valueRange = 2f..14f,
                    onValueChange = { v -> onUpdateSettings { it.copy(numberSpacingDp = v) } }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 4: Text Color Swatches
            SectionHeader("4. TEXT COLOR TINT")
            Spacer(modifier = Modifier.height(10.dp))

            StudioCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CuratedTextColors.forEach { (hex, name) ->
                        val selected = settings.textColorHex == hex
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onUpdateSettings { it.copy(textColorHex = hex) } }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(hex))
                                    .border(
                                        width = if (selected) 2.5.dp else 1.dp,
                                        color = if (selected) Color.White else Color.White.copy(alpha = 0.25f),
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = name.substringBefore(" "),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = if (selected) SilverPrimary else SilverMuted
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 5: Alignment & Dimensions
            SectionHeader("5. ALIGNMENT & WIDGET DIMENSIONS")
            Spacer(modifier = Modifier.height(10.dp))

            StudioCardContainer {
                Text(
                    text = "Horizontal Alignment",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SilverPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalAlignOption.entries.forEach { align ->
                        val active = settings.horizontalAlignment == align
                        SelectablePillButton(
                            label = align.label,
                            active = active,
                            onClick = { onUpdateSettings { it.copy(horizontalAlignment = align) } },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Vertical Alignment",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SilverPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VerticalAlignOption.entries.forEach { align ->
                        val active = settings.verticalAlignment == align
                        SelectablePillButton(
                            label = align.label,
                            active = active,
                            onClick = { onUpdateSettings { it.copy(verticalAlignment = align) } },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                ParameterSliderRow(
                    label = "Preview Width",
                    valueText = "${settings.widgetWidthDp.toInt()} dp",
                    value = settings.widgetWidthDp,
                    valueRange = 240f..400f,
                    onValueChange = { v -> onUpdateSettings { it.copy(widgetWidthDp = v) } }
                )
                ParameterSliderRow(
                    label = "Preview Height",
                    valueText = "${settings.widgetHeightDp.toInt()} dp",
                    value = settings.widgetHeightDp,
                    valueRange = 100f..260f,
                    onValueChange = { v -> onUpdateSettings { it.copy(widgetHeightDp = v) } }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            color = SilverAccent,
            letterSpacing = 1.6.sp
        )
    )
}

@Composable
fun StudioCardContainer(
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0E1118).copy(alpha = 0.88f))
            .border(0.75.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        content()
    }
}

@Composable
fun ParameterSliderRow(
    label: String,
    valueText: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SilverPrimary,
                    fontSize = 13.sp
                )
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    color = SilverAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = SilverPrimary,
                activeTrackColor = SilverPrimary,
                inactiveTrackColor = Color.White.copy(alpha = 0.16f)
            )
        )
    }
}

@Composable
fun SelectablePillButton(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (active) SilverPrimary else Color.White.copy(alpha = 0.06f)
            )
            .border(
                width = 1.dp,
                color = if (active) SilverPrimary else Color.White.copy(alpha = 0.14f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (active) Color(0xFF050507) else SilverPrimary,
                fontSize = 12.sp
            )
        )
    }
}

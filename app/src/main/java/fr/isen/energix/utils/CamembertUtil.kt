package fr.isen.energix.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import kotlin.math.min

object CamembertUtil {

    // Liste des couleurs centralisée
    val DefaultColors = listOf(
        Color(0xFFEF5350), Color(0xFF66BB6A), Color(0xFF42A5F5),
        Color(0xFFFFA726), Color(0xFFAB47BC), Color(0xFFFFEB3B)
    )

    @Composable
    fun CamembertSimple(
        data: Map<String, Float>,
        modifier: Modifier = Modifier,
        colors: List<Color> = DefaultColors
    ) {
        val total = data.values.sum()
        val animatedSweep = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            animatedSweep.animateTo(1f, animationSpec = tween(durationMillis = 2000))
        }

        val proportions = data.mapValues { it.value / total * animatedSweep.value }

        Canvas(modifier = modifier.size(220.dp)) {
            val canvasSize = min(size.width, size.height)
            var startAngle = -90f

            data.entries.forEachIndexed { index, entry ->
                val sweepAngle = proportions[entry.key]!! * 360f
                drawArc(
                    color = colors.getOrElse(index) { Color.Gray },
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(canvasSize, canvasSize),
                    topLeft = Offset((size.width - canvasSize) / 2, (size.height - canvasSize) / 2)
                )
                startAngle += sweepAngle
            }

            // Texte au centre
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 48f
                    isAntiAlias = true
                }
                canvas.nativeCanvas.drawText(
                    "${total.toInt()} kWh",
                    size.width / 2,
                    size.height / 2 + 16,
                    paint
                )
            }
        }
    }

    @Composable
    fun CamembertLegend(
        data: Map<String, Float>,
        colors: List<Color> = DefaultColors
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            data.entries.forEachIndexed { index, entry ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(colors.getOrElse(index) { Color.Gray }, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${entry.key}: ${entry.value} kWh")
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
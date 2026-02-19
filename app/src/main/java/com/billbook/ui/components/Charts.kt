package com.billbook.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.billbook.data.repository.CategoryStat
import kotlinx.coroutines.launch

@Composable
fun PieChart(
    data: List<CategoryStat>,
    modifier: Modifier = Modifier,
    holeRadius: Float = 0.5f
) {
    val animationProgress = remember { Animatable(0f) }
    
    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(1000))
    }
    
    Box(modifier = modifier.padding(16.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (data.isEmpty()) return@Canvas
            
            val total = data.sumOf { it.amount }
            if (total <= 0) return@Canvas
            
            val center = Offset(size.width / 2, size.height / 2)
            val radius = (size.minDimension / 2) * 0.9f
            val innerRadius = radius * holeRadius
            
            var startAngle = -90f
            
            data.forEach { stat ->
                val sweepAngle = ((stat.amount / total) * 360 * animationProgress.value).toFloat()
                val color = Color(stat.category?.color?.toLong() ?: 0xFF999999)
                
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = radius - innerRadius, cap = StrokeCap.Round)
                )
                
                startAngle += sweepAngle
            }
        }
    }
}

@Composable
fun BarChart(
    data: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    val animationProgress = remember { Animatable(0f) }
    
    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(800))
    }
    
    Canvas(modifier = modifier.padding(16.dp)) {
        if (data.isEmpty()) return@Canvas
        
        val maxValue = data.maxOrNull() ?: 1f
        val barWidth = size.width / (data.size * 2)
        val spacing = barWidth
        
        data.forEachIndexed { index, value ->
            val barHeight = (value / maxValue) * size.height * animationProgress.value
            val x = spacing + index * (barWidth + spacing)
            val y = size.height - barHeight
            
            drawRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
    }
}

@Composable
fun TrendLineChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    val animationProgress = remember { Animatable(0f) }
    
    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(1000))
    }
    
    Canvas(modifier = modifier.padding(16.dp)) {
        if (data.size < 2) return@Canvas
        
        val maxValue = data.maxOfOrNull { it.second } ?: 1.0
        val minValue = data.minOfOrNull { it.second } ?: 0.0
        val range = maxValue - minValue
        
        if (range <= 0) return@Canvas
        
        val stepX = size.width / (data.size - 1)
        
        val points = data.mapIndexed { index, (_, value) ->
            val x = index * stepX
            val y = size.height - ((value - minValue) / range * size.height * animationProgress.value).toFloat()
            Offset(x, y)
        }
        
        // 绘制线条
        for (i in 0 until points.size - 1) {
            drawLine(
                color = lineColor,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 4f
            )
        }
        
        // 绘制点
        points.forEach { point ->
            drawCircle(
                color = lineColor,
                radius = 6f,
                center = point
            )
        }
    }
}

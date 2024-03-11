package dev.phonecontrol.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

fun simCardImageVector(innerColor: Color = Color(0xFFe6e6e6)): ImageVector {
    return Builder(
        name = "Fuck", defaultWidth = 512.0.dp, defaultHeight = 512.0.dp,
        viewportWidth = 512.0f, viewportHeight = 512.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF303c42)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(444.875f, 109.792f)
            lineTo(338.167f, 3.125f)
            curveToRelative(-2.0f, -2.0f, -4.708f, -3.125f, -7.542f, -3.125f)
            horizontalLineTo(106.667f)
            curveTo(83.135f, 0.0f, 64.0f, 19.135f, 64.0f, 42.667f)
            verticalLineToRelative(426.667f)
            curveTo(64.0f, 492.865f, 83.135f, 512.0f, 106.667f, 512.0f)
            horizontalLineToRelative(298.667f)
            curveTo(428.865f, 512.0f, 448.0f, 492.865f, 448.0f, 469.333f)
            verticalLineToRelative(-352.0f)
            curveToRelative(0.0f, -2.833f, -1.125f, -5.541f, -3.125f, -7.541f)
            close()
        }
        path(
            fill = SolidColor(innerColor), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(426.667f, 469.333f)
            curveToRelative(0.0f, 11.76f, -9.573f, 21.333f, -21.333f, 21.333f)
            horizontalLineTo(106.667f)
            curveToRelative(-11.76f, 0.0f, -21.333f, -9.573f, -21.333f, -21.333f)
            verticalLineTo(42.667f)
            curveToRelative(0.0f, -11.76f, 9.573f, -21.333f, 21.333f, -21.333f)
            horizontalLineToRelative(219.542f)
            lineTo(426.667f, 121.75f)
            verticalLineToRelative(347.583f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF303c42)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(128.0f, 224.0f)
            verticalLineToRelative(192.0f)
            curveToRelative(0.0f, 17.646f, 14.354f, 32.0f, 32.0f, 32.0f)
            horizontalLineToRelative(192.0f)
            curveToRelative(17.646f, 0.0f, 32.0f, -14.354f, 32.0f, -32.0f)
            verticalLineTo(224.0f)
            curveToRelative(0.0f, -17.646f, -14.354f, -32.0f, -32.0f, -32.0f)
            horizontalLineTo(160.0f)
            curveToRelative(-17.646f, 0.0f, -32.0f, 14.354f, -32.0f, 32.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFffca28)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(160.0f, 213.333f)
            horizontalLineToRelative(42.667f)
            verticalLineToRelative(53.333f)
            horizontalLineToRelative(-53.333f)
            verticalLineTo(224.0f)
            curveToRelative(-0.001f, -5.885f, 4.781f, -10.667f, 10.666f, -10.667f)
            close()
            moveTo(224.0f, 213.333f)
            horizontalLineToRelative(64.0f)
            verticalLineToRelative(53.333f)
            horizontalLineToRelative(-64.0f)
            close()
            moveTo(149.333f, 416.0f)
            verticalLineToRelative(-42.667f)
            horizontalLineToRelative(53.333f)
            verticalLineToRelative(53.333f)
            horizontalLineTo(160.0f)
            curveToRelative(-5.885f, 0.001f, -10.667f, -4.781f, -10.667f, -10.666f)
            close()
            moveTo(224.0f, 373.333f)
            horizontalLineToRelative(64.0f)
            verticalLineToRelative(53.333f)
            horizontalLineToRelative(-64.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFffca28)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(352.0f, 426.667f)
            horizontalLineToRelative(-42.667f)
            verticalLineToRelative(-64.0f)
            arcTo(10.66f, 10.66f, 0.0f, false, false, 298.666f, 352.0f)
            horizontalLineTo(149.333f)
            verticalLineToRelative(-64.0f)
            horizontalLineToRelative(213.333f)
            verticalLineToRelative(128.0f)
            curveToRelative(0.001f, 5.885f, -4.781f, 10.667f, -10.666f, 10.667f)
            close()
            moveTo(362.667f, 224.0f)
            verticalLineToRelative(42.667f)
            horizontalLineToRelative(-53.333f)
            verticalLineToRelative(-53.333f)
            horizontalLineTo(352.0f)
            curveToRelative(5.885f, -0.001f, 10.667f, 4.781f, 10.667f, 10.666f)
            close()
        }
        path(
            fill = linearGradient(
                0.0f to Color(0x19000000), 1.0f to Color(0x00000000), start =
                Offset(257.2835f, 321.3096f), end = Offset(420.41925f, 484.424f)
            ), stroke = null,
            strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
            strokeLineMiter = 4.0f, pathFillType = NonZero
        ) {
            moveToRelative(376.293f, 203.309f)
            lineToRelative(-0.021f, 0.079f)
            curveTo(381.022f, 208.973f, 384.0f, 216.111f, 384.0f, 224.0f)
            verticalLineToRelative(192.0f)
            curveToRelative(0.0f, 17.646f, -14.354f, 32.0f, -32.0f, 32.0f)
            horizontalLineTo(160.0f)
            curveToRelative(-8.168f, 0.0f, -15.549f, -3.168f, -21.21f, -8.221f)
            lineToRelative(50.888f, 50.888f)
            horizontalLineToRelative(215.655f)
            curveToRelative(11.76f, 0.0f, 21.333f, -9.573f, 21.333f, -21.333f)
            verticalLineTo(253.682f)
            lineToRelative(-50.373f, -50.373f)
            close()
        }
        path(
            fill = linearGradient(
                0.0f to Color(0x33FFFFFF), 1.0f to Color(0x00FFFFFF), start =
                Offset(5.6572394f, 158.3232f), end = Offset(491.73648f, 385.0535f)
            ), stroke =
            null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
            strokeLineMiter = 4.0f, pathFillType = NonZero
        ) {
            moveTo(444.875f, 109.792f)
            lineTo(338.167f, 3.125f)
            curveToRelative(-2.0f, -2.0f, -4.708f, -3.125f, -7.542f, -3.125f)
            horizontalLineTo(106.667f)
            curveTo(83.135f, 0.0f, 64.0f, 19.135f, 64.0f, 42.667f)
            verticalLineToRelative(426.667f)
            curveTo(64.0f, 492.865f, 83.135f, 512.0f, 106.667f, 512.0f)
            horizontalLineToRelative(298.667f)
            curveTo(428.865f, 512.0f, 448.0f, 492.865f, 448.0f, 469.333f)
            verticalLineToRelative(-352.0f)
            curveToRelative(0.0f, -2.833f, -1.125f, -5.541f, -3.125f, -7.541f)
            close()
        }
    }
        .build()
}


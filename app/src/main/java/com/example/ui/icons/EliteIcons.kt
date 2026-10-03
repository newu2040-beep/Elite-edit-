package com.example.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object EliteIcons {

    private inline fun buildIcon(
        name: String,
        viewportSize: Float = 24f,
        block: ImageVector.Builder.() -> Unit
    ): ImageVector {
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = viewportSize,
            viewportHeight = viewportSize
        ).apply(block).build()
    }

    private inline fun ImageVector.Builder.strokePath(
        strokeWidth: Float = 1.8f,
        noinline block: PathBuilder.() -> Unit
    ) {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = strokeWidth,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathBuilder = block
        )
    }

    private inline fun ImageVector.Builder.fillPath(
        fillColor: Color = Color.White,
        noinline block: PathBuilder.() -> Unit
    ) {
        path(
            fill = SolidColor(fillColor),
            pathBuilder = block
        )
    }

    val Cut: ImageVector by lazy {
        buildIcon("EliteCut") {
            strokePath {
                // Two scissor rings and cross blades
                moveTo(6f, 6f)
                arcTo(3f, 3f, 0f, true, true, 6f, 12f)
                arcTo(3f, 3f, 0f, true, true, 6f, 6f)

                moveTo(6f, 14f)
                arcTo(3f, 3f, 0f, true, true, 6f, 20f)
                arcTo(3f, 3f, 0f, true, true, 6f, 14f)

                moveTo(8.5f, 9.5f)
                lineTo(20f, 19f)

                moveTo(8.5f, 16.5f)
                lineTo(20f, 7f)
            }
        }
    }

    val Split: ImageVector by lazy {
        buildIcon("EliteSplit") {
            strokePath {
                // Left box
                moveTo(4f, 5f)
                lineTo(9.5f, 5f)
                lineTo(9.5f, 19f)
                lineTo(4f, 19f)
                close()

                // Right box
                moveTo(14.5f, 5f)
                lineTo(20f, 5f)
                lineTo(20f, 19f)
                lineTo(14.5f, 19f)
                close()

                // Split dashed divider line
                moveTo(12f, 3f)
                lineTo(12f, 21f)
            }
        }
    }

    val Trim: ImageVector by lazy {
        buildIcon("EliteTrim") {
            strokePath {
                moveTo(6f, 4f)
                lineTo(6f, 20f)
                moveTo(6f, 12f)
                lineTo(10f, 12f)

                moveTo(18f, 4f)
                lineTo(18f, 20f)
                moveTo(18f, 12f)
                lineTo(14f, 12f)
            }
        }
    }

    val Copy: ImageVector by lazy {
        buildIcon("EliteCopy") {
            strokePath {
                moveTo(8f, 8f)
                lineTo(19f, 8f)
                lineTo(19f, 20f)
                lineTo(8f, 20f)
                close()

                moveTo(5f, 16f)
                lineTo(5f, 5f)
                lineTo(16f, 5f)
            }
        }
    }

    val Paste: ImageVector by lazy {
        buildIcon("ElitePaste") {
            strokePath {
                moveTo(9f, 5f)
                lineTo(15f, 5f)
                moveTo(9f, 3f)
                lineTo(15f, 3f)
                lineTo(15f, 6f)
                lineTo(9f, 6f)
                close()

                moveTo(6f, 6f)
                lineTo(6f, 21f)
                lineTo(18f, 21f)
                lineTo(18f, 6f)
            }
        }
    }

    val Duplicate: ImageVector by lazy {
        buildIcon("EliteDuplicate") {
            strokePath {
                moveTo(7f, 7f)
                lineTo(17f, 7f)
                lineTo(17f, 17f)
                lineTo(7f, 17f)
                close()

                moveTo(10f, 7f)
                lineTo(10f, 4f)
                lineTo(20f, 4f)
                lineTo(20f, 14f)
                lineTo(17f, 14f)

                moveTo(12f, 10f)
                lineTo(12f, 14f)
                moveTo(10f, 12f)
                lineTo(14f, 12f)
            }
        }
    }

    val Delete: ImageVector by lazy {
        buildIcon("EliteDelete") {
            strokePath {
                moveTo(4f, 6f)
                lineTo(20f, 6f)
                moveTo(10f, 3f)
                lineTo(14f, 3f)
                moveTo(6f, 6f)
                lineTo(7f, 20f)
                lineTo(17f, 20f)
                lineTo(18f, 6f)
                moveTo(10f, 10f)
                lineTo(10f, 16f)
                moveTo(14f, 10f)
                lineTo(14f, 16f)
            }
        }
    }

    val Undo: ImageVector by lazy {
        buildIcon("EliteUndo") {
            strokePath {
                moveTo(9f, 14f)
                lineTo(4f, 9f)
                lineTo(9f, 4f)
                moveTo(4f, 9f)
                lineTo(14f, 9f)
                arcTo(6f, 6f, 0f, false, true, 20f, 15f)
                arcTo(6f, 6f, 0f, false, true, 14f, 21f)
                lineTo(11f, 21f)
            }
        }
    }

    val Redo: ImageVector by lazy {
        buildIcon("EliteRedo") {
            strokePath {
                moveTo(15f, 14f)
                lineTo(20f, 9f)
                lineTo(15f, 4f)
                moveTo(20f, 9f)
                lineTo(10f, 9f)
                arcTo(6f, 6f, 0f, false, false, 4f, 15f)
                arcTo(6f, 6f, 0f, false, false, 10f, 21f)
                lineTo(13f, 21f)
            }
        }
    }

    val Text: ImageVector by lazy {
        buildIcon("EliteText") {
            strokePath {
                moveTo(4f, 7f)
                lineTo(4f, 4f)
                lineTo(20f, 4f)
                lineTo(20f, 7f)
                moveTo(12f, 4f)
                lineTo(12f, 20f)
                moveTo(9f, 20f)
                lineTo(15f, 20f)
            }
        }
    }

    val Fonts: ImageVector by lazy {
        buildIcon("EliteFonts") {
            strokePath {
                moveTo(5f, 19f)
                lineTo(12f, 4f)
                lineTo(19f, 19f)
                moveTo(7.5f, 14f)
                lineTo(16.5f, 14f)
            }
        }
    }

    val Sticker: ImageVector by lazy {
        buildIcon("EliteSticker") {
            strokePath {
                moveTo(12f, 3f)
                arcTo(9f, 9f, 0f, false, false, 3f, 12f)
                arcTo(9f, 9f, 0f, false, false, 12f, 21f)
                lineTo(21f, 12f)
                arcTo(9f, 9f, 0f, false, false, 12f, 3f)
                close()

                moveTo(12f, 21f)
                lineTo(17f, 16f)
                lineTo(21f, 16f)
            }
        }
    }

    val Audio: ImageVector by lazy {
        buildIcon("EliteAudio") {
            strokePath {
                moveTo(3f, 12f)
                lineTo(3f, 12f)
                moveTo(6f, 9f)
                lineTo(6f, 15f)
                moveTo(9f, 6f)
                lineTo(9f, 18f)
                moveTo(12f, 3f)
                lineTo(12f, 21f)
                moveTo(15f, 7f)
                lineTo(15f, 17f)
                moveTo(18f, 10f)
                lineTo(18f, 14f)
                moveTo(21f, 12f)
                lineTo(21f, 12f)
            }
        }
    }

    val ExtractAudio: ImageVector by lazy {
        buildIcon("EliteExtractAudio") {
            strokePath {
                moveTo(4f, 12f)
                lineTo(4f, 16f)
                moveTo(8f, 9f)
                lineTo(8f, 19f)
                moveTo(12f, 12f)
                lineTo(12f, 21f)
                moveTo(16f, 10f)
                lineTo(16f, 18f)
                moveTo(20f, 13f)
                lineTo(20f, 15f)

                // Arrow pointing down/out
                moveTo(12f, 3f)
                lineTo(12f, 8f)
                moveTo(9f, 5.5f)
                lineTo(12f, 8.5f)
                lineTo(15f, 5.5f)
            }
        }
    }

    val Adjust: ImageVector by lazy {
        buildIcon("EliteAdjust") {
            strokePath {
                // Slider 1
                moveTo(4f, 7f)
                lineTo(20f, 7f)
                moveTo(8f, 5f)
                lineTo(8f, 9f)

                // Slider 2
                moveTo(4f, 12f)
                lineTo(20f, 12f)
                moveTo(16f, 10f)
                lineTo(16f, 14f)

                // Slider 3
                moveTo(4f, 17f)
                lineTo(20f, 17f)
                moveTo(11f, 15f)
                lineTo(11f, 19f)
            }
        }
    }

    val Curves: ImageVector by lazy {
        buildIcon("EliteCurves") {
            strokePath {
                moveTo(4f, 20f)
                lineTo(20f, 20f)
                moveTo(4f, 4f)
                lineTo(4f, 20f)

                // S-curve
                moveTo(4f, 18f)
                curveTo(8f, 18f, 10f, 14f, 12f, 12f)
                curveTo(14f, 10f, 16f, 6f, 20f, 6f)
            }
        }
    }

    val Lut: ImageVector by lazy {
        buildIcon("EliteLut") {
            strokePath {
                moveTo(12f, 3f)
                lineTo(20f, 7.5f)
                lineTo(20f, 16.5f)
                lineTo(12f, 21f)
                lineTo(4f, 16.5f)
                lineTo(4f, 7.5f)
                close()

                moveTo(12f, 3f)
                lineTo(12f, 12f)
                lineTo(20f, 7.5f)
                moveTo(12f, 12f)
                lineTo(4f, 7.5f)
                moveTo(12f, 12f)
                lineTo(12f, 21f)
            }
        }
    }

    val Hsl: ImageVector by lazy {
        buildIcon("EliteHsl") {
            strokePath {
                moveTo(12f, 3f)
                arcTo(9f, 9f, 0f, true, true, 3f, 12f)
                arcTo(9f, 9f, 0f, false, true, 12f, 3f)

                moveTo(12f, 7f)
                lineTo(12f, 12f)
                lineTo(16f, 14f)
            }
        }
    }

    val Speed: ImageVector by lazy {
        buildIcon("EliteSpeed") {
            strokePath {
                moveTo(4f, 18f)
                arcTo(9f, 9f, 0f, true, true, 20f, 18f)
                moveTo(12f, 14f)
                lineTo(16f, 9f)
                moveTo(12f, 13f)
                arcTo(1.5f, 1.5f, 0f, true, true, 12f, 15f)
            }
        }
    }

    val CanvasRatio: ImageVector by lazy {
        buildIcon("EliteCanvas") {
            strokePath {
                moveTo(4f, 4f)
                lineTo(20f, 4f)
                lineTo(20f, 20f)
                lineTo(4f, 20f)
                close()

                moveTo(7f, 9f)
                lineTo(7f, 7f)
                lineTo(9f, 7f)
                moveTo(17f, 9f)
                lineTo(17f, 7f)
                lineTo(15f, 7f)
                moveTo(7f, 15f)
                lineTo(7f, 17f)
                lineTo(9f, 17f)
                moveTo(17f, 15f)
                lineTo(17f, 17f)
                lineTo(15f, 17f)
            }
        }
    }

    val Play: ImageVector by lazy {
        buildIcon("ElitePlay") {
            fillPath {
                moveTo(7f, 5f)
                lineTo(19f, 12f)
                lineTo(7f, 19f)
                close()
            }
        }
    }

    val Pause: ImageVector by lazy {
        buildIcon("ElitePause") {
            fillPath {
                moveTo(7f, 5f)
                lineTo(10.5f, 5f)
                lineTo(10.5f, 19f)
                lineTo(7f, 19f)
                close()

                moveTo(13.5f, 5f)
                lineTo(17f, 5f)
                lineTo(17f, 19f)
                lineTo(13.5f, 19f)
                close()
            }
        }
    }

    val Export: ImageVector by lazy {
        buildIcon("EliteExport") {
            strokePath {
                moveTo(4f, 14f)
                lineTo(4f, 20f)
                lineTo(20f, 20f)
                lineTo(20f, 14f)

                moveTo(12f, 3f)
                lineTo(12f, 15f)
                moveTo(7.5f, 7.5f)
                lineTo(12f, 3f)
                lineTo(16.5f, 7.5f)
            }
        }
    }

    val Settings: ImageVector by lazy {
        buildIcon("EliteSettings") {
            strokePath {
                moveTo(12f, 8f)
                arcTo(4f, 4f, 0f, true, true, 8f, 12f)
                arcTo(4f, 4f, 0f, false, true, 12f, 8f)

                moveTo(12f, 2f)
                lineTo(12f, 4f)
                moveTo(12f, 20f)
                lineTo(12f, 22f)
                moveTo(2f, 12f)
                lineTo(4f, 12f)
                moveTo(20f, 12f)
                lineTo(22f, 12f)
                moveTo(5f, 5f)
                lineTo(6.5f, 6.5f)
                moveTo(17.5f, 17.5f)
                lineTo(19f, 19f)
                moveTo(5f, 19f)
                lineTo(6.5f, 17.5f)
                moveTo(17.5f, 6.5f)
                lineTo(19f, 5f)
            }
        }
    }

    val Import: ImageVector by lazy {
        buildIcon("EliteImport") {
            strokePath {
                moveTo(4f, 14f)
                lineTo(4f, 20f)
                lineTo(20f, 20f)
                lineTo(20f, 14f)

                moveTo(12f, 3f)
                lineTo(12f, 15f)
                moveTo(7.5f, 10.5f)
                lineTo(12f, 15f)
                lineTo(16.5f, 10.5f)
            }
        }
    }

    val Video: ImageVector by lazy {
        buildIcon("EliteVideo") {
            strokePath {
                moveTo(3f, 6f)
                lineTo(21f, 6f)
                lineTo(21f, 18f)
                lineTo(3f, 18f)
                close()

                moveTo(7f, 6f)
                lineTo(7f, 18f)
                moveTo(17f, 6f)
                lineTo(17f, 18f)
                moveTo(3f, 10f)
                lineTo(7f, 10f)
                moveTo(3f, 14f)
                lineTo(7f, 14f)
                moveTo(17f, 10f)
                lineTo(21f, 10f)
                moveTo(17f, 14f)
                lineTo(21f, 14f)
            }
        }
    }

    val Fullscreen: ImageVector by lazy {
        buildIcon("EliteFullscreen") {
            strokePath {
                moveTo(4f, 9f)
                lineTo(4f, 4f)
                lineTo(9f, 4f)

                moveTo(15f, 4f)
                lineTo(20f, 4f)
                lineTo(20f, 9f)

                moveTo(20f, 15f)
                lineTo(20f, 20f)
                lineTo(15f, 20f)

                moveTo(9f, 20f)
                lineTo(4f, 20f)
                lineTo(4f, 15f)
            }
        }
    }
}

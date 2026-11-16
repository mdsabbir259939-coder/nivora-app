package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import java.io.File

data class ImagePresetOption(
    val key: String,
    val label: String
)

val NivoraImagePresets = listOf(
    ImagePresetOption("preset:eid", "Royal Silk Atelier"),
    ImagePresetOption("preset:hijab", "Chiffon Hijab Studio"),
    ImagePresetOption("preset:skincare", "Botanical Skincare"),
    ImagePresetOption("product:abaya", "Modest Abaya Card"),
    ImagePresetOption("product:dress", "Kurti & Dress Card"),
    ImagePresetOption("product:hijab", "Satin Hijab Card"),
    ImagePresetOption("product:serum", "Beauty Care Card"),
    ImagePresetOption("product:bag", "Leather Tote Card")
)

@Composable
fun NivoraArtwork(
    imageKey: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    when {
        imageKey.startsWith("/") && File(imageKey).exists() -> {
            AsyncImage(
                model = File(imageKey),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        imageKey == "preset:eid" -> {
            Image(
                painter = painterResource(id = R.drawable.img_banner_eid),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        imageKey == "preset:hijab" -> {
            Image(
                painter = painterResource(id = R.drawable.img_banner_hijab),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        imageKey == "preset:skincare" -> {
            Image(
                painter = painterResource(id = R.drawable.img_banner_skincare),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        imageKey == "preset:maintenance" -> {
            Image(
                painter = painterResource(id = R.drawable.img_maintenance_art),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        else -> {
            BoutiqueIllustrationCard(
                imageKey = imageKey,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun BoutiqueIllustrationCard(
    imageKey: String,
    modifier: Modifier = Modifier
) {
    val style = when (imageKey) {
        "product:abaya" -> Triple(
            listOf(Color(0xFFECE2D0), Color(0xFFD5C3A8)),
            Color(0xFF0F4C4C),
            Icons.Outlined.Checkroom to "ATELIER ABAYA"
        )
        "product:hijab" -> Triple(
            listOf(Color(0xFFD7E8E5), Color(0xFFB3D3CE)),
            Color(0xFF0D3D3D),
            Icons.Outlined.AutoAwesome to "SATIN COLLECTION"
        )
        "product:serum" -> Triple(
            listOf(Color(0xFFE9E2D2), Color(0xFFCBDAD5)),
            Color(0xFF145341),
            Icons.Outlined.Spa to "BOTANICAL CARE"
        )
        "product:bag" -> Triple(
            listOf(Color(0xFFE7D8C5), Color(0xFFCBB294)),
            Color(0xFF4A311D),
            Icons.Outlined.ShoppingBag to "CRAFTED TOTE"
        )
        else -> Triple(
            listOf(Color(0xFFDDEAE7), Color(0xFFEDE4D3)),
            Color(0xFF0F4C4C),
            Icons.Outlined.Diamond to "NIVORA EDIT"
        )
    }

    val (colors, accent, iconPair) = style
    val (icon: ImageVector, tag: String) = iconPair

    Box(
        modifier = modifier
            .background(Brush.linearGradient(colors))
            .drawBehind {
                drawCircle(
                    color = accent.copy(alpha = 0.08f),
                    radius = size.minDimension * 0.42f,
                    center = Offset(size.width * 0.82f, size.height * 0.22f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.28f),
                    radius = size.minDimension * 0.32f,
                    center = Offset(size.width * 0.16f, size.height * 0.85f)
                )
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = accent.copy(alpha = 0.12f),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = accent.copy(alpha = 0.85f)
            )
        }
    }
}

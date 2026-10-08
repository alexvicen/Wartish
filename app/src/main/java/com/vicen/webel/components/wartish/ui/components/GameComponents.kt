package com.vicen.webel.components.wartish.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vicen.webel.components.wartish.R
import com.vicen.webel.components.wartish.data.Material
import com.vicen.webel.components.wartish.ui.Gold
import com.vicen.webel.components.wartish.ui.Ink
import com.vicen.webel.components.wartish.ui.PanelRaised
import com.vicen.webel.components.wartish.ui.TextMain
import com.vicen.webel.components.wartish.ui.TextSoft
import com.vicen.webel.components.wartish.ui.WartishTheme

@Composable
internal fun PixelMaterial(type: Int, modifier: Modifier = Modifier) {
    val drawable = when (type) {
        0 -> R.drawable.materia_roca
        1 -> R.drawable.materia_madera
        2 -> R.drawable.materia_hierro
        3 -> R.drawable.materia_oro
        4 -> R.drawable.materia_gema
        5 -> R.drawable.piedra
        6 -> R.drawable.tablas_madera
        7 -> R.drawable.lingote_hierro
        8 -> R.drawable.lingote_oro
        else -> R.drawable.gema
    }
    Image(
        bitmap = ImageBitmap.imageResource(drawable),
        contentDescription = Material.entries.getOrNull(type)?.label,
        contentScale = ContentScale.FillBounds,
        filterQuality = androidx.compose.ui.graphics.FilterQuality.None,
        modifier = modifier,
    )
}

@Composable
internal fun GoldButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 46.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Gold,
            contentColor = Ink,
            disabledContainerColor = PanelRaised,
            disabledContentColor = TextSoft
        ),
    ) {
        Text(
            label,
            fontWeight = FontWeight.Black,
            letterSpacing = .6.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun ControlButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 46.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(label, color = Gold, fontWeight = FontWeight.Black, fontSize = 20.sp)
    }
}

@Composable
internal fun RusticBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .clickable(role = Role.Button, onClick = onClick),
        color = RusticWoodDark,
        shape = CutCornerShape(6.dp),
        border = BorderStroke(2.dp, RusticBrass),
        shadowElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "ATRÁS",
                color = RusticParchment,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Preview(name = "Materiales pixel art", showBackground = true)
@Composable
private fun PixelMaterialPreview() {
    WartishTheme {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Material.entries.chunked(5).forEach { rowMaterials ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowMaterials.forEach { material ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PixelMaterial(material.ordinal, Modifier.size(34.dp))
                            Text(material.label, color = TextMain, fontSize = 8.sp)
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Botón principal", showBackground = true)
@Composable
private fun GoldButtonPreview() {
    WartishTheme {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            GoldButton("MEJORAR", onClick = {})
            GoldButton("FALTAN MATERIALES", onClick = {}, enabled = false)
        }
    }
}

@Preview(name = "Botón de control", showBackground = true)
@Composable
private fun ControlButtonPreview() {
    WartishTheme {
        ControlButton("PAUSA", onClick = {})
    }
}

@Preview(name = "Volver al campamento", showBackground = true)
@Composable
private fun RusticBackButtonPreview() {
    WartishTheme {
        RusticBackButton(onClick = {})
    }
}

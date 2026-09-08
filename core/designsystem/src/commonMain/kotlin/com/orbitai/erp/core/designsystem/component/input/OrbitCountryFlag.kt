package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.resources.Res
import com.orbitai.erp.core.designsystem.resources.flag_ae
import com.orbitai.erp.core.designsystem.resources.flag_au
import com.orbitai.erp.core.designsystem.resources.flag_ca
import com.orbitai.erp.core.designsystem.resources.flag_de
import com.orbitai.erp.core.designsystem.resources.flag_gb
import com.orbitai.erp.core.designsystem.resources.flag_in
import com.orbitai.erp.core.designsystem.resources.flag_qa
import com.orbitai.erp.core.designsystem.resources.flag_sa
import com.orbitai.erp.core.designsystem.resources.flag_sg
import com.orbitai.erp.core.designsystem.resources.flag_us
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * Country flag image for [OrbitPhoneField] / country dropdown rows.
 *
 * Bundled PNGs (not emoji) so Android and iOS render the same mark at every density.
 */
@Composable
fun OrbitCountryFlag(
    country: OrbitCountry,
    modifier: Modifier = Modifier,
    size: Dp = OrbitCountryFlagDefaults.Size,
) {
    val res = country.flagDrawable()
    if (res != null) {
        Image(
            painter = painterResource(res),
            contentDescription = "${country.name} flag",
            modifier = modifier
                .size(width = size * 1.35f, height = size)
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.Crop,
        )
    }
}

object OrbitCountryFlagDefaults {
    val Size = 16.dp
}

private fun OrbitCountry.flagDrawable(): DrawableResource? = when (iso2.uppercase()) {
    "IN" -> Res.drawable.flag_in
    "AE" -> Res.drawable.flag_ae
    "SG" -> Res.drawable.flag_sg
    "GB" -> Res.drawable.flag_gb
    "US" -> Res.drawable.flag_us
    "SA" -> Res.drawable.flag_sa
    "QA" -> Res.drawable.flag_qa
    "AU" -> Res.drawable.flag_au
    "DE" -> Res.drawable.flag_de
    "CA" -> Res.drawable.flag_ca
    else -> null
}

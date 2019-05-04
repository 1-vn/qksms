/*
 * Copyright (C) 2019 DiepDT 1-VN <diep@1-vn.com>
 *
 * This file is part of ONESMS.
 *
 * ONESMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ONESMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ONESMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.onevn.ONESMS.feature.plus.experiment

import android.content.Context
import androidx.annotation.StringRes
import com.onevn.ONESMS.R
import com.onevn.ONESMS.experiment.Experiment
import com.onevn.ONESMS.experiment.Variant
import com.onevn.ONESMS.manager.AnalyticsManager
import javax.inject.Inject

class UpgradeButtonExperiment @Inject constructor(
    context: Context,
    analytics: AnalyticsManager
) : Experiment<@StringRes Int>(context, analytics) {

    override val key: String = "Upgrade Button"

    override val variants: List<Variant<Int>> = listOf(
            Variant("variant_a", R.string.onesms_plus_upgrade),
            Variant("variant_b", R.string.onesms_plus_upgrade_b),
            Variant("variant_c", R.string.onesms_plus_upgrade_c),
            Variant("variant_d", R.string.onesms_plus_upgrade_d))

    override val default: Int = R.string.onesms_plus_upgrade

}
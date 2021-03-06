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
package com.onevn.ONESMS.feature.plus

import android.graphics.Typeface
import android.os.Bundle
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding2.view.clicks
import com.onevn.ONESMS.BuildConfig
import com.onevn.ONESMS.R
import com.onevn.ONESMS.common.base.QkThemedActivity
import com.onevn.ONESMS.common.util.BillingManager
import com.onevn.ONESMS.common.util.FontProvider
import com.onevn.ONESMS.common.util.extensions.resolveThemeColor
import com.onevn.ONESMS.common.util.extensions.setBackgroundTint
import com.onevn.ONESMS.common.util.extensions.setTint
import com.onevn.ONESMS.common.util.extensions.setVisible
import com.onevn.ONESMS.common.widget.PreferenceView
import com.onevn.ONESMS.feature.plus.experiment.UpgradeButtonExperiment
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.collapsing_toolbar.*
import kotlinx.android.synthetic.main.preference_view.view.*
import kotlinx.android.synthetic.main.onesms_plus_activity.*
import javax.inject.Inject

class PlusActivity : QkThemedActivity(), PlusView {

    @Inject lateinit var fontProvider: FontProvider
    @Inject lateinit var upgradeButtonExperiment: UpgradeButtonExperiment
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory)[PlusViewModel::class.java] }

    override val upgradeIntent by lazy { upgrade.clicks() }
    override val upgradeDonateIntent by lazy { upgradeDonate.clicks() }
    override val donateIntent by lazy { donate.clicks() }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onesms_plus_activity)
        setTitle(R.string.title_onesms_plus)
        showBackButton(true)
        viewModel.bindView(this)

        free.setVisible(false)

        if (!prefs.systemFont.get()) {
            fontProvider.getLato { lato ->
                val typeface = Typeface.create(lato, Typeface.BOLD)
                collapsingToolbar.setCollapsedTitleTypeface(typeface)
                collapsingToolbar.setExpandedTitleTypeface(typeface)
            }
        }

        // Make the list titles bold
        linearLayout.children
                .mapNotNull { it as? PreferenceView }
                .map { it.titleView }
                .forEach { it.setTypeface(it.typeface, Typeface.BOLD) }

        val textPrimary = resolveThemeColor(android.R.attr.textColorPrimary)
        collapsingToolbar.setCollapsedTitleTextColor(textPrimary)
        collapsingToolbar.setExpandedTitleColor(textPrimary)

        val theme = colors.theme().theme
        donate.setBackgroundTint(theme)
        upgrade.setBackgroundTint(theme)
        thanksIcon.setTint(theme)
    }

    override fun render(state: PlusState) {
        description.text = getString(R.string.onesms_plus_description_summary, state.upgradePrice)
        upgrade.text = getString(upgradeButtonExperiment.variant, state.upgradePrice, state.currency)
        upgradeDonate.text = getString(R.string.onesms_plus_upgrade_donate, state.upgradeDonatePrice, state.currency)

        val fdroid = BuildConfig.FLAVOR == "noAnalytics"

        free.setVisible(fdroid)
        toUpgrade.setVisible(!fdroid && !state.upgraded)
        upgraded.setVisible(!fdroid && state.upgraded)
    }

    override fun initiatePurchaseFlow(billingManager: BillingManager, sku: String) {
        billingManager.initiatePurchaseFlow(this, sku)
    }

}
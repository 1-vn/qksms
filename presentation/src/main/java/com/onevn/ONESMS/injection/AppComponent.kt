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
package com.onevn.ONESMS.injection

import com.onevn.ONESMS.common.QKApplication
import com.onevn.ONESMS.common.QkDialog
import com.onevn.ONESMS.common.util.QkChooserTargetService
import com.onevn.ONESMS.common.widget.AvatarView
import com.onevn.ONESMS.common.widget.PagerTitleView
import com.onevn.ONESMS.common.widget.PreferenceView
import com.onevn.ONESMS.common.widget.QkEditText
import com.onevn.ONESMS.common.widget.QkSwitch
import com.onevn.ONESMS.common.widget.QkTextView
import com.onevn.ONESMS.feature.backup.BackupController
import com.onevn.ONESMS.feature.compose.DetailedChipView
import com.onevn.ONESMS.feature.conversationinfo.injection.ConversationInfoComponent
import com.onevn.ONESMS.feature.settings.SettingsController
import com.onevn.ONESMS.feature.settings.about.AboutController
import com.onevn.ONESMS.feature.settings.swipe.SwipeActionsController
import com.onevn.ONESMS.feature.themepicker.injection.ThemePickerComponent
import com.onevn.ONESMS.feature.widget.WidgetAdapter
import com.onevn.ONESMS.injection.android.ActivityBuilderModule
import com.onevn.ONESMS.injection.android.BroadcastReceiverBuilderModule
import com.onevn.ONESMS.injection.android.ServiceBuilderModule
import com.onevn.ONESMS.util.ContactImageLoader
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityBuilderModule::class,
    BroadcastReceiverBuilderModule::class,
    ServiceBuilderModule::class])
interface AppComponent {

    fun conversationInfoBuilder(): ConversationInfoComponent.Builder
    fun themePickerBuilder(): ThemePickerComponent.Builder

    fun inject(application: QKApplication)

    fun inject(controller: AboutController)
    fun inject(controller: BackupController)
    fun inject(controller: SettingsController)
    fun inject(controller: SwipeActionsController)

    fun inject(dialog: QkDialog)

    fun inject(fetcher: ContactImageLoader.ContactImageFetcher)

    fun inject(service: WidgetAdapter)

    /**
     * This can't use AndroidInjection, or else it will crash on pre-marshmallow devices
     */
    fun inject(service: QkChooserTargetService)

    fun inject(view: AvatarView)
    fun inject(view: DetailedChipView)
    fun inject(view: PagerTitleView)
    fun inject(view: PreferenceView)
    fun inject(view: QkEditText)
    fun inject(view: QkSwitch)
    fun inject(view: QkTextView)

}
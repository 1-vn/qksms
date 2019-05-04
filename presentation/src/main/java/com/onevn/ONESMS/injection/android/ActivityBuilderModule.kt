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
package com.onevn.ONESMS.injection.android

import com.onevn.ONESMS.feature.backup.BackupActivity
import com.onevn.ONESMS.feature.blocked.BlockedActivity
import com.onevn.ONESMS.feature.blocked.BlockedActivityModule
import com.onevn.ONESMS.feature.compose.ComposeActivity
import com.onevn.ONESMS.feature.compose.ComposeActivityModule
import com.onevn.ONESMS.feature.conversationinfo.ConversationInfoActivity
import com.onevn.ONESMS.feature.gallery.GalleryActivity
import com.onevn.ONESMS.feature.gallery.GalleryActivityModule
import com.onevn.ONESMS.feature.main.MainActivity
import com.onevn.ONESMS.feature.main.MainActivityModule
import com.onevn.ONESMS.feature.notificationprefs.NotificationPrefsActivity
import com.onevn.ONESMS.feature.notificationprefs.NotificationPrefsActivityModule
import com.onevn.ONESMS.feature.plus.PlusActivity
import com.onevn.ONESMS.feature.plus.PlusActivityModule
import com.onevn.ONESMS.feature.qkreply.QkReplyActivity
import com.onevn.ONESMS.feature.qkreply.QkReplyActivityModule
import com.onevn.ONESMS.feature.scheduled.ScheduledActivity
import com.onevn.ONESMS.feature.scheduled.ScheduledActivityModule
import com.onevn.ONESMS.feature.settings.SettingsActivity
import com.onevn.ONESMS.injection.scope.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [PlusActivityModule::class])
    abstract fun bindPlusActivity(): PlusActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindBackupActivity(): BackupActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ComposeActivityModule::class])
    abstract fun bindComposeActivity(): ComposeActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindConversationInfoActivity(): ConversationInfoActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [GalleryActivityModule::class])
    abstract fun bindGalleryActivity(): GalleryActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [NotificationPrefsActivityModule::class])
    abstract fun bindNotificationPrefsActivity(): NotificationPrefsActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [QkReplyActivityModule::class])
    abstract fun bindQkReplyActivity(): QkReplyActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ScheduledActivityModule::class])
    abstract fun bindScheduledActivity(): ScheduledActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindSettingsActivity(): SettingsActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [BlockedActivityModule::class])
    abstract fun bindBlockedActivity(): BlockedActivity

}
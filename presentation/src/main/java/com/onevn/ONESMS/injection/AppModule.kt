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

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.ViewModelProvider
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.onevn.ONESMS.common.ViewModelFactory
import com.onevn.ONESMS.common.util.NotificationManagerImpl
import com.onevn.ONESMS.common.util.ShortcutManagerImpl
import com.onevn.ONESMS.feature.conversationinfo.injection.ConversationInfoComponent
import com.onevn.ONESMS.feature.themepicker.injection.ThemePickerComponent
import com.onevn.ONESMS.listener.ContactAddedListener
import com.onevn.ONESMS.listener.ContactAddedListenerImpl
import com.onevn.ONESMS.manager.ActiveConversationManager
import com.onevn.ONESMS.manager.ActiveConversationManagerImpl
import com.onevn.ONESMS.manager.AlarmManager
import com.onevn.ONESMS.manager.AlarmManagerImpl
import com.onevn.ONESMS.manager.AnalyticsManager
import com.onevn.ONESMS.manager.AnalyticsManagerImpl
import com.onevn.ONESMS.manager.ExternalBlockingManager
import com.onevn.ONESMS.manager.ExternalBlockingManagerImpl
import com.onevn.ONESMS.manager.KeyManager
import com.onevn.ONESMS.manager.KeyManagerImpl
import com.onevn.ONESMS.manager.NotificationManager
import com.onevn.ONESMS.manager.PermissionManager
import com.onevn.ONESMS.manager.PermissionManagerImpl
import com.onevn.ONESMS.manager.RatingManager
import com.onevn.ONESMS.manager.ShortcutManager
import com.onevn.ONESMS.manager.WidgetManager
import com.onevn.ONESMS.manager.WidgetManagerImpl
import com.onevn.ONESMS.mapper.CursorToContact
import com.onevn.ONESMS.mapper.CursorToContactImpl
import com.onevn.ONESMS.mapper.CursorToConversation
import com.onevn.ONESMS.mapper.CursorToConversationImpl
import com.onevn.ONESMS.mapper.CursorToMessage
import com.onevn.ONESMS.mapper.CursorToMessageImpl
import com.onevn.ONESMS.mapper.CursorToPart
import com.onevn.ONESMS.mapper.CursorToPartImpl
import com.onevn.ONESMS.mapper.CursorToRecipient
import com.onevn.ONESMS.mapper.CursorToRecipientImpl
import com.onevn.ONESMS.mapper.RatingManagerImpl
import com.onevn.ONESMS.repository.BackupRepository
import com.onevn.ONESMS.repository.BackupRepositoryImpl
import com.onevn.ONESMS.repository.ContactRepository
import com.onevn.ONESMS.repository.ContactRepositoryImpl
import com.onevn.ONESMS.repository.ConversationRepository
import com.onevn.ONESMS.repository.ConversationRepositoryImpl
import com.onevn.ONESMS.repository.ImageRepository
import com.onevn.ONESMS.repository.ImageRepostoryImpl
import com.onevn.ONESMS.repository.MessageRepository
import com.onevn.ONESMS.repository.MessageRepositoryImpl
import com.onevn.ONESMS.repository.ScheduledMessageRepository
import com.onevn.ONESMS.repository.ScheduledMessageRepositoryImpl
import com.onevn.ONESMS.repository.SyncRepository
import com.onevn.ONESMS.repository.SyncRepositoryImpl
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(subcomponents = [
    ConversationInfoComponent::class,
    ThemePickerComponent::class])
class AppModule(private var application: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context = application

    @Provides
    fun provideContentResolver(context: Context): ContentResolver = context.contentResolver

    @Provides
    @Singleton
    fun provideRxPreferences(context: Context): RxSharedPreferences {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return RxSharedPreferences.create(preferences)
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory = factory

    // Listener

    @Provides
    fun provideContactAddedListener(listener: ContactAddedListenerImpl): ContactAddedListener = listener

    // Manager

    @Provides
    fun provideActiveConversationManager(manager: ActiveConversationManagerImpl): ActiveConversationManager = manager

    @Provides
    fun provideAlarmManager(manager: AlarmManagerImpl): AlarmManager = manager

    @Provides
    fun provideAnalyticsManager(manager: AnalyticsManagerImpl): AnalyticsManager = manager

    @Provides
    fun externalBlockingManager(manager: ExternalBlockingManagerImpl): ExternalBlockingManager = manager

    @Provides
    fun provideKeyManager(manager: KeyManagerImpl): KeyManager = manager

    @Provides
    fun provideNotificationsManager(manager: NotificationManagerImpl): NotificationManager = manager

    @Provides
    fun providePermissionsManager(manager: PermissionManagerImpl): PermissionManager = manager

    @Provides
    fun provideRatingManager(manager: RatingManagerImpl): RatingManager = manager

    @Provides
    fun provideShortcutManager(manager: ShortcutManagerImpl): ShortcutManager = manager

    @Provides
    fun provideWidgetManager(manager: WidgetManagerImpl): WidgetManager = manager


    // Mapper

    @Provides
    fun provideCursorToContact(mapper: CursorToContactImpl): CursorToContact = mapper

    @Provides
    fun provideCursorToConversation(mapper: CursorToConversationImpl): CursorToConversation = mapper

    @Provides
    fun provideCursorToMessage(mapper: CursorToMessageImpl): CursorToMessage = mapper

    @Provides
    fun provideCursorToPart(mapper: CursorToPartImpl): CursorToPart = mapper

    @Provides
    fun provideCursorToRecipient(mapper: CursorToRecipientImpl): CursorToRecipient = mapper


    // Repository

    @Provides
    fun provideBackupRepository(repository: BackupRepositoryImpl): BackupRepository = repository

    @Provides
    fun provideContactRepository(repository: ContactRepositoryImpl): ContactRepository = repository

    @Provides
    fun provideConversationRepository(repository: ConversationRepositoryImpl): ConversationRepository = repository

    @Provides
    fun provideImageRepository(repository: ImageRepostoryImpl): ImageRepository = repository

    @Provides
    fun provideMessageRepository(repository: MessageRepositoryImpl): MessageRepository = repository

    @Provides
    fun provideScheduledMessagesRepository(repository: ScheduledMessageRepositoryImpl): ScheduledMessageRepository = repository

    @Provides
    fun provideSyncRepository(repository: SyncRepositoryImpl): SyncRepository = repository

}
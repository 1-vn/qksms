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
package com.onevn.ONESMS.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.onevn.ONESMS.interactor.SendScheduledMessage
import com.onevn.ONESMS.repository.MessageRepository
import dagger.android.AndroidInjection
import javax.inject.Inject

class SendScheduledMessageReceiver : BroadcastReceiver() {

    @Inject lateinit var messageRepo: MessageRepository
    @Inject lateinit var sendScheduledMessage: SendScheduledMessage

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        intent.getLongExtra("id", -1L).takeIf { it >= 0 }?.let { id ->
            sendScheduledMessage.buildObservable(id).blockingSubscribe()
        }
    }

}
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

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import com.onevn.ONESMS.interactor.MarkFailed
import com.onevn.ONESMS.interactor.MarkSent
import dagger.android.AndroidInjection
import javax.inject.Inject

class SmsSentReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION = "com.onevn.ONESMS.SMS_SENT"
    }

    @Inject lateinit var markSent: MarkSent
    @Inject lateinit var markFailed: MarkFailed

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        context.unregisterReceiver(this)

        val id = intent.getLongExtra("id", 0L)

        when (resultCode) {
            Activity.RESULT_OK -> {
                val pendingResult = goAsync()
                markSent.execute(id) { pendingResult.finish() }
            }

            SmsManager.RESULT_ERROR_GENERIC_FAILURE,
            SmsManager.RESULT_ERROR_NO_SERVICE,
            SmsManager.RESULT_ERROR_NULL_PDU,
            SmsManager.RESULT_ERROR_RADIO_OFF -> {
                val pendingResult = goAsync()
                markFailed.execute(MarkFailed.Params(id, resultCode)) { pendingResult.finish() }
            }
        }
    }
}
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
package com.onevn.ONESMS.compat

import android.content.Context
import android.os.Build
import android.telephony.SubscriptionManager
import com.onevn.ONESMS.manager.PermissionManager
import javax.inject.Inject

class SubscriptionManagerCompat @Inject constructor(context: Context, private val permissions: PermissionManager) {

    private val subscriptionManager: SubscriptionManager?
        get() = field?.takeIf { permissions.hasPhone() }

    val activeSubscriptionInfoList: List<SubscriptionInfoCompat>
        get() {
            return if (Build.VERSION.SDK_INT >= 22) {
                subscriptionManager?.activeSubscriptionInfoList?.map { SubscriptionInfoCompat(it) } ?: listOf()
            } else listOf()
        }

    init {
        subscriptionManager = if (Build.VERSION.SDK_INT >= 22) SubscriptionManager.from(context) else null
    }

    fun addOnSubscriptionsChangedListener(listener: OnSubscriptionsChangedListener) {
        if (Build.VERSION.SDK_INT >= 22) {
            subscriptionManager?.addOnSubscriptionsChangedListener(listener.listener)
        }
    }

    fun removeOnSubscriptionsChangedListener(listener: OnSubscriptionsChangedListener) {
        if (Build.VERSION.SDK_INT >= 22) {
            subscriptionManager?.removeOnSubscriptionsChangedListener(listener.listener)
        }
    }

    abstract class OnSubscriptionsChangedListener {

        val listener: SubscriptionManager.OnSubscriptionsChangedListener? = if (Build.VERSION.SDK_INT >= 22) {
            object : SubscriptionManager.OnSubscriptionsChangedListener() {
                override fun onSubscriptionsChanged() {
                    this@OnSubscriptionsChangedListener.onSubscriptionsChanged()
                }
            }
        } else null

        abstract fun onSubscriptionsChanged()

    }

}
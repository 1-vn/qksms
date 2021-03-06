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
package com.onevn.ONESMS.feature.conversationinfo

import com.onevn.ONESMS.common.base.QkViewContract
import io.reactivex.Observable

interface ConversationInfoView : QkViewContract<ConversationInfoState> {

    fun nameClicks(): Observable<*>
    fun nameChanges(): Observable<String>
    fun notificationClicks(): Observable<*>
    fun themeClicks(): Observable<*>
    fun archiveClicks(): Observable<*>
    fun blockClicks(): Observable<*>
    fun deleteClicks(): Observable<*>
    fun confirmDelete(): Observable<*>

    fun showNameDialog(name: String)
    fun showThemePicker(threadId: Long)
    fun showDeleteDialog()

}
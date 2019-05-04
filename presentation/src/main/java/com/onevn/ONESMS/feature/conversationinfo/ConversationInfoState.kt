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

import com.onevn.ONESMS.model.MmsPart
import com.onevn.ONESMS.model.Recipient
import io.realm.RealmList
import io.realm.RealmResults

data class ConversationInfoState(
    val name: String = "",
    val recipients: RealmList<Recipient>? = null,
    val threadId: Long = 0,
    val archived: Boolean = false,
    val blocked: Boolean = false,
    val media: RealmResults<MmsPart>? = null,
    val hasError: Boolean = false
)
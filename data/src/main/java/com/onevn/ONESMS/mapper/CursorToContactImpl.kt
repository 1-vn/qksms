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
package com.onevn.ONESMS.mapper

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract.CommonDataKinds.Phone.*
import com.onevn.ONESMS.manager.PermissionManager
import com.onevn.ONESMS.model.Contact
import com.onevn.ONESMS.model.PhoneNumber
import javax.inject.Inject

class CursorToContactImpl @Inject constructor(
    private val context: Context,
    private val permissionManager: PermissionManager
) : CursorToContact {

    companion object {
        val URI = CONTENT_URI
        val PROJECTION = arrayOf(LOOKUP_KEY, NUMBER, TYPE, DISPLAY_NAME)

        const val COLUMN_LOOKUP_KEY = 0
        const val COLUMN_NUMBER = 1
        const val COLUMN_TYPE = 2
        const val COLUMN_DISPLAY_NAME = 3
    }

    override fun map(from: Cursor) = Contact().apply {
        lookupKey = from.getString(COLUMN_LOOKUP_KEY)
        name = from.getString(COLUMN_DISPLAY_NAME) ?: ""
        numbers.add(PhoneNumber().apply {
            address = from.getString(COLUMN_NUMBER) ?: ""
            type = context.getString(getTypeLabelResource(from.getInt(COLUMN_TYPE)))
        })
        lastUpdate = System.currentTimeMillis()
    }

    override fun getContactsCursor(): Cursor? {
        return when (permissionManager.hasContacts()) {
            true -> context.contentResolver.query(URI, PROJECTION, null, null, null)
            false -> null
        }
    }

}
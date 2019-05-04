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
package com.onevn.ONESMS.feature.scheduled

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.onevn.ONESMS.R
import com.onevn.ONESMS.common.base.QkAdapter
import com.onevn.ONESMS.common.base.QkViewHolder
import com.onevn.ONESMS.util.GlideApp
import kotlinx.android.synthetic.main.attachment_image_list_item.view.*
import javax.inject.Inject

class ScheduledMessageAttachmentAdapter @Inject constructor() : QkAdapter<Uri>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scheduled_message_image_list_item, parent, false)
        view.thumbnail.clipToOutline = true

        return QkViewHolder(view)
    }

    override fun onBindViewHolder(holder: QkViewHolder, position: Int) {
        val attachment = getItem(position)
        val view = holder.containerView

        GlideApp.with(view).load(attachment).into(view.thumbnail)
    }

}
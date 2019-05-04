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
package com.onevn.ONESMS.repository

import com.onevn.ONESMS.model.Attachment
import com.onevn.ONESMS.model.Message
import com.onevn.ONESMS.model.MmsPart
import io.realm.RealmResults

interface MessageRepository {

    fun getMessages(threadId: Long, query: String = ""): RealmResults<Message>

    fun getMessage(id: Long): Message?

    fun getMessageForPart(id: Long): Message?

    fun getUnreadCount(): Long

    fun getPart(id: Long): MmsPart?

    fun getPartsForConversation(threadId: Long): RealmResults<MmsPart>

    /**
     * Retrieves the list of messages which should be shown in the notification
     * for a given conversation
     */
    fun getUnreadUnseenMessages(threadId: Long): RealmResults<Message>

    /**
     * Retrieves the list of messages which should be shown in the quickreply popup
     * for a given conversation
     */
    fun getUnreadMessages(threadId: Long): RealmResults<Message>

    fun markAllSeen()

    fun markSeen(threadId: Long)

    fun markRead(vararg threadIds: Long)

    fun markUnread(vararg threadIds: Long)

    fun sendMessage(
        subId: Int,
        threadId: Long,
        addresses: List<String>,
        body: String,
        attachments: List<Attachment>,
        delay: Int = 0
    )

    /**
     * Attempts to send the SMS message. This can be called if the message has already been persisted
     */
    fun sendSms(message: Message)

    /**
     * Attempts to cancel sending the message with the given id
     */
    fun cancelDelayedSms(id: Long)

    fun insertSentSms(subId: Int, threadId: Long, address: String, body: String, date: Long): Message

    fun insertReceivedSms(subId: Int, address: String, body: String, sentTime: Long): Message

    /**
     * Marks the message as sending, in case we need to retry sending it
     */
    fun markSending(id: Long)

    fun markSent(id: Long)

    fun markFailed(id: Long, resultCode: Int)

    fun markDelivered(id: Long)

    fun markDeliveryFailed(id: Long, resultCode: Int)

    fun deleteMessages(vararg messageIds: Long)

}
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
package com.onevn.ONESMS.interactor

import com.onevn.ONESMS.model.Message
import com.onevn.ONESMS.repository.MessageRepository
import io.reactivex.Flowable
import javax.inject.Inject

class RetrySending @Inject constructor(private val messageRepo: MessageRepository) : Interactor<Message>() {

    override fun buildObservable(params: Message): Flowable<Message> {

        // We don't want to touch the supplied message on another thread in case it's a live realm
        // object, so copy the required fields into a new object that is safe to pass around threads
        val message = Message().apply {
            id = params.id
            type = params.type
            address = params.address
            body = params.body
            subId = params.subId
        }

        return Flowable.just(message)
                .filter { message.isSms() } // TODO support resending failed MMS
                .doOnNext { messageRepo.markSending(message.id) }
                .doOnNext { messageRepo.sendSms(message) }
    }

}
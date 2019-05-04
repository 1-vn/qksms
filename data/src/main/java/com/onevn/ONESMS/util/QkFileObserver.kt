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
package com.onevn.ONESMS.util

import android.os.FileObserver
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.io.File

class QkFileObserver(path: String) : FileObserver(path, CREATE or DELETE or MODIFY) {

    private val subject = BehaviorSubject.createDefault<String>(path).toSerialized()

    val observable: Observable<String> = subject
            .doOnSubscribe { startWatching() }
            .doOnDispose { stopWatching() }
            .share()

    init {
        // Make sure that the directory exists
        tryOrNull { File(path).mkdirs() }
    }

    override fun onEvent(event: Int, path: String?) {
        path?.let(subject::onNext)
    }

}
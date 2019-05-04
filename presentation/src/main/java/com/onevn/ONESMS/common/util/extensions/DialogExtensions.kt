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
package com.onevn.ONESMS.common.util.extensions

import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import io.reactivex.subjects.Subject

fun AlertDialog.Builder.setPositiveButton(@StringRes textId: Int, subject: Subject<Unit>): AlertDialog.Builder {
    return setPositiveButton(textId) { _, _ -> subject.onNext(Unit) }
}

fun AlertDialog.Builder.setNegativeButton(@StringRes textId: Int, subject: Subject<Unit>): AlertDialog.Builder {
    return setNegativeButton(textId) { _, _ -> subject.onNext(Unit) }
}

fun AlertDialog.Builder.setNeutralButton(@StringRes textId: Int, subject: Subject<Unit>): AlertDialog.Builder {
    return setNeutralButton(textId) { _, _ -> subject.onNext(Unit) }
}
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

import com.onevn.ONESMS.repository.BackupRepository
import io.reactivex.Flowable
import javax.inject.Inject

class PerformRestore @Inject constructor(
    private val backupRepo: BackupRepository
) : Interactor<String>() {

    override fun buildObservable(params: String): Flowable<*> {
        return Flowable.just(params)
                .doOnNext(backupRepo::performRestore)
    }

}
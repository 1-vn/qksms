/*
 * Copyright (C) 2017 Moez Bhatti <moez.bhatti@gmail.com>
 *
 * This file is part of QKSMS.
 *
 * QKSMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QKSMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QKSMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.moez.QKSMS.feature.compose

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.moez.QKSMS.R
import com.moez.QKSMS.common.base.QkController
import com.moez.QKSMS.common.util.DateFormatter
import com.moez.QKSMS.common.util.extensions.autoScrollToStart
import com.moez.QKSMS.common.util.extensions.resolveThemeColor
import com.moez.QKSMS.common.util.extensions.scrapViews
import com.moez.QKSMS.common.util.extensions.setBackgroundTint
import com.moez.QKSMS.common.util.extensions.setTint
import com.moez.QKSMS.common.util.extensions.setVisible
import com.moez.QKSMS.common.widget.ChipLayout
import com.moez.QKSMS.extensions.plus
import com.moez.QKSMS.feature.compose.injection.ComposeModule
import com.moez.QKSMS.injection.appComponent
import com.moez.QKSMS.model.Attachment
import com.moez.QKSMS.model.Contact
import com.moez.QKSMS.model.Message
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.compose_controller.*
import kotlinx.android.synthetic.main.toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ComposeController(
        val query: String = "",
        val threadId: Long = 0L,
        val address: String = "",
        val sharedText: String = "",
        val sharedAttachments: List<Attachment> = listOf()
) : QkController<ComposeView, ComposeState, ComposePresenter>(), ComposeView {

    companion object {
        const val CAMERA_REQUEST_CODE = 0
        const val GALLERY_REQUEST_CODE = 1
    }

    @Inject lateinit var attachmentAdapter: AttachmentAdapter
    @Inject lateinit var contactsAdapter: ContactAdapter
    @Inject lateinit var dateFormatter: DateFormatter
    @Inject lateinit var messageAdapter: MessagesAdapter

    @Inject override lateinit var presenter: ComposePresenter

    private val activityVisibleSubject: Subject<Boolean> = PublishSubject.create()
    private val queryChangeSubject: Subject<CharSequence> = PublishSubject.create()
    private val queryBackspaceSubject: Subject<Unit> = PublishSubject.create()
    private val queryEditorActionsSubject: Subject<Int> = PublishSubject.create()
    private val chipDeletedSubject: Subject<Contact> = PublishSubject.create()
    private val attachmentSelectedSubject: Subject<Uri> = PublishSubject.create()
    private val scheduleSelectedSubject: Subject<Long> = PublishSubject.create()
    private val qksmsPlusClicksSubject: Subject<Unit> = PublishSubject.create()
    private val backPressSubject: Subject<Unit> = PublishSubject.create()

    private var cameraDestination: Uri? = null

    init {
        appComponent
                .composeInfoBuilder()
                .composeModule(ComposeModule(this))
                .build()
                .inject(this)

        retainViewMode = RetainViewMode.RETAIN_DETACH
        layoutRes = R.layout.compose_controller
        menuRes = R.menu.compose
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        showBackButton(true)
    }

    override fun onViewCreated() {
        super.onViewCreated()

        contacts.itemAnimator = null

        messageAdapter.autoScrollToStart(messageList)
        messageAdapter.emptyView = messagesEmpty

        messageList.setHasFixedSize(true)
        messageList.adapter = messageAdapter

        attachments.adapter = attachmentAdapter

        message.supportsInputContent = true

        themedActivity?.theme
                ?.doOnNext { loading.setTint(it.theme) }
                ?.doOnNext { attach.setBackgroundTint(it.theme) }
                ?.doOnNext { attach.setTint(it.textPrimary) }
                ?.doOnNext { messageAdapter.theme = it }
                ?.autoDisposable(scope())
                ?.subscribe { messageList.scrapViews() }

        activity?.apply {
            window.callback = ComposeWindowCallback(window.callback, this)

            // These theme attributes don't apply themselves on API 21
            if (Build.VERSION.SDK_INT <= 22) {
                messageBackground.setBackgroundTint(resolveThemeColor(R.attr.bubbleColor))
                composeBackground.setBackgroundTint(resolveThemeColor(R.attr.composeBackground))
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {
        activityVisibleSubject.onNext(true)
    }

    override fun onActivityPaused(activity: Activity) {
        activityVisibleSubject.onNext(false)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val search = menu.findItem(R.id.search)
        val searchView = search?.actionView as? ChipLayout

        searchView?.textChanges?.autoDisposable(scope())?.subscribe(queryChangeSubject)
        searchView?.backspaces?.autoDisposable(scope())?.subscribe(queryBackspaceSubject)
        searchView?.actions?.autoDisposable(scope())?.subscribe(queryEditorActionsSubject)
        searchView?.chipDeleted?.autoDisposable(scope())?.subscribe(chipDeletedSubject)
    }

    override fun render(state: ComposeState) {
        if (state.hasError) {
            activity?.finish()
            return
        }

        themedActivity?.threadId?.onNext(state.selectedConversation)

        setTitle(when {
            state.selectedMessages > 0 -> activity?.getString(R.string.compose_title_selected, state.selectedMessages)
            state.query.isNotEmpty() -> state.query
            else -> state.conversationtitle
        })

        themedActivity?.toolbar?.subtitle = activity?.getString(R.string.compose_subtitle_results, state.searchSelectionPosition, state.searchResults).takeIf { state.query.isNotEmpty() }

        contacts.setVisible(state.contactsVisible)
        composeBar.setVisible(!state.contactsVisible && !state.loading)

        // Don't set the adapters unless needed
        if (state.editingMode && contacts.adapter == null) contacts.adapter = contactsAdapter


        themedActivity?.toolbar?.menu?.run {
            val search = findItem(R.id.search)
            val searchView = search?.actionView as? ChipLayout

            search?.isVisible = state.editingMode
            searchView?.setChips(state.selectedContacts)
            when {
                state.editingMode && search?.isActionViewExpanded == false -> search.expandActionView()
                !state.editingMode && search?.isActionViewExpanded == true -> search.collapseActionView()
            }

            findItem(R.id.call)?.isVisible = !state.editingMode && state.selectedMessages == 0 && state.query.isEmpty()
            findItem(R.id.info)?.isVisible = !state.editingMode && state.selectedMessages == 0 && state.query.isEmpty()
            findItem(R.id.copy)?.isVisible = !state.editingMode && state.selectedMessages == 1
            findItem(R.id.details)?.isVisible = !state.editingMode && state.selectedMessages == 1
            findItem(R.id.delete)?.isVisible = !state.editingMode && state.selectedMessages > 0
            findItem(R.id.forward)?.isVisible = !state.editingMode && state.selectedMessages == 1
            findItem(R.id.previous)?.isVisible = state.selectedMessages == 0 && state.query.isNotEmpty()
            findItem(R.id.next)?.isVisible = state.selectedMessages == 0 && state.query.isNotEmpty()
            findItem(R.id.clear)?.isVisible = state.selectedMessages == 0 && state.query.isNotEmpty()
        }

        contactsAdapter.data = state.contacts

        loading.setVisible(state.loading)

        sendAsGroup.setVisible(state.editingMode && state.selectedContacts.size >= 2)
        sendAsGroupSwitch.isChecked = state.sendAsGroup

        messageList.setVisible(state.sendAsGroup)
        messageAdapter.data = state.messages
        messageAdapter.highlight = state.searchSelectionId

        scheduledGroup.isVisible = state.scheduled != 0L
        scheduledTime.text = dateFormatter.getScheduledTimestamp(state.scheduled)

        attachments.setVisible(state.attachments.isNotEmpty())
        attachmentAdapter.data = state.attachments

        attach.animate().rotation(if (state.attaching) 45f else 0f).start()
        attaching.isVisible = state.attaching

        counter.text = state.remaining
        counter.setVisible(counter.text.isNotBlank())

        sim.setVisible(state.subscription != null)
        sim.contentDescription = activity?.getString(R.string.compose_sim_cd, state.subscription?.displayName)
        simIndex.text = "${state.subscription?.simSlotIndex?.plus(1)}"

        send.isEnabled = state.canSend
        send.imageAlpha = if (state.canSend) 255 else 128
    }

    override fun activityVisible(): Observable<Boolean> = activityVisibleSubject

    override fun queryChanges(): Observable<CharSequence> = queryChangeSubject

    override fun queryBackspaces(): Observable<*> = queryBackspaceSubject

    override fun queryEditorActions(): Observable<Int> = queryEditorActionsSubject

    override fun chipSelected(): Observable<Contact> = contactsAdapter.contactSelected

    override fun chipDeleted(): Observable<Contact> = chipDeletedSubject

    override fun menuReady(): Observable<*> = themedActivity?.menu ?: Observable.empty<Menu>()

    override fun optionItemSelected(): Observable<Int> = optionsItemSubject

    override fun sendAsGroupToggled(): Observable<*> = sendAsGroupBackground.clicks()

    override fun messageClicks(): Subject<Message> = messageAdapter.clicks

    override fun messagesSelected(): Observable<List<Long>> = messageAdapter.selectionChanges

    override fun sendingCancelled(): Subject<Message> = messageAdapter.cancelSending

    override fun attachmentDeleted(): Subject<Attachment> = attachmentAdapter.attachmentDeleted

    override fun textChanged(): Observable<CharSequence> = message.textChanges()

    override fun attachClicks(): Observable<*> = attach.clicks() + attachingBackground.clicks()

    override fun cameraClicks(): Observable<*> = camera.clicks() + cameraLabel.clicks()

    override fun galleryClicks(): Observable<*> = gallery.clicks() + galleryLabel.clicks()

    override fun scheduleClicks(): Observable<*> = schedule.clicks() + scheduleLabel.clicks()

    override fun attachmentSelected(): Observable<Uri> = attachmentSelectedSubject

    override fun inputContentSelected(): Observable<InputContentInfoCompat> = message.inputContentSelected

    override fun scheduleTimeSelected(): Observable<Long> = scheduleSelectedSubject

    override fun scheduleCancelled(): Observable<*> = sim.clicks()

    override fun simChanged(): Observable<*> = scheduledCancel.clicks()

    override fun sendClicks(): Observable<*> = send.clicks()

    override fun qksmsPlusClicks(): Subject<*> = qksmsPlusClicksSubject

    override fun backPresses(): Observable<*> = backPressSubject

    override fun clearSelection() {
        messageAdapter.clearSelection()
    }

    override fun showDetails(details: String) {
        activity?.apply {
            AlertDialog.Builder(this)
                    .setTitle(R.string.compose_details_title)
                    .setMessage(details)
                    .setCancelable(true)
                    .show()
        }
    }

    override fun requestStoragePermission() {
        activity?.apply {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }
    }

    override fun requestSmsPermission() {
        activity?.run {
            ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS), 0)
        }
    }

    override fun requestDatePicker() {
        activity?.apply {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    scheduleSelectedSubject.onNext(calendar.timeInMillis)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this)).show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    override fun requestCamera() {
        cameraDestination = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                .let { timestamp -> ContentValues().apply { put(MediaStore.Images.Media.TITLE, timestamp) } }
                .let { cv -> activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv) }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, cameraDestination)
        startActivityForResult(Intent.createChooser(intent, null), CAMERA_REQUEST_CODE)
    }

    override fun requestGallery() {
        val intent = Intent(Intent.ACTION_PICK)
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                .putExtra(Intent.EXTRA_LOCAL_ONLY, false)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setType("image/*")
        startActivityForResult(Intent.createChooser(intent, null), GALLERY_REQUEST_CODE)
    }

    override fun setDraft(draft: String) {
        message.setText(draft)
    }

    override fun scrollToMessage(id: Long) {
        messageAdapter.data?.second
                ?.indexOfLast { message -> message.id == id }
                ?.takeIf { position -> position != -1 }
                ?.let(messageList::scrollToPosition)
    }

    override fun showQksmsPlusSnackbar(message: Int) {
        Snackbar.make(contentView, message, Snackbar.LENGTH_LONG).run {
            setAction(R.string.button_more) { qksmsPlusClicksSubject.onNext(Unit) }
            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> cameraDestination
                GALLERY_REQUEST_CODE -> data?.data
                else -> null
            }?.let(attachmentSelectedSubject::onNext)
        }
    }

    override fun handleBack(): Boolean {
        super.handleBack()
        backPressSubject.onNext(Unit)
        return true
    }

}
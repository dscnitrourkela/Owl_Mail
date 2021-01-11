package github.sachin2dehury.nitrmail.others

import com.google.android.play.core.install.model.AppUpdateType

object Constants {

    const val BASE_URL = "https://mail.nitrkl.ac.in/"
    const val HOME_URL = "home/~/"
    const val I_MESSAGE_URL = "h/imessage"
    const val MESSAGE_URL = "h/message"
    const val COMPOSE_URL = "h/?action=compose"
    //"m/zmain?st=newmail"

    const val NOTIFICATION_ID = "NITR Mail Notification Sync"
    const val NOTIFICATION_CHANNEL = "NITR Mail Notification"

    const val SYNC_DELAY_TIME = 3600000L
    const val SYNC_WORK_MANAGER = "SYNC_WORK_MANAGER"

    const val INBOX_URL = "inbox.json"
    const val SENT_URL = "sent.json"
    const val DRAFT_URL = "drafts.json"
    const val JUNK_URL = "junk.json"
    const val TRASH_URL = "trash.json"

    const val CALENDAR_URL = "calendar.json"
    const val CONTACTS_URL = "contacts.json"
    const val TASKS_URL = "tasks.json"

    const val UPDATE_QUERY = "after:"

    const val AUTH_COOKIE = "co"
    const val AUTH_SET_COOKIE = "sc"
    const val AUTH_QUERY = "qp"

    const val DATABASE_NAME = "MAIL_DB"

    const val DATA_STORE_NAME = "MAIL_CREDENTIAL"

    const val KEY_CREDENTIAL = "KEY_CREDENTIAL"
    const val KEY_LAST_SYNC = "KEY_LAST_SYNC"
    const val KEY_TOKEN = "KEY_TOKEN"

    const val NO_CREDENTIAL = "NO_CREDENTIAL"
    const val NO_LAST_SYNC = 0L
    const val NO_TOKEN = "NO_TOKEN"

    const val DATE_FORMAT_YEAR = "dd-MM-yyyy"
    const val DATE_FORMAT_MONTH = "dd-MM"
    const val DATE_FORMAT_DATE = "dd-MM HH:mm"

    const val APP_UPDATE_TYPE = AppUpdateType.IMMEDIATE
}
package github.sachin2dehury.nitrmail.others

object Constants {

    const val BASE_URL = "https://mail.nitrkl.ac.in/home/~/"

    const val INBOX_URL = "inbox.json"
    const val SENT_URL = "sent.json"
    const val DRAFT_URL = "drafts.json"
    const val CONTACTS_URL = "contacts.json"
    const val TASKS_URL = "tasks.json"
    const val JUNK_URL = "junk.json"
    const val TRASH_URL = "trash.json"
    const val CALENDAR_URL = "calendar.json"
    const val ITEM_URL = "/?id="
//    https://mail.nitrkl.ac.in/service/home/~/?auth=co&view=text&id=
//    add query params

    const val INBOX_DATABASE = "inbox"
    const val SENT_DATABASE = "sent"
    const val DRAFT_DATABASE = "draft"
    const val JUNK_DATABASE = "junk"
    const val TRASH_DATABASE = "trash"

    const val ENCRYPTED_SHARED_PREF_NAME = "enc_shared_pref"

    const val KEY_LOGGED_IN_EMAIL = "KEY_LOGGED_IN_EMAIL"
    const val KEY_PASSWORD = "KEY_PASSWORD"

    const val NO_EMAIL = "NO_EMAIL"
    const val NO_PASSWORD = "NO_PASSWORD"

    const val DATE_FORMAT = "dd-MM-yyyy HH:mm"
}
package github.sachin2dehury.owlmail.others

object Constants {

    const val BASE_URL = "https://mail.nitrkl.ac.in/"
    const val HOME_URL = "home/~/"
    const val MOBILE_URL = "m/zmain"
    const val HTML_URL = "h/printmessage"

    const val NOTIFICATION_ID = "NITR Mail Notification Sync"
    const val NOTIFICATION_CHANNEL = "NITR Mail Notification"

//    const val CALENDAR_URL = "Calendar.json"
//    const val CONTACTS_URL = "Contacts.json"
//    const val TASKS_URL = "Tasks.json"

    const val AUTH_FROM_COOKIE = "?auth=co"
    const val AUTH_SET_COOKIE = "?auth=sc"
    const val AUTH_FROM_TOKEN = "&auth=qp&zauthtoken="
    const val CLIENT_VIEW = "&zms=ipad&action=view"
    const val LOAD_IMAGES = "&xim=1"
    const val COMPOSE_MAIL = "&st=newmail&zms=ipad"
    const val UPDATE_QUERY = "after:"
    const val JSON_FORMAT = "&fmt=json"

    const val MAIL_DATABASE = "MAIL_DB"
    const val PARSED_MAIL_DATABASE = "PARSED_MAIL_DB"
    const val DATA_STORE_NAME = "MAIL_CREDENTIAL"

    const val KEY_CREDENTIAL = "KEY_CREDENTIAL"
    const val KEY_LAST_SYNC = "KEY_LAST_SYNC"
    const val KEY_TOKEN = "KEY_TOKEN"
    const val KEY_SYNC_SERVICE = "KEY_SYNC_SERVICE"
    const val KEY_SHOULD_SYNC = "KEY_SHOULD_SYNC"
    const val KEY_DARK_THEME = "KEY_DARK_THEME"

    const val NO_CREDENTIAL = "NO_CREDENTIAL"
    const val NO_LAST_SYNC = 0L
    const val NO_TOKEN = "NO_TOKEN"

    const val DATE_FORMAT_YEAR = "dd-MM-yyyy"
    const val DATE_FORMAT_MONTH = "dd MMM"
    const val DATE_FORMAT_DATE = "hh:mm a"
    const val DATE_FORMAT_FULL = "hh:mm a dd MMM yyyy"
    const val DATE_FORMAT = "EEEE, MMMM dd, yyyy HH:mm a"

    const val YEAR = 31449600000L
    const val DAY = 86400000L
}
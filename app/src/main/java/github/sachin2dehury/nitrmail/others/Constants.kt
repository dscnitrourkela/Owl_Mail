package github.sachin2dehury.nitrmail.others

object Constants {

    const val INBOX_URL = "https://mail.nitrkl.ac.in/home/~/inbox?&fmt=json"
    const val SENT_URL = "https://mail.nitrkl.ac.in/home/~/sent?&fmt=json"
    const val DRAFT_URL = "https://mail.nitrkl.ac.in/home/~/drafts?&fmt=json"
    const val CONTACTS_URL = "https://mail.nitrkl.ac.in/home/~/contacts?&fmt=json"
    const val TASKS_URL = "https://mail.nitrkl.ac.in/home/~/tasks?&fmt=json"
    const val ITEM_URL = "https://mail.nitrkl.ac.in/home/~/?id="

    const val MIME_TAG = "MIME-Version: 1.0"
    const val FROM_TAG = "From: "
    const val TO_TAG = "To: "
    const val DATE_TAG = "Date: "
    const val UTC_TAG = " +0530"
    const val SUBJECT_TAG = "Subject: "
    const val CONTENT_TYPE_TAG = "Content-Type: "
    const val CHAR_SET_TAG = "; charset="
    const val ENCODING_TAG = "Content-Transfer-Encoding: "
    const val MESSAGE_ID_TAG = "Message-Id: "
    const val CLOSE_TAG = ">"

    const val BASE_64 = "base64"
}
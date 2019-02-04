package mhashim6.android.quickQuery

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText


class QQActivity : AppCompatActivity() {
    //	private static final String GOOGLE_IMAGE = "https://images.google.com/searchbyimage?image_url=";

    private lateinit var allEngines: Array<String>

    private lateinit var preferences: SharedPreferences

    private var query: String = ""

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allEngines = resources.getStringArray(R.array.engines)

        val starter = intent
        query = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Intent.ACTION_PROCESS_TEXT == starter.action)
            starter.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
        else
            starter.getCharSequenceExtra(Intent.EXTRA_TEXT).toString()

        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        showDialog()
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setView(inflateDialogView())
                .setIcon(R.drawable.ic_bubble)
                .setTitle("Search for")
                .setOnCancelListener { finish() }
                .show().window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT) //Controlling width and height.
    }

    private fun inflateDialogView(): View {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.qq_dialog, null)

        /*query*/
        val queryEditText = dialogView.findViewById<AppCompatEditText>(R.id.query_edit_text)
        queryEditText.setText(query)
        queryEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(newQuery: CharSequence, i: Int, i1: Int, i2: Int) {
                query = newQuery.toString()
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        /*ListView*/
        val enginesListView = dialogView.findViewById<ListView>(R.id.engines_list_view)
        val engines = preferences.getStringSet("engines", emptySet())!!
        val enginesArray = allEngines.intersect(engines).toTypedArray() // preserve original order of engines when displaying preferences' engines.

        val arrayAdapter = ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                allEngines.intersect(engines).toTypedArray())
        enginesListView.adapter = arrayAdapter
        enginesListView.setOnItemClickListener { _, _, pos, _ ->
            val selected = enginesArray[pos]
            when (selected) {
                "Google" -> webSearch(GOOGLE, query)
                "DuckDuckGo" -> webSearch(DUCK_DUCK_GO, query)
                "Google Play" -> webSearch(GOOGLE_PLAY, query)
                "YouTube" -> youtube(query)
            }
        }

        return dialogView
    }

    private fun youtube(query: String) {
        val intent = Intent(Intent.ACTION_SEARCH)
        intent.setPackage(YOUTUBE_PACKAGE)
        intent.putExtra("query", query)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else { //youtube is not installed.
            webSearch(YOUTUBE, query)
        }
    }

    private fun webSearch(engine: String, query: String) {
        Utils.openWebPage(this, engine + query.trim { it <= ' ' }.replace(MULTIPLE_SPACES.toRegex(), PLUS_SIGN))
    }

    companion object {
        private const val GOOGLE = "https://www.google.com/search?q="
        private const val DUCK_DUCK_GO = "https://duckduckgo.com/?q="
        private const val YOUTUBE = "https://www.youtube.com/results?search_query="
        private const val GOOGLE_PLAY = "market://search?q="
        private const val MULTIPLE_SPACES = " +"
        private const val PLUS_SIGN = "+"
        private const val YOUTUBE_PACKAGE = "com.google.android.youtube"
    }

}
package com.dude.ai

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private val bg = Color.rgb(8, 13, 24)
    private val panel = Color.rgb(17, 27, 43)
    private val cyan = Color.rgb(61, 220, 235)
    private val white = Color.rgb(235, 244, 250)
    private val muted = Color.rgb(139, 160, 180)
    private lateinit var messages: LinearLayout
    private lateinit var scroll: ScrollView
    private lateinit var input: EditText
    private val prefs by lazy { getSharedPreferences("dude_local_data", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        buildScreen()
        loadConversation()
        if (messages.childCount == 0) addMessage("DUDE", "DUDE systems online. Starter mode is active. Type help to see commands.")
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(14), dp(18), dp(12))
            setBackgroundColor(bg)
        }
        val header = TextView(this).apply {
            text = "D U D E\nPERSONAL INTELLIGENCE  •  LOCAL MODE"
            textSize = 18f
            letterSpacing = 0.12f
            gravity = Gravity.CENTER
            setTextColor(cyan)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, dp(12), 0, dp(18))
        }
        root.addView(header)
        val status = TextView(this).apply {
            text = "● CORE ONLINE   •   MEMORY ON   •   AI MODEL NOT CONNECTED"
            textSize = 10f
            setTextColor(cyan)
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = rounded(panel, 12)
        }
        root.addView(status)
        scroll = ScrollView(this).apply { isFillViewport = true; setPadding(0, dp(14), 0, dp(12)) }
        messages = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(messages)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        val composer = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        input = EditText(this).apply {
            hint = "Message DUDE..."
            setHintTextColor(muted)
            setTextColor(white)
            textSize = 15f
            singleLine = true
            imeOptions = EditorInfo.IME_ACTION_SEND
            setPadding(dp(14), dp(10), dp(14), dp(10))
            background = rounded(panel, 14)
            setOnEditorActionListener { _, action, _ ->
                if (action == EditorInfo.IME_ACTION_SEND) { sendMessage(); true } else false
            }
        }
        composer.addView(input, LinearLayout.LayoutParams(0, dp(52), 1f))
        val send = Button(this).apply {
            text = "SEND"
            isAllCaps = false
            setTextColor(bg)
            background = rounded(cyan, 14)
            setOnClickListener { sendMessage() }
        }
        composer.addView(send, LinearLayout.LayoutParams(dp(76), dp(52)).apply { leftMargin = dp(8) })
        root.addView(composer)
        val clear = TextView(this).apply {
            text = "CLEAR CONVERSATION"
            gravity = Gravity.CENTER
            textSize = 10f
            setTextColor(muted)
            setPadding(0, dp(14), 0, dp(4))
            setOnClickListener { messages.removeAllViews(); prefs.edit().remove("conversation").apply(); addMessage("DUDE", "Conversation cleared. Saved notes remain.") }
        }
        root.addView(clear)
        setContentView(root)
    }

    private fun sendMessage() {
        val text = input.text.toString().trim()
        if (text.isEmpty()) return
        addMessage("YOU", text)
        input.setText("")
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(input.windowToken, 0)
        addMessage("DUDE", respond(text))
    }

    private fun respond(raw: String): String {
        val text = raw.trim()
        val lower = text.lowercase(Locale.getDefault())
        return when {
            lower == "help" || lower == "commands" ->
                "Starter commands:\n• time — current time\n• date — today's date\n• remember [note] — save a note\n• what do you remember — list notes\n• forget all — erase notes\n• status — capabilities\n\nAn AI language model and voice are not connected yet."
            lower == "time" -> "Local time: " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            lower == "date" -> "Today is " + SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(Date())
            lower == "status" -> "DUDE v0.1 is running. Local commands and on-device notes work. No LLM, voice, web access, or autonomous actions are connected yet."
            lower.startsWith("remember ") -> {
                val note = text.substringAfter("remember ").trim()
                if (note.isBlank()) "Tell me what to remember." else {
                    val notes = getNotes()
                    notes.put(note)
                    prefs.edit().putString("notes", notes.toString()).apply()
                    "Saved locally: “$note”"
                }
            }
            lower in listOf("what do you remember", "show memory", "memory") -> {
                val notes = getNotes()
                if (notes.length() == 0) "No saved notes yet." else
                    "Saved notes:\n" + (0 until notes.length()).mapIndexed { index, i -> "${index + 1}. ${notes.optString(i)}" }.joinToString("\n")
            }
            lower == "forget all" -> { prefs.edit().remove("notes").apply(); "All saved notes erased from this app." }
            lower in listOf("hi", "hello", "hey", "hello dude", "hi dude") -> "DUDE is online. Type help to see what I can do."
            lower.contains("who are you") || lower.contains("what are you") -> "I am DUDE, a personal assistant prototype. This build has simple local commands and memory; my AI model is not built or connected yet."
            else -> "I received your message, but my AI model is not connected yet. Try help for the available local commands."
        }
    }

    private fun getNotes(): JSONArray = try {
        JSONArray(prefs.getString("notes", "[]") ?: "[]")
    } catch (_: Exception) { JSONArray() }

    private fun addMessage(sender: String, body: String) {
        val user = sender == "YOU"
        val bubble = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(13), dp(10), dp(13), dp(10))
            background = rounded(if (user) Color.rgb(21, 54, 69) else panel, 15)
        }
        bubble.addView(TextView(this).apply {
            text = sender
            textSize = 9f
            setTextColor(if (user) cyan else muted)
            typeface = Typeface.DEFAULT_BOLD
        })
        bubble.addView(TextView(this).apply {
            text = body
            textSize = 15f
            setTextColor(white)
            setPadding(0, dp(5), 0, 0)
        })
        val row = LinearLayout(this).apply {
            gravity = if (user) Gravity.END else Gravity.START
            setPadding(0, dp(5), 0, dp(5))
            addView(bubble, LinearLayout.LayoutParams((resources.displayMetrics.widthPixels * .82f).toInt(), -2))
        }
        messages.addView(row)
        scroll.post { scroll.fullScroll(ScrollView.FOCUS_DOWN) }
        saveConversation()
    }

    private fun saveConversation() {
        val arr = JSONArray()
        for (i in 0 until messages.childCount) {
            val row = messages.getChildAt(i) as? LinearLayout ?: continue
            val bubble = row.getChildAt(0) as? LinearLayout ?: continue
            val sender = (bubble.getChildAt(0) as? TextView)?.text?.toString() ?: continue
            val body = (bubble.getChildAt(1) as? TextView)?.text?.toString() ?: continue
            arr.put(org.json.JSONObject().put("sender", sender).put("body", body))
        }
        prefs.edit().putString("conversation", arr.toString()).apply()
    }

    private fun loadConversation() {
        try {
            val arr = JSONArray(prefs.getString("conversation", "[]") ?: "[]")
            for (i in 0 until arr.length()) {
                val item = arr.getJSONObject(i)
                addMessage(item.optString("sender"), item.optString("body"))
            }
        } catch (_: Exception) { prefs.edit().remove("conversation").apply() }
    }

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}

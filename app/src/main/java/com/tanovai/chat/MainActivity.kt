package com.tanovai.chat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.scaledrone.lib.*
import com.tanovai.chat.message.MessageAdapter
import com.tanovai.chat.message.MyMessage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(), RoomListener {

    private val memberData = MemberData(getRandomName(), getRandomColor())
    private val channelID = ""
    private val roomName = "observable-room"
    private var scaledrone: Scaledrone? = null
    private val messageAdapter = MessageAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        messages_view.adapter = messageAdapter
        connectScaledrome()

        sendMsgBtn.setOnClickListener {
            sendMessage(editText.text.toString())
            editText.text.clear()
        }
    }

    private fun sendMessage(message: String?) {
        if (message != null && message.isNotBlank()) {
            scaledrone?.publish(roomName, message)
        }
    }

    private fun onMessageReceive(receivedMessage: Message?) {
        // To transform the raw JsonNode into a POJO we can use an ObjectMapper
        val mapper = ObjectMapper()
        try { // member.clientData is a MemberData object, let's parse it as such
            val data = mapper.treeToValue(
                receivedMessage?.member?.clientData,
                MemberData::class.java
            )
            // if the clientID of the message sender is the same as our's it was sent by us
            val belongsToCurrentUser: Boolean =
                receivedMessage?.clientID.equals(scaledrone?.clientID)
            // since the message body is a simple string in our case we can use json.asText() to parse it as such
            // if it was instead an object we could use a similar pattern to data parsing
            val message =
                MyMessage(receivedMessage?.data?.asText(), data, belongsToCurrentUser)
            runOnUiThread {
                messageAdapter.add(message)
                // scroll the ListView to the last added element
                messages_view.setSelection(messages_view.count - 1)
            }
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
    }

    private fun connectScaledrome() {
        scaledrone = Scaledrone(channelID, memberData)
        scaledrone?.connect(object : Listener {
            override fun onOpen() {
                println("Scaledrone connection open")
                // Since the MainActivity itself already implement RoomListener we can pass it as a target
                scaledrone?.subscribe(roomName, this@MainActivity)
            }

            override fun onOpenFailure(ex: java.lang.Exception?) {
                System.err.println(ex)
            }

            override fun onFailure(ex: java.lang.Exception?) {
                System.err.println(ex)
            }

            override fun onClosed(reason: String?) {
                System.err.println(reason)
            }
        })
    }

    override fun onOpen(room: Room?) {
        Log.d("tag", "On open")
    }

    override fun onOpenFailure(room: Room?, ex: Exception?) {
        Log.d("tag", "On failure")
    }

    override fun onMessage(room: Room?, message: Message?) {
        onMessageReceive(message)
    }

    private fun getRandomName(): String {
        val adjs = arrayOf(
            "autumn",
            "hidden",
            "bitter",
            "misty",
            "silent",
            "empty",
            "dry",
            "dark",
            "summer",
            "icy",
            "delicate",
            "quiet",
            "white",
            "cool",
            "spring",
            "winter",
            "patient",
            "twilight",
            "dawn",
            "crimson",
            "wispy",
            "weathered",
            "blue",
            "billowing",
            "broken",
            "cold",
            "damp",
            "falling",
            "frosty",
            "green",
            "long",
            "late",
            "lingering",
            "bold",
            "little",
            "morning",
            "muddy",
            "old",
            "red",
            "rough",
            "still",
            "small",
            "sparkling",
            "throbbing",
            "shy",
            "wandering",
            "withered",
            "wild",
            "black",
            "young",
            "holy",
            "solitary",
            "fragrant",
            "aged",
            "snowy",
            "proud",
            "floral",
            "restless",
            "divine",
            "polished",
            "ancient",
            "purple",
            "lively",
            "nameless"
        )
        val nouns = arrayOf(
            "waterfall",
            "river",
            "breeze",
            "moon",
            "rain",
            "wind",
            "sea",
            "morning",
            "snow",
            "lake",
            "sunset",
            "pine",
            "shadow",
            "leaf",
            "dawn",
            "glitter",
            "forest",
            "hill",
            "cloud",
            "meadow",
            "sun",
            "glade",
            "bird",
            "brook",
            "butterfly",
            "bush",
            "dew",
            "dust",
            "field",
            "fire",
            "flower",
            "firefly",
            "feather",
            "grass",
            "haze",
            "mountain",
            "night",
            "pond",
            "darkness",
            "snowflake",
            "silence",
            "sound",
            "sky",
            "shape",
            "surf",
            "thunder",
            "violet",
            "water",
            "wildflower",
            "wave",
            "water",
            "resonance",
            "sun",
            "wood",
            "dream",
            "cherry",
            "tree",
            "fog",
            "frost",
            "voice",
            "paper",
            "frog",
            "smoke",
            "star"
        )
        return (
                adjs[Math.floor(Math.random() * adjs.size).toInt()] +
                        "_" +
                        nouns[Math.floor(Math.random() * nouns.size).toInt()]
                );
    }

    private fun getRandomColor(): String {
        val r = Random()
        val sb = StringBuffer("#")
        while (sb.length < 7) {
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7)
    }

    class MemberData {
        var name: String? = null
            private set
        var color: String? = null
            private set

        constructor(name: String?, color: String?) {
            this.name = name
            this.color = color
        }

        // Add an empty constructor so we can later parse JSON into MemberData using Jackson
        constructor() {}

    }

}

package com.tanovai.chat.message


import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.tanovai.chat.MainActivity.MemberData
import com.tanovai.chat.R


class MyMessage(
    // message body
    val text: String?,
    // data of the user that sent this message
    val memberData: MemberData,
    // is this message sent by us?
    val isBelongsToCurrentUser: Boolean
)


internal class MessageViewHolder {
    var avatar: View? = null
    var name: TextView? = null
    var messageBody: TextView? = null
}

class MessageAdapter(context: Context) : BaseAdapter() {
    var messages: MutableList<MyMessage> =
        ArrayList()
    var context: Context
    fun add(message: MyMessage) {
        messages.add(message)
        notifyDataSetChanged() // to render the list we need to notify
    }

    override fun getCount(): Int {
        return messages.size
    }

    override fun getItem(i: Int): Any {
        return messages[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    override fun getView(
        i: Int,
        convertView: View?,
        viewGroup: ViewGroup?
    ): View {
        var convertView = convertView
        val holder = MessageViewHolder()
        val messageInflater =
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val message = messages[i]
        if (message.isBelongsToCurrentUser) { // this message was sent by us so let's create a basic chat bubble on the right
            convertView = messageInflater.inflate(R.layout.my_message, null)
            holder.messageBody =
                convertView.findViewById<View>(R.id.message_body) as TextView
            convertView.tag = holder
            holder.messageBody!!.text = message.text
        } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
            convertView = messageInflater.inflate(R.layout.their_message, null)
            holder.avatar =
                convertView.findViewById(R.id.avatar) as View
            holder.name = convertView.findViewById<View>(R.id.name) as TextView
            holder.messageBody =
                convertView.findViewById<View>(R.id.message_body) as TextView
            convertView.tag = holder
            holder.name!!.text = message.memberData.name
            holder.messageBody!!.text = message.text
            val drawable = holder.avatar!!.background as GradientDrawable
            drawable.setColor(Color.parseColor(message.memberData.color))
            drawable.setColor(Color.parseColor(message.memberData.color))
        }
        return convertView
    }

    init {
        this.context = context
    }
}
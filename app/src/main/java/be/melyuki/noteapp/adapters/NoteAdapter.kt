package be.melyuki.noteapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.melyuki.noteapp.R
import be.melyuki.noteapp.models.Note
import java.time.format.DateTimeFormatter

class NoteAdapter(val Context: Context, val Notes: List<Note>) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    // region Listener Click On Item
    // Interface fonctionnelle
    // -> Pour permettre l'utilisation d'une lambda à l'utilisation du listener
    fun interface OnNoteClickListener {
        fun onNoteItemListener(note: Note)
    }
    // Listener "onClick" sur un élément
    private var onNoteClickListener : OnNoteClickListener? = null
    // Création du setteur de la variable privée "OnNoteClickListener"
    fun setOnNoteClickListener(onNoteClickListener: OnNoteClickListener) {
        this.onNoteClickListener = onNoteClickListener
    }
    // endregion

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle : TextView
        private val tvDate : TextView
        private val tvTime : TextView

        fun setTitle(name: String){
            tvTitle.text = name
        }
        fun setDate(name: String){
            tvDate.text = name
        }
        fun setTime(name: String){
            tvTime.text = name
        }

        init {
            tvTitle = itemView.findViewById(R.id.item_note_title)
            tvDate = itemView.findViewById(R.id.item_note_date)
            tvTime = itemView.findViewById(R.id.item_note_time)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val v : View = LayoutInflater.from(Context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(v)
    }

    override fun getItemCount(): Int {
        return Notes.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val target : Note = Notes[position]

        // Hold le nom de la note
        holder.setTitle(target.fileName)

        // Hold le date de création de la note
        holder.setDate(target.lastUpdate.format(DateTimeFormatter.ofPattern(
            Context.getString(
                R.string.format_date
            ))))
//        val formatDate = DateTimeFormatter.ofPattern(R.string.format_date.toString())
//        val updateDate = formatDate.format(target.lastUpdate)
//        holder.setDate(updateDate)

        // Hold l'heure de création de la note
        holder.setTime(target.lastUpdate.format(DateTimeFormatter.ofPattern(
            Context.getString(
                R.string.format_time
            ))))
//        val formatTime = DateTimeFormatter.ofPattern(R.string.format_time.toString())
//        val updateTime = formatTime.format(target.lastUpdate)
//        holder.setDate(updateTime)

        // Set du onClick sur l'Item "holder.itemView"
        holder.itemView.setOnClickListener { view ->
            onNoteClickListener?.onNoteItemListener(target)
        }
    }
}
package be.melyuki.noteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import be.melyuki.noteapp.databinding.ActivityNoteBinding
import java.io.File
import java.io.FileWriter

class NoteActivity : AppCompatActivity() {

    // Le "companion object" qui transite le nom de la note
    companion object {
        const val TITLE_NOTE = "TITLE_NOTE"
        const val NEW_NOTE = "NEW_NOTE"
    }

    private lateinit var binding : ActivityNoteBinding

    private lateinit var fileName : String
    private var newNote : Boolean = false
    private var modified : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Binding de l'activité
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Récupération des infos "extra" de l'intent
        fileName = intent.getStringExtra(TITLE_NOTE).toString()
        newNote = intent.getBooleanExtra(NEW_NOTE, false)

        // Set du nom de la note
        val setTitle = binding.titleNoteDefault
        setTitle.text = fileName
        // Update de l'EditText
        if(!newNote) {
            getNoteContent()
        }

        binding.btnNoteSave.setOnClickListener { saveNote() }
        binding.btnNoteCancel.setOnClickListener { cancelNote() }

        binding.etNoteText.addTextChangedListener { text: Editable? ->
            modified = true
        }

        // Back Button Android
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPress()
            }
        })
    }

    private fun onBackPress() {
        if(modified) {
            showLostDataDialog()
            return
        }
        finish()
    }

    private fun showLostDataDialog() {
        // Build le dialog
        val dialogBuilder = AlertDialog.Builder(this).apply {
            // Définition du titre et du message
            setTitle(getString(R.string.dialog_lost_data_title))
            setMessage(getString(R.string.dialog_lost_data_message))
            setPositiveButton(android.R.string.ok) { dialog, id ->
                // Si c'est ok, retour sur la MainActivity
                finish()
            }
            setNegativeButton(android.R.string.cancel) { dialog, id ->
                // Si c'est pas ok, on reste sur cette activité
                /* Donc on ne fait rien */
            }
            setNeutralButton(getString(R.string.dialog_save_quit)) { dialog, id ->
                // Si c'est ok mais qu'on veut sauver sans apporter d'autres modifs
                saveNote()
                finish()
            }
            // Force l'utilisation des boutons pour valider la dialog
            setCancelable(false)
        }
        // Créer et afficher l'alert
        dialogBuilder.create().show()

    }

    private fun getNoteContent() {
        openFileInput(fileName).bufferedReader().useLines { lines ->
            // Convertion du retour du buffer en un seul string
            val content : String = lines.joinToString("\n")

            // Affichage du content
            binding.etNoteText.setText(content)
        }
    }

    private fun saveNote() {
        val note = binding.etNoteText.text.toString()

        // Méthode "File()"
//        val newFile = File(filesDir, "$fileName.txt")
//        val Writer = FileWriter(newFile, true)
//        Writer.write(note)
//        Writer.close()

        // Méthode "openFile...()"
        openFileOutput(fileName, MODE_PRIVATE).use {fileOutputStream ->
            fileOutputStream.write(note.toByteArray())
        }

        Toast.makeText(this, R.string.toast_note_save, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun cancelNote() {
        if(newNote) {
            binding.etNoteText.text.clear()
        }
        else {
            getNoteContent()
        }
    }
}
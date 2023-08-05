package be.melyuki.noteapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import be.melyuki.noteapp.NoteActivity.Companion.NEW_NOTE
import be.melyuki.noteapp.NoteActivity.Companion.TITLE_NOTE
import be.melyuki.noteapp.adapters.NoteAdapter
import be.melyuki.noteapp.databinding.ActivityMainBinding
import be.melyuki.noteapp.models.Note
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    lateinit var btnCreate : Button
    lateinit var etCreate : EditText

    lateinit var fileNotes : MutableList<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Binding de l'activité
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ********** EDITTEXT ET BOUTON **********
        // Binding du Bouton "Create" et du listener
        btnCreate = binding.btnMainCreate
        btnCreate.setOnClickListener { editNewNote() }
        // Binding de l'EditText du nom de la note
        // et du textChangeListener pour unlock le bouton au remplissage de l'EditText
        etCreate = binding.etInputCreate
        etCreate.addTextChangedListener { lockBtn() }

        // ********** RECYCLERVIEW **********
        // Chargement de la liste des notes
        fileNotes = mutableListOf()
        // Chargement de la liste dans l'adapter
        val adapter : NoteAdapter = NoteAdapter(this, fileNotes)
        // Binding de la RecyclerView et setting avec l'adapter
        binding.rvNote.adapter = adapter
        // Setting du layoutManager
            // Dans l'xml en ajoutant dans la RecyclerView :
            // - app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
            // Il est préférable de le faire dans le code ci-après :
        binding.rvNote.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        // Rendre un élément de la RecyclerView clickable:
        // - créer une méthode dans l'adapteur
        // - l'utiliser ici
        adapter.setOnNoteClickListener { note ->
            editExistingNote(note)
        }
    }

    override fun onStart() {
        super.onStart()
        // Nettoyage de la liste (après modification*s de celle-ci)
        fileNotes.clear()
        // Chargement des notes dans la liste
        fileNotes.addAll(getNotes())

        // Notifier la RecyclerView des potentiels changements
        binding.rvNote.adapter?.notifyDataSetChanged()
    }

    private fun getNotes(): List<Note> {
        // Création d'une copie modifiable de la liste existante
        val copyList : MutableList<Note> = mutableListOf()

        // Récupération des notes dans le fichier
        for (filename in fileList()) {
            // Récupération du fichier de sauvegarde créé avec "openFileOutput"
            val file = File(filesDir, filename)
            // Récupération des infos d'une note du fichier de sauvegarde
            val dataNote = Note(
                file.name,
                LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault())
            )
            // Ajout des data de chaque note dans la copyList
            copyList.add(dataNote)
        }

        // Sans oublier de renvoyer la copyList
        return copyList.toList()
    }

    private fun lockBtn() {
        val inputUser = etCreate.text.toString()

        btnCreate.isEnabled = inputUser.isNotEmpty()
    }

    private fun editNewNote() {
        val newNote = binding.etInputCreate.text.toString()

        // Check si le nom existe déjà
        if(fileList().contains(newNote)) {
            showErrorDialog(getString(R.string.dialog_exist_file))
            return
        }

        // Navigation avec les infos nécessaires
        val intent = Intent(this, NoteActivity::class.java).apply {
            putExtra(NEW_NOTE, true)
            putExtra(TITLE_NOTE, newNote)
        }

        // Clear de l'editText et du focus
        etCreate.text.clear()
        etCreate.clearFocus()

        startActivity(intent)
    }

    private fun editExistingNote(note: Note) {
        val intent = Intent(this, NoteActivity::class.java).apply {
            putExtra(TITLE_NOTE, note.fileName)
            putExtra(NEW_NOTE, false)
        }
        startActivity(intent)
    }

    private fun showErrorDialog(message: String) {
        // Build d'une AlertDialog
        val builderAlert = AlertDialog.Builder(this).apply {
            setMessage(message)
            setPositiveButton(android.R.string.ok) { dialog, id ->
                /* On ne fait rien */
            }
            // Force l'utilisation du bouton "ok" pour valider la dialog
            setCancelable(false)
        }

        // Création du build
        val dialog : AlertDialog = builderAlert.create()

        // Affichage de l'alerte
        dialog.show()
    }
}
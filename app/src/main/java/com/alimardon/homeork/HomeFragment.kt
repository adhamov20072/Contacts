package com.alimardon.homeork

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.alimardon.homeork.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), androidx.appcompat.widget.SearchView.OnQueryTextListener {
    lateinit var binding: FragmentHomeBinding
    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    lateinit var dialogs: Dialogs
    var number: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewAdapter = RecyclerViewAdapter()
        GlobalScope.launch(IO) {
            setList()
        }
        binding.recyclerView.adapter = recyclerViewAdapter
        recyclerViewAdapter.setClickListener(object : RecyclerViewAdapter.SetOnLongClickListener {
            override fun longClick(note: Note) {
                AlertDialog.Builder(requireContext())
                    .setMessage("O'chirasizmi?")
                    .setPositiveButton("ha") { dialog, id ->
                        GlobalScope.launch(IO) {
                            deleteNote(note)
                            setList()
                        }
                    }
                    .setNegativeButton("Yo'q"){_,_->}
                    .create()
                    .show()
            }

            override fun setOnClickListener(note: Note) {
                alertDialog(
                    requireContext(),
                    "${note.title} \n ${note.phone}",
                    "O'zgartirish",
                    "Tel qilish",
                    "yopish"
                )
                dialogs = object : Dialogs {
                    override fun change() {
                        val action = HomeFragmentDirections.actionHomeFragmentToAddFragment(note)
                        findNavController().navigate(action)
                    }

                    override fun call() {
                        testPermissionAndCall(note.phone)
                    }
                }
            }

        })
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    call(number)
                }
            }
        binding.btnadd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFragment)
        }
        binding.edSearch.setOnQueryTextListener(this)

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchedTitle(newText.toString())
        return true
    }

    private fun deleteNote(note: Note) {
        DataBase.DataBaseBuilder.getDataBase(requireContext())?.noteDao()?.delete(note)
    }

    fun setList() {
        recyclerViewAdapter.submitList(getAllNotes())
    }

    fun searchedTitle(text: String) {
        val filteredList = ArrayList<Note>()
        GlobalScope.launch(IO) {
            for (item in DataBase.DataBaseBuilder.getDataBase(requireContext())?.noteDao()
                ?.getAllNotes()!!) {
                val historyText = item.title.lowercase() + item.phone.lowercase()
                if (historyText.contains(text.lowercase())) {
                    filteredList.add(item)
                }
            }
            if (filteredList.isNotEmpty()) {
                recyclerViewAdapter.submitList(filteredList)
            }
        }
    }

    fun getAllNotes(): List<Note>? {
        return DataBase.DataBaseBuilder.getDataBase(requireContext())?.noteDao()?.getAllNotes()
    }

    @SuppressLint("ServiceCast")
    fun call(number: String?) {
        startActivity(
            Intent(
                Intent.ACTION_CALL,
                Uri.parse("tel:$number")
            )
        )
    }

    fun testPermissionAndCall(number: String) {
        val given = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
        if (given) {
            call(number)
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
        }
    }

    fun alertDialog(
        context: Context,
        Massage: String,
        PositiveChange: String,
        NeutralCall: String,
        Negative: String
    ) {
        AlertDialog.Builder(context)
            .setMessage(Massage)
            .setPositiveButton(PositiveChange) { dialog, id ->
                dialogs.change()
            }
            .setNeutralButton(NeutralCall) { dialog, id ->
                dialogs.call()
            }
            .setNegativeButton(Negative) { dialog, id -> }
            .create()
            .show()
    }
}
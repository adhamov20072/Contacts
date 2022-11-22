package com.alimardon.homeork

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aimardon.secretnotes.fragments.AddFragmentArgs
import com.alimardon.homeork.databinding.FragmentAddBinding
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddFragment : Fragment() {
    lateinit var binding: FragmentAddBinding
    val args: AddFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.notes != null) {
            binding.addIsm.setText(args.notes!!.title)
            binding.addfamily.setText(args.notes!!.description)
            binding.addHaqida.setText(args.notes!!.tanishtirish)
            binding.addTelefon.setText(args.notes!!.phone)
        }
        binding.materialButton.setOnClickListener {
            if (args.notes != null) {
                val note: Note = Note(
                    args.notes!!.id,
                    binding.addIsm.text.toString(),
                    binding.addHaqida.text.toString(),
                    binding.addTelefon.text.toString(),
                    binding.addTelefon.text.toString()
                )
                GlobalScope.launch(IO) {
                    DataBase.DataBaseBuilder.getDataBase(requireContext())?.noteDao()?.update(note)
                }
            } else {
                val note: Note = Note(
                    0,

                    binding.addIsm.text.toString(),
                    binding.addHaqida.text.toString(),
                    binding.addTelefon.text.toString(),
                    binding.addTelefon.text.toString()
                )
                if (binding.addIsm.text.isNotEmpty()) {
                    GlobalScope.launch(IO) {
                        DataBase.DataBaseBuilder.getDataBase(requireContext())?.noteDao()
                            ?.insert(note)
                    }
                }
            }
            if (!binding.addIsm.text.isEmpty()) {
                findNavController().navigate(R.id.action_addFragment_to_homeFragment)
            }
        }
    }
}
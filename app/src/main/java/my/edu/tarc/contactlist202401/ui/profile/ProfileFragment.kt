package my.edu.tarc.contactlist202401.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import my.edu.tarc.contactlist202401.R
import my.edu.tarc.contactlist202401.databinding.FragmentProfileBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ProfileFragment : Fragment(), MenuProvider {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    private val getProfilePicture = registerForActivityResult(
        ActivityResultContracts.GetContent()){
        binding.imageViewProfile.setImageURI(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        readPreferences()
        readProfilePicture()

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.buttonSaveProfile.setOnClickListener {
            writeProfilePicture()
        }

        binding.imageViewProfile.setOnClickListener {
            getProfilePicture.launch("image/*")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.findItem(R.id.action_settings).setVisible(false)
        menu.findItem(R.id.action_profile).setVisible(false)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            android.R.id.home->{
                findNavController().navigateUp()
            }
        }
        return true
    }

    private fun readPreferences(){
        sharedPreferences = requireActivity().getSharedPreferences("profile_pref", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "")
        val email = sharedPreferences.getString("email", "")
        val phone = sharedPreferences.getString("phone", "")

        //Display shared preference values
        binding.editTextProfileName.setText(name)
        binding.editTextProfileEmail.setText(email)
        binding.editTextProfilePhone.setText(phone)
    }

    private fun writePreferences(){
        with(sharedPreferences.edit()){
            val name = binding.editTextProfileName.text.toString()
            val email = binding.editTextProfileEmail.text.toString()
            val phone = binding.editTextProfilePhone.text.toString()

            putString("name", name)
            putString("email", email)
            putString("phone", phone)
            apply() //writes the updates to the shared preference file
        }
    }

    private fun readProfilePicture() {
        val filename = "jennie-1.png"
        val file = File(this.context?.filesDir, filename)

        try {
            if (!file.exists()) {
                binding.imageViewProfile.setImageResource(R.drawable.profile)
            } else {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                binding.imageViewProfile.setImageBitmap(bitmap)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun  writeProfilePicture(){
        val filename = "jennie-1.png"
        // this.context?.filesDir = get the app file directory
        val file = File(this.context?.filesDir, filename)

        val bd = binding.imageViewProfile.drawable as BitmapDrawable
        val bitmap = bd.bitmap
        val outputStream: OutputStream

        try{
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            outputStream.flush()
            outputStream.close()
        }catch (e: FileNotFoundException){
            e.printStackTrace()
        }
    }
}
package com.example.minecraft.details

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.minecraft.ModViewModel
import com.example.minecraft.R
import com.example.minecraft.adapter.ChangeFavState
import java.io.File


class DetailsFragment : Fragment(), ChangeFavState {
    private var STORAGE_RQ = 10101
    private val args by navArgs<DetailsFragmentArgs>()
    private lateinit var mModViewModel: ModViewModel
    private lateinit var currentModPath: String

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        var assetManager: AssetManager = requireActivity().assets
        val favBtn = view.findViewById<ImageView>(R.id.fav_btn)
        val downloadBtn = view.findViewById<Button>(R.id.download_btn)
        val isFav = args.currentMod.isFav

        mModViewModel = ViewModelProvider(this).get(ModViewModel::class.java)

        view.findViewById<TextView>(R.id.detalized_title).text = args.currentMod.title
        view.findViewById<TextView>(R.id.detalized_content).text = args.currentMod.content

        var image = assetManager.open("images/${args.currentMod.imageUrl}")
        val bitmap = BitmapFactory.decodeStream(image)
        view.findViewById<ImageView>(R.id.detalized_image_view).setImageBitmap(bitmap)

        if (args.currentMod.isFav) {
            favBtn.setImageResource(R.drawable.ic_active_star)
        } else {
            favBtn.setImageResource(R.drawable.ic_inactive_star)
        }

        favBtn.setOnClickListener {
            var newState = changeState(args.currentMod, mModViewModel)
            args.currentMod.isFav = newState

            if (newState) {
                favBtn.setImageResource(R.drawable.ic_active_star)
            } else {
                favBtn.setImageResource(R.drawable.ic_inactive_star)
            }
        }

        val filePath = File(requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS).toString() + "/")
        val file = File(filePath, args.currentMod.modUri)
        if (file.exists()) { // If file exists
            // Change text to "Install"
            downloadBtn.text = getString(R.string.install)
            downloadBtn.setTextColor(resources.getColor(R.color.success))
        }

        downloadBtn.setOnClickListener {
            checkForPermissions(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    name = "Storage",
                    STORAGE_RQ
            )
        }
        return view
    }

    private fun saveMod(modUri: String, assetManager: AssetManager) {
        // Is the app has the memory to copy the file to the external memory
        if (isExternalStorageWritable()) {
            // downloadBtn - from the view
            val downloadBtn = requireView().findViewById<Button>(R.id.download_btn)
            // Creating a filePath and file
            val filePath = File(requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS).toString() + "/")
            val file = File(filePath, modUri)
            // If text of the button is "Download"
            if (downloadBtn.text == getString(R.string.download) && !file.exists()) {
                // Copy the file to external storage accessible by all
                downloadBtn.text = getString(R.string.downloading)
                // Copy the data from file in assets to the file, which has just been created
                file.writeBytes(assetManager.open("files/$modUri").readBytes())
                // Delay for 2 seconds before "Install" text appears, allowing to check another condition
                Handler().postDelayed({
                    downloadBtn.text = getString(R.string.install)
                    downloadBtn.setTextColor(resources.getColor(R.color.success))
                }, 2000)
            } else if (downloadBtn.text == getString(R.string.install) && file.exists()) { // If the text is "Install", meaning that the mod has been copied and is located in the directory
                try {
                    // Importing the mod to minecraft
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.data = (getString(R.string.importAppName) + file).toUri()
                    startActivity(intent)
                } catch (e: android.content.ActivityNotFoundException) {
                    // If Minecraft is not installed, then go to Google Play Store
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.googlePlayStoreUri) + getString(R.string.minecraft_app_name))))
                }
            }
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.i("State", "Yes, it is writable")
            return true
        }
        return false
    }

    private fun checkForPermissions(permission: String, name: String, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when {
                ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                    saveMod(args.currentMod.modUri, requireActivity().assets)
                }
                shouldShowRequestPermissionRationale(permission) -> showDialog(
                        permission,
                        name,
                        requestCode
                )

                else -> ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(permission),
                        requestCode
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray,
    ) {
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "$name permission refused", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "$name permission granted", Toast.LENGTH_SHORT).show()
            }
        }

        when (requestCode) {
            STORAGE_RQ -> innerCheck("Storage")
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(requireContext())

        builder.apply {
            setMessage("Permission to access your $name is required to use this app")
            setTitle("Permission required")
            setPositiveButton("OK") { dialog, which ->
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(permission),
                        requestCode
                )
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}
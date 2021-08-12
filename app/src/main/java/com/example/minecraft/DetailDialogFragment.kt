package com.example.minecraft

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.minecraft.adapter.ChangeFavState
import com.example.minecraft.data.Mod
import kotlinx.android.synthetic.main.fragment_detail_dialog.view.*
import java.io.File


class DetailDialogFragment(var mod: Mod, var position: Int, var recName: String): DialogFragment(), ChangeFavState {
    private var STORAGE_RQ = 200

    private lateinit var mModViewModel: ModViewModel

    private var mListener: Listener? = null

    interface Listener {
        fun updateData(isFav: Boolean, position: Int)
        fun changeUploadIcon(isImported: Boolean, position: Int)
    }

    fun setListener(listener: Listener) {
        mListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_detail_dialog, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val favBtn = rootView.findViewById<ImageView>(R.id.fav_btn)

        activity?.findViewById<Button>(R.id.fragment_replace)?.visibility = View.GONE
        rootView.cancel_dialog.setOnClickListener {
            dismiss()
        }
        mModViewModel = ViewModelProvider(this).get(ModViewModel::class.java)

        val image = requireActivity().assets.open("images/${mod.imageUrl}")
        val bitmap = BitmapFactory.decodeStream(image)
        rootView.findViewById<ImageView>(R.id.detalized_image_view).setImageBitmap(bitmap)

        rootView.detalized_content.text = mod.content
        rootView.detalized_title.text = mod.title

        if (mod.isFav) {
            favBtn.setImageResource(R.drawable.ic_active_star)
        } else {
            favBtn.setImageResource(R.drawable.ic_inactive_star)
        }

        favBtn.setOnClickListener {
            val newState = changeState(mod, mModViewModel)
            mod.isFav = newState

            if (recName == "Main") {
                mListener?.updateData(newState, position)
            }

            if (newState) {
                favBtn.setImageResource(R.drawable.ic_active_star)
            } else {
                favBtn.setImageResource(R.drawable.ic_inactive_star)
            }
        }

        val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(filePath, mod.modUri)
        if (mod.isImported && file.exists()) {
            rootView.upload_btn.setImageResource(R.drawable.downloaded)
        }

        rootView.upload_btn.setOnClickListener {
            checkForPermissions(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    name = "Storage",
                    STORAGE_RQ
            )
        }

        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.findViewById<Button>(R.id.fragment_replace)?.visibility = View.VISIBLE
    }

    private fun saveMod(modUri: String, assetManager: AssetManager) {
        // Is the app has the memory to copy the file to the external memory
        if (isExternalStorageWritable()) {
            // Creating a filePath and file
            val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            filePath.mkdirs()
            val file = File(filePath, modUri)
            if (!file.exists()) {
                // Copy the file to external storage accessible by all
                Toast.makeText(requireContext(), getString(R.string.downloading), Toast.LENGTH_SHORT).show()
                // Copy the data from file in assets to the file, which has just been created
                file.writeBytes(assetManager.open("files/$modUri").readBytes())
                Toast.makeText(requireContext(), getString(R.string.install), Toast.LENGTH_SHORT).show()
            // Delay for 2 seconds before "Install" text appears, allowing to check another condition
            } else if (file.exists()) { // If the text is "Install", meaning that the mod has been copied and is located in the directory
                val toast = Toast.makeText(requireContext(), getString(R.string.installing), Toast.LENGTH_SHORT)
                toast.show()
                try {
                    val updatedMod = Mod(mod.id, mod.title, mod.content, mod.imageUrl, mod.isFav, mod.modUri, true)
                    mModViewModel.updateMod(updatedMod)
                    mListener?.changeUploadIcon(true, position)
                    requireView().upload_btn.setImageResource(R.drawable.downloaded)

                    // Importing the mod to minecraft
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.data = (getString(R.string.importAppName) + file).toUri()
                    requireContext().startActivity(intent)
                } catch (e: android.content.ActivityNotFoundException) {
                    // If Minecraft is not installed, then go to Google Play Store
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.googlePlayStoreUri) + getString(R.string.minecraft_app_name))))
                }
            }
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true
        }
        return false
    }

    fun checkForPermissions(permission: String, name: String, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when {
                ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                    saveMod(mod.modUri, requireActivity().assets)
                }
                shouldShowRequestPermissionRationale(permission) -> {
                    showDialog(
                            permission,
                            name,
                            requestCode
                    )
                }

                else -> {
                    ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(permission),
                            requestCode
                    )
                }
            }
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
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "$name permission refused", Toast.LENGTH_SHORT).show()
            }
        }

        when (requestCode) {
            STORAGE_RQ -> innerCheck("Storage")
        }
    }
}
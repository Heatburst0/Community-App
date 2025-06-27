package com.kv.ablecommunity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)


        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text = text
        mProgressDialog.show()
    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this@BaseActivity,
                R.color.snackbar_error_color
            )
        )
        snackBar.show()
    }
    fun launchToast(message : String,context : Context){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }

    fun showErrorDialog(context: Context,warningtext : String,confirmText : String,denyText : String,confirmResponse : ()->Unit){
        val dialog = Dialog(context)

        dialog.setContentView(R.layout.error_dialog_layout)
        dialog.findViewById<TextView>(R.id.warningText).text = warningtext
        dialog.findViewById<TextView>(R.id.confirmText).text = confirmText
        dialog.findViewById<TextView>(R.id.denyText).text = denyText
        dialog.findViewById<TextView>(R.id.confirmText).setOnClickListener {
            confirmResponse()
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.denyText).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()


    }

}
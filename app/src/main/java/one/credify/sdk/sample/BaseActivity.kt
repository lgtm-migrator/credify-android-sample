package one.credify.sdk.sample

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onGetInputData(intent)

        createDialog()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackClick()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_left_arrow)
        }
    }

    open fun onBackClick() {

    }

    open fun onGetInputData(intent: Intent) {

    }

    fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun createDialog() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCancelable(false) // if you want user to wait for some process to finish,
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun showLoading() {
        dialog.show()
    }

    fun hideLoading() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    fun showConfirmDialog(
        title: String,
        message: String,
        cancelable: Boolean = false,
        positiveText: String,
        negativeText: String? = null,
        onPositiveButtonClick: () -> Unit,
        onNegativeButtonClick: (() -> Unit)? = null,
    ) {
        val alertDialog = AlertDialog.Builder(this).setCancelable(cancelable).create()
        if (title.isNotEmpty()) {
            alertDialog.setTitle(title)
        }
        if (message.isNotEmpty()) {
            alertDialog.setMessage(message)
        }

        if (positiveText.isNotEmpty()) {
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveText) { dialog, _ ->
                dialog.dismiss()
                onPositiveButtonClick.invoke()
            }
        }

        if (negativeText != null && negativeText.isNotEmpty()) {
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeText) { dialog, _ ->
                dialog.dismiss()
                onNegativeButtonClick?.invoke()
            }
        }

        alertDialog.show()
    }
}
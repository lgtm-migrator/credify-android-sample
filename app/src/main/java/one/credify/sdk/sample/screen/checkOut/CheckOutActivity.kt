package one.credify.sdk.sample.screen.checkOut

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_check_out.*
import kotlinx.android.synthetic.main.activity_my_page.tbToolbar
import one.credify.sdk.sample.BaseActivity
import one.credify.sdk.sample.R

class CheckOutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)

        setToolbar(tbToolbar)

        lnPayWithCredify.setOnClickListener {
            showMessage(getString(R.string.msg_this_is_in_development_coming_soon))
        }

        tvPlaceOrder.setOnClickListener {
            showMessage(getString(R.string.msg_this_is_a_sample_button))
        }
    }

    override fun onBackClick() {
        finish()
    }
}
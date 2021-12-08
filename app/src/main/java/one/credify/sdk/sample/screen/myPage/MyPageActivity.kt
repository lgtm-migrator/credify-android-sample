package one.credify.sdk.sample.screen.myPage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_my_page.*
import one.credify.sdk.CredifySDK
import one.credify.sdk.core.model.UserProfile
import one.credify.sdk.sample.BaseActivity
import one.credify.sdk.sample.Constants
import one.credify.sdk.sample.R

class MyPageActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mUser: UserProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        setToolbar(tbToolbar)

        tvEditProfile.setOnClickListener(this)
        tvChangePassword.setOnClickListener(this)
        tvAboutUs.setOnClickListener(this)
        tvMyDigitalPassport.setOnClickListener(this)

        tvUserName.text = "${mUser.name.firstName} ${mUser.name.lastName}"
    }

    override fun onBackClick() {
        finish()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tvEditProfile, R.id.tvChangePassword, R.id.tvAboutUs -> {
                showMessage(getString(R.string.msg_this_is_a_sample_component))
            }
            R.id.tvMyDigitalPassport -> {
                CredifySDK.instance.offerApi.showPassport(
                    context = this,
                    userProfile = mUser,
                    callback = object : CredifySDK.PassportPageCallback {
                        override fun onClose() {
                            showMessage(getString(R.string.msg_passport_is_showing))
                        }

                        override fun onShow() {
                            showMessage(getString(R.string.msg_passport_is_closed))
                        }
                    }
                )
            }
        }
    }

    override fun onGetInputData(intent: Intent) {
        val user = intent.getSerializableExtra(Constants.INTENT_USER_PROFILE) as? UserProfile
        if (user == null) {
            finish()
            return
        }

        mUser = user
    }

    companion object {
        fun startActivity(context: Context, user: UserProfile) {
            context.startActivity(
                Intent(context, MyPageActivity::class.java).apply {
                    putExtra(Constants.INTENT_USER_PROFILE, user)
                }
            )
        }
    }
}
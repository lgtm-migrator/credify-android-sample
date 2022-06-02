package one.credify.sdk.sample.screen.useCase

import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_use_case.*
import one.credify.sdk.core.model.UserProfile
import one.credify.sdk.sample.BaseActivity
import one.credify.sdk.sample.Constants
import one.credify.sdk.sample.R
import one.credify.sdk.sample.model.MarketInfo
import one.credify.sdk.sample.requester.UserRequester
import one.credify.sdk.sample.screen.banner.BannerActivity
import one.credify.sdk.sample.screen.checkOut.CheckOutActivity
import one.credify.sdk.sample.screen.myPage.MyPageActivity

class UseCaseActivity : BaseActivity() {
    private var mUser: UserProfile? = null

    private val mMarketInfo by lazy {
        MarketInfo(
            name = Constants.MARKET_NAME,
            apiKey = Constants.API_KEY,
            path = Constants.PATH,
            environment = Constants.ENVIRONMENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_use_case)

        tvCheckOut.setOnClickListener {
            startActivity(
                Intent(it.context, CheckOutActivity::class.java)
            )
        }

        tvBanner.setOnClickListener {
            mUser?.run {
                BannerActivity.startActivity(
                    context = it.context,
                    user = this,
                    marketInfo = mMarketInfo
                )
            }
        }

        tvMyPage.setOnClickListener {
            mUser?.run {
                MyPageActivity.startActivity(
                    context = it.context,
                    user = this,
                    marketInfo = mMarketInfo
                )
            }
        }

        loadRandomUser(marketInfo = mMarketInfo)
    }

    private fun loadRandomUser(marketInfo: MarketInfo) {
        getUserProfile(context = this, marketInfo = marketInfo, userId = null) {
            mUser = it
        }
    }

    private fun getUserProfile(
        context: Context,
        marketInfo: MarketInfo,
        userId: String?,
        callback: (user: UserProfile) -> Unit
    ) {
        UserRequester().getUserProfile(
            context = context,
            url = if (userId == null)
                marketInfo.getRandomUserUrl
            else
                marketInfo.getUserByIdUrl(userId),
            onRequest = {
                showLoading()
            },
            onResult = OnResult@{ isSuccess, user ->
                hideLoading()

                if (isSuccess && user != null) {
                    callback(user)
                    return@OnResult
                }

                showLoadingUserError(marketInfo = marketInfo)
            }
        )
    }

    private fun showLoadingUserError(marketInfo: MarketInfo) {
        showConfirmDialog(
            title = getString(R.string.lb_error),
            message = getString(R.string.msg_no_user_is_loaded),
            cancelable = false,
            positiveText = getString(R.string.lb_yes),
            onPositiveButtonClick = {
                loadRandomUser(marketInfo = marketInfo)
            },
            negativeText = getString(R.string.lb_no),
            onNegativeButtonClick = {
                finish()
            }
        )
    }
}
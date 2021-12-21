package one.credify.sdk.sample.screen.banner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_banner.*
import kotlinx.android.synthetic.main.activity_my_page.tbToolbar
import one.credify.common.ui.util.ImageHelper
import one.credify.sdk.CredifySDK
import one.credify.sdk.core.CredifyError
import one.credify.sdk.core.callback.OfferListCallback
import one.credify.sdk.core.model.Offer
import one.credify.sdk.core.model.OfferList
import one.credify.sdk.core.model.RedemptionResult
import one.credify.sdk.core.model.UserProfile
import one.credify.sdk.core.request.GetOfferListParam
import one.credify.sdk.sample.BaseActivity
import one.credify.sdk.sample.Constants
import one.credify.sdk.sample.R
import one.credify.sdk.sample.model.MarketInfo
import one.credify.sdk.sample.requester.MarketRequester

class BannerActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mUser: UserProfile
    private lateinit var mMarketInfo: MarketInfo

    private var mOfferList = OfferList(emptyList(), null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)

        setToolbar(tbToolbar)

        tvAllBestSeller.setOnClickListener(this)
        tvAllFeaturedProduct.setOnClickListener(this)
        ivOffer.setOnClickListener(this)

        getOfferList(
            user = mUser,
            onResult = OnResult@{ offerList ->
                mOfferList = offerList

                if (offerList.offerList.isEmpty()) {
                    showMessage(getString(R.string.msg_there_are_not_any_offers))
                    return@OnResult
                }

                offerList.offerList.firstOrNull { !it.useReferral }?.run {
                    ImageHelper.load(
                        iv = ivOffer,
                        url = campaign.thumbnailUrl
                    )

                    ivOffer.setTag(ivOffer.id, this)
                }
            }
        )
    }

    override fun onBackClick() {
        finish()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvAllBestSeller, R.id.tvAllFeaturedProduct -> {
                showMessage(getString(R.string.msg_this_is_a_sample_item))
            }
            R.id.ivOffer -> {
                val offer = ivOffer.getTag(ivOffer.id)

                if (offer is Offer) {
                    CredifySDK.instance.offerApi.showOffer(
                        context = this,
                        offer = offer,
                        userProfile = mUser,
                        credifyId = mOfferList.credifyId,
                        marketName = mMarketInfo.name,
                        pushClaimCallback = object : CredifySDK.PushClaimCallback {
                            override fun onPushClaim(
                                credifyId: String,
                                user: UserProfile,
                                resultCallback: CredifySDK.PushClaimResultCallback
                            ) {
                                MarketRequester().pushClaimToken(
                                    context = this@BannerActivity,
                                    marketInfo = mMarketInfo,
                                    localId = user.id,
                                    credifyId = credifyId,
                                    onRequest = {
                                        // Do nothing
                                    },
                                    onResult = { isSuccess ->
                                        resultCallback.onPushClaimResult(isSuccess = isSuccess)
                                    }
                                )
                            }
                        },
                        offerPageCallback = object : CredifySDK.OfferPageCallback {
                            override fun onClose(status: RedemptionResult) {
                                // There are three status
                                // - COMPLETED: the user redeemed offer successfully and the offer transaction status is COMPLETED.
                                // - PENDING:   the user redeemed offer successfully and the offer transaction status is PENDING.
                                // - CANCELED:  the user redeemed offer successfully and he canceled this offer afterwords OR he clicked
                                //              on the back button in any screens in the offer redemption flow.
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onGetInputData(intent: Intent) {
        val user = intent.getSerializableExtra(Constants.INTENT_USER_PROFILE) as? UserProfile
        val marketInfo = intent.getSerializableExtra(Constants.INTENT_MARKET_INFO) as? MarketInfo
        if (user == null || marketInfo == null) {
            finish()
            return
        }

        mUser = user
        mMarketInfo = marketInfo
    }

    private fun getOfferList(
        user: UserProfile,
        onResult: (offerList: OfferList) -> Unit
    ) {
        showLoading()

        val params = GetOfferListParam(
            phoneNumber = user.phone.phoneNumber,
            countryCode = user.phone.countryCode,
            localId = user.id,
            credifyId = mOfferList.credifyId,
            productTypes = emptyList()
        )

        CredifySDK.instance.offerApi.getOfferList(
            params = params,
            callback = object : OfferListCallback {
                override fun onSuccess(model: OfferList) {
                    hideLoading()

                    onResult(model)
                }

                override fun onError(throwable: CredifyError) {
                    hideLoading()

                    onResult(OfferList(emptyList(), null))
                }
            }
        )
    }

    companion object {
        fun startActivity(
            context: Context,
            user: UserProfile,
            marketInfo: MarketInfo
        ) {
            context.startActivity(
                Intent(context, BannerActivity::class.java).apply {
                    putExtra(Constants.INTENT_USER_PROFILE, user)
                    putExtra(Constants.INTENT_MARKET_INFO, marketInfo)
                }
            )
        }
    }
}
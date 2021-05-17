# Credify SDK Sample
- The SDK supports API level 23 and above ([distribution stats](https://developer.android.com/about/dashboards)).
- Update the `build.gradle`(project level). 
    ```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
            mavenCentral()
        }
    }
    ```

- Update the `build.gradle`(app level).
    ```
    dependencies {
        implementation 'one.credify.sdk:android-sdk:v0.1.3'
    }
    ```
- Sync project with Gradle file    
# Getting stated
  - Create `CredifySDK` instance. 
  - If you have created a class that extends from `Application` class. You only add the below code to the `onCreate()` method
    ```
    override fun onCreate() {
        super.onCreate()
        ...
        CredifySDK.Builder()
            .withApiKey([Your API Key])
            .withContext(this)
            .withEnvironment([Environment])
            .build()
        ...
    }
    ```
  - Otherwise, you need to create a new class that extends from `Application` class. In this example, I named it with `DemoApplication`.
    ```
    ...
    class DemoApplication : Application() {
        override fun onCreate() {
            super.onCreate()
    
            CredifySDK.Builder()
                .withApiKey([Your API Key])
                .withContext(this)
                .withEnvironment([Environment])
                .build()
        }
        ...
    }
    ```
    **AND** update your `AndroidManifest.xml` file.
    ```
    <application
        android:name=".DemoApplication"
        ...
        >
        ...
    </application>
    ```
  - After creating the `CredifySDK` instance, you can get it by using:
    ```
    val credifySDK = CredifySDK.instance
    ```
# Offer
##### Get offer list
  - First, you need to create a paramater by using `GetOfferListParam` class
  - Second, to get offer list from Credify, you should use `getOfferList` method:
    ```
    val params = GetOfferListParam(
        phoneNumber = [Your user phone number] (Optional),
        countryCode = [Your user phone number country code] (Optional),
        localId = [Your user id],
        credifyId = [Your user's credify id] (Optional)
    )
    
    CredifySDK.instance.getOfferList(params = params, callback: OfferListCallback)
    OR
    CredifySDK.instance.getOfferList(params = params): Observable<List<Offer>>
    ```
##### Show offer detail
  - Create `one.credify.sdk.core.model.UserProfile` object
    ```
    val user = UserProfile(
        id = [Your user id],
        name = Name(
            firstName = [Your user's first name],
            lastName = [Your user's last name],
            middleName = [Your user's middle name](Optional),
            name = [Your user's full name](Optional),
            verified = [Is your user's name verified?]
        ),
        phone = Phone(
            phoneNumber = [Your user's phone number],
            countryCode = [Your user's phone country code],
            verified = [Is your user's phone number verified?]
        ),
        email = [Your user's email],
        dob = [Your user's day of birth](Optional),
        address = [Your user's address](Optional)
    )
    ```
  - To show an **offer detail** by using:
    ```
    CredifySDK.instance.showOffer(
        context = [Context],
        offer = [one.credify.sdk.core.model.Offer object],
        userProfile = [one.credify.sdk.core.model.UserProfile object],
        credifyId = [Your user's credify id. If your user have created Credify account then it should not be null],
        marketName = [Your app name],
        pushClaimCallback = [CredifySDK.PushClaimCallback callback],
        offerPageCallback = [CredifySDK.OfferPageCallback callback]
    )
    ```
  - When you pass `credifyId` argument with **null** value then you have to handle the `CredifySDK.PushClaimCallback` callback for pushing claims. For example:
    ```
    CredifySDK.instance.showOffer(
        context = [Context],
        offer = [one.credify.sdk.core.model.Offer object],
        userProfile = [one.credify.sdk.core.model.UserProfile object],
        credifyId = [Your user's credify id],
        marketName = [Your app name],
        pushClaimCallback = object : CredifySDK.PushClaimCallback {
            override fun onPushClaim(
                credifyId: String,
                user: UserProfile,
                resultCallback: CredifySDK.PushClaimResultCallback
            ) {
                // Code for pushing claims, you need to call your API to do this task.
                // After pushing claims, you have to notify to Credify SDK. For example:
                resultCallback.onPushClaimResult(
                    isSuccess = [true if success. Otherwise, pass false]
                )
            }
        },
        offerPageCallback = [CredifySDK.OfferPageCallback callback]
    )
    ```
  - To handle when the **offer detail** page is closed, you have to handle the `CredifySDK.OfferPageCallback` callback. For example:   
    ```
    CredifySDK.instance.showOffer(
        context = [Context],
        offer = [one.credify.sdk.core.model.Offer object],
        userProfile = [one.credify.sdk.core.model.UserProfile object],
        credifyId = [Your user's credify id],
        marketName = [Your app name],
        pushClaimCallback = [CredifySDK.PushClaimCallback callback],
        offerPageCallback = object : CredifySDK.OfferPageCallback {
            override fun onClose() {
                // Your code logic here
            }
        }
    )
    ```
##### Referral
  - Using the below code for showing the **referrals** with `completed` status:
    ```
    CredifySDK.instance.showReferralResult(
        context = [Context],
        userProfile = [one.credify.sdk.core.model.UserProfile object],
        marketName = [Your app name],
        callback = object : CredifySDK.OnShowReferralResultCallback {
            override fun onShow() {
                // The page is showing on the UI
            }

            override fun onError(ex: Exception) {
                // There is an error
            }

            override fun onClose() {
                // The page is closed
            }
        }
    )
    ```
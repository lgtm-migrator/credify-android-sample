# Credify SDK Sample

The SDK supports API level 23 and above ([distribution stats](https://developer.android.com/about/dashboards)).

Update the `build.gradle`(project level). 
    
```java
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
        mavenCentral()
    }
}
```

Update the `build.gradle`(app level).
    
```java
android {
    ...
    
    aaptOptions {
        noCompress "tflite"
        noCompress "lite"
        noCompress "bic"
    }
    ...
}

dependencies {
    implementation 'one.credify.sdk:android-sdk:v0.1.11'
}
```

Sync project with Gradle file.

**Note:** If you gets the below error when you build your project.

<img width="569" alt="Screenshot 2021-07-01 at 20 10 33" src="https://user-images.githubusercontent.com/18586774/124133379-f9320600-daab-11eb-8ae1-84c4c6deed5a.png">

Then you need to add this line *tools:replace="android:supportsRtl"* into the *<application>* element in the *AndroidManifest.xml*.
    
<img width="622" alt="Screenshot 2021-07-01 at 20 24 52" src="https://user-images.githubusercontent.com/18586774/124133651-3d250b00-daac-11eb-80fd-981cf85568c8.png">    


## Getting stated

### Set up the SDK

Create `CredifySDK` instance. 

If you have created a class that extends from `Application` class. You only add the below code to the `onCreate()` method.
You can generate a new API key on the serviceX dashboard.

```kotlin
override fun onCreate() {
    super.onCreate()
    ...
    CredifySDK.Builder()
        .withApiKey([Your API Key])
        .withContext(this)
        .withEnvironment([Environment])
        .withTheme([ServiceXThemeConfig]) // it's available on SDK version v0.1.11
        .build()
    ...
}
```

Otherwise, you will need to create a new class that extends from `Application` class. In this example, I named it with `DemoApplication`.

```kotlin
...
class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Generates a singleton here
        CredifySDK.Builder()
            .withApiKey([Your API Key])
            .withContext(this)
            .withEnvironment([Environment])
            .withTheme([ServiceXThemeConfig]) // it's available on SDK version v0.1.11
            .build()
    }
    ...
}
```

**AND** update your `AndroidManifest.xml` file.

```xml
<application
    android:name=".DemoApplication"
    ...
    >
    ...
</application>
```


After creating the `CredifySDK` instance, you can access the singleton like following:
    
```kotlin
val credifySDK = CredifySDK.instance
```


### Offer usage

#### Get offers list

First, you need to create a parameter object by using `GetOfferListParam` class.

Secondly, to get offers list from the Credify SDK, you should use `getOfferList` method:
    
```kotlin
val params = GetOfferListParam(
    phoneNumber = // Your user phone number (Optional) - e.g. "32123456789",
    countryCode = // Your user phone number country code (Optional) - e.g. "+84",
    localId = // Your user id,
    credifyId = // Your user's credify id (Optional)
)

CredifySDK.instance.offerApi.getOfferList(params = params, callback: OfferListCallback)
// OR
CredifySDK.instance.offerApi.getOfferList(params = params): Observable<OfferList>

// NOTE: you need to pass credifyId when you call `CredifySDK.instance.offerApi.showOffer` method
data class OfferList(val offerList: List<Offer>, val credifyId: String?) : Serializable
```
    
> **Important**: you need to keep `credifyId` on your side. You have to send the `credifyId` to Credify SDK when you use the methods that require `credifyId`. E.g: `CredifySDK.instance.offerApi.showOffer`

#### Show an offer detail

Create `one.credify.sdk.core.model.UserProfile` object.
```kotlin
val user = UserProfile(
    id = // Your user id,
    name = UserName(
        firstName = // Your user's first name,
        lastName = // Your user's last name,
        middleName = // Your user's middle name (Optional),
        name = // Your user's full name (Optional)
    ),
    phone = UserPhoneNumber(
        phoneNumber = // Your user's phone number,
        countryCode = // Your user's phone country code
    ),
    email = // Your user's email,
    dob = // Your user's day of birth (Optional),
    address = // Your user's address (Optional)
)
```

To show an **offer detail** by using:

```kotlin
CredifySDK.instance.offerApi.showOffer(
    context = // Context,
    offer = // one.credify.sdk.core.model.Offer object,
    userProfile = // one.credify.sdk.core.model.UserProfile object,
    credifyId = // Your user's credify id. If your user have created Credify account then it should not be null,
    marketName = // Your app name,
    pushClaimCallback = // CredifySDK.PushClaimCallback callback,
    offerPageCallback = // CredifySDK.OfferPageCallback callback
)
```

You have to handle the `CredifySDK.PushClaimCallback` callback for pushing claims. For example:

```kotlin
CredifySDK.instance.offerApi.showOffer(
    context = // Context,
    offer = // one.credify.sdk.core.model.Offer object,
    userProfile = // one.credify.sdk.core.model.UserProfile object,
    credifyId = // Your user's credify id,
    marketName = // Your app name,
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
```
    
> **Important**: you need to keep `credifyId` on your side. You have to send the `credifyId` to Credify SDK when you use the methods that require `credifyId`. E.g: `CredifySDK.instance.offerApi.showOffer`

To handle when the **offer detail** page is closed, you have to handle the `CredifySDK.OfferPageCallback` callback. For example:   

```kotlin
CredifySDK.instance.offerApi.showOffer(
    context = // Context,
    offer = // one.credify.sdk.core.model.Offer object,
    userProfile = // one.credify.sdk.core.model.UserProfile object,
    credifyId = // Your user's credify id,
    marketName = // Your app name,
    pushClaimCallback = // CredifySDK.PushClaimCallback callback,
    offerPageCallback = object : CredifySDK.OfferPageCallback {
        override fun onClose() {
            // Your code logic here
        }
    }
)
```


#### Use Referral

Using the below code for showing the **referral information** with `completed` status:

```kotlin
CredifySDK.instance.referralApi.showReferralResult(
    context = // Context,
    userProfile = // one.credify.sdk.core.model.UserProfile object,
    marketName = // Your app name,
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

#### Show Passport

Using the below code for showing the **Passport web app**. This page will show all the offers which the user has redeemed.

```kotlin
CredifySDK.instance.offerApi.showPassport(
    context = // Context,
    userProfile = // one.credify.sdk.core.model.UserProfile object,
    callback = object : CredifySDK.PassportPageCallback {
        override fun onShow() {
            // The page is showing on the UI
        }

        override fun onClose() {
            // The page is closed
        }
    }
)
```

### Customize theme
Below is an example result if you do customize theme.

![Theme](./imgs/ThemeOverview.png)

You can use `ServiceXThemeConfig` class to config the `fonts`, `colors`, `input fields` and so on for the serviceX SDK. 
You have to create `ServiceXThemeConfig` object when initializing the SDK. But it is optional. The SDK will use the default theme if you don't want to customize theme
```kotlin
    CredifySDK.Builder()
        .withApiKey([Your API Key])
        .withContext(this)
        .withEnvironment([Environment])
        .withTheme([ServiceXThemeConfig]) // it's available on SDK version v0.1.11
        .build()
```

#### ServiceXThemeConfig class

```kotlin
/**
 * @param color: it is [ThemeColor] object
 * @param font: it is [ThemeFont] object
 * @param icon: it is [ThemeIcon] object
 * @param actionBarTopLeftRadius: top-left action bar radius
 * @param actionBarBottomLeftRadius: bottom-left action bar radius
 * @param actionBarTopRightRadius: top-right action bar radius
 * @param actionBarBottomRightRadius: bottom-right action bar radius
 * @param datePickerStyle: data picker theme(style id)
 * @param elevation: it is in dp
 * @param inputFieldRadius: it is in dp
 * @param modelRadius: it is in dp
 * @param buttonRadius: it is in dp
 */
class ServiceXThemeConfig(
    val context: Context,
    val color: ThemeColor,
    val font: ThemeFont,
    val icon: ThemeIcon,
    val actionBarTopLeftRadius: Float,
    val actionBarBottomLeftRadius: Float,
    val actionBarTopRightRadius: Float,
    val actionBarBottomRightRadius: Float,
    val datePickerStyle: Int,
    val elevation: Float,
    val inputFieldRadius: Float,
    val modelRadius: Int = 10,
    val buttonRadius: Float = 50F,
)
```
![Theme](./imgs/ThemeBorderRadius.png)

#### ThemeColor class

```kotlin
/**
 * All properties are in [Int]. We can use 
 * - Color.parse([String]),
 * - ContextCompat.getColor([Context], [Color resource id])
 * - Color.WHITE, Color.BLUE,...
 
 * For example: Color.parseColor("#ff00ff")
 */
class ThemeColor(
    val primaryBrandyStart: Int,
    val primaryBrandyEnd: Int,
    val primaryText: Int,
    val secondaryActive: Int,
    val secondaryDisable: Int,
    val secondaryText: Int,
    val secondaryComponentBackground: Int,
    val secondaryBackground: Int,
    val topBarTextColor: Int,
    val primaryButtonTextColor: Int,
    val primaryButtonBrandyStart: Int,
    val primaryButtonBrandyEnd: Int,
)
```
![Theme](./imgs/ThemeColor1.png)
![Theme](./imgs/ThemeColor2.png)
![Theme](./imgs/ThemeColor3.png)


#### ThemeFont class

```kotlin
/**
 * @param primaryFontFamily it is [Typeface] object. We can use ResourcesCompat.getFont(Context, R.font.your_font)
 * @param secondaryFontFamily it is [Typeface] object. We can use ResourcesCompat.getFont(Context, R.font.your_font)
 */
class ThemeFont(
    val context: Context,
    val primaryFontFamily: Typeface?,
    val secondaryFontFamily: Typeface?,
    val secondaryFontWeight: Int,
    val bigTitleFontSize: Float,
    val bigTitleFontLineHeight: Int,
    val modelTitleFontSize: Float,
    val modelTitleFontLineHeight: Int,
    val sectionTitleFontSize: Float,
    val sectionTitleFontLineHeight: Int,
    val bigFontSize: Float,
    val bigFontLineHeight: Int,
    val normalFontSize: Float,
    val normalFontLineHeight: Int,
    val smallFontSize: Float,
    val smallFontLineHeight: Int,
    val boldFontSize: Float,
    val boldFontLineHeight: Int,
)
```
![Theme](./imgs/ThemeFont1.png)
![Theme](./imgs/ThemeFont2.png)
![Theme](./imgs/ThemeFont3.png)
![Theme](./imgs/ThemeFont4.png)

#### ThemeIcon class

```kotlin
class ThemeIcon(
    val context: Context,
    val backIcon: Drawable?,
    val closeIcon: Drawable?,
)

```
![Theme](./imgs/ThemeIcon1.png)
![Theme](./imgs/ThemeIcon2.png)

## Contacts

If you find any bugs or issues, please let us know (dev@credify.one). Of course, welcome to open new issues in this GitHub!

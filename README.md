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
dependencies {
    implementation 'one.credify.sdk:android-sdk:v0.1.5'
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
CredifySDK.instance.offerApi.getOfferList(params = params): Observable<List<Offer>>
```

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
    offerPageCallback = // CredifySDK.OfferPageCallback callback
)
```

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


## Contacts

If you find any bugs or issues, please let us know (dev@credify.one). Of course, welcome to open new issues in this GitHub!

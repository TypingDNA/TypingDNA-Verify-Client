# TypingDNA Verify Java Client

## Usage and description

### Initializing the client

Firstly, you need to download the latest version of the client from the release page and include the JAR in your project.

Then import the __TypingDNAVerifyClient__ class and creat a client object:

```java
import com.typingdna.TypingDNAVerifyClient;
...
private TypingDNAVerifyClient client = new TypingDNAVerifyClient(APPLICATION_ID, CLIENT_ID, SECRET);
```

The __CLIENT_ID__ and __SECRET__ can be found in the *Verify* dashboard of your *TypingDNA* account. The __APPLICATION_ID__ will be generated when you create a new integration. For more information see the [Initial Setup](https://verify.typingdna.com/docs#api-setup) section of the *TypingDNA Verify* documentation.

### Getting the user data that will be passed to the frontend

> To authenticate a user, his/her phone number or email address or both must be passed to the TypingDNAVerifyClient.

Once initialized, the next step is to retrieve end-user data in the backend, for encryption and linking with the frontend button.

The __getDataAttributes__ method is called to pass credentials and encrypted user data to the frontend snippet. Both the language and flow are optional parameters, defaulting to ‘EN’ and ‘STANDARD’ respectively. Support for additional languages is in progress.

```java
TypingDNAVerifyDataAttributes typingDNADataAttributes = client.getDataAttributes(
    new TypingDNAVerifyPayload(
        "userEmail",
        "userPhoneNumberWithCountryCode",
        "EN",
        "STANDARD"  // "SHOW_OTP" alternatively
    )
);
```

### Validating an OTP received from the user

To validate an OTP received from the user simply call the __validateOTP__ method.

```java
TypingDNAValidateOTPResponse response = client.validateOTP(
        new TypingDNAVerifyPayload(
                "userEmail",
                "userPhoneNumberWithCountryCode"
        ),
        "otpCode"
);
```

### Manually sending an OTP

To manually send an OTP call the __sendOTP__ method. This method manually triggers the sending of a verification code to the user’s email or phone. This bypasses the *Verify* window’s typing verification flow, and is useful for scenarios in which users cannot type to confirm their identity, such as access via a mobile device.
  
```java
TypingDNASendOTPResponse response = client.sendOTP(
        new TypingDNAVerifyPayload(
                "userEmail",
                "userPhoneNumberWithCountryCode"
        )
);
```

## Reference:
- __TypingDNAVerifyClient__:
    - __new TypingDNAVerifyClient(*appId*, *clientId*, *secret*)__:
        - *appId* {String} - found in the *TypingDNA Verify* dashboard after creating an integration
        - *clientId* {String} - found in the *TypingDNA Verify* dashboard
        - *secret* {String} - found in the *TypingDNA Verify* dashboard
    - __*TypingDNAVerifyDataAttributes* getDataAttributes(*payload*) throws TypingDNAVerifyException__:
        - *payload* {TypingDNAVerifyPayload}
    - __*TypingDNAValidateOTPResponse* validateOTP(*payload*, *code*) throws TypingDNAVerifyException__:
        - *payload* {TypingDNAVerifyPayload}
        - *code* {String} - the OTP code received from the user
    - __*TypingDNASendOTPResponse* sendOTP(*payload*) throws TypingDNAVerifyException__:
        - *payload* {TypingDNAVerifyPayload}
- __TypingDNAVerifyPayload__:
    - __new TypingDNAVerifyPayload(*userEmail*, *userPhoneNumberWithCountryCode*, *language*, *flow*)__:
        - *userEmail* {String}
        - *userPhoneNumberWithCountryCode* {String}
        - *language* {String} - (default "EN") the language code. All the supported languages can be found in the *Verify* documentation
        - *flow* {String} - (default "STANDARD") "STANDARD" or "SHOW_OTP". For more information on the difference between these two flows check the *Verify* documentation
    - __new TypingDNAVerifyPayload(*userEmail*, *userPhoneNumberWithCountryCode*)__:
        - *userEmail* {String}
        - *userPhoneNumberWithCountryCode* {String}
- __TypingDNAVerifyDataAttributes__:
    - __new TypingDNAVerifyDataAttributes(*clientId*, *appId*, *payload*)__:
        - *clientId* {String} - found in the *TypingDNA Verify* dashboard
        - *appId* {String} - found in the *TypingDNA Verify* dashboard after creating an integration
        - *payload* {String}
- __TypingDNASendOTPResponse__:
    - __new TypingDNASendOTPResponse(*success*, *code*, *message*, *status*)__:
        - *success* {int} - 1 or 0
        - *code* {int} - the *TypingDNA Verify* response code
        - *message* {String} - a description of the outcome of the operation
        - *status* {int} - the HTTP status code
        - *otp* {String} - the OTP code sent to the user
- __TypingDNAValidateOTPResponse__:
    - __new TypingDNAValidateOTPResponse(*success*, *code*, *message*, *status*)__:
        - *success* {int} - 1 or 0
        - *code* {int} - the *TypingDNA Verify* response code
        - *message* {String} - a description of the outcome of the operation
        - *status* {int} - the HTTP status code

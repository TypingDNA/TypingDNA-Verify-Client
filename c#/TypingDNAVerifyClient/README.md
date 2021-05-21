# TypingDNA Verify .Net Client

## Usage and description

### Initializing the client

Firstly, you need to download the latest version of the client from the release page and include it in your project or install it using __NuGet__.

```bash
dotnet add package TypingDNAVerifyClient
```

Then import the __TypingDNA__ namespace and creat a client object:

```csharp
using TypingDNA;
...
TypingDNAVerifyClient client = new TypingDNAVerifyClient(APPLICATION_ID, CLIENT_ID, SECRET);
```

The __CLIENT_ID__ and __SECRET__ can be found in the *Verify* dashboard of your *TypingDNA* account. The __APPLICATION_ID__ will be generated when you create a new integration. For more information see the [Initial Setup](https://verify.typingdna.com/docs#api-setup) section of the *TypingDNA Verify* documentation.

### Getting the user data that will be passed to the frontend

> To authenticate a user, his/her phone number or email address or both must be passed to the TypingDNAVerifyClient.

Once initialized, the next step is to retrieve end-user data in the backend, for encryption and linking with the frontend button.

The __GetDataAttributes__ method is called to pass credentials and encrypted user data to the frontend snippet. Both the language and mode are optional parameters, defaulting to ‘EN’ and ‘standard’ respectively. Support for additional languages is in progress.

```csharp
TypingDNAVerifyDataAttributes typingDNADataAttributes = client.GetDataAttributes(
    new TypingDNAVerifyPayload(
        "userEmail",
        "userPhoneNumberWithCountryCode",
        "EN",
        "standard"  // "show_otp" alternatively
    )
);
```

### Validating an OTP received from the user

To validate an OTP received from the user simply call the __ValidateOTP__ method.

```csharp
TypingDNAValidateOTPResponse response = await client.ValidateOTP(
        new TypingDNAVerifyPayload(
                "userEmail",
                "userPhoneNumberWithCountryCode"
        ),
        "otpCode"
);
```

### Manually sending an OTP

To manually send an OTP call the __SendOTP__ method. This method manually triggers the sending of a verification code to the user’s email or phone. This bypasses the *Verify* window’s typing verification flow, and is useful for scenarios in which users cannot type to confirm their identity, such as access via a mobile device.
  
```csharp
TypingDNASendOTPResponse response = client.SendOTP(
        new TypingDNAVerifyPayload(
                "userEmail",
                "userPhoneNumberWithCountryCode"
        )
);
```

## Reference:
- __TypingDNAVerifyClient__:
    - __new TypingDNAVerifyClient(*appId*, *clientId*, *secret*)__:
        - *appId* {string} - found in the *TypingDNA Verify* dashboard after creating an integration
        - *clientId* {string} - found in the *TypingDNA Verify* dashboard
        - *secret* {string} - found in the *TypingDNA Verify* dashboard
    - __*TypingDNAVerifyDataAttributes* GetDataAttributes(*payload*) throws TypingDNAVerifyException__:
        - *payload* {TypingDNAVerifyPayload}
    - __*TypingDNAValidateOTPResponse* ValidateOTP(*payload*, *code*) throws TypingDNAVerifyException__:
        - *payload* {TypingDNAVerifyPayload}
        - *code* {string} - the OTP code received from the user
    - __*TypingDNASendOTPResponse* SendOTP(*payload*) throws TypingDNAVerifyException__:
        - *payload* {TypingDNAVerifyPayload}
- __TypingDNAVerifyPayload__:
    - __new TypingDNAVerifyPayload(*userEmail*, *userPhoneNumberWithCountryCode*, *language*, *mode*)__:
        - *userEmail* {string}
        - *userPhoneNumberWithCountryCode* {string}
        - *language* {string} - (default "EN") the language code. All the supported languages can be found in the *Verify* documentation
        - *mode* {string} - (default "standard") "standard" or "show_otp". For more information on the diference between these two modes check the *Verify* documentation
    - __new TypingDNAVerifyPayload(*userEmail*, *userPhoneNumberWithCountryCode*)__:
        - *userEmail* {string}
        - *userPhoneNumberWithCountryCode* {string}
- __TypingDNAVerifyDataAttributes__:
    - __new TypingDNAVerifyDataAttributes(*clientId*, *appId*, *payload*)__:
        - *clientId* {string} - found in the *TypingDNA Verify* dashboard
        - *appId* {string} - found in the *TypingDNA Verify* dashboard after creating an integration
        - *payload* {string}
- __TypingDNASendOTPResponse__:
    - __new TypingDNASendOTPResponse(*success*, *code*, *message*, *status*)__:
        - *success* {int} - 1 or 0
        - *code* {int} - the *TypingDNA Verify* response code
        - *message* {string} - a description of the outcome of the operation
        - *status* {int} - the HTTP status code
        - *otp* {string} - the OTP code sent to the user
- __TypingDNAValidateOTPResponse__:
    - __new TypingDNAValidateOTPResponse(*success*, *code*, *message*, *status*)__:
        - *success* {int} - 1 or 0
        - *code* {int} - the *TypingDNA Verify* response code
        - *message* {string} - a description of the outcome of the operation
        - *status* {int} - the HTTP status code

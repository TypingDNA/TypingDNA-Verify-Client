# TypingDNA Verify PHP Client

## Usage and description

### Initializing the client

Firstly, you need to download the latest version of the client from the release page and include the files in your project.

Then include the __TypingDNAVerifyClient__ class and creat a client instance:

```php
include('TypingDNAVerifyClient.php');
...
$typingDNAVerifyClient = new TypingDNAVerifyClient(APPLICATION_ID, CLIENT_ID, SECRET);
```

The __CLIENT_ID__ and __SECRET__ can be found in the *Verify* dashboard of your *TypingDNA* account. The __APPLICATION_ID__ will be generated when you create a new integration. For more information see the [Initial Setup](https://verify.typingdna.com/docs#api-setup) section of the *TypingDNA Verify* documentation.

### Getting the user data that will be passed to the frontend

> To authenticate a user, his/her phone number or email address or both must be passed to the TypingDNAVerifyClient.

Once initialized, the next step is to retrieve end-user data in the backend, for encryption and linking with the frontend button.

The __getDataAttributes__ method is called to pass credentials and encrypted user data to the frontend snippet. Both the language and mode are optional parameters, defaulting to ‘EN’ and ‘standard’ respectively. Support for additional languages is in progress.  

```php
$typingDNADataAttributes = $typingDNAVerifyClient->getDataAttributes([
    email => "userEmail",
    phoneNumber => "userPhoneNumberWithCountryCode",
    language => "EN",
    mode => "standard",  // "show_otp" alternatively
]);
```

### Validating an OTP received from the user

To validate an OTP received from the user simply call the __validateOTP__ method.

```php
$response = $typingDNAVerifyClient->validateOTP([
    email => "userEmail",
    phoneNumber => "userPhoneNumberWithCountryCode",
], "otpCode");
```

### Manually sending an OTP

To manually send an OTP call the __sendOTP__ method. This method manually triggers the sending of a verification code to the user’s email or phone. This bypasses the *Verify* window’s typing verification flow, and is useful for scenarios in which users cannot type to confirm their identity, such as access via a mobile device.  

```php
$response = $typingDNAVerifyClient->sendOTP([
    email => "userEmail",
    phoneNumber => "userPhoneNumberWithCountryCode",
]);
```

## Reference:
- __TypingDNAVerifyClient__:
    - __new TypingDNAVerifyClient(*appId*, *clientId*, *secret*)__:
        - *appId* {String} - found in the *TypingDNA Verify* dashboard after creating an integration
        - *clientId* {String} - found in the *TypingDNA Verify* dashboard
        - *secret* {String} - found in the *TypingDNA Verify* dashboard
    - __*TypingDNAVerifyDataAttributes* getDataAttributes(*payload*)__:
        - *payload* {Array}
            - *email* {String?} - user email
            - *phoneNumber* {String?} - user phone number with country code
            - *language* {String?} - (default "EN") the language code. All the supported languages can be found in the *Verify* documentation
            - *mode* {String?} - (default "standard") "standard" or "show_otp". For more information on the diference between these two modes check the *Verify* documentation
    - __*TypingDNAValidateOTPResponse* validateOTP(*payload*, *code*)__:
        - *payload* {Array}
            - *email* {String?} - user email
            - *phoneNumber* {String?} - user phone number with country code
        - *code* {String} - the OTP code received from the user
    - __*TypingDNASendOTPResponse* sendOTP(*payload*)__:
        - *payload* {Array}
            - *email* {String?} - user email
            - *phoneNumber* {String?} - user phone number with country code
- __TypingDNAVerifyDataAttributes__:
    - *clientId* {String} - found in the *TypingDNA Verify* dashboard
    - *applicationId* {String} - found in the *TypingDNA Verify* dashboard after creating an integration
    - *payload* {String}
- __TypingDNAValidateOTPResponse__:
    - *success* {number} - 1 or 0
    - *code* {number} - the *TypingDNA Verify* response code
    - *message* {String} - a description of the outcome of the operation
    - *status* {number} - the HTTP status code
- __TypingDNASendOTPResponse__:
    - *success* {number} - 1 or 0
    - *code* {number} - the *TypingDNA Verify* response code
    - *message* {String} - a description of the outcome of the operation
    - *status* {number} - the HTTP status code
    - *otp* {String?} - the OTP code sent to the user

# TypingDNA Verify JavaScript Client

## Usage and description

### Initializing the client

Firstly, you need to install the latest version of the client. This can be done using __npm__:  

```bash
npm install --save typingdna-verify-client
```

Then import the __TypingDNAVerifyClient__ class and creat a client object:  

```javascript
const TypingDNAVerifyClient = require('typingdna-verify-client');
...
const client = new TypingDNAVerifyClient({
    applicationId: 'APPLICATION_ID',
    clientId: 'CLIENT_ID',
    secret: 'SECRET',
});
```

The __CLIENT_ID__ and __SECRET__ can be found in the *Verify* dashboard of your *TypingDNA* account. The __APPLICATION_ID__ will be generated when you create a new integration. For more information see the [Initial Setup](https://verify.typingdna.com/docs#api-setup) section of the *TypingDNA Verify* documentation.

### Getting the user data that will be passed to the frontend

> To authenticate a user, his/her phone number or email address or both must be passed to the TypingDNAVerifyClient.

Once initialized, the next step is to retrieve end-user data in the backend, for encryption and linking with the frontend button.

The __getDataAttributes__ method is called to pass credentials and encrypted user data to the frontend snippet. Both the language and flow are optional parameters, defaulting to ‘EN’ and ‘STANDARD’ respectively. Support for additional languages is in progress.  

```javascript
const typingDNADataAttributes = client.getDataAttributes({
        email: "userEmail",
        phoneNumber: "userPhoneNumberWithCountryCode",
        language: "EN",
        flow: "STANDARD",  // "SHOW_OTP" alternatively
});
```

### Validating an OTP received from the user

To validate an OTP received from the user simply call the __validateOTP__ method.  

```javascript
const response = await client.validateOTP(
    {
        email: "userEmail",
        phoneNumber: "userPhoneNumberWithCountryCode",
    },
    "otpCode"
);
```

### Manually sending an OTP

To manually send an OTP call the __sendOTP__ method. This method manually triggers the sending of a verification code to the user’s email or phone. This bypasses the *Verify* window’s typing verification flow, and is useful for scenarios in which users cannot type to confirm their identity, such as access via a mobile device.  

```javascript
const response = await client.sendOTP({
    email: "userEmail",
    phoneNumber: "userPhoneNumberWithCountryCode",
});
```

## Reference:
- __TypingDNAVerifyClient__:
    - __new TypingDNAVerifyClient(*config*)__:
        - *config* {Object}:
            - *applicationId* {String} - found in the *TypingDNA Verify* dashboard after creating an integration
            - *clientId* {String} - found in the *TypingDNA Verify* dashboard
            - *secret* {String} - found in the *TypingDNA Verify* dashboard
    - __*TypingDNAVerifyDataAttributes* getDataAttributes(*config*)__:
        - *config* {Object}
            - *email* {String?} - user email
            - *phoneNumber* {String?} - user phone number with country code
            - *language* {String?} - (default "EN") the language code. All the supported languages can be found in the *Verify* documentation
            - *flow* {String?} - (default "STANDARD") "STANDARD" or "SHOW_OTP". For more information on the difference between these two flows check the *Verify* documentation
    - __*TypingDNAValidateOTPResponse* validateOTP(*config*, *code*)__:
        - *config* {Object}
        - *email* {String?} - user email
        - *phoneNumber* {String?} - user phone number with country code
        - *code* {String} - the OTP code received from the user
    - __*TypingDNASendOTPResponse* sendOTP(*config*)__:
        - *config* {Object}
            - *email* {String?} - user email
            - *phoneNumber* {String?} - user phone number with country code
- __TypingDNAVerifyDataAttributes__:
    - *clientId* {String} - found in the *TypingDNA Verify* dashboard
    - *applicationId* {String} - found in the *TypingDNA Verify* dashboard after creating an integration
    - *payload* {String}
- __TypingDNASendOTPResponse__:
    - *success* {number} - 1 or 0
    - *code* {number} - the *TypingDNA Verify* response code
    - *message* {String} - a description of the outcome of the operation
    - *status* {number} - the HTTP status code
    - *otp* {String?} - the OTP code sent to the user
- __TypingDNAValidateOTPResponse__:
    - *success* {number} - 1 or 0
    - *code* {number} - the *TypingDNA Verify* response code
    - *message* {String} - a description of the outcome of the operation
    - *status* {number} - the HTTP status code

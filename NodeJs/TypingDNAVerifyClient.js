/**
 * NodeJs implementation for the TypingDNA Verify Client.
 *
 * @version 1.1
 *
 * @copyright TypingDNA.com, SC TypingDNA SRL
 * @license http://www.apache.org/licenses/LICENSE-2.0
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*******************************************************
 * Typical usage
 *
 * var TypingDNAVerifyClient = require('TypingDNAVerifyClient');
 * var typingDNAVerifyClient = new TypingDNAVerifyClient({ clientId: 'Your Client ID', applicationId: 'Your Application ID', secret: 'Your TypingDNAVerify Secret' });
 *
 * The default TypingDNA Verify Host is https://verify.typingdna.com
 *******************************************************/

const https = require('https');
const crypto = require('crypto');

class TypingDNAVerifyClient {
    constructor({ clientId, applicationId, secret }) {
        if (!clientId) {
            throw new Error('Missing client id');
        }

        if (!applicationId) {
            throw new Error('Missing application id');
        }

        if (!secret) {
            throw new Error('Missing secret');
        }

        this.applicationId = applicationId;
        this.clientId = clientId;
        this.secret = secret;

        this.VERSION = 1.1;
        this.host = 'verify.typingdna.com';
    }

    static encrypt(string, secret, salt) {
        const encryptionKey = crypto.pbkdf2Sync(secret, salt, 10000, 32, 'sha512');
        const iv = crypto.randomBytes(16).toString('hex');

        const cipher = crypto.createCipheriv('aes-256-cbc', encryptionKey, Buffer.from(iv, 'hex'));
        let encrypted = cipher.update(string, 'utf8', 'hex');
        encrypted += cipher.final('hex');

        return `${encrypted}${iv}`;
    }

    encryptPayload({ email, phoneNumber, countryCode, language, flow }) {
        const payload = JSON.stringify({ email, phoneNumber, countryCode, language, flow, ts: Date.now() });
        return TypingDNAVerifyClient.encrypt(payload, this.secret, this.applicationId);
    }

    /**
     * The main method used to obtain the required data-attributes for the TypingDNA Verify Popup
     *
     * @param {string} email - The email of the user that is verified
     * @param {string} phoneNumber - The phone number of the user that is verified
     * @param {string} countryCode - The country code of the user that is verified
     * @param {string} language - The language used for the TypingDNA Verify Popup
     * @param {string} flow - The flow which the user will complete in the verify process
     * @returns {{ clientId, applicationId, payload }} - An object with the required data-attributes
     */
    getDataAttributes({ email, phoneNumber, countryCode, language, flow }) {
        return {
            clientId: this.clientId,
            applicationId: this.applicationId,
            payload: this.encryptPayload({ email, phoneNumber, countryCode, language, flow }),
        };
    }

    /**
     * This method is used to manually trigger the sending of the OTP
     *
     * @param {string} email - The email of the user that will receive the OTP
     * @param {string} phoneNumber - The phone number of the user that will receive the OTP
     * @param {string} countryCode - The country code of the user that will receive the OTP
     * @returns {Promise} - The response from the TypingDNA Verify API
     */
    sendOTP({ email, phoneNumber, countryCode }) {
        return this.request('/otp/send', { email, phoneNumber, countryCode });
    }

    /**
     * This method is used to validate the OTP
     *
     * @param {string} email - The email of the user that will be used for matching with the OTP
     * @param {string} phoneNumber - The phone number of the user that will be used for matching with the OTP
     * @param {string} countryCode - The country code of the user that will be used for matching with the OTP
     * @param {string} code - The OTP that needs to be validated
     * @returns {Promise} - The response from the TypingDNA Verify API
     */
    validateOTP({ email, phoneNumber, countryCode }, code) {
        return this.request('/otp/validate', { email, phoneNumber, countryCode }, { code });
    }

    request(path, { email, phoneNumber, countryCode }, data = {}) {
        return new Promise((resolve, reject) => {
            const options = {
                host: this.host,
                path,
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
            };

            const body = {
                clientId: this.clientId,
                applicationId: this.applicationId,
                payload: this.encryptPayload({ email, phoneNumber, countryCode }),
                version: this.VERSION,
            };

            Object.keys(data).forEach(key => {
                body[key] = data[key];
            });

            const request = https.request(options, response => {
                let data = '';

                response.on('data', chunk => {
                    data += chunk;
                });

                response.on('error', error => {
                    return reject(new Error(error.message));
                });

                response.on('end', () => {
                    try {
                        return resolve(JSON.parse(data));
                    } catch (error) {
                        return reject(new Error('Error parsing response'));
                    }
                });
            });

            request.on('error', error => {
                return reject(new Error(error.message));
            });

            request.write(JSON.stringify(body));
            request.end();
        });
    }
}

module.exports = TypingDNAVerifyClient;

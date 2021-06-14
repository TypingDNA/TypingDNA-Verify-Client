<?php
class TypingDNAVerifyClient {
    private $clientId;
    private $applicationId;
    private $secret;

    const VERSION = 1.1;

    function __construct($clientId, $applicationId, $secret) {
        if (!$clientId) {
            throw new Exception('Missing client id');
        }

        if (!$applicationId) {
            throw new Exception('Missing application id');
        }

        if (!$secret) {
            throw new Exception('Missing secret');
        }

        $this->clientId = $clientId;
        $this->applicationId = $applicationId;
        $this->secret = $secret;
    }

    private static $host = 'https://verify.typingdna.com';

    private function encrypt($string, $secret, $salt) {
        $encryptionKey = hash_pbkdf2('sha512', $secret, $salt, 10000, 32, true);
        $iv = openssl_random_pseudo_bytes(16);
        $encrypted = openssl_encrypt($string, 'AES-256-CBC', $encryptionKey, OPENSSL_RAW_DATA, $iv);

        return bin2hex($encrypted) . bin2hex($iv);
    }

    private function encryptPayload($payload) {
        $payloadArray = array();
        $validPayloadKeys = array('email', 'phoneNumber', 'countryCode', 'language', 'flow');

        foreach ($validPayloadKeys as &$key) {
            if (isset($payload[$key])) {
                $payloadArray[$key] = $payload[$key];
            }
        }

        $payloadArray['ts'] = microtime();

        return TypingDNAVerifyClient::encrypt(json_encode($payloadArray), $this->secret, $this->applicationId);
    }

    public function getDataAttributes($payload) {
        return array(
            'clientId' => $this->clientId,
            'applicationId' => $this->applicationId,
            'payload' => $this->encryptPayload($payload),
        );
    }

    public function sendOTP($payload) {
        return $this->request('/otp/send', $payload);
    }

    public function validateOTP($payload, $code) {
        return $this->request('/otp/validate', $payload, array('code' => $code));
    }

    private function request($path, $payload, $data = array()) {
        $body = array_merge(array(
            'clientId' => $this->clientId,
            'applicationId' => $this->applicationId,
            'payload' => $this->encryptPayload($payload),
            'version' => self::VERSION
        ), $data);

        $curl = curl_init(TypingDNAVerifyClient::$host . $path);
        curl_setopt($curl, CURLOPT_HTTPHEADER, array('Accept: application/json', 'Content-Type: application/json'));
        curl_setopt($curl, CURLOPT_POST, true);
        curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($body));
        curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);
        $response = curl_exec($curl);
        curl_close($curl);

        return json_decode($response, true);
    }
}
?>

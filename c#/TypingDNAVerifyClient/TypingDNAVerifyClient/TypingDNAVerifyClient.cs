using System;
using System.Text;
using System.IO;
using System.Security.Cryptography;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Net.Http;
using System.Threading.Tasks;

namespace TypingDNA
{
    public class TypingDNAVerifyClient
    {
        private string applicationId;
        private string clientId;
        private string secret;
        private static readonly TypingDNAVerifyAPI api = TypingDNAVerifyAPI.GetInstance();

        public TypingDNAVerifyClient(string applicationId, string clientId, string secret)
        {
            this.applicationId = applicationId;
            this.clientId = clientId;
            this.secret = secret;
        }

        public TypingDNAVerifyDataAttributes GetDataAttributes(TypingDNAVerifyPayload payload)
        {
            return new TypingDNAVerifyDataAttributes(
                clientId,
                applicationId,
                EncryptPayload(payload)
            );
        }

        public async Task<TypingDNASendOTPResponse> SendOTP(TypingDNAVerifyPayload payload)
        {
            var requestBody = new TypingDNAVerifyRequestBody(clientId, applicationId, EncryptPayload(payload));

            string json;
            try
            {
                json = JsonSerializer.Serialize<TypingDNAVerifyRequestBody>(requestBody);
            }
            catch (Exception e)
            {
                throw new TypingDNAVerifyException("Failed to serialize data", e);
            }

            var response = await api.request("/otp/send", json);

            try
            {
                var options = new JsonSerializerOptions { };
                options.Converters.Add(new NumberToStringConverter());

                return JsonSerializer.Deserialize<TypingDNASendOTPResponse>(response, options);
            }
            catch (Exception e)
            {
                throw new TypingDNAVerifyException("Failed to deserialize response data", e);
            }
        }

        public async Task<TypingDNAValidateOTPResponse> ValidateOTP(TypingDNAVerifyPayload payload, String code)
        {
            var requestBody = new TypingDNAVerifyRequestBodyWithOTP(clientId, applicationId, EncryptPayload(payload), code);

            string json;
            try
            {
                json = JsonSerializer.Serialize<TypingDNAVerifyRequestBodyWithOTP>(requestBody);
            }
            catch (Exception e)
            {
                throw new TypingDNAVerifyException("Failed to serialize data", e);
            }

            var response = await api.request("/otp/validate", json);

            try
            {
                return JsonSerializer.Deserialize<TypingDNAValidateOTPResponse>(response);
            }
            catch (Exception e)
            {
                throw new TypingDNAVerifyException("Failed to deserialize response data", e);
            }
        }

        private string EncryptPayload(TypingDNAVerifyPayload payload)
        {
            var data = JsonSerializer.Serialize(payload);
            return TypingDNAEncryption.Encrypt(data, secret, applicationId);
        }
    }

    class TypingDNAEncryption
    {
        public static string Encrypt(string data, string secret, string salt)
        {
            try
            {
                var encryptionKey = GenerateSecretKey(secret, salt);

                using (Aes aes = Aes.Create())
                {
                    var encrypted = DoEncrypt(data, encryptionKey, aes.IV);

                    return String.Format("{0}{1}", BitConverter.ToString(encrypted).Replace("-", ""), BitConverter.ToString(aes.IV).Replace("-", ""));
                }
            }
            catch (Exception e)
            {
                throw new TypingDNAVerifyException("Failed to encrypt data", e);
            }
        }

        private static byte[] GenerateSecretKey(string secret, string salt)
        {
            return new Rfc2898DeriveBytes(
                Encoding.ASCII.GetBytes(secret),
                Encoding.ASCII.GetBytes(salt),
                10000,
                HashAlgorithmName.SHA512
            ).GetBytes(32);
        }

        private static byte[] DoEncrypt(string data, byte[] encryptionKey, byte[] iv)
        {
            if (data == null || data.Length <= 0)
                throw new ArgumentNullException("data");
            if (encryptionKey == null || encryptionKey.Length <= 0)
                throw new ArgumentNullException("encryptionKey");
            if (iv == null || iv.Length <= 0)
                throw new ArgumentNullException("iv");
            byte[] encrypted;

            using (Aes aesAlg = Aes.Create())
            {
                aesAlg.Key = encryptionKey;
                aesAlg.IV = iv;

                var encryptor = aesAlg.CreateEncryptor(aesAlg.Key, aesAlg.IV);

                using (var msEncrypt = new MemoryStream())
                {
                    using (var csEncrypt = new CryptoStream(msEncrypt, encryptor, CryptoStreamMode.Write))
                    {
                        using (var swEncrypt = new StreamWriter(csEncrypt))
                        {
                            swEncrypt.Write(data);
                        }
                        encrypted = msEncrypt.ToArray();
                    }
                }
            }

            return encrypted;
        }

    }

    class TypingDNAVerifyAPI
    {
        private static readonly HttpClient client = new HttpClient();
        private static TypingDNAVerifyAPI instance = null;
        private string api = "https://verify.typingdna.com";

        public string API
        {
            get => api;
            set => api = value;
        }

        private TypingDNAVerifyAPI()
        {
            client.Timeout = TimeSpan.FromMilliseconds(8000);
        }

        public static TypingDNAVerifyAPI GetInstance()
        {
            if (instance == null)
            {
                instance = new TypingDNAVerifyAPI();
            }

            return instance;
        }

        public async Task<string> request(string path, string json)
        {
            try
            {
                client.DefaultRequestHeaders.Accept.Add(new System.Net.Http.Headers.MediaTypeWithQualityHeaderValue("application/json"));

                var content = new StringContent(json, Encoding.UTF8, "application/json");
                var response = await client.PostAsync(String.Format("{0}{1}", api, path), content);
                var responseJson = await response.Content.ReadAsStringAsync();

                return responseJson;
            }
            catch (Exception e)
            {
                throw new TypingDNAVerifyException(String.Format("Request to {0}{1} failed", api, path), e);
            }
        }
    }

    public class TypingDNAVerifyDataAttributes
    {
        private string clientId;
        private string applicationId;
        private string payload;

        [JsonPropertyName("clientId")]
        public string ClientId => clientId;
        [JsonPropertyName("applicationId")]
        public string ApplicationId => applicationId;
        [JsonPropertyName("payload")]
        public string Payload => payload;

        public TypingDNAVerifyDataAttributes(string clientId, string applicationId, string payload)
        {
            this.clientId = clientId;
            this.applicationId = applicationId;
            this.payload = payload;
        }
    }

    class TypingDNAVerifyRequestBody : TypingDNAVerifyDataAttributes
    {
        private readonly float version = 1.1f;

        [JsonPropertyName("version")]
        public float Version
        {
            get => version;
        }

        public TypingDNAVerifyRequestBody(string clientId, string applicationId, string payload) : base(clientId, applicationId, payload)
        {
        }
    }

    class TypingDNAVerifyRequestBodyWithOTP : TypingDNAVerifyRequestBody
    {
        private string otp;

        [JsonPropertyName("code")]
        public string OTP
        {
            get => otp;
        }

        public TypingDNAVerifyRequestBodyWithOTP(string clientId, string applicationId, string payload, string otp) : base(clientId, applicationId, payload)
        {
            this.otp = otp;
        }
    }

    public class TypingDNAVerifyPayload
    {
        private string email;
        private string phoneNumber;
        private string language = "EN";
        private string mode = "standard";

        [JsonPropertyName("email")]
        public string Email => email;
        [JsonPropertyName("phoneNumber")]
        public string PhoneNumber => phoneNumber;
        [JsonPropertyName("language")]
        public string Language => language;
        [JsonPropertyName("mode")]
        public string Mode => mode;

        public TypingDNAVerifyPayload(string phoneNumber, string email)
        {
            this.phoneNumber = phoneNumber;
            this.email = email;
        }

        public TypingDNAVerifyPayload(string phoneNumber, string email, string language, string mode)
        {
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.language = language;
            this.mode = mode;
        }
    }

    public abstract class TypingDNADefaultResponse
    {
        private int success;
        private int code;
        private string message;
        private int status;

        public bool IsSuccess
        {
            get => success == 1;
            set
            {
                success = value ? 1 : 0;
            }
        }

        [JsonPropertyName("success")]
        public int Success
        {
            get => success;
            set
            {
                success = value;
            }
        }

        [JsonPropertyName("code")]
        public int Code
        {
            get => code;
            set => code = value;
        }

        [JsonPropertyName("message")]
        public string Message
        {
            get => message;
            set => message = value;
        }

        [JsonPropertyName("status")]
        public int Status
        {
            get => status;
            set => status = value;
        }

        public TypingDNADefaultResponse()
        {

        }

        public TypingDNADefaultResponse(int success, int code, string message, int status)
        {
            this.success = success;
            this.code = code;
            this.message = message;
            this.status = status;
        }
    }

    public class TypingDNASendOTPResponse : TypingDNADefaultResponse
    {
        private string otp;

        [JsonPropertyName("otp")]
        public string OTP
        {
            get => otp;
            set => otp = value;
        }

        public TypingDNASendOTPResponse() : base()
        {

        }

        public TypingDNASendOTPResponse(int success, int code, string message, int status, string otp) : base(success, code, message, status)
        {
            this.otp = otp;
        }
    }

    public class TypingDNAValidateOTPResponse : TypingDNADefaultResponse
    {
        public TypingDNAValidateOTPResponse() : base()
        {

        }
    }

    class NumberToStringConverter : JsonConverter<object>
    {
        public override bool CanConvert(Type typeToConvert)
        {
            return typeof(string) == typeToConvert;
        }
        public override object Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
        {
            if (reader.TokenType == JsonTokenType.Number)
            {
                return reader.TryGetInt64(out long l) ?
                    l.ToString() :
                    reader.GetDouble().ToString();
            }

            if (reader.TokenType == JsonTokenType.String)
            {
                return reader.GetString();
            }

            using (JsonDocument document = JsonDocument.ParseValue(ref reader))
            {
                return document.RootElement.Clone().ToString();
            }
        }

        public override void Write(Utf8JsonWriter writer, object value, JsonSerializerOptions options)
        {
            writer.WriteStringValue(value.ToString());
        }
    }
}

public class TypingDNAVerifyException : Exception
{
    public TypingDNAVerifyException(string message)
        : base(message)
    {
    }

    public TypingDNAVerifyException(string message, Exception inner) : base(message, inner)
    {
    }
}

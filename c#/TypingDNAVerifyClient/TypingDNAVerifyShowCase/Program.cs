using System;
using TypingDNA;

namespace TypingDNAVerifyShowCase
{
    class Program
    {
        static void Main(string[] args)
        {
            string APPLICATION_ID = "";
            string CLIENT_ID = "";
            string SECRET = "";

            string USER_EMAIL = "";
            string USER_PHONE = "";
            string USER_LANG = "";

            TypingDNAVerifyClient client = new TypingDNAVerifyClient(APPLICATION_ID, CLIENT_ID, SECRET);

            TypingDNAVerifyPayload payload = new TypingDNAVerifyPayload(USER_PHONE, USER_EMAIL, USER_LANG, "standard");
            
            TypingDNASendOTPResponse response = client.SendOTP(payload).GetAwaiter().GetResult();
            Console.WriteLine(response.IsSuccess);
            Console.WriteLine(response.Code);
            Console.WriteLine(response.Message);
            Console.WriteLine(response.Status);
            Console.WriteLine(response.OTP);

            TypingDNAValidateOTPResponse response2 = client.ValidateOTP(payload, response.OTP).GetAwaiter().GetResult();
            Console.WriteLine(response2.IsSuccess);
            Console.WriteLine(response2.Code);
            Console.WriteLine(response2.Message);
            Console.WriteLine(response2.Status);
        }
    }
}

# Verify Documentation #

## About TypingDNA? ##

[TypingDNA](https://www.typingdna.com/) is a new kind of biometrics that recognizes users by their unique typing patterns, powering more affordable, user-friendly authentication and behavioral analysis solutions.

Our Verify and Authentication API solutions commonly replace traditional second factor authentication flows, enabling users to confirm their identity by typing -- without the additional UX friction introduced by more traditional methods.

## About Verify ##

Verify is a complete 2FA service designed to reduce reliance on traditional SMS and Email 2FA methods in the user authentication process. A Verify integration allows you to offer typing verification as a second factor in flows where traditional 2FA methods would otherwise have been required.

With Verify, end-users prove their identity by typing generated texts, only deferring to Root of Trust channels (SMS & Email) when strictly necessary. In most cases, when typing verification succeeds, codes are instead displayed on-screen or passed via a callback to the client application, depending on how the integration is configured.

The goal of Verify is to offer the best experience to end-users while reducing the costs typically associated with sending 2FA codes.

## Who is this Verify for? ##

Verify is an appropriate solution for application owners looking to improve the 2FA process for their users. Verify is a managed 2FA service that is designed for simple integration and an optimized UX -- end-user typing pattern collection, verification, and fallback logic is handled by TypingDNA, capturing data from users directly.

Compared to our RESTful Authentication API product, Verify is less customizable and is designed to replace a specific 2FA application flow. If you want to leverage typing pattern matching in a non-2FA context, or require advanced settings configuration and more flexible architecture/application flow support, consider the [Authentication API](https://www.typingdna.com/authentication-api.html).

## Getting started ##

To get started with TypingDNA Verify and get a full understanding of the integration step, check our [Verify API Documentation](https://verify.typingdna.com/docs/).

## Integrate the TypingDNA Verify Clients ##

This repository contains the clients that you can use to integrate TypingDNA Verify in the backend of your application. The currently supported languages are:

- [NodeJs client](/NodeJs/README.md)
- [Java client](/java/TypingDNAVerifyClient/README.md)

If you need support for other languages, [contact us](https://www.typingdna.com/contact.html) or check back later for updates.

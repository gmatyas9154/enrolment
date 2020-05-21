# Enrolment
Simple enrolment API

## Running the project
The project requires Java 11 to run.

Currently, the project should be cloned from github, and run using an IDE, or with the following
maven command (assuming the system has maven installed):

`mvn spring-boot:run`

## Usage
 The application exposes several endpoints, allowing an API client to 'enroll' new users, through the following steps:
 * make a POST request to the [Enrolment](docs/paths.md#enroll-a-new-client-to-the-system) , the request body should 
 be an [Enrolment](docs/definitions.md#enrolment) object, but only the [identity document](docs/definitions.md#identitydocument) should be 
 filled. If the object was correctly formatted, a new enrolment should be created with the INITIALIZED status.
 * after the enrolment is created, it can be retrieved using the [Get by ID](docs/paths.md#retrieve-an-enrolment) 
 or [Get all](docs/paths.md#retrieve-enrolments) endpoints
 * When the enrolment is in the INITIALIZED state, a full client check can be triggered using 
 the [Check](docs/paths.md#initiate-enrolment-check) endpoint. The check consists the of the following:
    * Identity document validation: it makes sure that the expiration date is after today
    * Credit score validation: calls an external service to retrieve the credit score of the user. 
    Currently the service is configured to hit a mock project on [Glitch.me](https://glitch.com/~en-rest-proj)
    * Checking for existing client: also calls an external service, which is also configured currently to hith
    the mock [Glitch.me](https://glitch.com/~en-rest-proj) project.
 * When the client check is done, a pdf document can be generated with the results using the 
 [Generate unsigned](docs/paths.md#download-enrolment-result) endpoint. This will produce a pdf to be signed, 
 and in case the client checks failed, it will list the reasons.
 * After signing the document, it can be scanned and uploaded back to system using the
  [Upload Document](docs/paths.md#upload-signed-document) endpoint, after which the document will be 
  available to be downloaded again at the [Download Signed Document](docs/paths.md#download-signed-document) endpoint

## Documentation
See the [Paths](docs/paths.md) and [Model Description](docs/definitions.md) documents 

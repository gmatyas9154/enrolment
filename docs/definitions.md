
<a name="definitions"></a>
## Definitions

<a name="object-describing-the-identity-document"></a>
### Object describing the identity document

|Name|Description|Schema|
|---|---|---|
|**address**  <br>*optional*||string|
|**cnp**  <br>*optional*||string|
|**countryCode**  <br>*optional*||string|
|**dateOfBirth**  <br>*optional*|**Example** : `"26.09.1985"`|string (date)|
|**documentId**  <br>*required*|Unique identifier of the document, like passport number, or CI serial number|string|
|**documentType**  <br>*required*|Type of ID document, like passport, or CI|enum (CI, PASSPORT)|
|**expirationDate**  <br>*required*|**Example** : `"26.09.1985"`|string (date)|
|**firstName**  <br>*required*||string|
|**id**  <br>*optional*||integer (int64)|
|**issuingAuthority**  <br>*optional*||string|
|**issuingDate**  <br>*optional*|**Example** : `"26.09.1985"`|string (date)|
|**lastName**  <br>*required*||string|
|**nationality**  <br>*optional*||string|
|**parentFirstName**  <br>*optional*||string|
|**passportType**  <br>*optional*||string|
|**placeOfBirth**  <br>*optional*||string|
|**sex**  <br>*optional*||string|


<a name="object-describing-the-result-of-the-enrolment-check"></a>
### Object describing the result of the enrolment check

|Name|Schema|
|---|---|
|**creditRisk**  <br>*optional*|enum (NO_RISK, MEDIUM_RISK, HIGH_RISK)|
|**creditScore**  <br>*optional*|integer (int32)|
|**existingClient**  <br>*optional*|boolean|
|**id**  <br>*optional*|integer (int64)|
|**validIdDocument**  <br>*optional*|boolean|


<a name="object-representing-the-enrolment"></a>
### Object representing the enrolment

|Name|Description|Schema|
|---|---|---|
|**checkResult**  <br>*optional*|Identity Check Result|[Object describing the result of the enrolment check](#object-describing-the-result-of-the-enrolment-check)|
|**document**  <br>*optional*|Object representing the signed document|[Object that represents an uploaded document](#object-that-represents-an-uploaded-document)|
|**id**  <br>*optional*|Unique Identifier|integer (int64)|
|**identityDocument**  <br>*required*|Identity document|[Object describing the identity document](#object-describing-the-identity-document)|
|**status**  <br>*optional*|Status of enrolment|enum (INITIALIZED, VERIFIED, SIGNED)|
|**unsignedPdfUrl**  <br>*optional*|Url of unsigned PDF|string|


<a name="object-that-represents-an-uploaded-document"></a>
### Object that represents an uploaded document

|Name|Description|Schema|
|---|---|---|
|**contentUrl**  <br>*optional*|Download url|string|
|**externalId**  <br>*optional*||string|
|**id**  <br>*optional*||integer (int64)|
|**storeType**  <br>*optional*||string|
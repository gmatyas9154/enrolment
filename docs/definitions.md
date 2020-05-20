
<a name="definitions"></a>
## Definitions

<a name="checkresult"></a>
### CheckResult
Object describing the result of the enrolment check


|Name|Schema|
|---|---|
|**creditRisk**  <br>*optional*|enum (NO_RISK, MEDIUM_RISK, HIGH_RISK)|
|**creditScore**  <br>*optional*|integer (int32)|
|**existingClient**  <br>*optional*|boolean|
|**id**  <br>*optional*|integer (int64)|
|**validIdDocument**  <br>*optional*|boolean|


<a name="document"></a>
### Document
Object that represents an uploaded document


|Name|Description|Schema|
|---|---|---|
|**contentUrl**  <br>*optional*|Download url|string|
|**externalId**  <br>*optional*||string|
|**id**  <br>*optional*||integer (int64)|
|**storeType**  <br>*optional*||string|


<a name="enrolment"></a>
### Enrolment
Object representing the enrolment


|Name|Description|Schema|
|---|---|---|
|**checkResult**  <br>*optional*|Identity Check Result|[CheckResult](#checkresult)|
|**document**  <br>*optional*|Object representing the signed document|[Document](#document)|
|**id**  <br>*optional*|Unique Identifier|integer (int64)|
|**identityDocument**  <br>*required*|Identity document|[IdentityDocument](#identitydocument)|
|**status**  <br>*optional*|Status of enrolment|enum (INITIALIZED, VERIFIED, SIGNED)|
|**unsignedPdfUrl**  <br>*optional*|Url of unsigned PDF|string|


<a name="identitydocument"></a>
### IdentityDocument
Object describing the identity document


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


<a name="paths"></a>
## Paths

<a name="enrollclientusingpost"></a>
### Enroll a new client to the system
```
POST /api/enrolment
```


#### Description
Creates a new enrolment, only identity document should be specified


#### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Body**|**enrolmentDTO**  <br>*required*|enrolmentDTO|[Enrolment](#object-representing-the-enrolment)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**201**|Enrolment successfully created|[Enrolment](#object-representing-the-enrolment)|
|**400**|Submitted data failed validation|No Content|
|**404**|Not Found|No Content|
|**500**|Unexpected error|No Content|


#### Consumes

* `application/json`


#### Produces

* `application/json`


#### Tags


<a name="fetchallenrolmentsusingget"></a>
### Retrieve enrolments
```
GET /api/enrolment
```


#### Description
Retrieves all the enrolments in the system


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Enrolments retrieved|< [Enrolment](#object-representing-the-enrolment) > array|
|**404**|No enrolments found|No Content|
|**500**|Unexpected error|No Content|


#### Produces

* `application/json`


#### Tags

* Enrolment Controller


<a name="fetchenrolmentusingget"></a>
### Retrieve an enrolment
```
GET /api/enrolment/{enrolmentId}
```


#### Description
Retrieves a specific enrolment, identified by the path parameter


#### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**enrolmentId**  <br>*required*|enrolmentId|integer (int64)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Enrolments retrieved|[Enrolment](#object-representing-the-enrolment)|
|**404**|Enrolment with specified ID not found|No Content|
|**500**|Unexpected error|No Content|


#### Produces

* `application/json`


#### Tags

* Enrolment Controller


<a name="clientcheckusingpost"></a>
### Initiate enrolment check
```
POST /api/enrolment/{enrolmentId}/check
```


#### Description
Initiate the checking of the enrolment which verifies ID document validity, credit score, and whether or not the person enrolling is already a client


#### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**enrolmentId**  <br>*required*|enrolmentId|integer (int64)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Enrolment check successfully completed|[CheckResult](#object-describing-the-result-of-the-enrolment-check)|
|**404**|Enrolment with specified ID not found|No Content|
|**500**|Unexpected error|No Content|


#### Consumes

* `application/json`


#### Produces

* `application/json`


#### Tags

* Enrolment Controller


<a name="uploaddocumentusingpost"></a>
### Upload Signed Document
```
POST /api/enrolment/{enrolmentId}/document
```


#### Description
Upload the signed enrolment or denial document


#### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**enrolmentId**  <br>*required*|enrolmentId|integer (int64)|
|**FormData**|**file**  <br>*required*|file|file|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**201**|Document is uploaded|[Document](#object-that-represents-an-uploaded-document)|
|**404**|Enrolment with specified ID not found|No Content|
|**500**|Unexpected error|No Content|


#### Consumes

* `application/pdf`
* `multipart/form-data`


#### Produces

* `application/json`


#### Tags

* Enrolment Controller


<a name="downloaddocumentusingget"></a>
### Download Signed Document
```
GET /api/enrolment/{enrolmentId}/document
```


#### Description
Download the signed copy of the enrolment or denial document


#### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**Enrolment Id**  <br>*required*|Enrolment unique identifier|string|
|**Path**|**enrolmentId**  <br>*required*|enrolmentId|integer (int64)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|File is downloaded|binary|
|**404**|Enrolment with specified ID not found|No Content|
|**500**|Unexpected error|No Content|


#### Produces

* `\*/*`
* `application/pdf`


#### Tags

* Enrolment Controller


<a name="downloadunsignedusingget"></a>
### Download Enrolment Result
```
GET /api/enrolment/{enrolmentId}/file/unsigned
```


#### Description
Download either the enrolment document or the denial document, specifying the reason(s) for denial. In either case the document can be printed, should be signed and re-uploaded to the system.


#### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**enrolmentId**  <br>*required*|enrolmentId|integer (int64)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|File is downloaded|binary|
|**404**|Enrolment with specified ID not found|No Content|
|**500**|Unexpected error|No Content|


#### Produces

* `\*/*`
* `application/pdf`


#### Tags

* Enrolment Controller




# ASE Dashboard Api Documentation


<a name="overview"></a>
## Overview
ASE Dashboard Api Documentation


### Version information
*Version* : 1.0


### Contact information
*Contact* : Siqi Zhuo  
*Contact Email* : siqi0903@gmail.com


### License information
*License* : Apache License Version 2.0  
*License URL* : https://www.apache.org/licenses/LICENSE-2.0  
*Terms of service* : Terms of service


### URI scheme
*Host* : localhost:8090  
*BasePath* : /


### Tags

* access-controller : Dashboard access permission management Operations
* bcp-controller : Operations pertaining to bcp ticket
* code-quality-controller : Operations pertaining to code quality data
* dashboard-controller : Operations pertaining to dashboard management
* data-synchronization-controller : Operations pertaining to synchronizing data from all related servers.
* functional-quality-controller : Operations pertaining to functional quality data
* jenkins-job-status-controller : Operations pertaining to jenkins job status data
* ping-controller : Operations pertaining to pinging servers with credentials.
* rsa-controller : Operations pertaining to RSA
* user-controller : Operations pertaining to user management.
* user-log-controller : Operations pertaining to manage log for user.




<a name="paths"></a>
## Resources

<a name="access-controller_resource"></a>
### Access-controller
Dashboard access permission management Operations


<a name="saveviewerusingpost"></a>
#### Add viewers to this dashboard.
```
POST /api/access/viewer/{dashboardId}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**dashboardId**  <br>*required*|dashboardId|integer (int64)|
|**Body**|**viewerList**  <br>*required*|viewerList|< [DashboardViewer](#dashboardviewer) > array|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[BasicResponse](#basicresponse)|
|**201**|Created|No Content|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="getviewerbydashboardidusingget"></a>
#### Return a list of viewers of this dashboard.
```
GET /api/access/viewer/{dashboardId}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**dashboardId**  <br>*required*|dashboardId|integer (int64)|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|< [DashboardViewer](#dashboardviewer) > array|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="checkviewaccessusingget"></a>
#### Check if this user has permission to access this dashboard.
```
GET /api/access/{dashboardId}/{userId}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**dashboardId**  <br>*required*|dashboardId|integer (int64)|
|**Path**|**userId**  <br>*required*|userId|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[BasicResponse](#basicresponse)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="bcp-controller_resource"></a>
### Bcp-controller
Operations pertaining to bcp ticket


<a name="getprojectmemberusingget"></a>
#### Return a list of project members whose tickets are counted in the total ticket number in this dashboard.
```
GET /api/bcp/projectMember/{dashboardId}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**dashboardId**  <br>*required*|dashboardId|integer (int64)|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|< string > array|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="getbcpticketusingget"></a>
#### Return a list of ticket data grouped by priority.
```
GET /api/ticket/{dashboardId}/{ticketType}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**dashboardId**  <br>*required*|dashboardId|integer (int64)|
|**Path**|**ticketType**  <br>*required*|ticketType|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|< [TicketResponse](#ticketresponse) > array|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="code-quality-controller_resource"></a>
### Code-quality-controller
Operations pertaining to code quality data


<a name="getcodedebtusingget"></a>
#### Return code debt data of this dashboard in this type.
```
GET /api/codeQuality/codeDebt/{dashboardId}/{type}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**dashboardId**  <br>*required*|dashboardId|integer (int64)|
|**Path**|**type**  <br>*required*|type|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|string|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="gettechissueusingget"></a>
#### Return techIssue data of this dashboard in this type.
```
GET /api/codeQuality/techIssue/{dashboardId}/{type}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**dashboardId**  <br>*required*|dashboardId|integer (int64)|
|**Path**|**type**  <br>*required*|type|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|string|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="dashboard-controller_resource"></a>
### Dashboard-controller
Operations pertaining to dashboard management


<a name="getactivedashboardbyowneridusingget"></a>
#### Return the active dashboard of this owner.
```
GET /api/dashboard/active/{ownerId}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**ownerId**  <br>*required*|ownerId|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|string|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="deletedashboardconfigusingdelete"></a>
#### deleteDashboardConfig
```
DELETE /api/dashboard/config
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Body**|**params**  <br>*required*|params|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|No Content|
|**204**|No Content|No Content|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|


##### Consumes

* `application/json`


##### Produces

* `*/*`


<a name="postdashboardconfigusingpost"></a>
#### create or update dashboard config, id 0 to create, other for update.
```
POST /api/dashboard/config/{id}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**id**  <br>*required*|id|integer (int64)|
|**Body**|**jsonBodyStr**  <br>*required*|jsonBodyStr|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[BasicResponse](#basicresponse)|
|**201**|Created|No Content|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `*/*`


<a name="getdashboardconfigusingget"></a>
#### Return dashboard config by id.
```
GET /api/dashboard/config/{id}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**id**  <br>*required*|id|integer (int64)|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|string|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="getdashboardbyidusingget"></a>
#### Return a dashboard by dashboard id.
```
GET /api/dashboard/{dashboardId}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**dashboardId**  <br>*required*|dashboardId|integer (int64)|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|string|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="updatedashboardactivestatususingpost"></a>
#### Update the active status of dashboards of an owner.
```
POST /api/dashboard/{id}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**id**  <br>*required*|id|integer (int64)|
|**Body**|**param**  <br>*required*|param|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[BasicResponse](#basicresponse)|
|**201**|Created|No Content|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `*/*`


<a name="deletedashboardbyidusingdelete"></a>
#### Delete a dashboard logically.
```
DELETE /api/dashboard/{id}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**id**  <br>*required*|id|integer (int64)|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[BasicResponse](#basicresponse)|
|**204**|No Content|No Content|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|


##### Consumes

* `application/json`


##### Produces

* `*/*`


<a name="getdashboardbyowneridusingget"></a>
#### Return list of dashboards of this owner.
```
GET /api/dashboardList/{ownerId}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**ownerId**  <br>*required*|ownerId|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|string|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="data-synchronization-controller_resource"></a>
### Data-synchronization-controller
Operations pertaining to synchronizing data from all related servers.


<a name="triggerdatajobusingget"></a>
#### Trigger a synchronization job for a dashboard.
```
GET /api/job/{dashboardId}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**dashboardId**  <br>*required*|dashboardId|integer (int64)|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[BasicResponse](#basicresponse)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="functional-quality-controller_resource"></a>
### Functional-quality-controller
Operations pertaining to functional quality data


<a name="getapitestdatausingget"></a>
#### Return apiTest data of this dashboard in this type.
```
GET /api/functionalQuality/apiTest/{dashboardId}/{level}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**dashboardId**  <br>*required*|dashboardId|integer (int64)|
|**Path**|**level**  <br>*required*|level|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|string|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="getutdatausingget"></a>
#### Return UT data of this dashboard in this type.
```
GET /api/functionalQuality/{utType}/{dashboardId}/{level}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**dashboardId**  <br>*required*|dashboardId|integer (int64)|
|**Path**|**level**  <br>*required*|level|string|
|**Path**|**utType**  <br>*required*|utType|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|string|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="jenkins-job-status-controller_resource"></a>
### Jenkins-job-status-controller
Operations pertaining to jenkins job status data


<a name="getjenkinsjobstatususingget"></a>
#### Return jenkins job status data of this dashboard in this level
```
GET /api/jenkinsJobStatus/{dashboardId}/{level}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**dashboardId**  <br>*required*|dashboardId|integer (int64)|
|**Path**|**level**  <br>*required*|level|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|< [JenkinsStatus](#jenkinsstatus) > array|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="ping-controller_resource"></a>
### Ping-controller
Operations pertaining to pinging servers with credentials.


<a name="pingjenkinsserverusingpost"></a>
#### Return if the credential used to ping jenkins server is authorized.
```
POST /api/ping/jenkinsServer
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Body**|**jsonBodyStr**  <br>*required*|jsonBodyStr|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[BasicResponse](#basicresponse)|
|**201**|Created|No Content|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="pingsonarserverusingpost"></a>
#### Return if the credential used to ping sonar server is authorized.
```
POST /api/ping/sonarServer
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Body**|**jsonBodyStr**  <br>*required*|jsonBodyStr|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[BasicResponse](#basicresponse)|
|**201**|Created|No Content|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="rsa-controller_resource"></a>
### Rsa-controller
Operations pertaining to RSA


<a name="decryptusingpost"></a>
#### Return the decrypted text.
```
POST /api/rsa/decryption
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Body**|**param**  <br>*required*|param|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|string|
|**201**|Created|No Content|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="getpublickeyusingget"></a>
#### Return the public key used to encrypt the text.
```
GET /api/rsa/publicKey
```


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|string|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="user-controller_resource"></a>
### User-controller
Operations pertaining to user management.


<a name="checkuserusingpost"></a>
#### Return if the credential is authorized to login.
```
POST /api/login
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Body**|**user**  <br>*required*|user|[User](#user)|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[BasicResponse](#basicresponse)|
|**201**|Created|No Content|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="registerusingpost"></a>
#### Register a new user.
```
POST /api/user
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Body**|**user**  <br>*required*|user|[User](#user)|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[BasicResponse](#basicresponse)|
|**201**|Created|No Content|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="checkusernameusingget"></a>
#### Return if the sapId exists already.
```
GET /api/user/checkSapId/{sapId}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**sapId**  <br>*required*|sapId|string|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[BasicResponse](#basicresponse)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="logoutusingget"></a>
#### User logout, and remove user's session.
```
GET /api/user/logout
```


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[BasicResponse](#basicresponse)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="checkusersessionusingget"></a>
#### Return if the user has session already.
```
GET /api/user/session
```


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[BasicResponse](#basicresponse)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="getuserinfousingget"></a>
#### Return the info of this user.
```
GET /api/user/{id}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**id**  <br>*required*|id|integer (int64)|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[User](#user)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="deleteuserusingdelete"></a>
#### Delete a user.
```
DELETE /api/user/{id}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**id**  <br>*required*|id|integer (int64)|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[BasicResponse](#basicresponse)|
|**204**|No Content|No Content|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`


<a name="user-log-controller_resource"></a>
### User-log-controller
Operations pertaining to manage log for user.


<a name="getlogbydashboardidusingget"></a>
#### Return list of logs about data synchronization of this dashboard in latest version.
```
GET /api/log/{dashboardId}
```


##### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Path**|**dashboardId**  <br>*required*|dashboardId|integer (int64)|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|string|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


##### Consumes

* `application/json`


##### Produces

* `application/json`




<a name="definitions"></a>
## Definitions

<a name="basicresponse"></a>
### BasicResponse

|Name|Schema|
|---|---|
|**message**  <br>*optional*|string|
|**status**  <br>*optional*|string|


<a name="dashboardviewer"></a>
### DashboardViewer

|Name|Schema|
|---|---|
|**dashboardId**  <br>*optional*|integer (int64)|
|**id**  <br>*optional*|integer (int64)|
|**viewerId**  <br>*optional*|string|


<a name="jenkinsstatus"></a>
### JenkinsStatus

|Name|Schema|
|---|---|
|**color**  <br>*optional*|string|
|**isLatest**  <br>*optional*|boolean|
|**isMain**  <br>*optional*|boolean|
|**latest**  <br>*optional*|boolean|
|**main**  <br>*optional*|boolean|
|**moduleName**  <br>*optional*|string|
|**saveAt**  <br>*optional*|string|
|**url**  <br>*optional*|string|


<a name="ticketresponse"></a>
### TicketResponse

|Name|Schema|
|---|---|
|**count**  <br>*optional*|integer (int32)|
|**isLatest**  <br>*optional*|boolean|
|**latest**  <br>*optional*|boolean|
|**priority**  <br>*optional*|string|
|**project**  <br>*optional*|string|
|**saveAt**  <br>*optional*|string|


<a name="timestamp"></a>
### Timestamp

|Name|Schema|
|---|---|
|**date**  <br>*optional*|integer (int32)|
|**day**  <br>*optional*|integer (int32)|
|**hours**  <br>*optional*|integer (int32)|
|**minutes**  <br>*optional*|integer (int32)|
|**month**  <br>*optional*|integer (int32)|
|**nanos**  <br>*optional*|integer (int32)|
|**seconds**  <br>*optional*|integer (int32)|
|**time**  <br>*optional*|integer (int64)|
|**timezoneOffset**  <br>*optional*|integer (int32)|
|**year**  <br>*optional*|integer (int32)|


<a name="user"></a>
### User

|Name|Schema|
|---|---|
|**createdTime**  <br>*optional*|[Timestamp](#timestamp)|
|**email**  <br>*optional*|string|
|**id**  <br>*optional*|integer (int64)|
|**password**  <br>*optional*|string|
|**sapId**  <br>*optional*|string|
|**userName**  <br>*optional*|string|






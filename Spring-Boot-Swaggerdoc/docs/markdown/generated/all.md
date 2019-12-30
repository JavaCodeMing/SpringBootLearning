# 用户信息API文档


<a name="overview"></a>
## 概览
这里除了查看接口功能外，还提供了调试测试功能


### 版本信息
*版本* : 1.0


### 联系方式
*名字* : 倾尽天下  
*邮箱* : 1206291365@qq.com


### URI scheme
*域名* : 127.0.0.1:8080  
*基础路径* : /


### 标签

* UserApi : 用户基本信息操作API




<a name="paths"></a>
## 资源

<a name="userapi_resource"></a>
### UserApi
用户基本信息操作API


<a name="deleteuserusingpost"></a>
#### 删除用户
```
POST /user/deleteUser
```


##### 说明
id必传


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**id**  <br>*必填*|用户id|integer (int64)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|string|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `application/json`


##### HTTP请求示例

###### 请求 path
```
/user/deleteUser
```


###### 请求 body
```json
{ }
```


##### HTTP响应示例

###### 响应 200
```json
"string"
```


<a name="getallusingget"></a>
#### 获取所有用户
```
GET /user/getAllUser
```


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|< [User](#user) > array|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `application/json`


##### HTTP请求示例

###### 请求 path
```
/user/getAllUser
```


##### HTTP响应示例

###### 响应 200
```json
[ {
  "address" : "string",
  "age" : 0,
  "id" : 0,
  "name" : "string",
  "sex" : "string"
} ]
```


<a name="getoneusingget"></a>
#### 根据id获取用户
```
GET /user/getUserById
```


##### 说明
id必传


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Query**|**id**  <br>*必填*|用户id|integer (int64)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[User](#user)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `application/json`


##### HTTP请求示例

###### 请求 path
```
/user/getUserById?id=1
```


##### HTTP响应示例

###### 响应 200
```json
{
  "address" : "string",
  "age" : 0,
  "id" : 0,
  "name" : "string",
  "sex" : "string"
}
```


<a name="getuserbynameandsexusingpost"></a>
#### 根据name和sex获取用户
```
POST /user/getUserByNameAndSex
```


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Query**|**userName**  <br>*必填*|用户名|string|
|**Query**|**userSex**  <br>*必填*|用户性别|string|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[User](#user)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `application/json`


##### HTTP请求示例

###### 请求 path
```
/user/getUserByNameAndSex?userName=关羽&userSex=男
```


##### HTTP响应示例

###### 响应 200
```json
{
  "address" : "string",
  "age" : 0,
  "id" : 0,
  "name" : "string",
  "sex" : "string"
}
```


<a name="insertuserusingpost"></a>
#### 新增用户
```
POST /user/insertUser
```


##### 说明
传json，数据放body


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**body**  <br>*必填*|用户对象json|string|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|string|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `application/json`


##### HTTP请求示例

###### 请求 path
```
/user/insertUser
```


###### 请求 body
```json
{ }
```


##### HTTP响应示例

###### 响应 200
```json
"string"
```


<a name="updateuserusingpost"></a>
#### 修改用户
```
POST /user/updateUser
```


##### 说明
传json，数据放body


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**body**  <br>*必填*|用户对象json|string|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|string|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `application/json`


##### HTTP请求示例

###### 请求 path
```
/user/updateUser
```


###### 请求 body
```json
{ }
```


##### HTTP响应示例

###### 响应 200
```json
"string"
```




<a name="definitions"></a>
## 定义

<a name="user"></a>
### User
用户信息对象


|名称|说明|类型|
|---|---|---|
|**address**  <br>*可选*|家庭住址  <br>**样例** : `"string"`|string|
|**age**  <br>*可选*|年龄  <br>**样例** : `0`|integer (int32)|
|**id**  <br>*可选*|用户id  <br>**样例** : `0`|integer (int32)|
|**name**  <br>*可选*|用户名  <br>**样例** : `"string"`|string|
|**sex**  <br>*可选*|性别  <br>**样例** : `"string"`|string|






# 멋쟁이사자처럼 종합 프로젝트
## MutsaSNS

## Endpoint
- `Get /api/v1/hello`<br>
http://ec2-43-200-170-38.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/#/hello-controller<br>

### 사용자
- 회원가입 `Post /api/v1/users/join`<br>
http://ec2-43-200-169-22.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/#/user-rest-controller/joinUsingPOST<br>
**Request Body**
```
{
  "password": "string",
  "userName": "string"
}
```
**Response Body**
```
{
  "result": {
    "userId": 0,
    "userName": "string"
  },
  "resultCode": "string"
}
```

- 로그인 `Post /api/v1/users/login`<br>
http://ec2-43-200-169-22.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/#/user-rest-controller/loginUsingPOST<br>
**Request Body**
```
{
  "password": "string",
  "userName": "string"
}
```
**Response Body**
```
{
  "result": {
    "jwt": "string"
  },
  "resultCode": "string"
}
```

### 포스트
- 포스트 작성 `Post /api/v1/posts`<br>
http://ec2-43-200-169-22.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/#/post-rest-controller/writeUsingPOST<br>
**Request Body**
```
{
  "body": "string",
  "title": "string"
}
```
**Response Body**
```
{
  "result": {
    "message": "string",
    "postId": 0
  },
  "resultCode": "string"
}
```

- 포스트 리스트 조회 `Get /api/v1/posts`<br>
http://ec2-43-200-169-22.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/#/post-rest-controller/getPostlistUsingGET<br>
**Response Body**
```
{
  "result": {
    "content": [
      {
        "body": "string",
        "createdAt": "2022-12-26T08:55:29.165Z",
        "id": 0,
        "lastModifiedAt": "2022-12-26T08:55:29.165Z",
        "title": "string",
        "userName": "string"
      }
    ],
    "empty": true,
    "first": true,
    "last": true,
    "number": 0,
    "numberOfElements": 0,
    "pageable": {
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 0,
      "paged": true,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "unpaged": true
    },
    "size": 0,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "totalElements": 0,
    "totalPages": 0
  },
  "resultCode": "string"
}
```

- 포스트 상세 조회 `Get /api/v1/posts/{id}`<br>
http://ec2-43-200-169-22.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/#/post-rest-controller/findPostByIdUsingGET<br>
**Response Body**
```
{
  "result": {
    "body": "string",
    "createdAt": "2022-12-26T08:56:55.992Z",
    "id": 0,
    "lastModifiedAt": "2022-12-26T08:56:55.992Z",
    "title": "string",
    "userName": "string"
  },
  "resultCode": "string"
}
```

- 포스트 수정 `Put /api/v1/posts/{id}`<br>
http://ec2-43-200-169-22.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/#/post-rest-controller/modifyUsingPUT<br>
**Request Body**
```
{
  "body": "string",
  "title": "string"
}
```
**Response Body**
```
{
  "result": {
    "message": "string",
    "postId": 0
  },
  "resultCode": "string"
}
```

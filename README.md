# MutsaSNS
> ## 미션 요구사항 분석 & 체크리스트
### 기능목록
**필수**
- [x] AWS EC2에 Docker로 배포
- [x] Swagger
- [x] Gitlab CI & Crontab CD
- [x] 회원가입
- [x] 로그인
- [x] 포스트 작성, 수정, 조회, 삭제
- [x] 포스트 리스트
- [x] 댓글
- [x] 좋아요
- [x] 마이피드
- [x] 알림
- [x] Swagger ApiOperation

**도전**
- [ ] 화면 UI 개발
- [x] ADMIN 회원이 일반 회원을 ADMIN으로 승격 시키는 기능
- [x] ADMIN 회원이 로그인 시 자신이 쓴 글이 아닌 글을 수정, 삭제 기능

## URL
http://ec2-43-200-169-22.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/

## ERD
![img.png](img.png)
## Endpoint
- `Get /api/v1/hello`<br>

### 사용자
- 회원가입 `Post /api/v1/users/join`<br>
  **Request Body**
  ```Json
  {
    "password": "string",
    "userName": "string"
  }
  ```
  **Response Body**
  ```Json
  {
    "result": {
      "userId": 0,
      "userName": "string"
    },
    "resultCode": "string"
  }
  ```

- 로그인 `Post /api/v1/users/login`<br>
  **Request Body**
  ```Json
  {
    "password": "string",
    "userName": "string"
  }
  ```
  **Response Body**
  ```Json
  {
    "result": {
      "jwt": "string"
    },
    "resultCode": "string"
  }
  ```
- 권한 부여 `Post /api/v1/users/{id}/role/change`<br>
  **Request Body**
  ```Json
  {
    "role" : "string"
  }
  ```
  **Response Body**
  ```Json
  {
    "result": {
      "role": "ADMIN" | "USER",
      "userId": 0,
      "userName": "string"
    },
    "resultCode": "string"
  }
  ```

### 포스트
- 포스트 작성 `Post /api/v1/posts`<br>
  **Request Body**
  ```Json
  {
    "body": "string",
    "title": "string"
  }
  ```
  **Response Body**
  ```Json
  {
    "result": {
      "message": "string",
      "postId": 0
    },
    "resultCode": "string"
  }
  ```

- 포스트 리스트 조회 `Get /api/v1/posts`<br>
  **Response Body**
  ```Json
  {
    "result": {
      "content": [
        {
          "body": "string",
          "createdAt": "yyyy/mm/dd hh:mm:ss",
          "id": 0,
          "lastModifiedAt": "yyyy/mm/dd hh:mm:ss",
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

- 포스트 상세 조회 `Get /api/v1/posts/{postId}`<br>
  **Response Body**
  ```Json
  {
    "result": {
      "body": "string",
      "createdAt": "yyyy/mm/dd hh:mm:ss",
      "id": 0,
      "lastModifiedAt": "yyyy/mm/dd hh:mm:ss",
      "title": "string",
      "userName": "string"
    },
    "resultCode": "string"
  }
  ```

- 포스트 수정 `Put /api/v1/posts/{id}`<br>
  **Request Body**
  ```Json
  {
    "body": "string",
    "title": "string"
  }
  ```
  **Response Body**
  ```Json
  {
    "result": {
      "message": "string",
      "postId": 0
    },
    "resultCode": "string"
  }
  ```
- 포스트 삭제 `Delete /api/v1/posts/{id}`<br>
  **Response Body**
  ```Json
  {
  "result": {
    "message": "string",
    "postId": 0
  },
  "resultCode": "string"
  }
  ```
### 댓글
- 댓글 조회 `GET /api/v1/posts/{postId}/comments`<br>
  **Response Body**
  ```Json
  {
  "result": {
    "content": [
      {
        "comment": "string",
        "createdAt": "yyyy/mm/dd hh:mm:ss",
        "id": 0,
        "lastModifiedAt": "yyyy/mm/dd hh:mm:ss",
        "postId": 0,
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
- 댓글 작성 `POST /api/v1/posts/{postsId}/comments/{id}`<br>
  **Request Body**
  ```Json
  { 
    "comment" : "string"
  }
  ```
  **Response Body**
  ```Json
  {
    "resultCode": "string",
    "result": {
      "id": 0,
      "comment": "string",
      "userName": "string",
      "postId": 0,
      "createdAt": "yyyy/mm/dd hh:mm:ss",
      "lastModifiedAt": "yyyy/mm/dd hh:mm:ss"
    }
  }
  ```
- 댓글 수정 `PUT /api/v1/posts/{postID}/comments`<br>
  **Request Body**
  ```Json
  { 
    "comment" : "string"
  }
  ```
  **Response Body**
  ```Json
  {
    "resultCode": "string",
    "result": {
      "id": 0,
      "comment": "string",
      "userName": "string",
      "postId": 0,
      "createdAt": "yyyy/mm/dd hh:mm:ss",
      "lastModifiedAt": "yyyy/mm/dd hh:mm:ss"
    }
  }
  ```
- 댓글 삭제 `DELETE /posts/{postsId}/comments/{id}`<br>
  **Response Body**
  ```Json
  {
    "resultCode": "string",
    "result":{
      "message": "string",
      "id": 0
    }
  }
  ```
### 좋아요
- 좋아요 누르기 `POST /api/v1/posts/{postId}/likes`<br>
  **Response Body**
  ```Json
  {
    "resultCode" : "string",
    "resut" : "string"
  }
  ```
- 좋아요 조회 `GET /api/v1/posts/{postId}/likes`<br>
  **Response Body**
  ```Json
  {
    "resultCode" : "string",
    "resut" : 0
  }
  ```
### 마이피드
- 조회 기능 `GET /api/v1/posts/my`<br>
  **Response Body**
  ```Json
  {
    "resultCode": "string",
    "result": {
      "content": [
        {
          "alarmType": "NEW_COMMENT_ON_POST",
          "createdAt": "yyyy/dd/mm hh:mm:ss",
          "fromUserId": 0,
          "id": 0,
          "targetId": 0,
          "text": "string"
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
    }
  }
  ```
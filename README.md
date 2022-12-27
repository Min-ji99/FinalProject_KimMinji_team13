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

**도전**
- [ ] 화면 UI 개발
- [x] ADMIN 회원이 일반 회원을 ADMIN으로 승격 시키는 기능
- [x] ADMIN 회원이 로그인 시 자신이 쓴 글이 아닌 글을 수정, 삭제 기능
- [ ] 댓글 기능
- [ ] 좋아요 기능
- [ ] ADMIN 회원이 로그인 시 자신이 쓴 댓글이 아닌 댓글 수정, 삭제 기능

> ## 1주차 미션 요약
[아쉬운 점]
- 테스트 코드 값을 하드코딩하지 않고 상수화 처리
- 중복되는 기능은 함수로 분리
- 테스트 코드를 제대로 구현하지 못함

[궁금한 점]
- 포스트 리스트를 조회할 때 정렬이 되었는지 확인하는 테스트 코드를 어떻게 작생하야하는가

## URL
http://ec2-43-200-169-22.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/

## Endpoint
- `Get /api/v1/hello`<br>

### 사용자
- 회원가입 `Post /api/v1/users/join`<br>
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
- 권한 부여 `Post /api/v1/users/{id}/role/change`<br>
  **Request Body**
  ```
  {
    "role" : "string"
  }
  ```
  **Response Body**
  ```
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

- 포스트 상세 조회 `Get /api/v1/posts/{postId}`<br>
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
- 포스트 삭제 `Delete /api/v1/posts/{id}`<br>
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

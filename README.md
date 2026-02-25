# meetkey-server repository

**🌍MeetKey: 다양한 국적의 친구들과 미션을 수행하며 배우는 언어 교환 채팅 플랫폼** </br></br>
나와 비슷한 관심사와 취향을 가진 글로벌 친구들을 매칭해주고, 미션을 수행하며 자연스럽게 언어 능력을 향상시킬 수 있는 커뮤니티입니다.

## 팀원 정보
<table>
  <tbody>
    <tr>
      <td align="center"><a href="https://github.com/rud15dns"><img src="https://github.com/rud15dns.png" width="100px;" alt=""/><br /><sub><b> 김채원 </b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/djeu1116"><img src="https://github.com/djeu1116.png" width="100px;" alt=""/><br /><sub><b> 김유현 </b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/happine2s"><img src="https://github.com/happine2s.png" width="100px;" alt=""/><br /><sub><b> 박소윤 </b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/leeseojun34"><img src="https://github.com/leeseojun34.png" width="100px;" alt=""/><br /><sub><b> 이서준 </b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/yoojinche"><img src="https://github.com/yoojinche.png" width="100px;" alt=""/><br /><sub><b> 제유진 </b></sub></a><br /></td>
    </tr>
    <tr>
    <td align="center">인증, 배포</td>
    <td align="center">역할 채우기</td>
    <td align="center">역할 채우기</td>
    <td align="center">역할 채우기</td>
    <td align="center">역할 채우기</td>
  </tr>
  </tbody>
</table>

## 기술 스택
- 프레임 워크: Spring Boot 3.5.4
- 언어: Java 17
- 데이터베이스: MySQL(RDS), Redis, AWS S3
- ORM: Spring Data JPA
- Real-time: WebSocket
- 인프라: AWS EC2, AWS RDS, Docker
- 외부 API: FCM, Coolsms, Kakao Map API


## 브랜치 전략
- main: 최종 브랜치
- develop: 상위 브랜치
- feat: 새로운 기능을 개발하는 브랜치
- refact: 코드 리팩토링 전용 브랜치
- fix: 버그를 수정하는 브랜치
  
각 브랜치에서 작업 후 develop 브랜치로 **PR(Pull Request)**을 생성하고 코드 리뷰를 거쳐 머지합니다.

## 서버 아키텍처 
<img width="703" height="495" alt="image" src="https://github.com/user-attachments/assets/72197562-9463-4122-9ab3-692c08a317f6" />


## 프로젝트 구조
```text
src
└── main
    ├── java
    │   └── com.meetkey.server
    │       └── domain
    │           ├── auth          
    │           ├── badge     
    │           ├── chat       
    │           ├── match      
    │           ├── member
    |           ├── mission
    |           ├── report
    │           └── notification              
    |       └── global         
    │           ├── annotation     
    │           ├── apiPayload       
    │           ├── common      
    │           ├── config
    |           ├── health
    |           ├── s3
    |           ├── security
    |           ├── sms
    |           ├── validator
    │           └── websocket

    └── resources
        ├── application.yml      
        └── static       
```

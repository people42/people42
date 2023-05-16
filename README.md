# 자율프로젝트 10팀 낭만을 찾아서

![](./assets/OG_image.png)

## 42

- (대충 위치기반 SNS라는 설명)

- (대충 소켓을 이용했다는 설명)

### 42 링크 : https://www.people42.com

## :clapper: 프로젝트 기간

1. 2023 04 10 (월) ~ 2023 05 19 (금)

1. 자율프로젝트 - 사이

## :scroll: 개요

### (대충 서비스 소개 한줄요약)

(대충 서비스 사용 권유하는듯한 광고용 소개멘트)

## :musical_score: 주요기능

### 1. (대충 위치기반 설명용 타이틀)

(대충 위치기반 자세한 설명)

### 2. (대충 소켓 설명용 타이틀)

(대충 소켓 자세한 설명)

## :hammer_and_wrench: 프로젝트에 사용된 기술

---

**Back-end : Spring Boot**

```Plain Text
- Springboot 2.7.10
- Spring Data JPA
- Spring Security
- Redis
- OAuth2
- MariaDB
- 적어주세요
```

**Front-end : 웹(React)**

```Plain Text
- Vite(React + Typescript)
- Recoil
- React-Router-Dom
- Axios
- Firebase Cloud Message(FCM)
- Web-Socket
- Styled-Components
- SEO(Open Graph, Helmet)
- Social Login(Google, Apple)
- Deep Link
```

**Front-end : Android (Kotlin)**

```Plain Text
- 적어주세요
```

**Front-end : IOS (Swift)**

```Plain Text
- 적어주세요
```

**CI/CD**

```Plain Text
- Git Webhook
- Jenkins
- Docker
- Docker Compoose
- Docker Registry
- Nginx
- Letsencrypt
- AWS EC2
- AWS S3
- Kubernetes
- Nginx-Ingress
- Kubernetes Dashboard
```

## :rocket: 협업방식

---

### **Matter Most**

- 작업시 소통을 위한 도구

- 짧은 코드나 참조 url 공유

### **Notion**

- Config 정리

- 회의록 저장

- 피드백 공유

- 일정 공유

### **Figma**

- 와이어 프레임 구성

- 프론트의 작업 지침서

### **Jira**

- 일정 관리

### **Git-lab**

- 개발 코드 형상 관리

- 메인, 작업 브랜치와 개인 브랜치 구분

<br><br>

## :triangular_flag_on_post: Project Info

---

### **깃 구조**

```
GIT LAB
  ├── fe
  │   └── forty-two
  ├── be
  │   └── fourtytwo
  ├── ios
  │   └── fourtytwo
  ├── android
  │   └── fourtytwo
  ├── docker-compose.yml
  ├── README.md
  ├── assets
  ├── excute
  ├── .gitconfig
  └── .githooks
```

```
GIT HUB
  ├── fe-deploy.yaml
  ├── fe-svc.yaml
  ├── be-deploy.yaml
  ├── be-svc.yaml
  └── ingress-42.yaml
```

### **브랜치전략**

![](./assets/branch.png)
<br><br>

### **파이프라인**

![](./assets/pipeline.png)
<br><br>

### Figma - 와이어프레임, 화면설계, 화면정의서

[Figma Link](https://www.figma.com/file/L3CelmgNIWFyPxj8kOer0Z/Untitled?type=design&node-id=234%3A4145&t=Xta8lvodXhF1Ulea-1)
<br><br>

### 기능명세서

[기능명세서]()

![]() <br>
<br>

### 아키텍쳐 설계

![](./assets/architecture.png)
<br><br>

### E-R Diagram

![](./assets/ERD.png)
<br><br>

### API 명세서

[API 명세서 LINK](https://stupendous-thyme-e20.notion.site/API-811a407d9fea4e1ab3b86bc83ee70c62)
<br><br>

## 서비스 화면

(대충 웹, Android, IOS가 gif찍어달라는 내용)

### Web

회원가입 

![web_signup](./assets/web/web_signup.gif)

홈 피드 

![web_message](./assets/web/web_message.gif)

- FCM Notification 
![web_notification](./assets/web/web_notification.gif)

- Web Socket 
![web_socket](./assets/web/web_socket.gif)

장소별 피드 

![web_feed](./assets/web/web_feed.gif)

사용자별 피드 

![web_user](./assets/web/web_user.gif)

테마 

![web_theme](./assets/web/web_theme.gif)

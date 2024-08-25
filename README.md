# :couple: 대학생을 위한 과팅어플 "과팅"

![ppt 첫번째 이미지]()

- 배포 URL :

<br>

## 프로젝트 소개

- 과팅은 대학생들을 주요 타겟층으로하여 소통과 만남을 중개해주는 어플입니다.
- 학교나 학과 기반의 네트워크를 형성할 수 있고, 학생들이 좀 더 신뢰할 수 있는 인맥을 구축하는데 도움을 줄 수 있습니다.
- 만나고 싶은 지역에서 인원 수에 맞게 검색이 가능하여 과팅을 진행할 수 있습니다.
- 채팅을 통해 만남이 성사되기 전에 상대방이 어떠한지 미리 알 수 있습니다.
- 알람, 결제 등 과팅을 도와줄 다양한 시스템이 존재합니다.

<br>

## 팀원 구성

<div align="center">

| **진상훈** | **김요한** | **김은지** | **문지은** | **박진우** | **이예진** |
| :------: |  :------: | :------: | :------: | :------: | :------: |
| [<img src="https://avatars.githubusercontent.com/u/156535365?v=4" height=150 width=150> <br/> @hun2zz](https://github.com/hun2zz) | [<img src="https://avatars.githubusercontent.com/u/86585468?v=4" height=150 width=150> <br/> @YoHan](https://github.com/yocong) | [<img src="https://avatars.githubusercontent.com/u/149495433?v=4" height=150 width=150> <br/> @silverji](https://github.com/eeungji) | [<img src="https://avatars.githubusercontent.com/u/173026656?v=4" height=150 width=150> <br/> @mizmizzz](https://github.com/mizmizzz) | [<img src="https://avatars.githubusercontent.com/u/160578029?v=4" height=150 width=150> <br/> @Jinwoo_Park](https://github.com/JinWooP98) | [<img src="https://avatars.githubusercontent.com/u/162073634?v=4" height=150 width=150> <br/> @jyaejin12](https://github.com/yaejin12) |

</div>

<br>

## 1. 개발 환경

- 개발 도구: Spring Boot
- 개발 주요 언어: JAVA / JPA  / JSX / JAVASCRIPT / AJAX / HTML / CSS / SCSS
- 라이브러리: REACT / REDIS / WEBSOCKET(socket.io)
- DB: Maria DB / MYSQL
- 배포: AWS / Docker / Github Action
- 툴 : GitHub / Notion / Discord / Google Calender / Figma / Postman / Blank Diagram
- API: KaKao 결제 API / 대학인증메일 (UnivCert) API
<br>

## 2. 브랜치 전략

- Git-flow 전략을 기반으로 main, main2 브랜치를 운용했습니다.
- main, main2 브랜치로 나누어 개발을 하였습니다.
    - **main** 브랜치는 배포 단계에서만 사용하는 브랜치입니다.
    - **main2** 브랜치는 개발 단계에서 git-flow의 main 역할을 하는 브랜치입니다.

<br>

## 3. 프로젝트 구조

```
---src
    +---main
    |   +---java
    |   |   \---com
    |   |       \---project
    |   |           \---api
    |   |               +---auth
    |   |               |   \---filter
    |   |               +---config
    |   |               +---exception
    |   |               +---handler
    |   |               \---metting
    |   |                   +---controller
    |   |                   +---dto
    |   |                   |   +---request
    |   |                   |   \---response
    |   |                   +---entity
    |   |                   +---repository
    |   |                   +---service
    |   |                   \---util
    |   +---resources
    |   |   +---mapper
    |   |   \---static
    |   |       \---assets
    |   |           +---css
    |   |           +---img
    |   |           \---js
    |   \---webapp
    |       \---WEB-INF
    |           \---views
    \---test
        \---java
            \---com
                \---project
                    \---api
                        +---auth
                        \---metting
                            +---controller
                            +---entity
                            +---repository
                            \---service
```
```
📦src
 ┣ 📂assets
 ┃ ┣ 📂css
 ┃ ┃ ┣ 📜common.css
 ┃ ┃ ┗ 📜reset.css
 ┃ ┣ 📂fonts
 ┃ ┃ ┗ 📂pretendard
 ┃ ┃ ┃ ┣ 📜Pretendard-Black.woff
 ┃ ┃ ┃ ┣ 📜Pretendard-Bold.woff
 ┃ ┃ ┃ ┣ 📜Pretendard-ExtraBold.woff
 ┃ ┃ ┃ ┣ 📜Pretendard-ExtraLight.woff
 ┃ ┃ ┃ ┣ 📜Pretendard-Light.woff
 ┃ ┃ ┃ ┣ 📜Pretendard-Medium.woff
 ┃ ┃ ┃ ┣ 📜Pretendard-Regular.woff
 ┃ ┃ ┃ ┣ 📜Pretendard-SemiBold.woff
 ┃ ┃ ┃ ┗ 📜Pretendard-Thin.woff
 ┃ ┣ 📂images
 ┃ ┃ ┣ 📂icons
 ┃ ┃ ┃ ┣ 📜c-camera.svg
 ┃ ┃ ┃ ┣ 📜c-chat-hamburger.svg
 ┃ ┃ ┃ ┣ 📜c-chat-send.svg
 ┃ ┃ ┃ ┣ 📜c-chat.svg
 ┃ ┃ ┃ ┣ 📜c-copy.svg
 ┃ ┃ ┃ ┣ 📜c-crown.svg
 ┃ ┃ ┃ ┣ 📜c-edit-board.svg
 ┃ ┃ ┃ ┣ 📜c-group-settings.svg
 ┃ ┃ ┃ ┣ 📜c-logo.svg
 ┃ ┃ ┃ ┣ 📜c-modal-close.svg
 ┃ ┃ ┃ ┣ 📜c-scr-arrow.svg
 ┃ ┃ ┃ ┣ 📜c-user-group-solid.svg
 ┃ ┃ ┃ ┣ 📜c-user.svg
 ┃ ┃ ┃ ┣ 📜c-view.svg
 ┃ ┃ ┃ ┣ 📜h-alarmActiveBtn.svg
 ┃ ┃ ┃ ┣ 📜h-alarmBtn.svg
 ┃ ┃ ┃ ┣ 📜h-board-active.svg
 ┃ ┃ ┃ ┣ 📜h-board.svg
 ┃ ┃ ┃ ┣ 📜h-chat-active.svg
 ┃ ┃ ┃ ┣ 📜h-chat.svg
 ┃ ┃ ┃ ┣ 📜h-chevron-left-solid.svg
 ┃ ┃ ┃ ┣ 📜h-group-active.svg
 ┃ ┃ ┃ ┣ 📜h-group.svg
 ┃ ┃ ┃ ┣ 📜h-home-active.svg
 ┃ ┃ ┃ ┣ 📜h-home.svg
 ┃ ┃ ┃ ┣ 📜h-messageBox.svg
 ┃ ┃ ┃ ┣ 📜h-mypage-active.svg
 ┃ ┃ ┃ ┣ 📜h-mypage.svg
 ┃ ┃ ┃ ┣ 📜h-plus.svg
 ┃ ┃ ┃ ┣ 📜icon-correct-D.svg
 ┃ ┃ ┃ ┣ 📜icon-correct.svg
 ┃ ┃ ┃ ┣ 📜icon-error.svg
 ┃ ┃ ┃ ┣ 📜m-check-active-B.svg
 ┃ ┃ ┃ ┣ 📜m-check-active.svg
 ┃ ┃ ┃ ┣ 📜m-check.svg
 ┃ ┃ ┃ ┣ 📜m-close-more.svg
 ┃ ┃ ┃ ┣ 📜m-more.svg
 ┃ ┃ ┃ ┗ 📜m-personnel.svg
 ┃ ┃ ┣ 📂login
 ┃ ┃ ┃ ┣ 📜defaultProfile.png
 ┃ ┃ ┃ ┗ 📜logo.svg
 ┃ ┃ ┣ 📂mypage
 ┃ ┃ ┃ ┣ 📜check.svg
 ┃ ┃ ┃ ┣ 📜coong.jpg
 ┃ ┃ ┃ ┣ 📜payment.svg
 ┃ ┃ ┃ ┗ 📜pen.svg
 ┃ ┃ ┗ 📜profile.jpg
 ┃ ┗ 📂js
 ┃ ┃ ┣ 📂test-chat
 ┃ ┃ ┃ ┣ 📜TestChat.js
 ┃ ┃ ┃ ┣ 📜TestChatFetch.js
 ┃ ┃ ┃ ┗ 📜TestChatWebSocket.js
 ┃ ┃ ┣ 📂webSocket
 ┃ ┃ ┃ ┗ 📜MainWebSocket.js
 ┃ ┃ ┗ 📜Verification.js
 ┣ 📂components
 ┃ ┣ 📂common
 ┃ ┃ ┣ 📂buttons
 ┃ ┃ ┃ ┣ 📂checkboxbutton
 ┃ ┃ ┃ ┃ ┣ 📜Checkbox.jsx
 ┃ ┃ ┃ ┃ ┣ 📜Checkbox.module.scss
 ┃ ┃ ┃ ┃ ┗ 📜CheckboxButtonGroup.jsx
 ┃ ┃ ┃ ┣ 📂matchingButton
 ┃ ┃ ┃ ┃ ┣ 📜MatchingButton.jsx
 ┃ ┃ ┃ ┃ ┗ 📜MatchingButton.module.scss
 ┃ ┃ ┃ ┣ 📂radiobutton
 ┃ ┃ ┃ ┃ ┣ 📜RadioButton.jsx
 ┃ ┃ ┃ ┃ ┣ 📜RadioButton.module.scss
 ┃ ┃ ┃ ┃ ┗ 📜RadioButtonChil.jsx
 ┃ ┃ ┃ ┣ 📜DefaultButton.js
 ┃ ┃ ┃ ┣ 📜MtButtons.jsx
 ┃ ┃ ┃ ┗ 📜MtButtons.module.scss
 ┃ ┃ ┣ 📂groupBoxs
 ┃ ┃ ┃ ┣ 📜GroupBox.jsx
 ┃ ┃ ┃ ┣ 📜GroupBox.module.scss
 ┃ ┃ ┃ ┗ 📜RequestBtns.jsx
 ┃ ┃ ┣ 📂inputs
 ┃ ┃ ┃ ┣ 📜DefaultInput.jsx
 ┃ ┃ ┃ ┗ 📜DefaultInput.module.scss
 ┃ ┃ ┣ 📂loading
 ┃ ┃ ┃ ┣ 📜Loading.jsx
 ┃ ┃ ┃ ┗ 📜Loading.module.scss
 ┃ ┃ ┣ 📂modal
 ┃ ┃ ┃ ┣ 📜InviteModal.jsx
 ┃ ┃ ┃ ┣ 📜InviteModal.module.scss
 ┃ ┃ ┃ ┣ 📜ModalLayout.jsx
 ┃ ┃ ┃ ┣ 📜ModalLayout.module.scss
 ┃ ┃ ┃ ┣ 📜PaymentChoiceModal.jsx
 ┃ ┃ ┃ ┗ 📜PaymentChoiceModal.module.scss
 ┃ ┃ ┣ 📂regionFilterBoxs
 ┃ ┃ ┃ ┣ 📜RegionFilterBox.jsx
 ┃ ┃ ┃ ┗ 📜RegionFilterBox.module.scss
 ┃ ┃ ┗ 📂scroll-section
 ┃ ┃ ┃ ┣ 📜ScrollSection.jsx
 ┃ ┃ ┃ ┗ 📜ScrollSection.module.scss
 ┃ ┣ 📂memberList
 ┃ ┃ ┣ 📜MemberList.jsx
 ┃ ┃ ┗ 📜MemberList.module.scss
 ┃ ┣ 📂myGroupSelectModal
 ┃ ┃ ┣ 📜MyGroupSelectModal.jsx
 ┃ ┃ ┗ 📜MyGroupSelectModal.module.scss
 ┃ ┗ 📂textarea
 ┃ ┃ ┣ 📜Textarea.jsx
 ┃ ┃ ┗ 📜Textarea.module.scss
 ┣ 📂config
 ┃ ┣ 📜auth.js
 ┃ ┣ 📜host-config.js
 ┃ ┗ 📜route-config.js
 ┣ 📂context
 ┃ ┣ 📜MainWebSocketContext.js
 ┃ ┣ 📜ModalContext.js
 ┃ ┗ 📜ModalProvider.js
 ┣ 📂hook
 ┃ ┣ 📜useFetchGet.js
 ┃ ┗ 📜useFetchRequest.js
 ┣ 📂layout
 ┃ ┣ 📂components
 ┃ ┃ ┣ 📜FloatingNavigation.jsx
 ┃ ┃ ┗ 📜MainNavigation.jsx
 ┃ ┣ 📜alarm.module.scss
 ┃ ┣ 📜alarmFetch.js
 ┃ ┣ 📜Header.js
 ┃ ┣ 📜Header.module.scss
 ┃ ┗ 📜RootLayout.js
 ┣ 📂pages
 ┃ ┣ 📂alarm
 ┃ ┃ ┣ 📂component
 ┃ ┃ ┃ ┗ 📜AlarmContent.jsx
 ┃ ┃ ┣ 📜AlarmPage.js
 ┃ ┃ ┗ 📜AlarmPage.module.scss
 ┃ ┣ 📂board
 ┃ ┃ ┣ 📂boardModify
 ┃ ┃ ┃ ┣ 📜BoardModify.js
 ┃ ┃ ┃ ┗ 📜BoardModify.module.scss
 ┃ ┃ ┣ 📂boardWrite
 ┃ ┃ ┃ ┣ 📜BoardWrite.js
 ┃ ┃ ┃ ┗ 📜BoardWrite.module.scss
 ┃ ┃ ┣ 📂board_detail
 ┃ ┃ ┃ ┣ 📜BoardDetail.js
 ┃ ┃ ┃ ┗ 📜BoardDetail.module.scss
 ┃ ┃ ┣ 📂components
 ┃ ┃ ┃ ┣ 📂modal
 ┃ ┃ ┃ ┃ ┣ 📜ConfirmDelBoard.jsx
 ┃ ┃ ┃ ┃ ┣ 📜CreateBoardModal.jsx
 ┃ ┃ ┃ ┃ ┗ 📜CreateBoardModal.module.scss
 ┃ ┃ ┃ ┣ 📜BoardBox.jsx
 ┃ ┃ ┃ ┣ 📜BoardList.jsx
 ┃ ┃ ┃ ┣ 📜DetailBody.jsx
 ┃ ┃ ┃ ┣ 📜DetailBottom.jsx
 ┃ ┃ ┃ ┣ 📜DetailHead.jsx
 ┃ ┃ ┃ ┣ 📜EmptyBoard.jsx
 ┃ ┃ ┃ ┣ 📜EmptyBoard.module.scss
 ┃ ┃ ┃ ┣ 📜MyBoardList.jsx
 ┃ ┃ ┃ ┣ 📜ReplyBox.jsx
 ┃ ┃ ┃ ┗ 📜TabBox.jsx
 ┃ ┃ ┣ 📜Board.js
 ┃ ┃ ┗ 📜Board.module.scss
 ┃ ┣ 📂chat
 ┃ ┃ ┣ 📂components
 ┃ ┃ ┃ ┣ 📂chatDelete_Modal
 ┃ ┃ ┃ ┃ ┗ 📜ChatDeleteModal.jsx
 ┃ ┃ ┃ ┣ 📂member_modal
 ┃ ┃ ┃ ┃ ┗ 📜ChatMembersModal.jsx
 ┃ ┃ ┃ ┣ 📜ChatBody.jsx
 ┃ ┃ ┃ ┣ 📜ChatHead.jsx
 ┃ ┃ ┃ ┣ 📜ChatInput.jsx
 ┃ ┃ ┃ ┣ 📜ChatMenu.jsx
 ┃ ┃ ┃ ┣ 📜MessageBox.jsx
 ┃ ┃ ┃ ┗ 📜MessageContent.jsx
 ┃ ┃ ┣ 📂js
 ┃ ┃ ┃ ┣ 📜ChatFetch.js
 ┃ ┃ ┃ ┗ 📜ChatWebSocket.js
 ┃ ┃ ┣ 📜Chat.js
 ┃ ┃ ┗ 📜Chat.module.scss
 ┃ ┣ 📂error
 ┃ ┃ ┣ 📜ErrorPage.js
 ┃ ┃ ┗ 📜ErrorPage.module.scss
 ┃ ┣ 📂group
 ┃ ┃ ┣ 📂components
 ┃ ┃ ┃ ┣ 📂modal
 ┃ ┃ ┃ ┃ ┣ 📜GroupCreateModal.jsx
 ┃ ┃ ┃ ┃ ┣ 📜GroupCreateModal.module.scss
 ┃ ┃ ┃ ┃ ┣ 📜GroupDeleteModal.jsx
 ┃ ┃ ┃ ┃ ┣ 📜GroupDeleteModal.module.scss
 ┃ ┃ ┃ ┃ ┣ 📜GroupDelModal.jsx
 ┃ ┃ ┃ ┃ ┣ 📜GroupDelModal.module.scss
 ┃ ┃ ┃ ┃ ┣ 📜GroupExileModal.jsx
 ┃ ┃ ┃ ┃ ┣ 📜GroupExileModal.module.scss
 ┃ ┃ ┃ ┃ ┣ 📜GroupInviteModal.jsx
 ┃ ┃ ┃ ┃ ┣ 📜GroupInviteModal.module.scss
 ┃ ┃ ┃ ┃ ┣ 📜GroupLeaveModal.jsx
 ┃ ┃ ┃ ┃ ┣ 📜GroupLeaveModal.module.scss
 ┃ ┃ ┃ ┃ ┣ 📜GroupSettingModal.jsx
 ┃ ┃ ┃ ┃ ┗ 📜RequestModal.jsx
 ┃ ┃ ┃ ┣ 📂skeleton
 ┃ ┃ ┃ ┃ ┣ 📜GroupViewBody.module.scss
 ┃ ┃ ┃ ┃ ┣ 📜GroupViewBodySkeleton.jsx
 ┃ ┃ ┃ ┃ ┣ 📜GroupViewHead.module.scss
 ┃ ┃ ┃ ┃ ┗ 📜GroupViewHeadSkeleton.jsx
 ┃ ┃ ┃ ┣ 📜GroupLeader.jsx
 ┃ ┃ ┃ ┣ 📜GroupViewBody.jsx
 ┃ ┃ ┃ ┣ 📜GroupViewBottom.jsx
 ┃ ┃ ┃ ┣ 📜GroupViewHead.jsx
 ┃ ┃ ┃ ┗ 📜Information.jsx
 ┃ ┃ ┣ 📜Group.js
 ┃ ┃ ┣ 📜Group.module.scss
 ┃ ┃ ┣ 📜GroupCreate.js
 ┃ ┃ ┗ 📜GroupCreate.module.scss
 ┃ ┣ 📂invite
 ┃ ┃ ┣ 📂components
 ┃ ┃ ┃ ┣ 📜JoinEndPage.jsx
 ┃ ┃ ┃ ┣ 📜JoinEndPage.module.scss
 ┃ ┃ ┃ ┗ 📜JoinGroupWithInvite.jsx
 ┃ ┃ ┗ 📜InvitePage.js
 ┃ ┣ 📂login
 ┃ ┃ ┣ 📂components
 ┃ ┃ ┃ ┣ 📂find_password
 ┃ ┃ ┃ ┃ ┣ 📜ConfirmIdentityModal.jsx
 ┃ ┃ ┃ ┃ ┣ 📜ConfirmIdentityModal.module.scss
 ┃ ┃ ┃ ┃ ┣ 📜NewPasswordModal.jsx
 ┃ ┃ ┃ ┃ ┗ 📜NewPasswordModal.module.scss
 ┃ ┃ ┃ ┣ 📂ProfileModal
 ┃ ┃ ┃ ┃ ┣ 📜ProfileMenuModal.jsx
 ┃ ┃ ┃ ┃ ┗ 📜ProfileMenuModal.module.scss
 ┃ ┃ ┃ ┣ 📜FirstLoginNickName.jsx
 ┃ ┃ ┃ ┣ 📜FirstLoginNickName.module.scss
 ┃ ┃ ┃ ┣ 📜FirstLoginProfile.jsx
 ┃ ┃ ┃ ┣ 📜FirstLoginProfile.module.scss
 ┃ ┃ ┃ ┣ 📜PasswordResetPage.jsx
 ┃ ┃ ┃ ┗ 📜PasswordResetPage.module.scss
 ┃ ┃ ┣ 📜FirstLoginPage.js
 ┃ ┃ ┣ 📜IntroPage.js
 ┃ ┃ ┣ 📜IntroPage.module.scss
 ┃ ┃ ┣ 📜LoginPage.js
 ┃ ┃ ┗ 📜LoginPage.module.scss
 ┃ ┣ 📂main
 ┃ ┃ ┣ 📂components
 ┃ ┃ ┃ ┣ 📜MainFilter.jsx
 ┃ ┃ ┃ ┣ 📜MainFilter.module.scss
 ┃ ┃ ┃ ┣ 📜MeetingList.jsx
 ┃ ┃ ┃ ┣ 📜MeetingList.module.scss
 ┃ ┃ ┃ ┣ 📜RegionFilter.jsx
 ┃ ┃ ┃ ┗ 📜RegionFilter.module.scss
 ┃ ┃ ┣ 📜EmptyGroups.js
 ┃ ┃ ┣ 📜Main.js
 ┃ ┃ ┗ 📜Main.module.scss
 ┃ ┣ 📂mypage
 ┃ ┃ ┣ 📂components
 ┃ ┃ ┃ ┣ 📂mypage_modal
 ┃ ┃ ┃ ┃ ┣ 📜MypageModal.jsx
 ┃ ┃ ┃ ┃ ┗ 📜MypageModal.module.scss
 ┃ ┃ ┃ ┣ 📜ActionSection.jsx
 ┃ ┃ ┃ ┣ 📜ActionSection.module.scss
 ┃ ┃ ┃ ┣ 📜ProfileImage.jsx
 ┃ ┃ ┃ ┣ 📜ProfileImage.module.scss
 ┃ ┃ ┃ ┣ 📜ProfileSection.jsx
 ┃ ┃ ┃ ┗ 📜ProfileSection.module.scss
 ┃ ┃ ┣ 📂modify_information
 ┃ ┃ ┃ ┣ 📂components
 ┃ ┃ ┃ ┃ ┣ 📂modal
 ┃ ┃ ┃ ┃ ┃ ┣ 📜PasswordUpdateModal.jsx
 ┃ ┃ ┃ ┃ ┃ ┣ 📜PasswordUpdateModal.module.scss
 ┃ ┃ ┃ ┃ ┃ ┣ 📜PhoneNumberUpdateModal.jsx
 ┃ ┃ ┃ ┃ ┃ ┗ 📜PhoneNumberUpdateModal.module.scss
 ┃ ┃ ┃ ┃ ┣ 📜ConfirmWithdraw.jsx
 ┃ ┃ ┃ ┃ ┣ 📜DisabledInfoInputs.jsx
 ┃ ┃ ┃ ┃ ┣ 📜DisabledInformations.jsx
 ┃ ┃ ┃ ┃ ┣ 📜EmailInput.jsx
 ┃ ┃ ┃ ┃ ┣ 📜EnableInputInformation.jsx
 ┃ ┃ ┃ ┃ ┣ 📜EnableInputInputs.jsx
 ┃ ┃ ┃ ┃ ┣ 📜PasswordInput.jsx
 ┃ ┃ ┃ ┃ ┗ 📜VerificationInput.jsx
 ┃ ┃ ┃ ┣ 📂withdraw
 ┃ ┃ ┃ ┃ ┣ 📜Withdraw.js
 ┃ ┃ ┃ ┃ ┗ 📜Withdraw.module.scss
 ┃ ┃ ┃ ┣ 📜CheckPass.js
 ┃ ┃ ┃ ┣ 📜CheckPass.module.scss
 ┃ ┃ ┃ ┣ 📜ModifyInformation.js
 ┃ ┃ ┃ ┗ 📜ModifyInformation.module.scss
 ┃ ┃ ┣ 📂mypage_chats
 ┃ ┃ ┃ ┣ 📂components
 ┃ ┃ ┃ ┃ ┗ 📜MyChatList.jsx
 ┃ ┃ ┃ ┣ 📜MyChatFetch.js
 ┃ ┃ ┃ ┣ 📜MyChats.js
 ┃ ┃ ┃ ┗ 📜MyChats.module.scss
 ┃ ┃ ┣ 📂mypage_groups
 ┃ ┃ ┃ ┣ 📜MyGroups.js
 ┃ ┃ ┃ ┗ 📜MyGroups.module.scss
 ┃ ┃ ┗ 📜MyPage.js
 ┃ ┣ 📂payment
 ┃ ┃ ┣ 📂components
 ┃ ┃ ┃ ┗ 📂modal
 ┃ ┃ ┃ ┃ ┣ 📜PaymentModal.jsx
 ┃ ┃ ┃ ┃ ┣ 📜PaymentModal.module.scss
 ┃ ┃ ┃ ┃ ┣ 📜SuccessModal.jsx
 ┃ ┃ ┃ ┃ ┗ 📜SuccessModal.module.scss
 ┃ ┃ ┣ 📜Payment.js
 ┃ ┃ ┣ 📜PaymentApproval.js
 ┃ ┃ ┗ 📜PaymentApproval.module.scss
 ┃ ┗ 📂sign_up
 ┃ ┃ ┣ 📂components
 ┃ ┃ ┃ ┣ 📂create_email
 ┃ ┃ ┃ ┃ ┣ 📜CreateEmail.jsx
 ┃ ┃ ┃ ┃ ┣ 📜EmailInput.jsx
 ┃ ┃ ┃ ┃ ┣ 📜EmailInput.module.scss
 ┃ ┃ ┃ ┃ ┣ 📜MajorInput.jsx
 ┃ ┃ ┃ ┃ ┗ 📜VerificationInput.jsx
 ┃ ┃ ┃ ┣ 📂create_informations
 ┃ ┃ ┃ ┃ ┗ 📜CreateInformation.jsx
 ┃ ┃ ┃ ┣ 📂create_password
 ┃ ┃ ┃ ┃ ┗ 📜CreatePassword.jsx
 ┃ ┃ ┃ ┣ 📜SignUpComplete.jsx
 ┃ ┃ ┃ ┗ 📜SignUpComponent.module.scss
 ┃ ┃ ┣ 📜SignUp.js
 ┃ ┃ ┗ 📜SignUp.module.scss
 ┣ 📂store
 ┃ ┣ 📜index.js
 ┃ ┣ 📜Login-slice.js
 ┃ ┗ 📜MainFilterLoading-slice.js
 ┣ 📜App.css
 ┣ 📜App.js
 ┣ 📜index.css
 ┗ 📜index.js
```


<br>

## 4. 역할 분담

### :smirk: 진상훈

- **기능**
    - 그룹 생성/삭제, 그룹 참여자 초대코드생성/수락/거절/추방, 회원 탈퇴, 이메일 인증, 배포

<br>
    
### :smiley: 김요한

- **기능**
    - 대학 이메일 인증, 회원가입, 로그인/자동로그인, 멤버십 조회/결제, 회원 정보 수정

<br>

### :flushed: 김은지

- **기능**
    - 회원 정보 조회/수정, 프로필 이미지 수정

<br>

### :wink: 문지은

- **기능**
    - 그룹 매칭 신청/수락/거절, 채팅 방 생성, 게시판 조회/생성/수정/삭제
    
<br>

### :stuck_out_tongue: 박진우

- **기능**
    - 채팅 전송, 채팅 방 조회/삭제
    
<br>

### :stuck_out_tongue_closed_eyes: 이예진

- **기능**
    - 그룹 조회/필터링, 게시판 덧글 생성/수정/삭제
    
<br>

### :muscle: 공통

- **UI**
    - 디자인
    
<br>

## 5. 개발 기간 및 작업 관리

### 개발 기간

- 전체 개발 기간 : 2024-07-15 ~ 2024-08-29

<br>

### 작업 관리

- Discord와 Notion을 사용하여 진행 상황을 공유했습니다.
- 매일 회의를 진행하여 작업 순서와 방향성에 대한 고민을 나누고 Notion에 회의 내용을 기록했습니다.

<br>

## 6. 페이지별 기능

<br>

### [회원가입]
- 회원가입 설명

| 회원가입 |
|----------|
|![회원가입 설명 이미지]()|

<br>

### [로그인]
- 로그인 설명

| 로그인 |
|----------|
|![로그인 설명 이미지]()|

<br>

### [메인페이지]
- 메인페이지 설명

| 메인 |
|----------|
|![메인 설명 이미지]()|

<br>

### [헤더]
- 헤더 설명

| 헤더 |
|----------|
|![헤더 설명 이미지]()|

<br>

### [그룹]
- 그룹 설명

| 그룹 |
|----------|
|![그룹 설명 이미지]()|

<br>

### [채팅]
- 채팅 설명

| 채팅 |
|----------|
|![채팅 설명 이미지]()|

<br>

### [익명게시판]
- 익명게시판 설명

| 익명게시판 |
|----------|
|![익명게시판 설명 이미지]()|

<br>

### [마이페이지]
- 마이페이지 설명

| 마이페이지 |
|----------|
|![마이페이지 설명 이미지]()|

<br>

### [멤버십]
- 멤버십 설명

| 멤버십 |
|----------|
|![멤버십 설명 이미지]()|

<br>

### [알림]
- 알림 설명

| 알림 |
|----------|
|![알림 설명 이미지]()|

<br>

## 7. 트러블 슈팅 (아래는 예시이므로 참고하면 좋을듯?)

- [탭메뉴 프로필 버튼 이슈](https://github.com/likelion-project-README/README/wiki/README-8.%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85_%ED%83%AD%EB%A9%94%EB%89%B4-%ED%94%84%EB%A1%9C%ED%95%84-%EB%B2%84%ED%8A%BC-%EC%9D%B4%EC%8A%88)

- [프로필 수정 이슈](https://github.com/likelion-project-README/README/wiki/README-8.%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85_%ED%94%84%EB%A1%9C%ED%95%84-%EC%88%98%EC%A0%95-%EC%9D%B4%EC%8A%88)

<br>

## 8. 향후 업데이트
    
<br>

## 9. 프로젝트 후기

### :smirk: 진상훈

다들 고생 많으셨습니다.

<br>

### :smiley: 김요한

배포시 오류 없이 돌아가는 것, 맡은 바 모든 기능을 구현하는 것, 팀원들과 감정 상하지 않고 별탈 없이 마무리 하는 것이 목표였는데 모두 이룰 수 있어서 정말 뿌듯했습니다.
팀원들이 모두 각자의 자리에서 최선을 다해주었기때문에 해낼 수 있었다고 생각합니다. 고생 많으셨습니다 ~!

<br>

### :flushed: 김은지

다들 고생 많으셨습니다.

<br>

### :wink: 문지은

다들 고생 많으셨습니다.
### :stuck_out_tongue: 박진우

다들 고생 많으셨습니다.

<br>

### :stuck_out_tongue_closed_eyes: 이예진

다들 고생 많으셨습니다.
<br>

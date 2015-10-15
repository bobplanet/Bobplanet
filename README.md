![스크린샷](https://cloud.githubusercontent.com/assets/7744953/10517292/18c0fe90-7397-11e5-9148-b9285b7c5321.png)

# 프로젝트 개요
- SK플래닛의 사내식당 메뉴를 읽어와 스마트폰으로 푸쉬해주는 프로젝트

## 현재 구현된 기능
- 사내식당 메뉴 조회: 구현완료 (UI는 아주 못생김)
- 푸쉬알림: 부분구현 (수동발송. 자동발송 스케줄링 구현 필요)

## 향후 구현할 기능
- 메뉴에 대한 평가
- 평점 데이터를 이용한 취향분석 및 Predictive Analytics

# 모듈 설명
- app: 안드로이드 클라이언트
	- 패키지명: 상용버전은 kr.bobplanet.android, 개발버전은 kr.bobplanet.android.debug
	- 개발버전의 경우 ActionBar에 표시되는 앱 이름, GA 트래킹코드 등이 다르므로 src/debug에 별도 코드를 둔다.
- backend: 서버 API
	- Google [AppEngine](https://cloud.google.com/appengine/) + [Cloud Endpoints](https://cloud.google.com/endpoints/)로 작성
	- AppEngine 기반인만큼 JSON serialization 기반 API로 구현됨
	- 데이터는 NoSQL DB인 [DataStore](https://cloud.google.com/datastore/)에 저장

# 이용 라이브러리
## App
- [EventBus](https://github.com/greenrobot/EventBus)
	- Android의 기본 이벤트 전파방식 대신 사용
	- 각 객체들간의 coupling을 약화시킬 수 있음
- [Volley](https://android.googlesource.com/platform/frameworks/volley)
	- 현재는 이미지 다운로더로만 사용함
	- JAR 다운로드 귀찮으니 비공식저장소(https://github.com/mcxiaoke/android-volley) 이용
- [ArrayPagerAdapter](https://github.com/commonsguy/cwac-pager)
	- 일간메뉴 화면의 좌우 swipe 기능을 위해 사용
- [SmoothProgressBar](https://github.com/castorflex/SmoothProgressBar)
	- 데이터로딩할 때 뜨는 progress bar를 좀더 예쁘게 만들기 위해 사용
	
## Backend
- [Objectify](https://github.com/objectify/objectify)
	- Google Datastore에 손쉽게 객체를 넣었다 뺐다 할 수 있도록 해주는 라이브러리

# 프로젝트 참여 안내
## 오픈소스 build
- app 개발버전은 본 소스만으로도 충분히 build할 수 있습니다.
- 다만 상용버전은 사이닝키를 지켜야하는 관계로, keystore를 별도로 받으셔야 해요.
- 서버 역시 API key나 클라이언트ID 등 민감한 정보는 제외했습니다.

## 팀 참여
- 어떤 기여도 대환영합니다! [프로젝트 멤버들](https://github.com/orgs/bobplanet/people)에게 요청해주세요 :)


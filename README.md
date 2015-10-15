# 프로젝트 개요
- SK플래닛의 사내식당 메뉴를 읽어와 스마트폰으로 푸쉬해주는 프로젝트

![스크린샷](https://cloud.githubusercontent.com/assets/7744953/10517292/18c0fe90-7397-11e5-9148-b9285b7c5321.png)

## 주요 기능
- 사내식당 메뉴 조회
- 푸쉬알림
- 메뉴평가

## 향후 구현할 기능
- 메뉴평가자의 평점 데이터를 이용한 취향분석 및 Predictive Analytics

# 모듈 설명
- app: 안드로이드 클라이언트
	- 패키지명: 상용버전은 kr.bobplanet.android, 개발버전은 kr.bobplanet.android.debug
	- 개발버전의 경우 ActionBar에 표시되는 앱 이름, GA 트래킹코드 등이 다르므로 src/debug에 별도 코드를 둔다.
- backend: 서버 API
	- 

# 이용 라이브러리
## App
- EventBus (https://github.com/greenrobot/EventBus)
	- Android의 기본 이벤트 전파방식 대신 사용
	- 각 객체들간의 coupling을 약화시킬 수 있음
- Volley (https://android.googlesource.com/platform/frameworks/volley)
	- 현재는 이미지 다운로더로만 사용함
	- JAR 다운로드 귀찮으니 비공식저장소(https://github.com/mcxiaoke/android-volley) 이용
- ArrayPagerAdapter (https://github.com/commonsguy/cwac-pager)
	- 일간메뉴 화면의 좌우 swipe 기능을 위해 사용
- SmoothProgressBar (https://github.com/castorflex/SmoothProgressBar)
	- 데이터로딩할 때 뜨는 progress bar를 좀더 예쁘게 만들기 위해 사용
	
## Backend
- Objectify (https://github.com/objectify/objectify)
	- Google Datastore에 손쉽게 객체를 넣었다 뺐다 할 수 있도록 해주는 라이브러리

# 서브모듈 개요
- P넷의 10층식당 메뉴 페이지를 scrape하여 Google Cloud의 DataStore로 업로드
	- 따라서, P넷 접근이 가능한 위치에서 실행되어야 함
	- 현재 작성된 프로그램은 VDI의 Windows 머신에서 실행
	- 매주 금요일 정도에는 다음주 메뉴가 올라오므로 매주 토요일 새벽에 batch 실행
- 로컬환경에서의 용이한 데이터분석을 위해 데이터사본은 local의 SQLite에도 저장
	- 단, 하나의 컬럼이 여러개의 entity를 가질 수 없는 RDB의 한계상 child 테이블로 분리

# git 리파지토리 위치
- Bobplanet 전체 리파지토리의 scraper 서브모듈로 관리함

# 오리지널 사운드 트랙을 아시나요(오사트아)
### OST 짧게 듣고 곡 제목과 아티스트명을 맞히는 퀴즈 게임 앱

<img src="https://user-images.githubusercontent.com/49242646/148938862-4d3eb6b1-3832-4d88-b704-d5240cc6984f.jpg" width="205" height="370"/> <img src="https://user-images.githubusercontent.com/49242646/148938889-33ec18a0-1cbd-4f5a-a902-4b9f7d5b695f.jpg" width="205" height="370"/>
<img src="https://user-images.githubusercontent.com/49242646/148938912-937ac36f-8ace-4d96-b567-3555a8a07fb9.jpg" width="205" height="370"/>

<img src="https://user-images.githubusercontent.com/49242646/148938937-3fb03a6c-2b7f-4dcd-9053-04720f927291.jpg" width="205" height="370"/> <img src="https://user-images.githubusercontent.com/49242646/148938944-9712ad47-0f3e-4123-a284-8f6c13d4b242.jpg" width="205" height="370"/>

## A. 개발 팀원  
- 김승찬
- 최윤정

## B. 개발 환경
- OS: Android(minSdk: 21, targetSdk: 31)
- Language: Kotlin, Python
- Back-end: Django
- IDE: Android Studio, VS Code

- Target Device: Galaxy S7

## C. 애플리케이션 소개
  +  ### 카카오, 구글 소셜 로그인
      + 카카오 및 구글 로그인 API를 활용하여, 프로필 사진 및 닉네임으로 게임 프로필을 만든다.
  +  ### SoundPool을 이용하여 음악 재생
  +  ### Django, Retrofit2를 활용한 프로필 및 퀴즈 데이터 관리

## D. 게임 설명
  + 초기 상태 : 하트 5개, 별(힌트) 5개
  + 퀴즈 순서는 랜덤이다.
  1. LP를 누르면 OST가 재생된다.
      + 자동적으로 일시정지되기 전에, 재생을 일시정지하고 싶다면 다시 LP를 누르면 된다.       
  2. 기본값인 검정색 LP는 OST가 2초만 나온다.
  3. 모르겠는 경우, 힌트를 사용할 수 있다.      
      + 파란색 LP : OST 4초 재생
      + 빨간색 LP : OST 6초 재생
      + 파란색 LP를 선택하고, 빨간색 LP를 선택하면 선택할 때마다 별이 1개씩 차감된다.
      + 파란색 LP를 선택하지 않고, 빨간색 LP를 선택하면 한 번에 별이 2개 차감된다.
        + 처음부터 빨간색 LP를 힌트로 사용하는 경우에는 4초 재생하는 파란색 LP도 자유롭게 재생 가능하다.
      + 힌트를 다 사용하면 OST를 2초만 들을 수 있다.
  4. OST 제목과 아티스트명을 모두 맞혀야 정답으로 인정된다.
      + 띄어쓰기는 틀려도 정답으로 처리했다.
      + 답을 제출하는 방법은 2가지이다.
        + 제출 버튼 누르기
        + 아티스트명 입력 후 키보드에 완료 버튼 누르기
  5. 제출하면 정답여부와 드라마 제목, 출연 배우, OST 제목, 아티스트명을 다이얼로그 창으로 알려준다.
      + 다음 버튼을 누르면 다음 문제로 이동한다.
  6. 하트가 0이 되는 경우에는 게임이 종료된다.

## E. Font  
  + 배달의민족 도현체

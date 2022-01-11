# 오리지널 사운드 트랙을 아시나요(오사트아)
+ ### OST 짧게 듣고 곡 제목과 아티스트명을 맞히는 게임

## 팀원  
- 김승찬, 유니스트 16, 컴퓨터공학과
- 최윤정, 숙명여대 19, 컴퓨터과학전공  

## 카카오, 구글 소셜 로그인 기능 구현
## SoundPool을 이용하여 음악 재생

## 게임 룰
  + 초기 상태 : 하트 5개, 별(힌트) 5개
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
  5. 하트가 0이 되는 경우에는 게임이 종료된다.

+ ### Font  
        배달의민족 도현체

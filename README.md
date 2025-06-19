#YOOGIHUN_BACKEND
<br/>
<br/>

#실행
1. docker-compose up -d mysql 을 입력하여 mysql 컨테이너를 실행합니다.
<br/>
2. 몇초간 대기 후 mysql 컨테이너가 정상적으로 실행이 완료되면 ./gradlew build 를 입력하여 빌드합니다.
   <br/>
   * 만약 빌드 실패 시 mysql 컨테이너가 실행 완료된 상태인지 확인 해주시면 감사하겠습니다.
<br/>
3. docker-compose up -d api 를 입력하여 api 컨테이너를 실행합니다.
<br/>
4. API 문서 경로: http://localhost:8080/swagger-ui.html
<br/>

#종료: 
docker-compose down

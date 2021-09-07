# gcs-storage-akka-stream-test
akka (scala) 환경에서의 google cloud storage SDK api 테스트

# google cloud storage SDK 사용을 위한 환경변수 설정
IAM 관리자 > 서비스 계정 > 키 > 메뉴에서 credentials.json key file 을 생성

| 환경변수 이름 | 환경변수 값 (형태) |
|---|:---:|
| `GCS_CLIENT_ID` | credentials.json의 client_id
| `GCS_PROJECT_NAME` | credentials.json의 project_id
| `GCS_EMAIL_PREFIX` | credentials.json의 client_email 중 @ 앞에 것
| `GCS_BUCKET` | google cloud storage 버킷 이름 
| `GCS_READ_KEY` | credentials.json의 private_key
| `GCS_PRIVATE_KEY_ID` | credentials.json의 private_key_id


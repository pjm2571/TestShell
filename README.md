# 📌 프로젝트 소개
가상 SSD를 제작하고, SSD 제품을 테스트 할 수 있는 Test Shell을 제작한다.
- [SSD 레포지토리](https://github.com/jinii9/ssd-project)
- [TestShell 레포지토리](https://github.com/jinii9/ssd-project)

## 1️⃣ 가상 SSD 프로그램
- Read 명령어와 Write 명령어만 존재
- LBA 단위는 4Byte
- LBA 0~99까지 100칸을 저장할 수 있다.
- 실습 방법
  ```
  W 3 0x12345678
  R 2
  ```
## 2️⃣ Test Shell 프로그램
- 사용 가능 명령어
  write / read / exit / help / fullwrite / fullread
- 실습 방법
  ```
  help
  fullwrite 0x00000000
  fullread
  write 3 0x12345678
  read 3
  fullread
  exit
  ```
- Test Script 실습 방법
  ```
  testapp1
  testapp2
  ```

# 📂 Format
## 1️⃣ nand.txt  
사용자가 Write 할 때마다, nand.txt 파일에 기록이 된다.
- 파일 Format
각 공간마다 LBA (Logical Block Address) 라는 주소를 가진다.
이때 LBA 단위는 4 Byte이고, LBA 0~99까지 100칸을 저장할 수 있다.
 
![image](https://github.com/user-attachments/assets/002e8b76-1674-4713-b588-3d3abd54f773)

## 2️⃣ result.txt
화면에 출력하지 않고, result.txt 파일에 결과를 저장한다.
Read 명령어 수행 시, result.txt 파일 내용이 교체된다.
- 파일 Format
result.txt 에 읽은 값이 적힌다.
 
![image](https://github.com/user-attachments/assets/41e27333-b1be-43bc-b134-534b9f33dbf4)


# 📊 시퀀스 다이어그램
## 1️⃣ sds 의 write 시 일어나는 sequence
  ![image](https://github.com/user-attachments/assets/ac07442d-08de-4e6e-8f7f-063549975f9c)
## 2️⃣ sds의 read 시 일어나는 sequence
  ![image](https://github.com/user-attachments/assets/0dd07547-8101-4b09-9c4c-15bda48c5043)
## 3️⃣ test shell sequence
![image](https://github.com/user-attachments/assets/4b2c545f-f0dc-492f-a274-3c3df27687a1)


# 📖 시나리오
1. `help` 명령어를 통해 명령어를 확인한다.
2. `fullwrite 0x00000000` 명령어를 통해 nand.txt를 초기화한다. (nand.txt)
3. `fullread` 명령어를 통해 초기화된 값을 확인하기 위해 모든 LBA의 값들을 화면에 출력한다. (nand.txt)
4. `write 3 0x12345678`명령어를 통해 3번 LBA에 0x12345678를 기록한다. (nand.txt)
5. `read 3`명령어를 통해 바뀐 3번 LBA를 읽는다. (nand.txt, result.txt)
6. `fullread` 명령어를 통해 모든 LAB의 값들을 화면에 출력한다. (nand.txt, result.txt)
7. `testapp1` 명령어를 통해 fullwriteㅇ와 fullread가 정상 동작하는지 확인한다.
8. `testapp2` 명령어를 통해 write와 read가 정상 동작하는지 확인한다.  
  • 0 ~ 5 번 LBA 에 0xAAAABBBB 값으로 총 30번 Write를 수행한다.  
  • 0 ~ 5 번 LBA 에 0x12345678 값으로 1 회 Over Write를 수행한다.  
  • 0 ~ 5 번 LBA Read 했을 때 정상적으로 값이 읽히는지 확인한다.  


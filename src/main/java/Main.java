import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {

    private static final String FULLWRITE = "fullwrite";
    private static final String FULLREAD = "fullread";
    private static final String EXIT = "exit";
    private static final String HELP = "help";
    private static final String TESTAPP1 = "testapp1";
    private static final String TESTAPP2 = "testapp2";
    private static final String READ = "read";
    private static final String WRITE = "write";

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("입력 > ");  // 사용자로부터 입력을 받기 위한 프롬프트
            String input = scanner.nextLine();

            if (input.startsWith(EXIT)) {
                System.out.println("프로그램 종료");
                break;
            }

            if (input.startsWith(HELP)) {
                printHelp(); // 도움말 출력 메서드
                continue;
            }

            if (input.startsWith(FULLWRITE)) {
                handleFullWrite(input); // fullwrite 작업 수행 메서드
            }

            if (input.startsWith(FULLREAD)) {
                handleFullRead(100); // fullread 작업 수행 메서드
            }

            if (input.startsWith(TESTAPP1)) {
                handleTestApp1(); // testapp1 작업 수행 메서드
            }

            if (input.startsWith(TESTAPP2)) {
                handleTestApp2(); // testapp2 작업 수행 메서드
            }

            if (input.startsWith(WRITE)) {
                System.out.println("WRITE 작업 시작");
                String[] wordsArray = input.split(" ");

                if (wordsArray.length != 3) {
                    System.out.println("입력 형식이 잘못되었습니다.");
                    continue;
                }

                String command = String.format("java -jar ssd.jar W %s %s", wordsArray[1], wordsArray[2]);
                executeCommand(command);
                System.out.println("WRITE 작업 완료");
            }

            if (input.startsWith(READ)) {
                handleRead(input);  // read 작업 수행 메서드
            }
        }
    }

    // read 작업 수행
    private static void handleRead(String input) throws IOException, InterruptedException {
        System.out.println("READ 작업 시작");
        StringTokenizer st = new StringTokenizer(input, " ");
        st.nextToken();  // "read" 토큰 넘기기

        if (!st.hasMoreTokens()) {
            System.out.println("LBA 번호를 입력해주세요.");
            return;
        }

        int lba = Integer.parseInt(st.nextToken());

        // ssd.jar에 LBA 읽기 명령 전달
        String command = String.format("java -jar ssd.jar R %d", lba);
        executeCommand(command);

        // result.txt에서 결과 읽기
        String result = readFromFile("result.txt");

        // 결과 출력
        System.out.println("LBA " + lba + "번에서 읽은 값: " + result);
        System.out.println("READ 작업 완료");
    }

    // fullwrite 작업 수행
    private static void handleFullWrite(String input) throws IOException, InterruptedException {
        System.out.println("FULL WRITE 작업 시작");
        StringTokenizer st = new StringTokenizer(input, " ");
        st.nextToken();  // "fullwrite" 토큰 넘기기

        if (!st.hasMoreTokens()) {
            System.out.println("값을 입력해주세요.");
            return;
        }

        String value = st.nextToken();
        executeWriteOperations(100, value);
        System.out.println("FULL WRITE 작업 완료");
    }

    // fullread 작업 수행 (기본적으로 100개의 라인 읽기)
    private static void handleFullRead(int count) throws IOException, InterruptedException {
        System.out.println("FULL READ 작업 시작");
        ArrayList<String> arr = executeReadOperations(count);

        // 읽은 값 출력
        for (String value : arr) {
            System.out.println(value);
        }
        System.out.println("FULL READ 작업 완료");
    }

    // testapp1 작업 수행: fullwrite -> fullread 순차 수행
    private static void handleTestApp1() throws IOException, InterruptedException {
        System.out.println("fullwrite 0x00000000 수행");
        executeWriteOperations(100, "0x00000000");
        System.out.println("fullwrite 완료");

        handleFullRead(100);  // fullread 실행
    }

    // testapp2 작업 수행: 특정 패턴으로 쓰기, 읽기 실행
    private static void handleTestApp2() throws IOException, InterruptedException {
        System.out.println("0 ~ 5 번 LBA 에 0xAAAABBBB 값으로 총 30번 Write를 수행");
        executeWriteOperations(6, "0xAAAABBBB", 30);

        System.out.println("0 ~ 5 번 LBA 에 0x12345678 값으로 1 회 Over Write를 수행");
        executeWriteOperations(6, "0x12345678");

        System.out.println("0 ~ 5 번 LBA Read 했을 때 정상적으로 출력 확인");
        ArrayList<String> arr = executeReadOperations(6);

        // 읽은 값 출력
        for (String value : arr) {
            System.out.println(value);
        }
    }

    // 반복적인 write 작업을 처리하는 메서드 (overload)
    private static void executeWriteOperations(int count, String value) throws IOException, InterruptedException {
        executeWriteOperations(count, value, 1);  // 기본 1회 실행
    }

    // write 작업을 처리하는 메서드
    private static void executeWriteOperations(int count, String value, int repeat) throws IOException, InterruptedException {
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < repeat; j++) {
                String command = String.format("java -jar ssd.jar W %d %s", i, value);
                executeCommand(command);
            }
        }
    }

    // 반복적인 read 작업을 처리하는 메서드
    private static ArrayList<String> executeReadOperations(int count) throws IOException, InterruptedException {
        ArrayList<String> arr = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String command = String.format("java -jar ssd.jar R %d", i);
            executeCommand(command);
            arr.add(readFromFile("result.txt"));
        }
        return arr;
    }

    // 도움말 출력
    private static void printHelp() {
        System.out.println("사용 가능한 명령어:");
        System.out.println("  read <value> : value의 값을 읽기");
        System.out.println("  fullwrite <value> : 100개의 값을 쓰기");
        System.out.println("  fullread : 100개의 값을 읽기");
        System.out.println("  exit : 프로그램 종료");
        System.out.println("  testapp1 : fullwrite 수행 후, fullread");
        System.out.println("  testapp2 : testapp2 수행");
    }

    // 외부 명령어를 실행하는 메서드
    private static String executeCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.redirectErrorStream(true);  // 오류 스트림을 표준 출력으로 리다이렉트

        Process process = processBuilder.start();
        StringBuilder result = new StringBuilder();

        // 프로세스의 출력 스트림을 읽어서 반환
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }

        process.waitFor();  // 프로세스가 종료될 때까지 대기
        return result.toString().trim();  // 결과 반환
    }

    // 파일에서 읽어 ArrayList에 추가하는 메서드
    private static String readFromFile(String fileName) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result.toString().trim();  // 파일의 내용을 반환
    }
}

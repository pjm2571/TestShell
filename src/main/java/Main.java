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
            System.out.print("입력 > ");
            String input = scanner.nextLine();

            if (input.startsWith(EXIT)) {
                System.out.println("프로그램 종료");
                break;
            } else if (input.startsWith(HELP)) {
                printHelp();
            } else if (input.startsWith(FULLWRITE)) {
                handleFullWrite(input);
            } else if (input.startsWith(FULLREAD)) {
                handleFullRead(100);
            } else if (input.startsWith(TESTAPP1)) {
                handleTestApp1();
            } else if (input.startsWith(TESTAPP2)) {
                handleTestApp2();
            } else if (input.startsWith(WRITE)) {
                handleWrite(input);
            } else if (input.startsWith(READ)) {
                handleRead(input);
            } else {
                System.out.println("[ERROR] INVALID COMMAND");
            }
        }
    }

    // WRITE 작업 수행
    private static void handleWrite(String input) throws IOException, InterruptedException {
        System.out.println("WRITE 작업 시작");
        String[] args = input.split(" ");

        if (args.length != 3) {
            System.out.println("입력 형식이 잘못되었습니다. 사용법: write <LBA> <value>");
            return;
        }

        int lba = Integer.parseInt(args[1]);
        String value = args[2];
        String command = String.format("java -jar ssd.jar W %d %s", lba, value);
        executeCommand(command);

        System.out.println("WRITE 작업 완료");
    }

    // READ 작업 수행
    private static void handleRead(String input) throws IOException, InterruptedException {
        System.out.println("READ 작업 시작");
        StringTokenizer st = new StringTokenizer(input, " ");
        st.nextToken();  // "read" 토큰 넘기기

        int lba = Integer.parseInt(st.nextToken());
        String command = String.format("java -jar ssd.jar R %d", lba);
        executeCommand(command);

        String result = readFromFile("result.txt");
        System.out.println("LBA " + lba + "번에서 읽은 값: " + result);
        System.out.println("READ 작업 완료");
    }

    // FULLWRITE 작업 수행
    private static void handleFullWrite(String input) throws IOException, InterruptedException {
        System.out.println("FULL WRITE 작업 시작");
        StringTokenizer st = new StringTokenizer(input, " ");
        st.nextToken();

        if (!st.hasMoreTokens()) {
            System.out.println("값을 입력해주세요.");
            return;
        }

        String value = st.nextToken();
        executeWriteOperations(100, value);
        System.out.println("FULL WRITE 작업 완료");
    }

    // FULLREAD 작업 수행
    private static void handleFullRead(int count) throws IOException, InterruptedException {
        System.out.println("FULL READ 작업 시작");
        ArrayList<String> arr = executeReadOperations(count);

        for (String value : arr) {
            System.out.println(value);
        }
        System.out.println("FULL READ 작업 완료");
    }

    // TESTAPP1 작업 수행: FULLWRITE -> FULLREAD
    private static void handleTestApp1() throws IOException, InterruptedException {
        System.out.println("fullwrite 0x00000000 수행");
        executeWriteOperations(100, "0x00000000");
        System.out.println("fullwrite 완료");

        handleFullRead(100);
    }

    // TESTAPP2 작업 수행: 특정 패턴으로 쓰기, 읽기 실행
    private static void handleTestApp2() throws IOException, InterruptedException {
        System.out.println("0 ~ 5 번 LBA 에 0xAAAABBBB 값으로 총 30번 Write를 수행");
        executeWriteOperations(6, "0xAAAABBBB", 30);

        System.out.println("0 ~ 5 번 LBA 에 0x12345678 값으로 1회 Over Write를 수행");
        executeWriteOperations(6, "0x12345678");

        System.out.println("0 ~ 5 번 LBA Read 했을 때 정상적으로 출력 확인");
        ArrayList<String> arr = executeReadOperations(6);

        for (String value : arr) {
            System.out.println(value);
        }
    }

    // 반복적인 WRITE 작업을 처리하는 메서드 (Overload)
    private static void executeWriteOperations(int count, String value)
            throws IOException, InterruptedException {
        executeWriteOperations(count, value, 1);
    }

    // WRITE 작업을 처리하는 메서드
    private static void executeWriteOperations(int count, String value, int repeat)
            throws IOException, InterruptedException {
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < repeat; j++) {
                String command = String.format("java -jar ssd.jar W %d %s", i, value);
                executeCommand(command);
            }
        }
    }

    // 반복적인 READ 작업을 처리하는 메서드
    private static ArrayList<String> executeReadOperations(int count)
            throws IOException, InterruptedException {
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
        System.out.println("  write <LBA> <value> : 특정 LBA에 값을 쓰기");
        System.out.println("  read <LBA> : 특정 LBA에서 값을 읽기");
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

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }

        process.waitFor();  // 프로세스가 종료될 때까지 대기
        return result.toString().trim();  // 결과 반환
    }

    // 파일에서 결과를 읽어오는 메서드
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

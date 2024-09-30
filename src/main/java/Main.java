import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {

    private static final String FULLWRITE = "fullwrite";
    private static final String FULLREAD = "fullread";
    private static final String EXIT = "exit";
    private static final String HELP = "help";

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("입력 > ");  // 프롬프트
            String input = scanner.nextLine();

            if (input.startsWith(EXIT)) {
                System.out.println("프로그램 종료");
                break;
            }

            if (input.startsWith(HELP)) {
                System.out.println("사용 가능한 명령어:");
                System.out.println("  fullwrite <value> : 100개의 값을 쓰기");
                System.out.println("  fullread : 100개의 값을 읽기");
                System.out.println("  exit : 프로그램 종료");
                continue;
            }

            if (input.startsWith(FULLWRITE)) {
                System.out.println("FULL WRITE 작업 시작");
                StringTokenizer st = new StringTokenizer(input, " ");
                st.nextToken();  // "fullwrite" 토큰 넘기기

                if (!st.hasMoreTokens()) {
                    System.out.println("값을 입력해주세요.");
                    continue;
                }

                String value = st.nextToken();

                for (int i = 0; i < 100; i++) {
                    String command = String.format("java -jar ssd.jar W %d %s", i, value);
                    executeCommand(command);
                }
                System.out.println("FULL WRITE 작업 완료");
            }

            if (input.startsWith(FULLREAD)) {
                System.out.println("FULL READ 작업 시작");
                ArrayList<String> arr = new ArrayList<>();

                for (int i = 0; i < 100; i++) {
                    String command = String.format("java -jar ssd.jar R %d", i);
                    executeCommand(command);  // 파일에 쓰이는 명령어 실행

                    // 결과 파일에서 읽은 값을 ArrayList에 추가
                    arr.add(readFromFile("result.txt"));
                }

                // 읽은 값 출력
                for (String value : arr) {
                    System.out.println(value);
                }

                System.out.println("FULL READ 작업 완료");
            }
        }
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

package Client;

import java.util.Scanner;

public class IOHandler {
    String id, password;
    Scanner sc = new Scanner(System.in);

    public void loginIO() {
        System.out.println("ID : ");
        id = sc.next();
        System.out.println("Password : ");
        password = sc.next();
    }

    public int studentMenu() {
        System.out.println("------------메뉴번호를 선택하세요.------------");
        System.out.println("1. 개인정보 및 비밀번호 조회");
        System.out.println("2. 개인정보 및 비밀번호 수정");
        System.out.println("3. 수강신청");
        System.out.println("4. 수강정정");
        System.out.println("5. 본인 시간표 조회");
        System.out.println("6. 개설 교과목 목록 조회");
        System.out.println("7. 선택 교과목 강의계획서 조회");
        System.out.println("------------------------------------------");

        int selectMenu = sc.nextInt();
        return selectMenu;
    }

    public int professorMenu() {
        System.out.println("------------메뉴번호를 선택하세요.------------");
        System.out.println("1. 개인정보 및 비밀번호 조회");
        System.out.println("2. 개인정보 및 비밀번호 수정");
        System.out.println("3. 강의계획서 입력");
        System.out.println("4. 강의계획서 수정");
        System.out.println("5. 담당 교과목 목록 조회");
        System.out.println("6. 담당 교과목 강의계획서 조회");
        System.out.println("7. 담당 교과목 수강 신청 학생 목록 조회");
        System.out.println("8. 담당 교과목 시간표 조회");
        System.out.println("------------------------------------------");

        int selectMenu = sc.nextInt();
        return selectMenu;
    }

    public void adminMenu() {
        System.out.println("------------메뉴번호를 선택하세요.------------");
        System.out.println("1. 교수/학생 계정 생성");
        System.out.println("2. 교과목 생성/수정/삭제");
        System.out.println("3. 강의계획서 입력 기간 설정");
        System.out.println("4. 학년별 수강신청 기간 설정");
        System.out.println("5. 교수/학생 정보 조회");
        System.out.println("6. 개설 교과목 정보 조회");
        System.out.println("------------------------------------------");

        int selectMenu = sc.nextInt();
        switch (selectMenu) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
        }
    }
}
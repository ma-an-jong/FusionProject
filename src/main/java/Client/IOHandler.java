package Client;

import java.util.Scanner;

public class IOHandler {
    Scanner sc = new Scanner(System.in);

    //로그인
    public String[] loginIO() {
        String idpw[] = new String[2];

        System.out.print("ID : ");
        idpw[0] = sc.next();
        System.out.print("Password : ");
        idpw[1] = sc.next();

        return idpw;
    }

    //학생 메뉴
    public int studentMenu() {
        System.out.println("------------메뉴번호를 선택하세요.------------");
        System.out.println("1. 개인정보 및 비밀번호 조회");
        System.out.println("2. 개인정보 및 비밀번호 수정");
        System.out.println("3. 수강신청");
        System.out.println("4. 수강정정");
        System.out.println("5. 본인 시간표 조회");
        System.out.println("6. 전체 교과목 조회");
        System.out.println("7. 개설 교과목 목록 조회");
        System.out.println("8. 선택 교과목 강의계획서 조회");
        System.out.println("9. 내 수강과목 조회");
        System.out.println("10. 종료");
        System.out.println("------------------------------------------");

        int selectMenu = sc.nextInt();
        return selectMenu;
    }

    //교수 메뉴
    public int professorMenu() {
        System.out.println("------------메뉴번호를 선택하세요.------------");
        System.out.println("1. 개인정보 및 비밀번호 조회");
        System.out.println("2. 개인정보 및 비밀번호 수정");
        System.out.println("3. 강의계획서 입력");
        System.out.println("4. 강의계획서 삭제");
        System.out.println("5. 전체 교과목 조회");
        System.out.println("6. 담당 교과목 목록 조회");
        System.out.println("7. 담당 교과목 강의계획서 조회");
        System.out.println("8. 담당 교과목 수강 신청 학생 목록 조회");
        System.out.println("9. 담당 교과목 시간표 조회");
        System.out.println("10. 종료");
        System.out.println("------------------------------------------");

        int selectMenu = sc.nextInt();
        return selectMenu;
    }

    //관리자 메뉴
    public int adminMenu() {
        System.out.println("------------메뉴번호를 선택하세요.------------");
        System.out.println("1. 교수 계정 생성");
        System.out.println("2. 학생 계정 생성");
        System.out.println("3. 교과목 생성");
        System.out.println("4. 교과목 수정");
        System.out.println("5. 교과목 삭제");
        System.out.println("6. 강의계획서 입력 기간 설정");
        System.out.println("7. 학년별 수강신청 기간 설정");
        System.out.println("8. 교수 정보 조회");
        System.out.println("9. 학생 정보 조회");
        System.out.println("10. 교수, 학생 전체 조회");
        System.out.println("11. 개설 교과목 정보 조회");
        System.out.println("12. 개설 교과목 등록");
        System.out.println("13. 개설 교과목 수정");
        System.out.println("14. 개설 교과목 삭제");
        System.out.println("15. 전체 교과목 조회");
        System.out.println("16. 종료");
        System.out.println("------------------------------------------");

        int selectMenu = sc.nextInt();
        return selectMenu;
    }
}

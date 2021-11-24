package Client;

public class Main {
    public static void main(String[] args) {
        IOHandler ioHandler = new IOHandler();
        SocketClient socketClient = new SocketClient();
        LoginClient loginClient = new LoginClient();
        LookupClient lookupClient = new LookupClient();

        ioHandler.loginIO();
        String loginResult[] = socketClient.run(loginClient.login());

        String id = loginResult[1];
        String password = loginResult[2];

        while(true) {
            int selectMenu;

            switch (loginResult[0]) {
                case "s":
                    //학생인 경우
                    selectMenu = ioHandler.studentMenu();
                    switch (selectMenu) {
                        case 1:
                            //개인정보 조회
                            lookupClient.personalInfo(id);
                            break;
                        case 2:
                            //개인정보 수정
                            break;
                        case 3:
                            //수강신청
                            break;
                        case 4:
                            //수강정정
                            break;
                        case 5:
                            //본인시간표 조회
                            lookupClient.timeTableLookUp(id);
                            break;
                        case 6:
                            //개설교과목 목록 조회
                            lookupClient.lectureLookUp();
                            break;
                        case 7:
                            //선택교과목 강의계획서 조회
                            break;
                    }
                    break;
                case "p":
                    //교수인 경우
                    selectMenu = ioHandler.professorMenu();
                    switch (selectMenu) {
                        case 1:
                            //개인정보 조회
                            break;
                        case 2:
                            //개인정보 수정
                            break;
                        case 3:
                            //강의계획서 입력
                            break;
                        case 4:
                            //강의계획서 수정
                            break;
                        case 5:
                            //담당교과목 목록 조회
                            break;
                        case 6:
                            //담당교과목 강의계획서 조회
                            break;
                        case 7:
                            //담당교과목 수강신청 학생 목록 조회
                            break;
                        case 8:
                            //담당교과목 시간표 조회
                            break;
                    }
                    break;
                case "a":
                    //관리자인 경우
                    ioHandler.adminMenu();
                    break;
            }
        }


    }
}

package Client;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        IOHandler ioHandler = new IOHandler();
        Scanner sc = new Scanner(System.in);
        SocketClient socketClient = new SocketClient();
        LoginClient loginClient = new LoginClient();
        LookupClient lookupClient = new LookupClient();
        EnrollClient enrollClient = new EnrollClient();
        UpdateClient updateClient = new UpdateClient();

        //로그인
        String loginResult[] = socketClient.run(loginClient.login(ioHandler.loginIO()));
        String id = loginResult[2];
        String grade = loginResult[3];
        String category = loginResult[4];
        String num = loginResult[5];

        boolean distinct = false;

        //로그인 후
        while(!distinct) {
            int selectMenu;

            //category별 메뉴
            switch (category) {
                case "s":
                    //학생인 경우
                    selectMenu = ioHandler.studentMenu();
                    switch (selectMenu) {
                        case 1:
                            //개인정보 조회
                            String stdMInfo[] = socketClient.run(lookupClient.stdMyInfo(id));
                            if(stdMInfo[1].equals("14")) {
                                System.out.println(stdMInfo[2]);
                            }
                            else System.out.println("조회 실패");
                            break;
                        case 2:
                            //개인정보 수정
                            String modifyStud[] = socketClient.run(updateClient.modifyStudent(num));
                            if(modifyStud[1].equals("0")) System.out.println("개인정보 수정 성공");
                            else System.out.println("개인정보 수정 실패");
                            break;
                        case 3:
                            //수강신청
                            String registration[] = socketClient.run(enrollClient.registration(grade));

                            if(registration[1].equals("6")) {
                                //수강신청 인증됐으면
                                String enrollMySub[] = socketClient.run(enrollClient.enrollMySub(id));
                                if(enrollMySub[1].equals("A")) System.out.println("수강신청 성공");
                                else System.out.println("수강신청 실패");
                            }
                            else System.out.println("수강신청 기간이 아닙니다.");
                            break;
                        case 4:
                            //수강정정
                            String modifyCertif[] = socketClient.run(enrollClient.registration(grade));
                            if(modifyCertif[1].equals("6")) {
                                //수강정정 인증됐으면
                                String modifyMySub[] = socketClient.run(updateClient.deleteMySub(num));
                                if(modifyMySub[1].equals("8")) System.out.println("수강정정 성공");
                                else System.out.println("수강정정 실패");
                            }
                            else System.out.println("수강정정 기간이 아닙니다.");
                            break;
                        case 5:
                            //본인시간표 조회
                            // 시간 과목이름 강의실
                            String timet[] = socketClient.run(lookupClient.timeTableLookUp(id));
                            for(int i=2 ; i<timet.length ; i++) {
                                System.out.println(timet[i]);
                            }
                            break;
                        case 6:
                            //전체 교과목 조회
                            String allSub[] = socketClient.run(lookupClient.SubjectLoopUp());
                            for(int i=2 ; i<allSub.length ; i++) {
                                System.out.println(allSub[i]);
                            }
                            break;
                        case 7:
                            //개설교과목 목록 조회
                            String lec[] = socketClient.run(lookupClient.allLectureLookUp());
                            for(int i=2 ; i<lec.length ; i++) {
                                System.out.println(lec[i].replace("\t","\n"));
                            }
                            break;
                        case 8:
                            //선택교과목 강의계획서 조회
                            String syllabus[] = socketClient.run(lookupClient.lookupSyllabus());
                            System.out.println(syllabus[2]);
                            break;
                        case 9:
                            //내 수강과목 조회
                            String mySublook[] = socketClient.run(lookupClient.mySubjectLookUp(id));
                            if(mySublook[1].equals("10")) {
                                for(int i=2 ; i<mySublook.length ; i++) {
                                    System.out.println(mySublook[i]);
                                }
                            }
                            else System.out.println("조회 실패");
                            break;
                        case 10:
                            distinct = true;
                            break;
                    }
                    break;
                case "p":
                    //교수인 경우
                    selectMenu = ioHandler.professorMenu();
                    switch (selectMenu) {
                        case 1:
                            //개인정보 조회
                            String profMInfo[] = socketClient.run(lookupClient.profMyInfo(id));
                            System.out.println(profMInfo[2]);
                            break;
                        case 2:
                            //개인정보 수정
                            String modifyProfe[] = socketClient.run(updateClient.modifyProfessor(num));
                            if(modifyProfe[1].equals("0")) System.out.println("개인정보 수정 성공");
                            else System.out.println("개인정보 수정 실패");
                            break;
                        case 3:
                            //강의계획서 입력기간 인증
                            String access[] = socketClient.run(enrollClient.syllabusPeriodAccess());
                            //기간 인증 성공하면
                            //강의계획서 등록
                            if(access[1].equals("6")){
                                String enrollSyllabus[] = socketClient.run(enrollClient.enrollSyllabus());
                                if(enrollSyllabus[1].equals("2")) System.out.println("강의계획서 등록 성공");
                                else System.out.println("강의계획서 등록 실패");
                            }
                            else System.out.println("강의계획서 입력기간이 아닙니다.");
                            break;
                        case 4:
                            //강의계획서 삭제
                            String deleteS[] = socketClient.run(updateClient.deleteSyllabus());
                            if(deleteS[1].equals("0")) System.out.println("강의계획서 삭제 성공");
                            else System.out.println("강의계획서 삭제 실패");
                            break;
                        case 5:
                            //전체 교과목 조회
                            String allSubject[] = socketClient.run(lookupClient.SubjectLoopUp());
                            for(int i=2 ; i<allSubject.length ; i++) {
                                System.out.println(allSubject[i]);
                            }
                            break;
                        case 6:
                            //담당교과목 목록 조회
                            String lookProfSub[] = socketClient.run(lookupClient.teachingSubject(id));
                            for(int i=2 ; i<lookProfSub.length ; i++) {
                                System.out.println(lookProfSub[i].replace("\t","\n"));
                            }
                            break;
                        case 7:
                            //담당교과목 강의계획서 조회
                            String mySlla[] = socketClient.run(lookupClient.lookMySyllabus());
                            System.out.println(mySlla[2]);
                            break;
                        case 8:
                            //담당교과목 수강신청 학생 목록 조회
                            int ch = 0; int page = 1;

                            System.out.print("과목코드 : ");
                            String subject = sc.next();

                            boolean end = false;
                            //페이징
                            while(!end) {
                                String lookMyStd[] = socketClient.run(lookupClient.myStudentLookUp(page, subject));

                                System.out.println("현재페이지 : " + page);
                                for(int i=2 ; i<lookMyStd.length ; i++) {
                                    System.out.println(lookMyStd[i].replace("\t", "\n"));
                                }

                                System.out.println("======================");
                                System.out.println("1. 이전페이지");
                                System.out.println("2. 다음페이지");
                                System.out.println("3. 종료");
                                System.out.println("======================");
                                ch = sc.nextInt();

                                switch(ch) {
                                    case 1:
                                        if(page == 1) System.out.println("이전 페이지가 없습니다.");
                                        else page--;
                                        break;
                                    case 2:
                                        if(lookMyStd[1].equals("9")) System.out.println("다음 페이지가 없습니다.");
                                        else page++;
                                        break;
                                    case 3:
                                        end = true;
                                        break;
                                }
                            }
                            break;
                        case 9:
                            //담당교과목 시간표 조회
                            String profMyTime[] = socketClient.run(lookupClient.teachingTable(id));
                            for(int i=2 ; i<profMyTime.length ; i++) {
                                System.out.println(profMyTime[i]);
                            }
                            break;
                        case 10:
                            distinct = true;
                            break;
                    }
                    break;
                case "a":
                    //관리자인 경우
                    selectMenu = ioHandler.adminMenu();
                    switch (selectMenu) {
                        case 1:
                            //교수 계정 생성
                            String enrollProf[] = socketClient.run(enrollClient.enrollProfessor(category));
                            break;
                        case 2:
                            //학생 계정 생성
                            String enrollStd[] = socketClient.run(enrollClient.enrollStudent());
                            break;
                        case 3:
                            //교과목 생성
                            String enrollSub[] = socketClient.run(enrollClient.enrollSubject(category));
                            break;
                        case 4:
                            //교과목 수정
                            String updateSub[] = socketClient.run(updateClient.updateSubject(category));
                            break;
                        case 5:
                            //교과목 삭제
                            String deleteSub[] = socketClient.run(updateClient.deleteSubject(category));
                            if(deleteSub[1].equals("14")) System.out.println("교과목 삭제 성공");
                            else System.out.println("교과목 삭제 실패");
                            break;
                        case 6:
                            //강의계획서 입력 기간 설정
                            String syllabusPeriod[] = socketClient.run(enrollClient.enrollSyllabusPeriod());
                            if(syllabusPeriod[1].equals("16")) System.out.println("강의계획서 입력기간 설정 성공");
                            else System.out.println("강의계획서 입력기간 설정 실패");
                            break;
                        case 7:
                            //학년별 수강신청 기간 설정
                            String registPeriod[] = socketClient.run(enrollClient.enrollRegistPeriod());
                            if(registPeriod[1].equals("18")) System.out.println("수강신청 입력기간 설정 성공");
                            else System.out.println("수강신청 입력기간 설정 실패");
                            break;
                        case 8:
                            //교수 정보 조회
                            String profInfo[] = socketClient.run(lookupClient.professorLookUp());
                            for(int i=2 ; i<profInfo.length ; i++) {
                                System.out.println(profInfo[i].replace("\t", "\n"));
                            }
                            break;
                        case 9:
                            //학생 정보 조회
                            String stdInfo[] = socketClient.run(lookupClient.studentLookUp());
                            for(int i=2 ; i<stdInfo.length ; i++) {
                                System.out.println(stdInfo[i].replace("\t", "\n"));
                            }
                            break;
                        case 10:
                            //교수, 학생 전체 조회
                            String allMem[] = socketClient.run(lookupClient.allMemberLookUp());
                            for(int i=2 ; i< allMem.length ; i++) {
                                System.out.println(allMem[i].replace("\t", "\n"));
                            }
                            break;
                        case 11:
                            //개설 교과목 목록 조회
                            String lectureInfo[] = socketClient.run(lookupClient.lectureLookup());
                            for(int i=2 ; i<lectureInfo.length ; i++) {
                                System.out.println(lectureInfo[i].replace("\t", "\n"));
                            }
                            break;
                        case 12:
                            //개설 교과목 등록
                            String enrollLec[] = socketClient.run(enrollClient.enrollLecture());
                            if(enrollLec[1].equals("12")) System.out.println("개설교과목 등록 성공");
                            else System.out.println("개설교과목 등록 실패");
                            break;
                        case 13:
                            //개설 교과목 수정
                            String modifyLec[] = socketClient.run(updateClient.modifyLecture());
                            if(modifyLec[1].equals("14")) System.out.println("개설교과목 수정 성공");
                            else System.out.println("개설교과목 수정 실패");
                            break;
                        case 14:
                            //개설 교과목 삭제
                            String deleteLec[] = socketClient.run(updateClient.deleteLecture());
                            if(deleteLec[1].equals("14"))System.out.println("개설교과목 삭제 성공");
                            else System.out.println("개설교과목 삭제 실패");
                            break;
                        case 15:
                            //전체 교과목 조회
                            String allSubLook[] = socketClient.run(lookupClient.SubjectLoopUp());
                            for(int i=2 ; i<allSubLook.length ; i++) {
                                System.out.println(allSubLook[i].replace("\t", "\n"));
                            }
                            break;
                        case 16:
                            distinct = true;
                            break;
                    }
                    break;
            }
        }
    }
}
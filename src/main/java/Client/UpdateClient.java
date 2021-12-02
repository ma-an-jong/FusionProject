package Client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

//수정,삭제 요청
public class UpdateClient {
    Scanner sc = new Scanner(System.in);

    //수정
    //학생 개인정보 수정
    public String[] modifyStudent(String n) {
        String modifystd[] = new String[8];

        modifystd[0] = Protocol.CS_REQ_STUDENT_PERSONALINFO_UPDATE;
        modifystd[1] = n;

        while(true) {
            System.out.println("----------개인정보 수정----------");
            System.out.println("수정할 항목의 번호를 입력하세요.");
            System.out.println("1. id");
            System.out.println("2. password");
            System.out.println("3. 이름");
            System.out.println("4. 학과");
            System.out.println("5. 전화번호");
            System.out.println("6. 학년");
            System.out.println("7. 종료");
            System.out.println("-------------------------------");

            int ch = sc.nextInt();
            if(ch == 7) break;

            switch (ch) {
                case 1:
                    System.out.print("수정할 id : ");
                    modifystd[2]  = sc.next();
                    break;
                case 2:
                    System.out.print("수정할 password : ");
                    modifystd[3]  = sc.next();
                    break;
                case 3:
                    System.out.print("수정할 이름 : ");
                    modifystd[4]  = sc.next();
                    break;
                case 4:
                    System.out.print("수정할 학과 : ");
                    modifystd[5]  = sc.next();
                    break;
                case 5:
                    System.out.print("수정할 전화번호 : ");
                    modifystd[6]  = sc.next();
                    break;
                case 6:
                    System.out.print("수정할 학년 : ");
                    modifystd[7]  = sc.next();
                    break;
            }
        }

        return modifystd;
    }

    //교수 개인정보 수정
    public String[] modifyProfessor(String num) {
        String modifyprof[] = new String[7];

        while(true) {
            System.out.println("----------개인정보 수정----------");
            System.out.println("수정할 항목의 번호를 입력하세요.");
            System.out.println("1. id");
            System.out.println("2. password");
            System.out.println("3. 이름");
            System.out.println("4. 부서");
            System.out.println("5. 전화번호");
            System.out.println("6. 종료");
            System.out.println("-------------------------------");

            modifyprof[0] = Protocol.CS_REQ_PROFESSOR_PERSONALINFO_UPDATE;
            modifyprof[1] = num; //교번

            int ch = sc.nextInt();
            if(ch == 6) break;

            switch (ch) {
                case 1:
                    System.out.print("수정할 id : ");
                    modifyprof[2] = sc.next();
                    break;
                case 2:
                    System.out.print("수정할 password : ");
                    modifyprof[3]  = sc.next();
                    break;
                case 3:
                    System.out.print("수정할 이름 : ");
                    modifyprof[4]  = sc.next();
                    break;
                case 4:
                    System.out.print("수정할 부서 : ");
                    modifyprof[5]  = sc.next();
                    break;
                case 5:
                    System.out.print("수정할 전화번호 : ");
                    modifyprof[6]  = sc.next();
                    break;
            }
        }

        return modifyprof;
    }

    //교과목 수정(과목명 수정)
    public String[] updateSubject() {
        String updateSub[] = new String[4];

        System.out.print("수정할 과목명 : ");
        String subName = sc.next();

        System.out.print("수정 과목명 : ");
        String modifyName = sc.next();

        updateSub[0] = Protocol.CS_REQ_SUBJECT_UPDATE;
        updateSub[1] = subName;
        updateSub[2] = modifyName;

        return updateSub;
    }

    //개설교과목 수정
    public String[] modifyLecture() {
        String modifylec[] = new String[7];

        modifylec[0] = Protocol.CS_REQ_LECTURE_UPDATE;

        System.out.print("수정할 개설교과목코드 : ");
        modifylec[1] = sc.next(); //과목코드 입력받아서 해당 교과목 수정

        while(true) {
            System.out.println("-------------개설교과목 수정---------------");
            System.out.println("수정할 항목의 번호를 입력하세요.");
            System.out.println("1. 강의실");
            System.out.println("2. 최대강의인원");
            System.out.println("3. 담당교수변경(교번)");
            System.out.println("4. 종료");
            System.out.println("-------------------------------");

            int ch = sc.nextInt();
            if(ch == 4) break;

            switch (ch) {
                case 1:
                    System.out.print("수정 강의실 : ");
                    modifylec[2] = sc.next();
                    break;
                case 2:
                    System.out.print("수정 최대강의인원 : ");
                    modifylec[3] = sc.next();
                    break;
                case 3:
                    System.out.print("수정 담당교수변경(교번) : ");
                    modifylec[4] = sc.next();
                    break;
            }
        }

        return modifylec;
    }

    //삭제
    //수강 과목 삭제
    public String[] deleteMySub(String n) {
        String mysub[] = new String[3];

        System.out.print("삭제할 과목 코드 : ");
        String subject = sc.next();

        mysub[0] = Protocol.CS_REQ_MYSUBJECT_DELETE;
        mysub[1] = n;
        mysub[2] = subject;

        return mysub;
    }

    //교과목 삭제
    public String[] deleteSubject() {
        String sub[] = new String[2];

        System.out.print("삭제할 과목 코드 : ");
        String subject = sc.next();

        sub[0] = Protocol.CS_REQ_SUBJECT_DELETE;
        sub[1] = subject;

        return sub;
    }

    //개설 교과목 삭제
    public String[] deleteLecture() {
        String lecture[] = new String[2];

        System.out.print("삭제할 개설교과목 코드 : ");
        String lec = sc.next();

        lecture[0] = Protocol.CS_REQ_LECTURE_DELETE;
        lecture[1] = lec;

        return lecture;
    }

    //강의계획서 삭제(과목코드)
    public String[] deleteSyllabus() {
        String deleteSylla[] = new String[2];

        System.out.print("강의계획서 삭제할 과목코드 : ");
        String deletecode = sc.next();

        deleteSylla[0] = Protocol.CS_REQ_SYLLABUS_DELETE;
        deleteSylla[1] = deletecode;

        return deleteSylla;
    }
}

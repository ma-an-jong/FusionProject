package Client;

import java.text.SimpleDateFormat;
import java.util.Scanner;

//수정,삭제 요청
public class UpdateClient {
    Scanner sc = new Scanner(System.in);

    //수정
    //개인정보 수정
    public String[] modifyPersonal(String id) {
        String person[] = new String[4];
        
        while(true) {
            String input = "";
            
            System.out.println("수정할 개인정보(id, pw, 이름, 전화번호, 학과 중 입력, 종료 입력 시 종료) : ");
            String choice = sc.next();
            if(choice.equals("종료")) break;

            switch (choice) {
                case "id":
                    System.out.println("수정할 id : ");
                    input = sc.next();
                    break;
                case "pw":
                    System.out.println("수정할 password : ");
                    input = sc.next();
                    break;
                case "이름":
                    System.out.println("수정할 이름 : ");
                    input = sc.next();
                    break;
                case "전화번호":
                    System.out.println("수정할 전화번호 : ");
                    input = sc.next();
                    break;
                case "학과":
                    System.out.println("수정할 학과 : ");
                    input = sc.next();
                    break;
            }

            person[0] = Protocol.CS_REQ_PERSONALINFO_UPDATE;
            person[1] = id;
            person[2] = choice;
            person[3] = input;

            return person;
        }
        return null;
    }

    //수강 정정 요청(정정기간인지)
    public String[] modifySubject() {
        String correction[] = new String[2];
        SimpleDateFormat nowDate = new SimpleDateFormat( "yyyy-MM-dd");

        correction[0] = Protocol.CS_REQ_MYSUBJECT_MODIFY;
        correction[1] = nowDate.toString();

        return correction;
    }

    //교과목 수정
    public String[] updateSubject(String id) {
        String updateSub[] = new String[5];

        while(true) {
            System.out.println("수정할 과목코드(종료 입력 시 종료) : ");
            String subCode = sc.next();
            if(subCode.equals("종료")) break;

            System.out.println("수정사항(과목코드, 과목명, 학년 중) : ");
            String choice = sc.next();
            String input = "";
            switch (choice) {
                case "과목코드":
                    System.out.println("수정 코드 : ");
                    input = sc.next();
                    break;
                case "과목명":
                    System.out.println("수정 과목명 : ");
                    input = sc.next();
                    break;
                case "학년" :
                    System.out.println("수정 학년 : ");
                    input = sc.next();
                    break;
            }

            updateSub[0] = Protocol.CS_REQ_SUBJECT_UPDATE;
            updateSub[1] = id;
            updateSub[2] = subCode;
            updateSub[3] = choice;
            updateSub[4] = input;

            return updateSub;
        }
        return null;
    }

    //개설교과목 수정
    public String[] modifyLecture(String id) {
        String lecture[] = new String[5];

        while(true) {
            System.out.println("수정할 개설교과목코드(종료 입력 시 종료) : ");
            String lecCode = sc.next();
            if(lecCode.equals("종료")) break;

            System.out.println("수정사항(과목코드, 과목명, 교수, 강의시간, 최대학생수, 강의실 중) : ");
            String ch = sc.next();
            String input = "";
            switch (ch) {
                case "과목코드":
                    System.out.println("수정 코드 : ");
                    input = sc.next();
                    break;
                case "과목명":
                    System.out.println("수정 과목명 : ");
                    input = sc.next();
                    break;
                case "교수":
                    System.out.println("수정 교수명 : ");
                    input = sc.next();
                    break;
                case "강의시간":
                    System.out.println("수정 강의시간 : ");
                    input = sc.next();
                    break;
                case "최대학생수":
                    System.out.println("수정 최대 학생 수 : ");
                    input = sc.next();
                    break;
                case "강의실" :
                    System.out.println("수정 강의실 : ");
                    input = sc.next();
                    break;
            }

            lecture[0] = Protocol.CS_REQ_LECTURE_UPDATE;
            lecture[1] = id;
            lecture[2] = lecCode;
            lecture[3] = ch;
            lecture[4] = input;

            return lecture;
        }
        return null;
    }

    //삭제
    //수강 과목 삭제
    public String[] deleteMySub(String id) {
        String mysub[] = new String[3];

        System.out.println("삭제할 과목 코드 : ");
        String subject = sc.next();

        mysub[0] = Protocol.CS_REQ_MYSUBJECT_DELETE;
        mysub[1] = id;
        mysub[2] = subject;

        return mysub;
    }

    //교과목 삭제
    public String[] deleteSubject(String id) {
        String sub[] = new String[3];

        System.out.println("삭제할 과목 코드 : ");
        String subject = sc.next();

        sub[0] = Protocol.CS_REQ_SUBJECT_DELETE;
        sub[1] = id;
        sub[2] = subject;

        return sub;
    }

    //개설 교과목 삭제
    public String[] deleteLecture(String id) {
        String lecture[] = new String[3];

        System.out.println("삭제할 개설교과목 코드 : ");
        String lec = sc.next();

        lecture[0] = Protocol.CS_REQ_LECTURE_DELETE;
        lecture[1] = id;
        lecture[2] = lec;

        return lecture;
    }
}

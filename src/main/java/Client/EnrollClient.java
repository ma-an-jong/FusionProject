package Client;

import java.text.SimpleDateFormat;
import java.util.Scanner;

//등록 요청
public class EnrollClient {
    Scanner sc = new Scanner(System.in);

    //학생
    //수강신청 요청
    public String[] registration(String id) {
        String regist[] = new String[2];
        SimpleDateFormat nowDate = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");

        regist[0] = Protocol.CS_REQ_REGISTRATION;
        regist[1] = id;
        regist[2] = nowDate.toString();

        return regist;
    }

    //수강과목 등록
    public String[] enrollMySub(String id) {
        String enrollSub[] = new String[4];
        String subject;
        String subs = "";
        int cnt = 0;

        //종료방식은 보류
        System.out.println("------------수강과목 등록(1 입력 시 종료)------------");
        while(true) {
            System.out.println("수강신청 할 과목코드 : ");
            subject = sc.next();
            if(subject.equals("1")) break;
            cnt++;
            subs += subject + Protocol.splitter;
        }

        enrollSub[0] = Protocol.CS_REQ_MYSUBJECT_ENROLL;
        enrollSub[1] = id;
        //과목
        enrollSub[2] = subs;
        enrollSub[3] = Integer.toString(cnt); //수강등록한 과목 갯수

        return enrollSub;
    }

    //교수
    //담당 교과목 강의계획서 등록(보류)

    //관리자
    //사용자 계정 등록
    public String[] enrollMember() {
        String member[] = new String[4];

        System.out.println("------------사용자 계정 등록------------");
        System.out.println("id : ");
        String enrollId = sc.next();
        System.out.println("password : ");
        String enrollPw = sc.next();
        System.out.println("category : ");
        String enrollCt = sc.next();

        member[0] = Protocol.CS_REQ_MEMBER_ENROLL;
        member[1] = enrollId;
        member[2] = enrollPw;
        member[3] = enrollCt;

        return member;
    }

    //교과목 등록
    public String[] enrollSubject() {
        String subject[] = new String[4];

        System.out.println("------------교과목 등록------------");
        System.out.println("과목코드 : ");
        String subCode = sc.next();
        System.out.println("과목명 : ");
        String subName = sc.next();
        System.out.println("수강학년 : ");
        String subGrade = sc.next();

        subject[0] = Protocol.CS_REQ_SUBJECT_ENROLL;
        subject[1] = subCode;
        subject[2] = subName;
        subject[3] = subGrade;

        return subject;
    }

    //개설교과목 등록
    public String[] enrollLecture() {
        String lecture[] = new String[8];

        System.out.println("------------개설교과목 등록------------");
        System.out.println("교과목 id : ");
        String lectureId = sc.next();
        System.out.println("교수명 : ");
        String prof = sc.next();
        System.out.println("강의계획서"); //보류
        String syllabus = sc.next();
        System.out.println("강의시간 : ");
        String time = sc.next();
        System.out.println("최대 학생 수 : ");
        String maxStd = sc.next();
        System.out.println("현재 학생 수 : ");
        String currentStd = sc.next();
        System.out.println("강의실 : ");
        String classroom = sc.next();

        lecture[0] = Protocol.CS_REQ_LECTURE_ENROLL;
        lecture[1] = lectureId;
        lecture[2] = prof;
        lecture[3] = syllabus; //보류
        lecture[4] = time;
        lecture[5] = maxStd;
        lecture[6] = currentStd;
        lecture[7] = classroom;

        return lecture;
    }

    //강의계획서 입력기간 등록
    public String[] enrollSyllabusPeriod() {
        String sPeriod[] = new String[3];

        System.out.println("------------강의계획서 입력기간 등록------------");
        //Date로 받을지 String으로 받을지
        System.out.println("강의계획서 입력 시작날짜 : ");
        String startDay = sc.next();
        System.out.println("강의계획서 입력 종료날짜 : ");
        String endDay = sc.next();

        sPeriod[0] = Protocol.CS_REQ_SYLLABUSPERIOD_ENROLL;
        sPeriod[1] = startDay;
        sPeriod[2] = endDay;

        return sPeriod;
    }

    //수강신청기간 등록
    public String[] enrollRegistPeriod() {
        String rPeriod[] = new String[3];

        System.out.println("------------수강신청기간 입력기간 등록------------");
        //Date로 받을지 String으로 받을지
        System.out.println("수강신청 시작날짜 : ");
        String startDay = sc.next();
        System.out.println("수강신청 종료날짜 : ");
        String endDay = sc.next();

        rPeriod[0] = Protocol.CS_REQ_REGISTRATIONPERIOD_ENROLL;
        rPeriod[1] = startDay;
        rPeriod[2] = endDay;

        return rPeriod;
    }
}

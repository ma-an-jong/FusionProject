package Client;

import java.io.*;
import java.util.Scanner;

//등록 요청
public class EnrollClient {
    Scanner sc = new Scanner(System.in);

    //학생
    //수강신청 요청
    public String[] registration(String grade) {
        String regist[] = new String[2];

        regist[0] = Protocol.CS_REQ_REGISTRATION;
        //해당 학년 수강신청 기간인지 확인
        regist[1] = grade;

        return regist;
    }

    //수강과목 등록
    public String[] enrollMySub(String id) {
        String enrollSub[] = new String[3];
        String subject;

        System.out.println("================수강과목 등록================");
        System.out.print("수강신청 할 과목코드 : ");
        subject = sc.next();

        enrollSub[0] = Protocol.CS_REQ_MYSUBJECT_ENROLL;
        enrollSub[1] = id;
        enrollSub[2] = subject;

        return enrollSub;
    }

    //교수
    //강의계획서 등록 요청 (기간 확인)
    public String[] syllabusPeriodAccess() {
        String enrollSyllabus[] = new String[1];
        enrollSyllabus[0] = Protocol.CS_REQ_SYLLABUS_ENROLL;

        return enrollSyllabus;
    }

    //담당 교과목 강의계획서 등록 (과목코드 + 내용)
    public String[] enrollSyllabus() {
        String enrollSylla[] = new String[3];

        System.out.print("강의계획서 등록할 과목코드 : ");
        String enrollSub = sc.next();
        System.out.print("강의계획서 등록 내용 : ");
        String contents = sc.next();

        enrollSylla[0] = Protocol.CS_REQ_SYLLABUS_FILE;
        enrollSylla[1] = enrollSub;
        enrollSylla[2] = contents;

        return enrollSylla;
    }

    //관리자
    //학생 계정 등록
    //학번, password, 이름, 학과, 학년, 전화번호
    public String[] enrollStudent() {
        String std[] = new String[7];

        System.out.println("------------학생 계정 등록------------");
        System.out.print("학번 : ");
        String stdNum = sc.next();
        System.out.print("이름 : ");
        String name = sc.next();
        System.out.print("password : ");
        String password = sc.next();
        System.out.print("학과 : ");
        String department = sc.next();
        System.out.print("전화번호 : ");
        String phoneNum = sc.next();
        System.out.print("학년 : ");
        String grade = sc.next();

        std[0] = Protocol.CS_REQ_STUDENT_ENROLL;
        std[1] = stdNum;
        std[2] = name;
        std[3] = password;
        std[4] = department;
        std[5] = phoneNum;
        std[6] = grade;

        return std;
    }

    //교수 계정 등록
    public String[] enrollProfessor() {
        String prof[] = new String[6];

        System.out.println("------------교수 계정 등록------------");
        System.out.print("교번 : ");
        String stdNum = sc.next();
        System.out.print("이름 : ");
        String name = sc.next();
        System.out.print("password : ");
        String password = sc.next();
        System.out.print("학과 : ");
        String department = sc.next();
        System.out.print("전화번호 : ");
        String phoneNum = sc.next();

        prof[0] = Protocol.CS_REQ_PROFESSOR_ENROLL;
        prof[1] = stdNum;
        prof[2] = name;
        prof[3] = password;
        prof[4] = department;
        prof[5] = phoneNum;

        return prof;
    }

    //교과목 등록
    //과목코드, 과목명, 학년
    public String[] enrollSubject(String category) {
        String subject[] = new String[4];

        System.out.println("------------교과목 등록------------");
        System.out.print("과목코드 : ");
        String subCode = sc.next();
        System.out.print("과목명 : ");
        String subName = sc.next();
        System.out.print("수강학년 : ");
        String subGrade = sc.next();

        subject[0] = Protocol.CS_REQ_SUBJECT_ENROLL;
        subject[1] = subCode;
        subject[2] = subName;
        subject[3] = subGrade;

        return subject;
    }

    //개설교과목 등록
    //월12, 수34 있으면 월12/수34 이렇게 구분(/)
    public String[] enrollLecture() {
        String lecture[] = new String[6];

        System.out.println("------------개설교과목 등록------------");
        System.out.print("과목코드 : ");
        String lectureId = sc.next();
        System.out.print("교번 : ");
        String prof = sc.next();
        System.out.print("강의시간(요일 나눠지는 경우 '/'로 구분) : ");
        String time = sc.next();
        System.out.print("최대 학생 수 : ");
        String maxStd = sc.next();
        System.out.print("강의실 : ");
        String classroom = sc.next();

        lecture[0] = Protocol.CS_REQ_LECTURE_ENROLL;
        lecture[1] = lectureId;
        lecture[2] = prof;
        lecture[3] = time;
        lecture[4] = maxStd;
        lecture[5] = classroom;

        return lecture;
    }

    //강의계획서 입력기간 등록
    public String[] enrollSyllabusPeriod() {
        String sPeriod[] = new String[3];

        System.out.println("------------강의계획서 입력기간 등록------------");
        System.out.print("강의계획서 입력 시작날짜(yyyy-MM-dd) : ");
        String startDay = sc.next();
        System.out.print("강의계획서 입력 종료날짜(yyyy-MM-dd) : ");
        String endDay = sc.next();

        sPeriod[0] = Protocol.CS_REQ_SYLLABUSPERIOD_ENROLL;
        sPeriod[1] = startDay;
        sPeriod[2] = endDay;

        return sPeriod;
    }

    //수강신청기간 등록
    public String[] enrollRegistPeriod() {
        String rPeriod[] = new String[4];

        System.out.println("------------수강신청기간 입력기간 등록------------");
        System.out.print("학년 : ");
        String grade = sc.next();
        System.out.print("수강신청 시작날짜(yyyy-MM-dd) : ");
        String startDay = sc.next();
        System.out.print("수강신청 종료날짜(yyyy-MM-dd) : ");
        String endDay = sc.next();

        rPeriod[0] = Protocol.CS_REQ_REGISTRATIONPERIOD_ENROLL;
        rPeriod[1] = grade;
        rPeriod[2] = startDay;
        rPeriod[3] = endDay;

        return rPeriod;
    }
}

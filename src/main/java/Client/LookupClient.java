package Client;

import java.util.Scanner;

//조회 요청
public class LookupClient {
    Scanner sc = new Scanner(System.in);

    //학생 개인정보 조회
    public String[] stdMyInfo(String id) {
        String personalArr[] = new String[2];
        personalArr[0] = Protocol.CS_REQ_STUDENT_PERSONALINFO_VIEW;
        personalArr[1] = id;

        return personalArr;
    }

    //교수 개인정보 조회
    public String[] profMyInfo(String id) {
        String personalArr[] = new String[2];
        personalArr[0] = Protocol.CS_REQ_PROFESSOR_PERSONALINFO_VIEW;
        personalArr[1] = id;

        return personalArr;
    }

    //전체 교과목 조회
    public String[] SubjectLoopUp() {
        String subject[] = new String[1];
        subject[0] = Protocol.CS_REQ_SUBJECT_VIEW;

        return subject;
    }

    //개설 교과목 목록 조회
    public String[] allLectureLookUp() {
        String lecture[] = new String[1];
        lecture[0] = Protocol.CS_REQ_LECTURE_VIEW;

        return lecture;
    }

    //강의계획서 조회(과목코드)
    public String[] lookupSyllabus() {
        String lookSyllabus[] = new String[2];

        System.out.print("강의계획서 조회할 과목코드 : ");
        String lookSub = sc.next();

        lookSyllabus[0] = Protocol.CS_REQ_SYLLABUS_VIEW;
        lookSyllabus[1] = lookSub;

        return lookSyllabus;
    }

    //관리자
    //교수 정보 조회(선택조회)
    public String[] professorLookUp() {
        System.out.print("조회할 교수 교번 : ");
        String profNum = sc.next(); //교번으로 조회

        String prof[] = new String[2];
        prof[0] = Protocol.CS_REQ_PROFESSOR_VIEW;
        prof[1] = profNum;

        return prof;
    }

    //학생 정보 조회(선택조회)
    public String[] studentLookUp() {
        System.out.print("조회할 학생 학번 : ");
        String stdNum = sc.next(); //학번으로 조회

        String std[] = new String[2];
        std[0] = Protocol.CS_REQ_STUDENT_VIEW;
        std[1] = stdNum;

        return std;
    }

    //교수,학생 정보 조회(전체조회)
    public String[] allMemberLookUp() {
        String allMember[] = new String[1];
        allMember[0] = Protocol.CS_REQ_ALLMEMBER_VIEW;

        return allMember;
    }

    //개설교과목 정보 조회
    public String[] lectureLookup() {
        String lec[] = new String[1];
        lec[0] = Protocol.CS_REQ_LECTUREINFO_VIEW;

        return lec;
    }

    //학생
    //수강 과목 조회
    public String[] mySubjectLookUp(String id) {
        String mySub[] = new String[2];
        mySub[0] = Protocol.CS_REQ_MYSUBJECT_VIEW;
        mySub[1] = id;

        return mySub;
    }

    //본인 시간표 조회
    public String[] timeTableLookUp(String id) {
        String timetable[] = new String[2];
        timetable[0] = Protocol.CS_REQ_TIMETABLE_VIEW;
        timetable[1] = id;

        return timetable;
    }

    //교수
    //담당교과목 강의계획서 조회
    public String[] lookMySyllabus() {
        String mySyllabus[] = new String[3];
        mySyllabus[0] = Protocol.CS_REQ_MYSYLLABUS_VIEW;

        System.out.print("강의계획서 조회할 과목코드 : ");
        mySyllabus[1] = sc.next();

        return mySyllabus;
    }

    //담당교과목 목록 조회
    public String[] teachingSubject(String id) {
        String sub[] = new String[2];
        sub[0] = Protocol.CS_REQ_TEACHING_VIEW;
        sub[1] = id;

        return sub;
    }

    //담당교과목 시간표 조회
    public String[] teachingTable(String id) {
        String teachTable[] = new String[2];
        teachTable[0] = Protocol.CS_REQ_TEACHINGTABLE_VIEW;
        teachTable[1] = id;

        return teachTable;
    }

    //담당교과목 수강학생 조회
    public String[] myStudentLookUp(int page, String sub) {
        String myStudent[] = new String[3];
        myStudent[0] = Protocol.CS_REQ_MYSTUDENT_VIEW;
        myStudent[1] = sub;
        myStudent[2] = Integer.toString(page);

        return myStudent;
    }
}

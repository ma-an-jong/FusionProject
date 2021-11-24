package Client;

import java.util.Scanner;

//조회 요청
public class LookupClient {
    Scanner sc = new Scanner(System.in);

    //개인정보 조회
    public String[] personalInfo(String id) {
        String personalArr[] = new String[2];
        personalArr[0] = Protocol.CS_REQ_PERSONALINFO_VIEW;
        personalArr[1] = id;

        return personalArr;
    }

    //전체 교과목 조회
    public String[] SubjectLoopUp() {
        String subject[] = new String[1];
        subject[0] = Protocol.CS_REQ_SUBJECT_VIEW;

        return subject;
    }

    //개설 과목 조회
    public String[] lectureLookUp() {
        String lecture[] = new String[1];
        lecture[0] = Protocol.CS_REQ_LECTURE_VIEW;

        return lecture;
    }

    //강의계획서 조회(보류)

    //관리자
    //교수,학생 정보 조회(선택조회)
    public String[] memberLookUp() {
        //이름으로 조회한다는 가정
        System.out.println("조회할 회원의 이름 : ");
        String name = sc.next();

        String member[] = new String[2];
        member[0] = Protocol.CS_REQ_MEMBER_VIEW;
        member[1] = name;

        return member;
    }

    //교수,학생 정보 조회(전체조회)
    public String[] allMemberLookUp() {
        String allMember[] = new String[1];
        allMember[0] = Protocol.CS_REQ_ALLMEMBER_VIEW;

        return allMember;
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
    //담당교과목 강의계획서 조회(보류)

    //담당교과목 시간표 조회
    public String[] teachingTable(String id) {
        String teachTable[] = new String[2];
        teachTable[0] = Protocol.CS_REQ_TEACHINGTABLE_VIEW;
        teachTable[1] = id;

        return teachTable;
    }

    //담당교과목 수강학생 조회
    public String[] myStudentLookUp(String id) {
        System.out.println("과목명 : ");
        String subject = sc.next();

        String myStudent[] = new String[3];
        myStudent[0] = Protocol.CS_REQ_MYSTUDENT_VIEW;
        myStudent[1] = id;
        myStudent[2] = subject;

        return myStudent;
    }
}

package Server;

public class TimeTableInfo implements Comparable<TimeTableInfo> {

    //각 요일에 대한 비교를 하기위해 값 설정
    private static enum Day {
        월(1),
        화(2),
        수(3),
        목(4),
        금(5);
        int value;
        private Day(int value){
            this.value = value;
        }
        public int getValue(){
            return value;
        }
    }

    String name;
    String lectureTime;
    String classRoom;


    public TimeTableInfo(String name,String lectureTime,String classRoom){
        this.name = name;
        this.lectureTime = lectureTime;
        this.classRoom = classRoom;
    }

    // 요일별로 정렬하기위한 compareTo메소드 오버라이딩
    @Override
    public int compareTo(TimeTableInfo o) {
        if(this.lectureTime.charAt(0) == o.lectureTime.charAt(0))
        {
            String a = this.lectureTime.substring(1);
            String b = o.lectureTime.substring(1);

            return a.compareTo(b);
        }
        else
        {
            return Day.valueOf(String.valueOf(this.lectureTime.charAt(0))).getValue() - Day.valueOf(String.valueOf(o.lectureTime.charAt(0))).getValue();
        }

    }

    public String toString(){
        return "과목명: " + name +"강의 시간: " + lectureTime + "강의실: " + classRoom+" ";
    }

}


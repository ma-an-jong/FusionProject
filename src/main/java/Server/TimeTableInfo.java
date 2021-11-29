package Server;

public class TimeTableInfo implements Comparable<TimeTableInfo> {

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


    @Override
    public int compareTo(TimeTableInfo o) {
        if(this.lectureTime.charAt(0) == o.lectureTime.charAt(0))
        {
            String a = this.lectureTime.substring(1);
            String b = o.lectureTime.substring(1);

            return a.hashCode() - b.hashCode();
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


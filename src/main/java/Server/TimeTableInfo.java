package Server;

public class TimeTableInfo implements Comparable<TimeTableInfo> {

    private static enum Day {
        월(5),
        화(4),
        수(3),
        목(2),
        금(1);
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
        return name +"\n" + lectureTime +"\n" + classRoom+"\n";
    }

}


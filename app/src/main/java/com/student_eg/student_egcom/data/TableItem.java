package com.student_eg.student_egcom.data;

/**
 * Created by mostafa on 25/02/16.
 */
public class TableItem {
    private int id;
    private String courseName, instructor, hall_or_place, duration, day_no;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getHall_or_place() {
        return hall_or_place;
    }

    public void setHall_or_place(String hall_or_place) {
        this.hall_or_place = hall_or_place;
    }

    public String getDuration() {
        switch (duration){
            case "1":
                duration = "الفتره ساعه";
                break;
            case "2":
                duration = "الفتره ساعتين";
                break;
            case "3":
                duration = "الفتره ثلاث ساعات";
                break;
            case "4":
                duration = "الفتره اربع ساعات";
                break;

        }
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDay_no() {
        switch (day_no){
            case "1":
                day_no = "الاحد";
            break;
            case "2":
                day_no = "الاثنين";
                break;
            case "3":
                day_no = "الثلاثاء";
                break;
            case "4":
                day_no = "الاربعاء";
                break;
            case "5":
                day_no = "الخميس";
                break;
            case "6":
                day_no = "الجمعه";
                break;
            case "7":
                day_no = "السبت";
                break;
            default:
                break;
        }
        return day_no;
    }

    public void setDay_no(String day_no) {
        this.day_no = day_no;
    }
}

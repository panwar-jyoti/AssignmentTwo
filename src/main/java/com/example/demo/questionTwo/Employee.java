package com.example.demo.questionTwo;

package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Time;
import java.time.LocalTime;
//import java.util.Date;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Employee {
    private java.sql.Date date;
    private java.sql.Date month;
    private String Team;
    private String PanelName;
    private String Round;
    private String skill;
    private Time time;
    private String CurrentLoc;
    private String PreferredLoc;
    private String CandidateName;
}
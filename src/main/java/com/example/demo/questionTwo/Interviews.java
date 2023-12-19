package com.example.demo.questionTwo;


import java.sql.Date;
import java.sql.Time;

public class Interviews {
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

    public Interviews(Date date, Date month, String team, String panelName, String round, String skill, Time time, String currentLoc, String preferredLoc, String candidateName) {
        this.date = date;
        this.month = month;
        Team = team;
        PanelName = panelName;
        Round = round;
        this.skill = skill;
        this.time = time;
        CurrentLoc = currentLoc;
        PreferredLoc = preferredLoc;
        CandidateName = candidateName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public String getTeam() {
        return Team;
    }

    public void setTeam(String team) {
        Team = team;
    }

    public String getPanelName() {
        return PanelName;
    }

    public void setPanelName(String panelName) {
        PanelName = panelName;
    }

    public String getRound() {
        return Round;
    }

    public void setRound(String round) {
        Round = round;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getCurrentLoc() {
        return CurrentLoc;
    }

    public void setCurrentLoc(String currentLoc) {
        CurrentLoc = currentLoc;
    }

    public String getPreferredLoc() {
        return PreferredLoc;
    }

    public void setPreferredLoc(String preferredLoc) {
        PreferredLoc = preferredLoc;
    }

    public String getCandidateName() {
        return CandidateName;
    }

    public void setCandidateName(String candidateName) {
        CandidateName = candidateName;
    }

    @Override
    public String toString() {
        return "Interviews{" +
                "date=" + date +
                ", month=" + month +
                ", Team='" + Team + '\'' +
                ", PanelName='" + PanelName + '\'' +
                ", Round='" + Round + '\'' +
                ", skill='" + skill + '\'' +
                ", time=" + time +
                ", CurrentLoc='" + CurrentLoc + '\'' +
                ", PreferredLoc='" + PreferredLoc + '\'' +
                ", CandidateName='" + CandidateName + '\'' +
                '}';
    }
}
package com.question;

/**
 * Created by eyup.altindal on 11.3.2015.
 */
public class Question {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQ_level() {
        return q_level;
    }

    public void setQ_level(int q_level) {
        this.q_level = q_level;

        switch (q_level) {
            case 1: {
                this.q_time = 60;
                this.q_xp = 3;
                break;
            }
            case 2: {
                this.q_time = 80;
                this.q_xp = 4;
                break;
            }
            case 3: {
                this.q_time = 100;
                this.q_xp = 5;
                break;
            }
            case 4: {
                this.q_time = 120;
                this.q_xp = 6;
                break;
            }
            case 5: {
                this.q_time = 140;
                this.q_xp = 7;
                break;
            }
            case 6: {
                this.q_time = 160;
                this.q_xp = 8;
                break;
            }
            default: {
                this.q_time = 0;
                this.q_xp = 0;
                break;
            }

        }

    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public int getQ_time() {
        return q_time;
    }

    public int getQ_xp() {
        return q_xp;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


    int id;
    int q_level;
    String paragraph = "";
    String question = "";
    String answer = "";
    String a = "";
    String b = "";
    String c = "";
    String d = "";
    String e = "";
    int q_time = 0;
    int q_xp = 0;
    String response = "";

}

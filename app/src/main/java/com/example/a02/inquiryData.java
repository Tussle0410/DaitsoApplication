package com.example.a02;

import java.io.Serializable;

public class inquiryData implements Serializable {
    private String inquiry_title;
    private String inquiry_comment;
    private String inquiry_no;
    private String answerCheck;

    public String getInquiry_title() {
        return inquiry_title;
    }

    public void setInquiry_title(String inquiry_title) {
        this.inquiry_title = inquiry_title;
    }

    public String getInquiry_comment() {
        return inquiry_comment;
    }

    public void setInquiry_comment(String inquiry_comment) {
        this.inquiry_comment = inquiry_comment;
    }

    public String getInquiry_no() {
        return inquiry_no;
    }

    public void setInquiry_no(String inquiry_no) {
        this.inquiry_no = inquiry_no;
    }

    public String getAnswerCheck() {
        return answerCheck;
    }

    public void setAnswerCheck(String answerCheck) {
        this.answerCheck = answerCheck;
    }


}

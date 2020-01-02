package com.yan.login.util;

import java.io.Serializable;

public class SpinnerOption implements Serializable {

    private int value ;
    private String text ;

    public SpinnerOption() {
    }

    public SpinnerOption(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return text;
    }
}

package io.github.t3r1jj.develog.model.domain;

import lombok.Getter;

public class Editor {
    @Getter
    private String input = "";
    @Getter
    private String output = "";

    public Editor(String input) {
        setInput(input);
    }

    public void setInput(String input) {
        this.input = input;
        this.output = input;
    }

    @Override
    public String toString() {
        return "Editor{" +
                ", input='" + input + '\'' +
                ", output='" + output + '\'' +
                '}';
    }
}

package io.github.t3r1jj.develog.model.domain;

import io.github.t3r1jj.develog.model.data.Note;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EditorTest {

    @Test
    void parseMarkdown_H1() {
        Editor editor = new Editor("# test");
        String output = editor.getOutput();
        System.out.println(output);
        assertTrue(output.contains("<h1>"));
    }

    @Test
    void parseMarkdown_ol_li() {
        Editor editor = new Editor("1. a\n2. b\n3. c");
        String output = editor.getOutput();
        System.out.println(output);
        assertTrue(output.contains("<li>"));
        assertTrue(output.contains("<ol>"));
    }

    @Test
    void parseUML() {
        Editor editor = new Editor("@startuml\n" +
                "Class01 <|-- Class02\n" +
                "Class03 *-- Class04\n" +
                "Class05 o-- Class06\n" +
                "Class07 .. Class08\n" +
                "Class09 -- Class10\n" +
                "@enduml");
        String output = editor.getOutput();
        System.out.println(output);
        assertTrue(output.contains("<svg"));
    }

    @Test
    void parseMarkdownUML() {
        Editor editor = new Editor("# HEADER" +
                "@startuml\n" +
                "Class01 <|-- Class02\n" +
                "Class03 *-- Class04\n" +
                "Class05 o-- Class06\n" +
                "Class07 .. Class08\n" +
                "Class09 -- Class10\n" +
                "@enduml " +
                "1. a\n2. b\n3. c"
        );
        String output = editor.getOutput();
        System.out.println(output);
        assertTrue(output.contains("<h1>"), "Contains header from markdown");
        assertTrue(output.contains("<svg"), "Generated UML image");
        assertTrue(output.contains("<li>"), "Contains list items from markdown");
    }

    @Test
    void multiNoteEditor() {
        Editor editor = spy(new Editor());
        editor.setInput(Arrays.asList(Note.builder().body("abc").build(), Note.builder().body("defg").build()));
        verify(editor, times(2)).parse(anyString());
        System.out.println(editor.getOutput());
        assertTrue(editor.getInput().contains("---"));
    }

    @Test
    void isEmpty() {
        assertTrue(new Editor("").isEmpty());
        assertFalse(new Editor("a").isEmpty());
    }

    @Test
    void handleIOException() {
        Editor editor = spy(new Editor());
        String output = editor.handleIOException(new IOException("TEST: Missing system component?"));
        assertTrue(output.contains("ERROR"), "Contains error instead of UML");
    }
}
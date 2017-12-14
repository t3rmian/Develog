package io.github.t3r1jj.develog.model.domain;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;
import io.github.t3r1jj.develog.model.data.Note;
import lombok.Getter;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.springframework.lang.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;


public class Editor {
    private final Parser parser;
    private final HtmlRenderer renderer;
    @Getter
    private String input = "";
    @Getter
    private String output = "";

    public Editor() {
        MutableDataSet options = new MutableDataSet();
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }

    public Editor(@NonNull String input) {
        this();
        setInput(input);
    }

    public void setInput(List<Note> notes) {
        notes.forEach(n -> {
            String inputPart = "---\n\n#### " + n.getDate().toString() + "\n\n---\n\n" + n.getBody();
            this.input += inputPart;
            this.output += parse(inputPart);
        });
    }

    public void setInput(String input) {
        this.input = input;
        this.output = parse(input);
    }

    String parse(String input) {
        return parseMarkdown(parsePlantUML(input));
    }

    private String parseMarkdown(String input) {
        Node document = parser.parse(input);
        return renderer.render(document);
    }

    private String parsePlantUML(String input) {
        String output = input;
        List<String> groups = PlantUmlMatcher.findPlantUMLSubstrings(input);
        for (String group : groups) {
            SourceStringReader reader = new SourceStringReader(group);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            String outputPart;
            try {
                String desc = reader.generateImage(outputStream, new FileFormatOption(FileFormat.SVG));
                outputStream.close();
                outputPart = new String(outputStream.toByteArray(), Charset.forName("UTF-8"));
            } catch (IOException ioe) {
                ioe.printStackTrace();
                outputPart = "ERROR: " + ioe.getLocalizedMessage();
            }
            output = output.replace(group, outputPart);
        }
        return output;
    }

    public boolean isEmpty() {
        return Objects.equals(input, "") && Objects.equals(output, "");
    }

    @Override
    public String toString() {
        return "Editor{" +
                ", input='" + input + '\'' +
                ", output='" + output + '\'' +
                '}';
    }
}

package uniquiz;

import uniquiz.model.CategoriesLinesAccumulator;
import uniquiz.model.Question;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by fazzou on 04.03.16.
 */
public class UniquizQuestionsLoader {

    public Map<String, List<Question>> loadQuestions() throws IOException {
        Map<String, List<Question>> quizzes = new HashMap<>();
        String baseUrl = "http://defozo.cba.pl/";
        List<String> quizzesToLoad = new ArrayList<>();
        quizzesToLoad.add("biology");
        for (String path : quizzesToLoad) {
            try (InputStream is = new URL(baseUrl + path + ".txt").openConnection().getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                 Stream<String> stream = reader.lines()) {
                List<CategoriesLinesAccumulator> list = new ArrayList<>();
                stream.forEach((el) -> splitIntoQuestions(list, el));
                List<Question> biologyQuestions = list.stream()
                        .map(Question::new)
                        .collect(Collectors.toList());
                quizzes.put(path, biologyQuestions);
            }
        }
        return quizzes;
    }

    protected void splitIntoQuestions(List<CategoriesLinesAccumulator> acc, String newElement) {
        if (isJustNewLine(newElement)) {
            acc.stream()
                    .filter((cat) -> !cat.isClosed())
                    .findFirst()
                    .ifPresent(CategoriesLinesAccumulator::close);
            acc.add(new CategoriesLinesAccumulator());
        } else if (isEndLine(newElement)) {
            acc.stream()
                    .filter((cat) -> !cat.isClosed())
                    .findFirst()
                    .ifPresent(acc::remove);
        } else {
            acc.stream()
                    .filter((cat) -> !cat.isClosed())
                    .findFirst()
                    .ifPresent((e) -> e.push(newElement));
        }
    }

    protected Boolean isJustNewLine(String line) {
        return line.equals("");
    }

    protected Boolean isEndLine(String line) {
        return line.equals(">end");
    }
}

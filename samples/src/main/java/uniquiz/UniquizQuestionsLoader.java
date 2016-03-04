package uniquiz;

import uniquiz.model.CategoriesLinesAccumulator;
import uniquiz.model.Question;

import java.io.IOException;
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
        String fileName = "biology.txt";
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            List<CategoriesLinesAccumulator> list = new ArrayList<>();
            stream.forEach((el) -> splitIntoQuestions(list, el));
            List<Question> biologyQuestions = list.stream()
                    .map(Question::new)
                    .collect(Collectors.toList());
            quizzes.put("biology", biologyQuestions);
        }
        return quizzes;
    }

    private void splitIntoQuestions(List<CategoriesLinesAccumulator> acc, String newElement) {
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
                    .get()
                    .push(newElement);
        }
    }

    private Boolean isJustNewLine(String line) {
        return line.equals("");
    }

    private Boolean isEndLine(String line) {
        return line.equals(">end");
    }
}

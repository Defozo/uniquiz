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
public class ExampleQuestionsLoader extends UniquizQuestionsLoader {

    @Override
    public Map<String, List<Question>> loadQuestions() throws IOException {
        Map<String, List<Question>> quizzes = new HashMap<>();
        List<String> lines = new ArrayList<>();
        lines.add("");
        lines.add("What is the name of study of plants?");
        lines.add(">a.Botany");
        lines.add("b.Limnology");
        lines.add("c.Biophysics");
        lines.add("d.Mammalogy");
        lines.add("");
        lines.add("What is the name of study of fish?");
        lines.add("a.Mammalogy");
        lines.add("b.Ornithology");
        lines.add("c.Phycology");
        lines.add(">d.Ichtyology");
        lines.add("");
        lines.add("What is the name of study of viruses?");
        lines.add("a.Phycology");
        lines.add("b.Limnology");
        lines.add(">c.Virology");
        lines.add("d.Mammalogy");
        lines.add("");
        lines.add("What is the namy of study of birds?");
        lines.add("a.Virology");
        lines.add("b.Mammalogy");
        lines.add(">c.Ornithology");
        lines.add("d.Botany");
        lines.add("");
        lines.add(">end");
        List<CategoriesLinesAccumulator> list = new ArrayList<>();
        lines.stream().forEach((el) -> splitIntoQuestions(list, el));
        List<Question> biologyQuestions = list.stream()
                .map(Question::new)
                .collect(Collectors.toList());
        quizzes.put("biology", biologyQuestions);
        return quizzes;
    }
}

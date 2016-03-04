package uniquiz;

import uniquiz.model.Question;

import java.util.Collections;
import java.util.List;

/**
 * Created by fazzou on 04.03.16.
 */
public class QuizController {
    private List<Question> questions;

    public QuizController(List<Question> questions) {
        this.questions = questions;
    }

    public Question getRandomQuestion() {
        Collections.shuffle(questions);
        return questions.get(0);
    }
}

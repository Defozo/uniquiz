package uniquiz;

import uniquiz.model.Question;

import java.util.ArrayList;
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

    public int size() {
        return questions.size();
    }

    public Question forId(int id) {
        if (id < questions.size()) {
            return this.questions.get(id);
        } else {
            return null;
        }
    }

    public Question getRandomQuestion() {
        List<Question> toShuffle = new ArrayList<>(questions);
        Collections.shuffle(toShuffle);

        Question res = toShuffle.get(0);
        res.setId(questions.indexOf(res));
        return res;
    }
}

package uniquiz.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fazzou on 04.03.16.
 */
public class Question {
    private String question;
    private Map<String, String> answers = new HashMap<>();
    private List<String> correct = new ArrayList<>();
    private int id;

    public Question(CategoriesLinesAccumulator cla) {
        List<String> tmpList = cla.getLines();
        this.question = tmpList.get(0);
        tmpList.remove(0);
        tmpList.stream()
                .forEach(this::addAnswerFor);

    }

    private void addAnswerFor(String answerLine) {
        String[] arr = answerLine.split("\\.");
        if (arr[0].startsWith(">")) {
            String withoutMark = arr[0].replace(">", "");
            answers.put(withoutMark, arr[1]);
            correct.add(withoutMark);
        } else {
            answers.put(arr[0], arr[1]);
        }
    }

    public String getQuestion() {
        return question;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public boolean isCorrect(String response) {
        return correct.contains(response);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", answers=" + answers +
                ", correct=" + correct +
                '}';
    }
}

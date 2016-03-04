/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package uniquiz;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import uniquiz.model.Question;

/**
 * This sample shows how to create a simple speechlet for handling intent requests and managing
 * session interactions.
 */
public class UniquizSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(UniquizSpeechlet.class);

    private static final String QUIZ_NAME = "QUIZ_NAME";
    private static final String SCORE = "SCORE";
    private static final String ALREADY_ASKED_COUNT = "ALREADY_ASKED_COUNT";
    private static final String ASKED_QUESTION_ID = "ASKED_QUESTION_ID";
    private static final String RESPONSE_SLOT = "Response";
    private static final String CATEGORY_SLOT = "Category";

    private Map<String, List<Question>> quizzes = null;

    public Map<String, List<Question>> getQuizzes() {
        return quizzes;
    }

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        initializeQuizzes();
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        initializeQuizzes();
        if (quizzes != null) {
            return getWelcomeResponse();
        } else {
            return getSpeechletResponse("Error during loading questions", null, false);
        }
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        // Get intent from the request object.
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        if ("StartQuizIntent".equals(intentName)) {
            session.setAttribute(QUIZ_NAME, "");
            session.setAttribute(SCORE, 0);
            session.setAttribute(ALREADY_ASKED_COUNT, 0);
            session.setAttribute(ASKED_QUESTION_ID, -1);
            initializeQuizzes();
            return startQuiz(intent, session);
        } else if ("EndQuizIntent".equals(intentName)) {
            session.setAttribute(QUIZ_NAME, "");
            session.setAttribute(ASKED_QUESTION_ID, -1);
            initializeQuizzes();
            return endQuiz(intent, session);
        } else if ("ResponseIntent".equals(intentName)) {
            return respondToQuestion(intent, session);
        } else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any cleanup logic goes here
    }

    private void initializeQuizzes() {
        UniquizQuestionsLoader uql = new ExampleQuestionsLoader();
        try {
            quizzes = uql.loadQuestions();
        } catch (IOException ignored) {
        }
    }

    private SpeechletResponse startQuiz(final Intent intent, final Session session) {
        session.setAttribute(QUIZ_NAME, "biology");
        return askQuestion(intent, session, null);
    }

    private SpeechletResponse askQuestion(final Intent intent, final Session session, String additionalText) {
        String name = (String) session.getAttribute(QUIZ_NAME);
        QuizController qc = new QuizController(quizzes.get(name));
        Question askedQuestion = qc.getRandomQuestion();
        session.setAttribute(ASKED_QUESTION_ID, askedQuestion.getId());
        int asked = (int) session.getAttribute(ALREADY_ASKED_COUNT);
        String speech = asked + ". " + askedQuestion.getQuestion() + ",";
        for (String key: askedQuestion.getAnswers().keySet()) {
            speech = speech + key + " - " + askedQuestion.getAnswers().get(key) + ",";
        }
        session.setAttribute(ALREADY_ASKED_COUNT, asked + 1);

        String extendedResponse;
        if (additionalText != null) {
            extendedResponse = additionalText + speech;
        } else {
            extendedResponse = speech;
        }

        return getSpeechletResponse(extendedResponse, speech, true);
    }

    private SpeechletResponse endQuiz(final Intent intent, final Session session) {
        String speechText;

        int score = (int) session.getAttribute(SCORE);
        int asked = (int) session.getAttribute(ALREADY_ASKED_COUNT);

        if (asked != 0) {
            speechText = String.format("Your score is %s per %s", score, asked);
        } else {
            speechText = "Ok, ending";
        }
        session.setAttribute(SCORE, 0);
        session.setAttribute(ALREADY_ASKED_COUNT, 0);
        session.setAttribute(ASKED_QUESTION_ID, -1);
        return getSpeechletResponse(speechText, speechText, false);
    }

    private SpeechletResponse respondToQuestion(final Intent intent, final Session session) {
        int score = (int) session.getAttribute(SCORE);
        String speechText;

        String category = (String) session.getAttribute(QUIZ_NAME);
        int askedId = (int) session.getAttribute(ASKED_QUESTION_ID);
        QuizController qc = new QuizController(quizzes.get(category));
        Question askedQuestion = qc.forId(askedId);

        if (askedQuestion != null) {
            String response = intent.getSlot(RESPONSE_SLOT).getValue();
            boolean valid = askedQuestion.isCorrect(response);
            if (valid) {
                speechText = "Right, ";
                session.setAttribute(SCORE, score + 1);
            } else {
                speechText = "Sorry, bad answer, you said " + response + " and correct one is " + askedQuestion.correct() + ", ";
            }
        } else {
            speechText = "Sorry, no question was asked, ";
        }
        session.setAttribute(ASKED_QUESTION_ID, -1);

        return askQuestion(intent, session, speechText);
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual welcome message
     */
    private SpeechletResponse getWelcomeResponse() {
        // Create the welcome message.
        String speechText =
                "Welcome to universal quiz";
        String repromptText =
                "Ask me about something";

        return getSpeechletResponse(speechText, repromptText, true);
    }


    /**
     * Returns a Speechlet response for a speech and reprompt text.
     */
    private SpeechletResponse getSpeechletResponse(String speechText, String repromptText,
            boolean isAskResponse) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Session");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        if (isAskResponse) {
            // Create reprompt
            PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
            repromptSpeech.setText(repromptText);
            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(repromptSpeech);

            return SpeechletResponse.newAskResponse(speech, reprompt, card);

        } else {
            return SpeechletResponse.newTellResponse(speech, card);
        }
    }
}

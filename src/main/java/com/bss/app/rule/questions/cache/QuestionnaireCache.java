package com.bss.app.rule.questions.cache;

import com.bss.app.rule.engine.dto.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuestionnaireCache implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionnaireCache.class);
    private ConcurrentHashMap<String, List<Answer>> userQnsAnsMap = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        userQnsAnsMap = new ConcurrentHashMap<>();
    }

    public void saveAnswersForUser(final String userId, final List<Answer> answers) {
        LOGGER.info("Started adding answers for user: {}, no of answers: {}", userId, answers.size());
        if (!userQnsAnsMap.containsKey(userId)) {
            userQnsAnsMap.put(userId, new ArrayList<>());
        }
        userQnsAnsMap.get(userId).addAll(answers);
        LOGGER.info("Finished adding answers for user:{}, no of answers: {}", userId, answers.size());
    }

    public Integer getCurrentQnsNo(final String userId) {
        LOGGER.info("Started finding current question no for user:{}", userId);
        Integer no = 0;
        if (userQnsAnsMap.contains(userId)) {
            no = userQnsAnsMap.get(userId).size();
        }
        LOGGER.info("Finished finding current question no for user:{}, no:{}", userId, no);

        return no;
    }

    public List<Answer> removeAnswersForUser(final String userId) {
        return userQnsAnsMap.remove(userId);
    }
}

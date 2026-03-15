package com.bss.app.rule.engine;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.times;

public class RuleEngineApplicationTest {

    @Test
    public void main_withEmptyArgs_callsSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            String[] args = new String[0];
            RuleEngineApplication.main(args);
            mocked.verify(() -> SpringApplication.run(RuleEngineApplication.class, args), times(1));
        }
    }

    @Test
    public void main_withArgs_callsSpringApplicationRunWithSameArgs() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            String[] args = new String[]{"--spring.config.name=test", "--some.prop=value"};
            RuleEngineApplication.main(args);
            mocked.verify(() -> SpringApplication.run(RuleEngineApplication.class, args), times(1));
        }
    }
}

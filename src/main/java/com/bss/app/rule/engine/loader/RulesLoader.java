package com.bss.app.rule.engine.loader;

import com.bss.app.rule.engine.dto.Rule;
import com.bss.app.rule.engine.exception.RuleEngineException;
import com.bss.app.rule.engine.reader.ExcelFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class RulesLoader implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RulesLoader.class);
    @Autowired
    private ExcelFileReader excelFileReader;
    private List<Rule> rules;

    @Override
    public void afterPropertiesSet() throws Exception {
        ExcelFileReader reader = new ExcelFileReader();
        this.rules = reader.readRulesFromFile("/mnt/sda5/work/codebase/rule-engine/data/input-file.xlsx");
        LOGGER.info("Rules Loaded: {}", rules);
    }

    public List<Rule> loadRules(final FileInputStream inputStream) throws RuleEngineException {
        LOGGER.info("Started loading rules from source");
        List<Rule> newRules = null;
        try {
            newRules = excelFileReader.readRulesFromInputStream(excelFileReader, inputStream);
            if (!CollectionUtils.isEmpty(newRules)) {
                rules = newRules;
            }
            LOGGER.info("Finished loading rules from source, no of rules loaded: {}", newRules.size());
        } catch (IOException e) {
            throw new RuleEngineException("Exception occurred while loading rules from source", e);
        }

        return newRules;
    }

    public List<Rule> getRules() {
        return rules;
    }
}
package com.bss.app.rule.engine.controller;

import com.bss.app.rule.engine.dto.RuleContext;
import com.bss.app.rule.engine.service.RuleEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/rules")
public class RuleEngineController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleEngineController.class);
    @Autowired
    private RuleEngineService ruleEngineService;


    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public List<String> fireRules(@RequestBody RuleContext ruleContext) {
        List<String> response = null;
        LOGGER.info("Started firing rule against context: {}", ruleContext);
        response = ruleEngineService.processRequest(ruleContext);
        LOGGER.info("Finished firing rule against context: {}, response: {}", ruleContext, response);

        return response;
    }
}

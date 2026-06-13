package com.example.springaiquickstart.ctrl;

import com.example.springaiquickstart.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class RagCtrl {
    @Autowired
    private RagService ragService;

    /**
     * 简单 RAG 问答
     * @param question
     * @param topK
     * @return
     */
    @GetMapping("/rag/ask")
    public RagService.RagResponse ask(@RequestParam(defaultValue = "Spring AI 怎么做 RAG") String question,
                                      @RequestParam(defaultValue = "3") Integer topK) {
        return ragService.ask(question, topK);
    }
}

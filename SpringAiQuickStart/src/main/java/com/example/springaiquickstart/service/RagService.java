package com.example.springaiquickstart.service;

import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {
    @Autowired
    private VectorSearchService vectorSearchService;

    @Autowired
    private DeepSeekChatModel chatModel;

    public RagResponse ask(String question, Integer topK) {
        int limit = topK == null || topK < 1 ? 3 : topK;
        List<VectorSearchService.SearchResult> references = vectorSearchService.search(question, limit);
        String context = buildContext(references);
        String prompt = buildPrompt(question, context);
        String answer = chatModel.call(prompt);
        return new RagResponse(question, answer, references);
    }

    private String buildContext(List<VectorSearchService.SearchResult> references) {
        return references.stream()
                .map(item -> "- " + item.text())
                .collect(Collectors.joining("\n"));
    }

    private String buildPrompt(String question, String context) {
        return """
                你是一个基于知识库回答问题的助手。
                请优先根据【参考资料】回答【用户问题】。
                如果参考资料中没有相关信息，请直接说明“参考资料中没有找到相关信息”，不要编造。

                【参考资料】
                %s

                【用户问题】
                %s

                【回答要求】
                1. 用中文回答。
                2. 回答要简洁清楚。
                3. 可以适当总结参考资料，但不要脱离参考资料。
                """.formatted(context, question);
    }

    public record RagResponse(String question,
                              String answer,
                              List<VectorSearchService.SearchResult> references) {
    }
}

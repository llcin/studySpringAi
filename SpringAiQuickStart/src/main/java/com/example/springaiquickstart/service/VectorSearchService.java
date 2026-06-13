package com.example.springaiquickstart.service;

import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class VectorSearchService {
    @Autowired
    private ZhiPuAiEmbeddingModel embeddingModel;

    private final List<VectorItem> vectorStore = new CopyOnWriteArrayList<>();

    private static final List<String> INIT_TEXTS = List.of(
            "Spring AI 是 Spring 生态中用于集成大模型能力的项目，支持聊天、向量化和 RAG。",
            "文本向量化可以把自然语言转换成数字向量，方便做语义搜索和相似度匹配。",
            "智谱 AI 提供了大语言模型和文本向量模型，可以通过 API 在 Java 应用中调用。",
            "余弦相似度通过计算两个向量夹角的余弦值，衡量两段文本在语义空间中的接近程度。",
            "RAG 检索增强生成通常先从知识库中检索相关文本，再把文本交给大模型生成答案。",
            "DeepSeek 是一个大模型服务，可以用于对话、写作、代码生成等场景。",
            "鸣鸣很忙公司base长沙大小周加班，base上海的话双休，其中base 长沙每天加班可以折算调休或者发钱。base上海只有周末能提加班申请"


    );

    public float[] embedding(String message) {
        return embeddingModel.embed(message);
    }

    public List<String> initVectorStore() {
        vectorStore.clear();
        for (String text : INIT_TEXTS) {
            vectorStore.add(new VectorItem(text, embeddingModel.embed(text)));
        }
        return vectorStore.stream()
                .map(VectorItem::text)
                .toList();
    }

    public List<SearchResult> search(String message, Integer topK) {
        if (vectorStore.isEmpty()) {
            initVectorStore();
        }

        float[] queryVector = embeddingModel.embed(message);
        return vectorStore.stream()
                .map(item -> new SearchResult(item.text(), cosineSimilarity(queryVector, item.vector())))
                .sorted(Comparator.comparingDouble(SearchResult::score).reversed())
                .limit(topK)
                .toList();
    }

    private double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        int length = Math.min(vectorA.length, vectorB.length);
        for (int i = 0; i < length; i++) {
            dot += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }
        if (normA == 0 || normB == 0) {
            return 0.0;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public record VectorItem(String text, float[] vector) {
    }

    public record SearchResult(String text, double score) {
    }
}

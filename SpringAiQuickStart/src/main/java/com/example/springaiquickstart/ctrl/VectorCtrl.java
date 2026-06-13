package com.example.springaiquickstart.ctrl;

import com.example.springaiquickstart.service.VectorSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai")
public class VectorCtrl {
    @Autowired
    private VectorSearchService vectorSearchService;

    /**
     * 文本向量化
     * @param message
     * @return
     */
    @GetMapping("/embedding")
    public float[] embedding(@RequestParam(defaultValue = "你好，智谱 AI") String message) {
        return vectorSearchService.embedding(message);
    }

    /**
     * 初始化向量数据
     * @return
     */
    @GetMapping("/vector/init")
    public List<String> initVectorStore() {
        return vectorSearchService.initVectorStore();
    }

    /**
     * 向量相似性搜索
     * @param message
     * @param topK
     * @return
     */
    @GetMapping("/vector/search")
    public List<VectorSearchService.SearchResult> searchVector(@RequestParam(defaultValue = "Spring AI 怎么做语义检索") String message,
                                                               @RequestParam(defaultValue = "3") Integer topK) {
        return vectorSearchService.search(message, topK);
    }
}

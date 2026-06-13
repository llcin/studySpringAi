package com.example.springaiquickstart.ctrl;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@RestController
@RequestMapping("/ai")
public class ChatCtrl {
    @Autowired
    private DeepSeekChatModel chatModel;

    @Autowired
    private ZhiPuAiEmbeddingModel embeddingModel;

    /**
     * 简单会话
     * @param message
     * @return
     */
    @GetMapping("/chat")
    public String chat(@RequestParam(defaultValue = "你好，简单介绍一下你自己") String message) {
        return chatModel.call(message);
    }

    /**
     * 流式会话
     * @param message
     * @param response
     * @return
     */
    @GetMapping(value = "/stream")
    public Flux<String> stream(@RequestParam(defaultValue = "你好，用三句话介绍一下 Spring AI")
                                            String message, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        Prompt p =  new Prompt(message);
        Flux<ChatResponse> responseFlux =  chatModel.stream(p);
        Flux<String> resp = responseFlux.map(new Function<ChatResponse, String>() {
            @Override
            public String apply(ChatResponse chatResponse) {
                return chatResponse.getResult().getOutput().getText();
            }
        });
       return resp;
    }

    /**
     * 文本向量化
     * @param message
     * @return
     */
    @GetMapping("/embedding")
    public float[] embedding(@RequestParam(defaultValue = "你好，智谱 AI") String message) {
        return embeddingModel.embed(message);
    }

    /**
     * 参数设置
     *
     * @param subject
     * @param style
     * @param temperature
     * @param topP
     * @param maxTokens
     * @param model
     * @return
     */
    @GetMapping("/poem")
    public String poem(@RequestParam(defaultValue = "春天") String subject,
                       @RequestParam(defaultValue = "七言绝句") String style,
                       @RequestParam(defaultValue = "0.8") Double temperature,
                       @RequestParam(defaultValue = "0.9") Double topP,
                       @RequestParam(defaultValue = "800") Integer maxTokens,
                       @RequestParam(defaultValue = "deepseek-chat") String model) {
        String promptText = """
                请以“%s”为主题，写一首%s。
                要求：
                1. 语言优美，有画面感。
                2. 不要解释创作过程。
                3. 只返回诗歌正文。
                """.formatted(subject, style);

        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model(model)
                .temperature(temperature)//发散度 ，低值 更加保守 高值更加有创意
                .topP(topP)//top 低值从概率更高的词里面选择 ，值高允许更多低概率词参与
                .maxTokens(maxTokens)
                .build();

        ChatResponse response = chatModel.call(new Prompt(promptText, options));
        return response.getResult().getOutput().getText();
    }

}

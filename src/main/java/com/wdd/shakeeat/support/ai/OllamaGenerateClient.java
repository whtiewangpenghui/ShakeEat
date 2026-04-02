package com.wdd.shakeeat.support.ai;

public interface OllamaGenerateClient {

    String generate(String baseUrl, String model, String prompt, double temperature);
}

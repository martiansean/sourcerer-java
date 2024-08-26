package com.sourcerer.sourcerer1.news;

import java.util.Optional;


public record RssUrl(
    Optional<String> name,
    String url
) {}

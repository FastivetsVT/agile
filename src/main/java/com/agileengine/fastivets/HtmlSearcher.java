package com.agileengine.fastivets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HtmlSearcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlSearcher.class);

    private static final String CHARSET_NAME = "utf8";

    private static final String CSS_QUERY_FORMAT = "[%s=\"%s\"]";

    public static Optional<Element> findElementById(File htmlFile, String targetElementId) {
        return parseDocument(htmlFile).map(doc -> doc.getElementById(targetElementId));
    }

    public static List<Element> searchForSimilarElements(File htmlFile, Element targetElement) {
        Optional<Document> document = parseDocument(htmlFile);
        if (!document.isPresent()) {
            return Collections.emptyList();
        }
        String cssQuery = StreamSupport.stream(
                targetElement.attributes().spliterator(),
                false).map(attribute -> String.format(CSS_QUERY_FORMAT, attribute.getKey(), attribute.getValue())).collect(Collectors.joining(", "));
        return document.get().select(cssQuery);
    }

    private static Optional<Document> parseDocument(File htmlFile) {
        try {
            return Optional.of(Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath()));
        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }
}

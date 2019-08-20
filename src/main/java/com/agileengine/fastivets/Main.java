package com.agileengine.fastivets;

import javafx.util.Pair;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String PROPERTY_ID_KEY = "element.id.to.search";

    public static void main(String[] args) {
        String attributeIdToFind = PropertyReaderUtil.getProperty(PROPERTY_ID_KEY);
        LOGGER.info("Searching for id: {}", attributeIdToFind);
        if (args.length < 2) {
            throw new IllegalArgumentException("Should be provided 2 arguments: origin filepath and path to filepath to search similar element");
        }
        File originFile = new File(args[0]);
        Optional<Element> elementById = HtmlSearcher.findElementById(originFile, attributeIdToFind);
        if (!elementById.isPresent()) {
            LOGGER.warn("Not found element with id '{}' in original file '{}'", attributeIdToFind, originFile.getName());
            return;
        }
        Optional<String> attributes = elementById
                .map(button -> button.attributes().asList().stream()
                        .map(attr -> attr.getKey() + " = " + attr.getValue())
                        .collect(Collectors.joining(", ")));
        if (!attributes.isPresent()) {
            LOGGER.warn("Not found attributes for element with id '{}' in original file '{}'", attributeIdToFind, originFile.getName());
            return;
        }
        LOGGER.info("For file '{}' target element attrs: [{}]", originFile.getName(), attributes.get());

        File fileToSearchSimilarElement = new File(args[1]);
        List<Element> similarElements = HtmlSearcher.searchForSimilarElements(fileToSearchSimilarElement, elementById.get());
        if (similarElements.isEmpty()) {
            LOGGER.warn("Not found similar elements in file '{}'", fileToSearchSimilarElement.getName());
            return;
        }
        LOGGER.info("For file '{}' found similar elements: {}\nSorted by relevance:", fileToSearchSimilarElement.getName(), similarElements.size());
        RelevanceScorer.scoreElements(similarElements, elementById.get()).forEach(Main::printResult);
    }

    private static void printResult(Pair<Element, Integer> result) {
        Element element = result.getKey();
        List<String> path = Stream.concat(Stream.of(element), element.parents().stream()).map(Element::tagName).collect(Collectors.toList());
        Collections.reverse(path);
        LOGGER.info("{} ({}) = {}", String.join(" -> ", path), element, result.getValue());
    }
}

package com.agileengine.fastivets;

import javafx.util.Pair;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import java.util.*;

public class RelevanceScorer {

    private static final Comparator<Pair<Element, Integer>> SCORE_COMPARATOR = Comparator.comparing(Pair::getValue);

    public static List<Pair<Element, Integer>> scoreElements(List<Element> similarElements, Element targetElement){
        Attributes targetAttributes = targetElement.attributes();
        List<Pair<Element, Integer>> scoredElements = new ArrayList<>(similarElements.size());
        for (Element similarElement : similarElements) {
            Iterator<Attribute> targetAttributesIterator = targetAttributes.iterator();
            int relevanceScore = 0;
            while (targetAttributesIterator.hasNext()) {
                Attribute targetAttribute = targetAttributesIterator.next();
                if (similarElement.attr(targetAttribute.getKey()).equals(targetAttribute.getValue())) {
                    relevanceScore++;
                }
            }
            if (targetElement.hasText() && targetElement.text().equals(similarElement.text())) {
                relevanceScore++;
            }
            scoredElements.add(new Pair<>(similarElement, relevanceScore));
        }
        scoredElements.sort(SCORE_COMPARATOR.reversed());
        return scoredElements;
    }
}

package org.openstreetmap.atlas.tags.cache;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.openstreetmap.atlas.tags.Taggable;
import org.openstreetmap.atlas.tags.annotations.validation.Validators;

/**
 * Cache for Tags of certain type. For applications that check tags on big numbers of objects, it
 * would save time to cache associations between tag names and their representative Enum values.
 *
 * @author gpogulsky
 * @author sbhalekar
 * @param <T>
 *            - type of tag Enum class
 */
public class Tagger<T extends Enum<T>> implements Serializable
{
    private static final long serialVersionUID = -9170158494924659179L;

    private final Class<T> type;
    private final String tagName;

    private final ConcurrentMap<String, Optional<T>> storedTags;

    public Tagger(final Class<T> type)
    {
        // This would not work properly with localized Tags.
        // So far we don't have any Enum-based tags that are localized.
        // But if they appear, this code should prevent those (throw).

        this.type = type;
        this.tagName = Validators.findTagNameIn(type);
        // Using concurrent hashmap to avoid concurrency issues while reading and writing
        this.storedTags = new ConcurrentHashMap<>();
    }

    public Optional<T> getTag(final Taggable taggable)
    {
        final Optional<String> possibleTagValue = taggable.getTag(this.tagName);
        if (possibleTagValue.isPresent())
        {
            final String tagValue = possibleTagValue.get();
            final Optional<T> tag = Validators.fromAnnotation(this.type, taggable);
            // this will make sure that key value pair is added only if key is not present or
            // the initial value is null
            this.storedTags.putIfAbsent(tagValue, tag);
            return this.storedTags.get(tagValue);
        }
        return Optional.empty();
    }
}

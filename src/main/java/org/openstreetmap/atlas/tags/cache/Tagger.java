package org.openstreetmap.atlas.tags.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    private final Map<String, Optional<T>> storedTags;

    public Tagger(final Class<T> type)
    {
        // This would not work properly with localized Tags.
        // So far we don't have any Enum-based tags that are localized.
        // But if they appear, this code should prevent those (throw).

        this.type = type;
        this.tagName = Validators.findTagNameIn(type);
        this.storedTags = new HashMap<>();
    }

    public Optional<T> getTag(final Taggable taggable)
    {
        final Optional<String> possibleTagValue = taggable.getTag(this.tagName);
        if (possibleTagValue.isPresent())
        {
            final String tagValue = possibleTagValue.get();
            Optional<T> value = this.storedTags.get(tagValue);
            if (value == null)
            {
                synchronized (this)
                {
                    // if hash map doesn't contain the key then add the key value pair and return
                    // the tag value stored in the map
                    if (!this.storedTags.containsKey(tagValue))
                    {
                        value = Validators.fromAnnotation(this.type, taggable);
                        this.storedTags.put(tagValue, value);
                    }
                    // if hash map contains the key that means value is null so return empty
                    // optional instead of null to avoid NullPointerException
                    else
                    {
                        value = Optional.empty();
                    }
                }
            }
            return value;
        }
        return Optional.empty();
    }

}

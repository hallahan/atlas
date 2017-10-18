package org.openstreetmap.atlas.tags.annotations.extraction;

import java.io.Serializable;
import java.util.Optional;

import org.openstreetmap.atlas.tags.annotations.validation.NonEmptyStringValidator;

/**
 * Extracts non-empty String values
 *
 * @author mgostintsev
 */
public final class NonEmptyStringExtractor implements Serializable
{
    private static final long serialVersionUID = 4351273829548549527L;

    private static final NonEmptyStringValidator VALIDATOR = new NonEmptyStringValidator();

    public static Optional<String> validateAndExtract(final String value)
    {
        if (VALIDATOR.isValid(value))
        {
            return Optional.of(value);
        }

        return Optional.empty();
    }

    private NonEmptyStringExtractor()
    {
    }
}

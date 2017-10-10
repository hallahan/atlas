package org.openstreetmap.atlas.tags.annotations.extraction;

import java.io.Serializable;
import java.util.Optional;

import org.openstreetmap.atlas.tags.annotations.Tag;
import org.openstreetmap.atlas.tags.annotations.Tag.Range;
import org.openstreetmap.atlas.tags.annotations.validation.LongValidator;

/**
 * Extracts long {@link Number} values.
 *
 * @author mgostintsev
 */
public class LongExtractor implements TagExtractor<Long>, Serializable
{
    private static final long serialVersionUID = -434657580531093809L;

    @Override
    public Optional<Long> validateAndExtract(final String value, final Tag tag)
    {
        final LongValidator validator = new LongValidator();
        final Range range = tag.range();
        if (range != null)
        {
            validator.setRange(range.min(), range.max());

            for (final long exclusion : range.exclude())
            {
                validator.excludeValue(exclusion);
            }
        }

        if (validator.isValid(value))
        {
            return Optional.of(Long.parseLong(value));
        }

        return Optional.empty();
    }
}

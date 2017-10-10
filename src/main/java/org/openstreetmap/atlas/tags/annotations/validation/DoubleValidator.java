package org.openstreetmap.atlas.tags.annotations.validation;

/**
 * Checks if the value of a tag is either an exact value or can be coerced into a java double and
 * within an optional range of accepted values
 *
 * @author cstaylor
 */
public class DoubleValidator extends NumericValidator
{
    private static final long serialVersionUID = -5016900921350800370L;

    @Override
    protected Number parse(final String value)
    {
        return Double.parseDouble(value);
    }
}

package org.openstreetmap.atlas.tags.annotations.validation;

import java.io.Serializable;

import org.openstreetmap.atlas.utilities.scalars.Speed;

/**
 * Checks if the value of the tag matches the OSM conventions for denoting speed. See more on the
 * <a href="http://wiki.openstreetmap.org/wiki/Speed_limits">speed limits wiki</a> and
 * <a href="http://wiki.openstreetmap.org/wiki/Key:maxspeed">max speed wiki</a>. Note: the
 * validation has been loosened slightly from OSM standards to handle more valid limits.
 * Specifically, OSM dictates if km/h are the unit, no unit should be specified. The validation
 * below identifies 'kph' as a valid suffix.
 *
 * @author mgostintsev
 */
public class SpeedValidator implements TagValidator, Serializable
{
    private static final long serialVersionUID = -4334583747429085244L;

    private static final DoubleValidator DOUBLE_VALIDATOR;

    static
    {
        DOUBLE_VALIDATOR = new DoubleValidator();
        DOUBLE_VALIDATOR.setMinimum(0);
    }

    @Override
    public boolean isValid(final String value)
    {
        if (value.endsWith(Speed.MPH))
        {
            return DOUBLE_VALIDATOR
                    .isValid(value.substring(0, value.length() - Speed.MPH.length()).trim());
        }
        else if (value.endsWith(Speed.KNOTS))
        {
            return DOUBLE_VALIDATOR
                    .isValid(value.substring(0, value.length() - Speed.KNOTS.length()).trim());
        }
        else if (value.endsWith(Speed.KPH))
        {
            return DOUBLE_VALIDATOR
                    .isValid(value.substring(0, value.length() - Speed.KPH.length()).trim());
        }
        else
        {
            return DOUBLE_VALIDATOR.isValid(value);
        }
    }
}

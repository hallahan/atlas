package org.openstreetmap.atlas.proto.converters;

import org.openstreetmap.atlas.exception.CoreException;
import org.openstreetmap.atlas.proto.ProtoIntegerArray;
import org.openstreetmap.atlas.proto.ProtoIntegerArrayOfArrays;
import org.openstreetmap.atlas.utilities.arrays.IntegerArrayOfArrays;
import org.openstreetmap.atlas.utilities.conversion.TwoWayConverter;

import com.google.common.primitives.Ints;

/**
 * Converts between the {@link IntegerArrayOfArrays} and its autogenerated protobuf counterpart.
 *
 * @author lcram
 */
public class ProtoIntegerArrayOfArraysConverter
        implements TwoWayConverter<ProtoIntegerArrayOfArrays, IntegerArrayOfArrays>
{
    @Override
    public ProtoIntegerArrayOfArrays backwardConvert(final IntegerArrayOfArrays array)
    {
        if (array.size() > Integer.MAX_VALUE)
        {
            throw new CoreException("Cannot convert {}, size too large ({})",
                    array.getClass().getName(), array.size());
        }

        final ProtoIntegerArrayOfArrays.Builder arraysBuilder = ProtoIntegerArrayOfArrays
                .newBuilder();

        for (final int[] elementArray : array)
        {
            final ProtoIntegerArray.Builder subArrayBuilder = ProtoIntegerArray.newBuilder();
            if (elementArray == null)
            {
                throw new CoreException("{} cannot convert arrays with null elements",
                        this.getClass().getName());
            }
            for (final int element : elementArray)
            {
                subArrayBuilder.addElements(element);
            }
            arraysBuilder.addArrays(subArrayBuilder);
        }

        if (array.getName() != null)
        {
            arraysBuilder.setName(array.getName());
        }

        return arraysBuilder.build();
    }

    @Override
    public IntegerArrayOfArrays convert(final ProtoIntegerArrayOfArrays protoArray)
    {
        final IntegerArrayOfArrays integerArrayOfArrays = new IntegerArrayOfArrays(
                protoArray.getArraysCount(), protoArray.getArraysCount(),
                protoArray.getArraysCount());
        protoArray.getArraysList().stream().forEach(array ->
        {
            final int[] items = Ints.toArray(array.getElementsList());
            integerArrayOfArrays.add(items);
        });

        if (protoArray.hasName())
        {
            integerArrayOfArrays.setName(protoArray.getName());
        }

        return integerArrayOfArrays;
    }
}

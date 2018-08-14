package org.openstreetmap.atlas.geography.atlas.delta;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.builder.text.TextAtlasBuilder;
import org.openstreetmap.atlas.streaming.compression.Decompressor;
import org.openstreetmap.atlas.streaming.resource.InputStreamResource;


/**
 * @author hallahan
 */
public class AtlasDeltaGeoJsonIntegrationTest
{
    private Atlas before;
    private Atlas after;
    private AtlasDelta delta;

    @Before
    public void readAtlases()
    {
        before = new TextAtlasBuilder()
                .read(new InputStreamResource(() -> AtlasDeltaIntegrationTest.class
                        .getResourceAsStream("DMA_9-168-233-base.txt.gz"))
                        .withDecompressor(Decompressor.GZIP)
                        .withName("DMA_9-168-233-base.txt.gz"));
        after = new TextAtlasBuilder()
                .read(new InputStreamResource(() -> AtlasDeltaIntegrationTest.class
                        .getResourceAsStream("DMA_9-168-233-alter.txt.gz"))
                        .withDecompressor(Decompressor.GZIP)
                        .withName("DMA_9-168-233-alter.txt.gz"));
        delta = new AtlasDelta(before, after, false).generate();
    }

    /**
     * This is a basic test that should start failing if you change what the delta GeoJSON looks like.
     */
    @Test
    public void testGeoJson()
    {
        final String geoJson = delta.toGeoJson();
        final int len = geoJson.length();
        Assert.assertEquals(22424431, len);
    }

    @Test
    public void testRelationsGeoJson()
    {
        final String relationsGeoJson = delta.toRelationsGeoJson();
        final int len = relationsGeoJson.length();
        Assert.assertEquals(454484, len);
    }

    @Test
    public void parseGeoJson()
    {
        final String geoJsonStr = delta.toGeoJson();

        final JsonObject geoJson = new JsonParser().parse(geoJsonStr).getAsJsonObject();
        final JsonArray features = geoJson.getAsJsonArray("features");

        int i = 0;
        for (; i < features.size(); ++i)
        {
            final JsonObject feature = features.get(i).getAsJsonObject();
            final JsonObject properties = feature.getAsJsonObject("properties");

            final JsonElement diffVal = properties.get("diff");
            Assert.assertNotNull(diffVal);
            final JsonElement diffReasonVal = properties.get("diff:reason");
            Assert.assertNotNull(diffReasonVal);
            final JsonElement diffTypeVal = properties.get("diff:type");
            Assert.assertNotNull(diffTypeVal);

            // a diff property should be before or after.
            Assert.assertThat(diffVal.getAsString(), CoreMatchers.anyOf(CoreMatchers.is("BEFORE"), CoreMatchers.is("AFTER")));

            // Make sure we have a reason and a type.
            Assert.assertTrue(diffReasonVal.getAsString().length() > 0);
            Assert.assertTrue(diffTypeVal.getAsString().length() > 0);
        }

        Assert.assertEquals(47646, i);
    }
}

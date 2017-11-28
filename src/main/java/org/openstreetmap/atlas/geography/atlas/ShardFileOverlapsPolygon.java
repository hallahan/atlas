package org.openstreetmap.atlas.geography.atlas;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.openstreetmap.atlas.geography.Polygon;
import org.openstreetmap.atlas.geography.sharding.DynamicTileSharding;
import org.openstreetmap.atlas.streaming.resource.File;
import org.openstreetmap.atlas.streaming.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Predicate that uses a sharding tree to determine whether a given atlas shard file overlaps a
 * given Polygon. By default it depends on shard files following the naming convention of
 * [name]_[zoom]-[x]-[y].atlas.gz, where the .gz extension is optional. For example,
 * XYZ_9-272-162.atlas.gz and XYZ_9-272-162.atlas are valid name formats. The shard filename pattern
 * can be overridden to work with other naming conventions as long as the [zoom]-[x]-[y] portion of
 * the name still exists as the first group in the pattern.
 * <p>
 * TODO(rmegraw): Possibly refactor this class in the future to get shard name from atlas metadata
 * rather than filename beucase this would be robust to file naming convention changes. As of
 * 11/28/2017 there would be a performance hit getting the metadata if the file is gzipped or read
 * over the network. Per christopher_s_taylor: "If the atlas is a non-gzipped local file it will
 * take advantage of the random access nature of ZipFile to read just the serialized Java metadata
 * object, but if the file is gzipped or being read over the network (say via HDFS) all of the data
 * in that particular shard will be loaded into memory, whether we need it or not."
 *
 * @author rmegraw
 */
public class ShardFileOverlapsPolygon implements Predicate<Resource>
{
    private static final Logger logger = LoggerFactory.getLogger(ShardFileOverlapsPolygon.class);

    /**
     * Matches shard filenames such as XYZ_9-272-162.atlas.gz and XYZ_9-272-162.atlas
     */
    public static final String DEFAULT_SHARD_FILE_REGEX = "^.+_(\\d{1,2}-\\d+-\\d+)\\.atlas(\\.gz)?$";

    private final Pattern shardFilePattern;

    private final Set<String> shardsOverlappingPolygon;

    /**
     * @param shardingTree
     *            Sharding tree
     * @param bounds
     *            Polygon over which shard file overlap is tested
     */
    public ShardFileOverlapsPolygon(final DynamicTileSharding shardingTree, final Polygon bounds)
    {
        this(shardingTree, bounds, DEFAULT_SHARD_FILE_REGEX);
    }

    /**
     * @param shardingTree
     *            Sharding tree
     * @param bounds
     *            Polygon over which shard file overlap is tested
     * @param shardFileRegex
     *            Regex which must extract [zoom]-[x]-[y] portion of shard filename as the first
     *            group (see default regex for example)
     */
    public ShardFileOverlapsPolygon(final DynamicTileSharding shardingTree, final Polygon bounds,
            final String shardFileRegex)
    {
        this.shardFilePattern = Pattern.compile(shardFileRegex);
        this.shardsOverlappingPolygon = new HashSet<>();
        shardingTree.shards(bounds)
                .forEach(shard -> this.shardsOverlappingPolygon.add(shard.getName()));
    }

    @Override
    public boolean test(final Resource resource)
    {
        boolean result = false;

        if (resource instanceof File)
        {
            final String filename = FilenameUtils.getName(((File) resource).getFile().getName());
            final Matcher matcher = this.shardFilePattern.matcher(filename);
            if (matcher.find())
            {
                final String shardName = matcher.group(1);
                if (this.shardsOverlappingPolygon.contains(shardName))
                {
                    logger.debug("Resource {} overlaps polygon.", resource.getName());
                    result = true;
                }
                else
                {
                    logger.debug("Resource {} does not overlap polygon.", resource.getName());
                }
            }
            else
            {
                logger.debug("Resource {} does not match shard filename pattern.",
                        resource.getName());
            }
        }
        else
        {
            logger.debug("Resource {} is not a File.", resource.getName());
        }

        return result;
    }

}

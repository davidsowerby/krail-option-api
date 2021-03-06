/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.option.persist;

import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.OptionKey;
import uk.q3c.krail.option.RankOption;
import uk.q3c.krail.option.UserHierarchy;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.*;

/**
 * Provides a unique identifier for cached {@link Option}
 * <p>
 * Created by David Sowerby on 19/02/15.
 */
@Immutable
public class OptionCacheKey<T> {


    private final UserHierarchy hierarchy;
    private final String requestedRankName;
    private final OptionKey<T> optionKey;
    private final RankOption rankOption;

    /**
     * Calls {@link OptionCacheKey#OptionCacheKey(UserHierarchy, RankOption, int, OptionKey)} with the the lowest rank where rankOption is
     * {@link RankOption#LOWEST_RANK}, otherwie is called with rank of 0
     */
    public OptionCacheKey(UserHierarchy hierarchy, RankOption rankOption, OptionKey<T> optionKey) {
        this(hierarchy, rankOption, rankOption == RankOption.LOWEST_RANK ? hierarchy.lowestRank() : 0, optionKey);
    }

    /**
     * @param rankOption    determines whether this key represents the lowest or highest in a hierarchy, or a specific rank
     * @param hierarchy     the hierarchy to use
     * @param requestedRank which rank to look for - only required if {@code rankOption} is {@link RankOption#SPECIFIC_RANK}, for
     *                      {@link RankOption#HIGHEST_RANK} or {@link RankOption#LOWEST_RANK} use the alternative constructor:
     *                      {@link OptionCacheKey#OptionCacheKey(UserHierarchy, RankOption, OptionKey)}
     * @param optionKey     an object representing a unique key for the option within its context
     */
    public OptionCacheKey(UserHierarchy hierarchy, RankOption rankOption, int requestedRank, OptionKey<T> optionKey) {
        checkNotNull(hierarchy);
        checkNotNull(rankOption);
        checkNotNull(optionKey);
        checkArgument(requestedRank >= 0);
        this.rankOption = rankOption;
        this.hierarchy = hierarchy;
        this.requestedRankName = hierarchy.rankName(requestedRank);
        this.optionKey = optionKey;
    }

    /**
     * Constructs a copy with rank and RankOption changed
     *
     * @param cacheKey   the key to copy
     * @param rank       the new rank
     * @param rankOption the #rankOption to set
     */
    public OptionCacheKey(OptionCacheKey<T> cacheKey, int rank, RankOption rankOption) {
        this(cacheKey.getHierarchy(), rankOption, rank, cacheKey.getOptionKey());
    }


    /**
     * copy constructor which changes the RankOption to {@code rankOption}
     *
     * @param cacheKey   the key to copy
     * @param rankOption the new rankOption to use
     */
    public OptionCacheKey(OptionCacheKey<T> cacheKey, RankOption rankOption) {
        checkNotNull(cacheKey);
        checkNotNull(rankOption);
        this.rankOption = rankOption;
        this.hierarchy = cacheKey.getHierarchy();
        this.requestedRankName = cacheKey.getRequestedRankName();
        this.optionKey = cacheKey.getOptionKey();

    }

    /**
     * Constructs a copy with {@link #requestedRankName} changed to {@code rankName}.  The {@link #rankOption} may be forced to {@link
     * RankOption#SPECIFIC_RANK}
     * with {@code makeSpecific}
     *
     * @param cacheKey   the key to copy
     * @param rankName   the new rank name
     * @param rankOption the #rankOption to set
     */
    public OptionCacheKey(OptionCacheKey<T> cacheKey, String rankName, RankOption rankOption) {
        checkNotNull(cacheKey);
        checkNotNull(rankName);
        checkNotNull(rankOption);
        this.requestedRankName = rankName;
        this.optionKey = cacheKey.getOptionKey();
        this.hierarchy = cacheKey.getHierarchy();
        this.rankOption = rankOption;
    }


    public UserHierarchy getHierarchy() {
        return hierarchy;
    }

    public OptionKey<T> getOptionKey() {
        return optionKey;
    }

    public String getRequestedRankName() {
        return requestedRankName;
    }


    public RankOption getRankOption() {
        return rankOption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OptionCacheKey)) {
            return false;
        }

        OptionCacheKey<?> that = (OptionCacheKey) o;

        if (!hierarchy.equals(that.hierarchy)) {
            return false;
        }
        if (!optionKey.equals(that.optionKey)) {
            return false;
        }
        if (rankOption != that.rankOption) {
            return false;
        }

        //if a SPECIFIC, we need to compare the rank name as well
        if (rankOption == RankOption.SPECIFIC_RANK) {
            return requestedRankName.equals(that.requestedRankName);
        } else {
            return true;
        }

    }

    @Override
    public int hashCode() {
        int result = hierarchy.hashCode();

        result = 31 * result + optionKey.hashCode();
        result = 31 * result + rankOption.hashCode();

        // if a SPECIFIC, include the rank name
        if (rankOption == RankOption.SPECIFIC_RANK) {
            result = 31 * result + requestedRankName.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return "OptionCacheKey{" + "hierarchy=" + hierarchy.persistenceName() +
                ", requestedRankName='" + requestedRankName + '\'' +
                ", optionKey=" + optionKey.compositeKey() +
                ", rankOption=" + rankOption +
                '}';
    }
}

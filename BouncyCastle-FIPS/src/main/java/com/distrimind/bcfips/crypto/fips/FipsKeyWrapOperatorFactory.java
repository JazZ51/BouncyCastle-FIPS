package com.distrimind.bcfips.crypto.fips;

import com.distrimind.bcfips.crypto.Key;
import com.distrimind.bcfips.crypto.KeyWrapOperatorFactory;
import com.distrimind.bcfips.crypto.Parameters;

/**
 * Base class for the approved mode KeyWrapOperatorFactory implementations.
 *
 * @param <T> the parameters type associated with the final implementation of this factory.
 */
public abstract class FipsKeyWrapOperatorFactory<T extends Parameters, K extends Key>
    implements KeyWrapOperatorFactory<T, K>
{
    // protect constructor
    FipsKeyWrapOperatorFactory()
    {
        FipsStatus.isReady();
    }

    public abstract FipsKeyWrapper<T> createKeyWrapper(K key, T parameters);

    public abstract FipsKeyUnwrapper<T> createKeyUnwrapper(K key, T parameters);
}

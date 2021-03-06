package com.distrimind.bcfips.crypto.fips;

import com.distrimind.bcfips.crypto.EncapsulatedSecretExtractor;

/**
 * Base class for the approved mode EncapsulatedSecretExtractor implementations.
 *
 * @param <T> the parameters type associated with the final implementation of this extractor.
 */
public abstract class FipsEncapsulatedSecretExtractor<T extends FipsParameters>
    implements EncapsulatedSecretExtractor<T>
{
    // protect constructor
    FipsEncapsulatedSecretExtractor()
    {
    }
}

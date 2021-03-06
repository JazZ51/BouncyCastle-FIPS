package com.distrimind.bcfips.jcajce.interfaces;

import java.math.BigInteger;

import com.distrimind.bcfips.jcajce.spec.ECDomainParameterSpec;

/**
 * Interface that a ECGOST-3410 private key needs to conform to.
 */
public interface ECGOST3410PrivateKey
    extends GOST3410Key<ECDomainParameterSpec>, java.security.PrivateKey
{
    /**
     * Return S - the private value.
     *
     * @return the private value for the key.
     */
    BigInteger getS();
}

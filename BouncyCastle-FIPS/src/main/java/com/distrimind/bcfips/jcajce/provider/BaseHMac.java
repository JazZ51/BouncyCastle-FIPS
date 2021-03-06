package com.distrimind.bcfips.jcajce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.MacSpi;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.distrimind.bcfips.crypto.Algorithm;
import com.distrimind.bcfips.crypto.AuthenticationParameters;
import com.distrimind.bcfips.crypto.CryptoServicesRegistrar;
import com.distrimind.bcfips.crypto.MACOperatorFactory;
import com.distrimind.bcfips.crypto.OutputMACCalculator;
import com.distrimind.bcfips.crypto.PasswordBasedDeriver;
import com.distrimind.bcfips.crypto.UpdateOutputStream;
import com.distrimind.bcfips.crypto.fips.FipsAlgorithm;
import com.distrimind.bcfips.crypto.fips.FipsMACOperatorFactory;
import com.distrimind.bcfips.crypto.fips.FipsSHS;
import com.distrimind.bcfips.crypto.general.GeneralAlgorithm;
import com.distrimind.bcfips.crypto.general.SecureHash;
import com.distrimind.bcfips.jcajce.PKCS12Key;

class BaseHMac
    extends MacSpi //implements PBE
{
    private static FipsMACOperatorFactory<FipsSHS.AuthParameters> fipsFactory = new FipsSHS.MACOperatorFactory();
    private static MACOperatorFactory<SecureHash.AuthParameters> generalFactory;

    private final Algorithm algorithm;
    private final MACOperatorFactory factory;
    private final MacParametersCreator parametersCreator;
    private OutputMACCalculator macCalculator;
    private UpdateOutputStream macStream;

    protected BaseHMac(
        FipsAlgorithm algorithm, MacParametersCreator parametersCreator)
    {
        this.algorithm = algorithm;
        this.factory = fipsFactory;
        this.parametersCreator = parametersCreator;
    }

    protected BaseHMac(
        GeneralAlgorithm algorithm, MacParametersCreator parametersCreator)
    {
        this.algorithm = algorithm;
        this.factory = getGeneralMACFactory();
        this.parametersCreator = parametersCreator;
    }

    private MACOperatorFactory<SecureHash.AuthParameters> getGeneralMACFactory()
    {
        if (CryptoServicesRegistrar.isInApprovedOnlyMode())
        {
            return null;
        }

        if (generalFactory == null)
        {
            generalFactory = new SecureHash.MACOperatorFactory();
        }

        return generalFactory;
    }

    protected void engineInit(
        Key                     key,
        AlgorithmParameterSpec  params)
        throws InvalidKeyException, InvalidAlgorithmParameterException
    {
        AuthenticationParameters parameters;

        if (params instanceof PBEParameterSpec)
        {
            try
            {
                parameters = parametersCreator.createParameters(true, null, null);
            }
            catch (IllegalArgumentException e)
            {
                throw new InvalidAlgorithmParameterException("Invalid algorithm parameter: " + e.getMessage(), e);
            }
        }
        else
        {
            parameters = parametersCreator.createParameters(true, params, null);
        }

        if (key instanceof PKCS12Key)
        {
            PBEParameterSpec spec;
            int keySizeInBits = parameters.getMACSizeInBits();

            if (params != null)
            {
                if (params instanceof PBEParameterSpec)
                {
                    spec = (PBEParameterSpec)params;
                }
                else
                {
                    throw new InvalidAlgorithmParameterException("PBE algorithms can only take PBEParameterSpec");
                }
            }
            else if (key instanceof PBEKey)
            {
                PBEKey pbeKey = (PBEKey)key;

                spec = new PBEParameterSpec(pbeKey.getSalt(), pbeKey.getIterationCount());
            }
            else
            {
                throw new InvalidKeyException("No algorithm parameters provided when required.");
            }

            SecretKey pbeKey;

            try
            {
                pbeKey = (SecretKey)key;
            }
            catch (Exception e)
            {
                throw new InvalidKeyException("PBE schemes requires a SecretKey/PBEKey");
            }

            if (key instanceof PKCS12Key)
            {
                key = new SecretKeySpec(ProvPKCS12.getSecretKey(pbeKey, Utils.getUnderlyingDigestAlgorithm(algorithm), spec, PasswordBasedDeriver.KeyType.MAC, keySizeInBits), algorithm.getName());
            }
            else
            {
                throw new InvalidKeyException("Unable to use passed in key for PBE");
            }
        }

        macCalculator = factory.createOutputMACCalculator(Utils.convertKey(algorithm, key), parameters);
        macStream = macCalculator.getMACStream();
    }

    protected int engineGetMacLength() 
    {
        return (parametersCreator.getBaseParameters().getMACSizeInBits() + 7) / 8;
    }

    protected void engineReset() 
    {
        if (macCalculator != null)
        {
            macCalculator.reset();
        }
    }

    protected void engineUpdate(
        byte    input) 
    {
        macStream.update(input);
    }

    protected void engineUpdate(
        byte[]  input,
        int     offset,
        int     len) 
    {
        macStream.update(input, offset, len);
    }

    protected byte[] engineDoFinal() 
    {
        return macCalculator.getMAC();
    }
}

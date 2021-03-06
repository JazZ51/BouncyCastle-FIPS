/***************************************************************/
/******    DO NOT EDIT THIS CLASS bc-java SOURCE FILE     ******/
/***************************************************************/
package com.distrimind.bcfips.crypto.internal.io;

import java.io.IOException;

import com.distrimind.bcfips.crypto.CryptoServicesRegistrar;
import com.distrimind.bcfips.crypto.UpdateOutputStream;
import com.distrimind.bcfips.crypto.internal.Mac;

public class MacOutputStream
    extends UpdateOutputStream
{
    private final String algorithmName;
    private final boolean isApprovedMode;

    protected Mac mac;

    public MacOutputStream(
        Mac          mac)
    {
        this.algorithmName = mac.getAlgorithmName();
        this.isApprovedMode = CryptoServicesRegistrar.isInApprovedOnlyMode();
        this.mac = mac;
    }

    public void write(int b)
        throws IOException
    {
        Utils.approvedModeCheck(isApprovedMode, algorithmName);

        mac.update((byte)b);
    }

    public void write(
        byte[] b,
        int off,
        int len)
        throws IOException
    {
        Utils.approvedModeCheck(isApprovedMode, algorithmName);

        mac.update(b, off, len);
    }

    public byte[] getMac()
    {
        Utils.approvedModeCheck(isApprovedMode, algorithmName);

        byte[] res = new byte[mac.getMacSize()];

        mac.doFinal(res, 0);

        return res;
    }
}

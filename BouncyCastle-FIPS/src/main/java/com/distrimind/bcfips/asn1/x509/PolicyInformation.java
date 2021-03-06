/***************************************************************/
/******    DO NOT EDIT THIS CLASS bc-java SOURCE FILE     ******/
/***************************************************************/
package com.distrimind.bcfips.asn1.x509;

import com.distrimind.bcfips.asn1.ASN1EncodableVector;
import com.distrimind.bcfips.asn1.ASN1Object;
import com.distrimind.bcfips.asn1.ASN1ObjectIdentifier;
import com.distrimind.bcfips.asn1.ASN1Primitive;
import com.distrimind.bcfips.asn1.ASN1Sequence;
import com.distrimind.bcfips.asn1.DERSequence;

public class PolicyInformation
    extends ASN1Object
{
    private ASN1ObjectIdentifier   policyIdentifier;
    private ASN1Sequence          policyQualifiers;

    private PolicyInformation(
        ASN1Sequence seq)
    {
        if (seq.size() < 1 || seq.size() > 2)
        {
            throw new IllegalArgumentException("Bad sequence size: "
                    + seq.size());
        }

        policyIdentifier = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));

        if (seq.size() > 1)
        {
            policyQualifiers = ASN1Sequence.getInstance(seq.getObjectAt(1));
        }
    }

    public PolicyInformation(
        ASN1ObjectIdentifier policyIdentifier)
    {
        this.policyIdentifier = policyIdentifier;
    }

    public PolicyInformation(
        ASN1ObjectIdentifier policyIdentifier,
        ASN1Sequence        policyQualifiers)
    {
        this.policyIdentifier = policyIdentifier;
        this.policyQualifiers = policyQualifiers;
    }

    public static PolicyInformation getInstance(
        Object obj)
    {
        if (obj == null || obj instanceof PolicyInformation)
        {
            return (PolicyInformation)obj;
        }

        return new PolicyInformation(ASN1Sequence.getInstance(obj));
    }

    public ASN1ObjectIdentifier getPolicyIdentifier()
    {
        return policyIdentifier;
    }
    
    public ASN1Sequence getPolicyQualifiers()
    {
        return policyQualifiers;
    }
    
    /* 
     * PolicyInformation ::= SEQUENCE {
     *      policyIdentifier   CertPolicyId,
     *      policyQualifiers   SEQUENCE SIZE (1..MAX) OF
     *              PolicyQualifierInfo OPTIONAL }
     */ 
    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();
        
        v.add(policyIdentifier);

        if (policyQualifiers != null)
        {
            v.add(policyQualifiers);
        }
        
        return new DERSequence(v);
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("Policy information: ");
        sb.append(policyIdentifier);

        if (policyQualifiers != null)
        {
            StringBuilder p = new StringBuilder();
            for (int i = 0; i < policyQualifiers.size(); i++)
            {
                if (p.length() != 0)
                {
                    p.append(", ");
                }
                p.append(PolicyQualifierInfo.getInstance(policyQualifiers.getObjectAt(i)));
            }

            sb.append("[");
            sb.append(p);
            sb.append("]");
        }

        return sb.toString();
    }
}

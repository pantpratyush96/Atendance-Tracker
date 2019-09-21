package com.example.pratyush.geofencing;


public class NetworkType
{
    private String cell1TAC;

    private String wifiSSID;

    private String cell1MCC;

    private String cell1CID;

    private String cell1CI;

    private String cell1PCI;

    private String cell1MNC;

    private String cell1LAC;

    private String wifiBSSID;



    public String getCell1TAC ()
    {
        return cell1TAC;
    }

    public void setCell1TAC (String cell1TAC)
    {
        this.cell1TAC = cell1TAC;
    }

    public String getWifiSSID ()
    {
        return wifiSSID;
    }

    public void setWifiSSID (String wifiSSID)
    {
        this.wifiSSID = wifiSSID;
    }

    public String getCell1MCC ()
    {
        return cell1MCC;
    }

    public void setCell1MCC (String cell1MCC)
    {
        this.cell1MCC = cell1MCC;
    }

    public String getCell1CID ()
    {
        return cell1CID;
    }

    public void setCell1CID (String cell1CID)
    {
        this.cell1CID = cell1CID;
    }

    public String getCell1CI ()
    {
        return cell1CI;
    }

    public void setCell1CI (String cell1CI)
    {
        this.cell1CI = cell1CI;
    }

    public String getCell1PCI ()
    {
        return cell1PCI;
    }

    public void setCell1PCI (String cell1PCI)
    {
        this.cell1PCI = cell1PCI;
    }

    public String getCell1MNC ()
    {
        return cell1MNC;
    }

    public void setCell1MNC (String cell1MNC)
    {
        this.cell1MNC = cell1MNC;
    }

    public String getCell1LAC ()
    {
        return cell1LAC;
    }

    public void setCell1LAC (String cell1LAC)
    {
        this.cell1LAC = cell1LAC;
    }

    public String getWifiBSSID ()
    {
        return wifiBSSID;
    }

    public void setWifiBSSID (String wifiBSSID)
    {
        this.wifiBSSID = wifiBSSID;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [cell1TAC = "+cell1TAC+", wifiSSID = "+wifiSSID+", cell1MCC = "+cell1MCC+", cell1CID = "+cell1CID+", cell1CI = "+cell1CI+", cell1PCI = "+cell1PCI+", cell1MNC = "+cell1MNC+", cell1LAC = "+cell1LAC+", wifiBSSID = "+wifiBSSID+"]";
    }
}
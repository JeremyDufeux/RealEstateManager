package com.openclassrooms.realestatemanager.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars Dollars
     * @return euros
     */
    public static double convertDollarsToEuros(double dollars){
        return dollars * 0.852483;
    }

    /**
     * Conversion d'un prix d'un bien immobilier (Euros vers Dollars)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param euros Euros
     * @return dollars
     */
    public static double convertEurosToDollars(double euros){
        return euros * 1.17304;
    }

    /**
     * Conversion d'une surface (pied carré vers metre carré)
     * @param sqFt square foot
     * @return square meter
     */
    public static double convertSquareFootToSquareMeters(double sqFt){
        return sqFt * 0.09290304;
    }

    /**
     * Conversion d'une surface (metre carré vers pied carré)
     * @param m2 square meter
     * @return square foot
     */
    public static double convertSquareMetersToSquareFoot(double m2){
        return m2 * 10.76391041671;
    }

    public static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return today date as formatted string
     */
    public static String getTodayDate(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context context
     * @return true if internet is available, false if not
     */
    public static Boolean isInternetAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}

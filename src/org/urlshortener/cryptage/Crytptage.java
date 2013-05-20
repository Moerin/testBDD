/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.urlshortener.cryptage;

import com.modp.checkdigits.CheckISO7064Mod11_10;

/**
 *
 * @author Seb
 */
public class Crytptage {
    // Alphabet de base qui sert a la construction de la grammaire de l'url
    private static final String ALPHABET = "ABCDEFGHJKLMNPRTUVWXYZ2346789";
    // Base qui permettra de passer d'un nombre en base 10 a un nombre en base 29
    private static final int BASE = 29;

    /**
     * Permet de transformer un index numérique en base 10
     * en concatenation d'éléments "ABCDEFGHJKLMNPRTUVWXYZ2346789";
     * @param num int  
     * @return String inversé ex: H59G
     */
    public static String crypter(int num) {
        // Recoit les resultats de la conversion de num 
        StringBuilder sb = new StringBuilder();
        /* Objet qui rajoute une clef de controle basée sur la norme
        ISO7064 en modulo 11 et base 10*/
        CheckISO7064Mod11_10 check = new CheckISO7064Mod11_10();
        // Concatene la clef de controle au parametre
        num = Integer.parseInt(check.encode(String.valueOf(num)));
        
        // Boucle de conversion
        while (num > 0) {
            sb.append(ALPHABET.charAt(num % BASE));
            num /= BASE;
        }

        /* Le decodage s'effectue en convertissant les termes selon leur "poids"
         * ex : 1234 -> conversion [16,13,1] -> reverse [1,13,16] -> decodage
         1 * 29^2 + 13 * 29^1 + 16 ^ 29^0 -> 1234
        le string envoye de cette maniere facilite le decodage */
        return sb.reverse().toString();
    }
    
    /*
     * Permet de transformer une chaine de caractére contenant une url
     * en nombre
     * @ param str String
     * @ return int le nombre décodé
     */
    public static int decrypter(String str) {
        int num = 0;
        
        CheckISO7064Mod11_10 check = new CheckISO7064Mod11_10();
        
        /* Verification de l'intégrité du caractère grace à la clef de controle
        renvoi une erreur si l'url ne correspond pas */
        if(!check.verify(str)){
            System.err.println("L'url ne correspond pas");
        }
        
        // Boucle de conversion
        for (int i = 0, len = str.length(); i < len; i++) {
            //num = num * BASE + ALPHABET.indexOf(str.charAt(i));
            num = num + ALPHABET.indexOf(str.charAt(i)) * (int) Math.pow(BASE, i);
        }
        
        // Retourne et extrait le nombre sans sa clef de controle
        return Integer.parseInt(check.getData(String.valueOf(num)));
    }
}

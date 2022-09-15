package elecdisjunct.data.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

/**
 * Static class for hashing and salting passwords,
 * and verifying them when users try to log in.
 *
 * This implementation is obviously similar to the examples it was based on,
 * but there might not be <i>too</i> many ways to use these classes
 *
 * @author Tore Bergebakken
 */

public class Password {

    // these constants must not be changed once we begin storing passwords in the database
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 1000;
    private static final int KEY_LENGTH = 256;

    /**
     * Method for hashing a salted password
     *
     * @param password  The password to be hashed, should be read directly as an array for more certain removal
     * @param salt      The salt we'll use to hash it - please use the salting method provided here
     *
     * @return          byte[] with hashed password
     */
    public static byte[] hash(char[] password, byte[] salt) {

        PBEKeySpec speccie = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH); // what we'll use to hash shit
        Arrays.fill(password, 'H'); // example used MIN_VALUE
        password = null; // like clearPassword() does, making garbage collection thrash it if it looks in this method immediately
        // a lil' bit excessive tho

        try {

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512"); // originally SHA1 but we want it higher don't we

            return factory.generateSecret(speccie).getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) { // no need to handle them differently, this WILL happen BEFORE the array is filled
            e.printStackTrace();
            // quite the fatal error if it actually happens...
        } finally {
            speccie.clearPassword(); // yay we clear it
        }

        return null;
    }

    /**
     * Generates a random salt to use in the hashing method.
     * Uses SecureRandom to do so, preferring the NativePRNG in the OS
     * - changing that won't render our data unusable, mind you.
     *
     * @return      the byte[] with the tasteless salt, NULL in case of failure
     */
    public static byte[] getSalt() {

        byte[] theSalt = null;

        try {

            //SecureRandom rng = SecureRandom.getInstance("NativePRNG");
            SecureRandom rng = SecureRandom.getInstance("SHA1PRNG"); //is an alternative - I presume the above is present on all OSes supporting Java?

            theSalt = new byte[SALT_LENGTH]; // if that's normal salt length

            rng.nextBytes(theSalt); // filling it with sparkling random crystals

        } catch (NoSuchAlgorithmException nsa) {
            nsa.printStackTrace();
        }

        return theSalt; // return null if we fail
    }

    /**
     * Hashes the input with the original salt, then checks if it gives the same result as the original hashing.
     * This verifies that it is the correct password.
     *
     * @param input     What the user typed, as a char[]
     * @param actual    The byte[] stored in the database - 32 bytes
     * @param salt      The byte[] with the salt used to hash the actual password - 16 bytes
     * @return          Whether this is the same password or not
     */
    public static boolean verify(char[] input, byte[] actual, byte[] salt) {

        byte[] hashedInput = hash(input, salt);
        Arrays.fill(input, 'C');

        for (int i = 0; i < hashedInput.length; i++) {

            if (actual[i] != hashedInput[i]) {
                return false; // WAS NOT THE SAME
            }

        }

        return true; // was the same
        //return Arrays.equals(hashedInput, actual); maybe better alternative
    }

}

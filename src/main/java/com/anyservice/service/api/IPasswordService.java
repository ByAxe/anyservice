package com.anyservice.service.api;

public interface IPasswordService {

    /**
     * Covert a password to storable hash
     *
     * @param password user password
     * @return hash representation of a given password
     */
    String hash(String password);

    /**
     * Check whether the password is correct
     *
     * @param password typed password
     * @param hash     saved representation of a password
     * @return is password correct
     */
    boolean verifyHash(String password, String hash);
}

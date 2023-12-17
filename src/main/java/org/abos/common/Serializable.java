package org.abos.common;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This interface is an alternative to {@link java.io.Serializable}.
 * Classes who implement this interface should also implement {@code public static MyClass readObject(DataInputStream dis) throws Exception}
 * (or something similar) which has to fit {@link #writeObject(DataOutputStream)}.
 */
public interface Serializable {

    /**
     * Writes the calling instance into a {@link DataOutputStream}. 
     * @param dos the data output stream
     * @throws IOException If an I/O exception occurs.
     */
    void writeObject(final DataOutputStream dos) throws IOException;
    
}

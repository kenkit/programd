/*
    Alicebot Program D
    Copyright (C) 1995-2001, A.L.I.C.E. AI Foundation
    
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.
    
    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, 
    USA.
*/

package org.alicebot.multiplexor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import org.alicebot.util.DeveloperError;
import org.alicebot.util.FileManager;
import org.alicebot.util.Globals;

/**
 *  <p>
 *  Presently more a proof-of-concept than anything else,
 *  for checking the new {@link Multiplexor}
 *  architecture.
 *  </p>
 *  <p>
 *  Uses &quot;flat-file&quot; Java properties files,
 *  as in Program B, to store predicate data.
 *  </p>
 *
 *  @author Noel Bush
 *  @version 4.1.3
 */
public class FlatFileMultiplexor extends Multiplexor
{
    /** The container for all predicate sets. */
    private static Hashtable predicateSets;

    /** The name of the subdirectory for the predicate files. */
    private static final String FFM_DIR_NAME =
        Globals.getProperty("programd.multiplexor.ffm-dir", "ffm");

    /** The suffix for a predicates storage file. */
    private static final String PREDICATES_SUFFIX = ".predicates";

    /** The string &quot;FlatFileMultiplexor predicates file&quot;. */
    private static final String FFM_FILE_LABEL =
        "FlatFileMultiplexor predicates file";

    /**
     *  Always returns true (FlatFileMultiplexor currently
     *  does not support authentication).
     */
    public boolean checkUser(
        String userid,
        String password,
        String secretKey,
        String botid)
    {
        return true;
    }

    /**
     *  Always returns true (FlatFileMultiplexor currently
     *  does not support authentication).
     */
    public boolean createUser(
        String userid,
        String password,
        String secretKey,
        String botid)
    {
        return true;
    }

    /**
     *  Always returns true (FlatFileMultiplexor currently
     *  does not support authentication).
     */
    public boolean changePassword(
        String userid,
        String password,
        String secretKey,
        String botid)
    {
        return true;
    }

    /**
     *  Saves a predicate to disk.
     */
    public void savePredicate(
        String name,
        String value,
        String userid,
        String botid)
    {
        // Test whether predicateSets is intialized.
        if (predicateSets == null)
        {
            predicateSets = new Hashtable();
        }

        Properties predicates = loadPredicates(userid, botid);

        // Store the predicate value.
        predicates.setProperty(name, value);

        // Write predicates to disk immediately.
        savePredicates(predicates, userid, botid);
    }

    /**
     *  Loads the value of a predicate from disk.
     */
    public String loadPredicate(String name, String userid, String botid)
        throws NoSuchPredicateException
    {
        // Test whether predicateSets is intialized.
        if (predicateSets == null)
        {
            predicateSets = new Hashtable();
        }

        Properties predicates = loadPredicates(userid, botid);

        // Try to get the predicate value.
        String result = predicates.getProperty(name);

        if (result == null)
        {
            throw new NoSuchPredicateException(name);
        }
        return result;
    }

    /**
     *  Loads the predicates file for a given userid.
     *  Ensures that the directory exists.
     *
     *  @param userid
     */
    private static Properties loadPredicates(String userid, String botid)
    {
        Properties predicates = new Properties();

        String fileName =
            FFM_DIR_NAME
                + File.separator
                + botid
                + File.separator
                + userid
                + PREDICATES_SUFFIX;

        File predicateFile = FileManager.checkOrCreate(fileName, FFM_FILE_LABEL);
        if (predicateFile.canRead())
        {
            try
            {
                predicates.load(new FileInputStream(predicateFile));
            }
            catch (IOException e)
            {
                throw new DeveloperError("Error trying to load predicates.", e);
            }
        }
        return predicates;
    }

    /**
     *  Saves the predicates file for a given userid.
     *  Ensures that the directory exists.
     *
     *  @param userid
     */
    private static void savePredicates(
        Properties predicates,
        String userid,
        String botid)
    {
        String fileName =
            FFM_DIR_NAME
                + File.separator
                + botid
                + File.separator
                + userid
                + PREDICATES_SUFFIX;
        FileManager.checkOrCreate(fileName, FFM_FILE_LABEL);
        FileOutputStream outputStream;
        try
        {
            outputStream = FileManager.getFileOutputStream(fileName);
        }
        catch (FileNotFoundException e)
        {
            throw new DeveloperError("Could not locate just-created file: \"" + fileName + "\".");
        }

        try
        {
            predicates.store(outputStream, null);
        }
        catch (IOException e)
        {
            System.err.println(System.getProperty("user.dir"));
            System.err.println(e.getMessage());
            throw new DeveloperError("Error trying to save predicates.", e);
        }
    }

    public int useridCount(String botid)
    {
        return 0;
    }
}
package net.revtut.libraries.generic.util;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Files Library.
 *
 * <P>A library with methods related to files.</P>
 *
 * @author Joao Silva
 * @version 1.0
 */
public final class Files {

    /**
     * Constructor of Files
     */
    private Files() {}

    /**
     * Get all the lines of a file
     * @param file file to get all the lines
     * @return all lines of the file
     */
    public static List<String> getLines(final File file) {
        try {
            return getLines(new FileInputStream(file));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all the lines of a input stream
     * @param inputStream input stream to get all lines
     * @return all lines of the input stream
     */
    public static List<String> getLines(final InputStream inputStream) {
        final List<String> lines = new ArrayList<>();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = reader.readLine()) != null)
                lines.add(line);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * Copy from a file to another one
     *
     * @param inFile file to be copied
     * @param outFile file to copy to
     * @return true if successfull
     */
    public static boolean copyFile(final File inFile, final File outFile) {
        try {
            return copyFile(new FileInputStream(inFile), outFile);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Copy from a file to another one
     *
     * @param inputStream file input stream
     * @param outFile file to copy to
     * @return true if successfull
     */
    public static boolean copyFile(final InputStream inputStream, final File outFile) {
        try {
            final OutputStream outputStream = new FileOutputStream(outFile);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Copy existing directory to new location.
     *
     * @param srcDir source of the folder to copy
     * @param trgDir target of the folder
     * @return true if successfull
     */
    public static boolean copyDirectory(final File srcDir, final File trgDir) {
        try {
            if (srcDir.isDirectory()) {
                // Check if target folder exists
                if (!trgDir.exists())
                    if(!trgDir.mkdirs())
                        return false;
                // List of files inside source directory
                final String[] fList = srcDir.list();
                for (final String aFList : fList) {
                    final File dest = new File(trgDir, aFList);
                    final File source = new File(srcDir, aFList);

                    // Copy that file / directory
                    copyDirectory(source, dest);
                }
            } else {
                // Copy the file
                copyFile(srcDir, trgDir);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Delete directory. Sub-files and sub-directories will be deleted to.
     *
     * @param dir folder to remove
     * @return true it successfull when removing directory
     */
    public static boolean removeDirectory(final File dir) {
        try {
            if (dir.isDirectory()) {
                final File[] files = dir.listFiles();
                if (files != null)
                    for (final File c : files)
                        removeDirectory(c);
            }
            if(!dir.delete())
                Bukkit.getLogger().log(Level.WARNING, "Error while trying to delete " + dir.getName() + ".");
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Unzip a file to a directory
     * @param zipFile zip file to be unzip
     * @param outputFolder output folder of the unzip file
     * @return true if successfully, false otherwise
     */
    public static boolean unzipFile(final File zipFile, final File outputFolder) {
        final byte[] buffer = new byte[2048];

        try {
            final FileInputStream fInput = new FileInputStream(zipFile);
            final ZipInputStream zipInput = new ZipInputStream(fInput);

            ZipEntry entry = zipInput.getNextEntry();

            while(entry != null) {
                final String entryName = entry.getName();
                final File file = new File(outputFolder.getName() + File.separator + entryName);

                // Create the directories of the ZIP file
                if(entry.isDirectory()) {
                    if(!file.exists() && !file.mkdirs())
                            Bukkit.getLogger().log(Level.WARNING, "Error while creating folder " + file.getName() + ".");
                } else {
                    final FileOutputStream fOutput = new FileOutputStream(file);
                    int count;
                    while ((count = zipInput.read(buffer)) > 0)
                        fOutput.write(buffer, 0, count); // Write 'count' bytes to the file output stream
                    fOutput.close();
                }

                // Close current ZIP entry and get the next one
                zipInput.closeEntry();
                entry = zipInput.getNextEntry();
            }

            // close the last ZipEntry
            zipInput.closeEntry();

            zipInput.close();
            fInput.close();
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

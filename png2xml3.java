package png2xml3;

import java.io.*;
import java.util.*;



public class png2xml3 {
    private static List<File> images;
    private static List<File[]> subfolderIndex;
    private static StringBuilder icon;
    private static StringBuilder drawable;

    public static void main(String args[]) {
        // Get .jar directory
        String dir = new File(png2xml3.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
        // Replace %20 with real spaces
        dir = dir.replace("%20", " ");

        // Show progress & Scan
        System.out.println("\nDirectory: " + dir + "\nScanning...");
        images = new ArrayList<File>(Arrays.asList(Scan4Images(dir)));

        // Scan for subfolders
        // Add images in subfolder to list of arrays
        // Also add those images to the master list
        File[] subfolders = Scan4Subfolders(dir);
        subfolderIndex = new ArrayList<File[]>();
        for (File f : subfolders) {
            String subfolderDir = f.getAbsolutePath();
            File[] subfolderImages = Scan4Images(subfolderDir);
            Arrays.sort(subfolderImages);
            subfolderIndex.add(subfolderImages);
            for (File s : subfolderImages) {
                images.add(s);
            }
        }

        // Sort master list alphabetically
        Collections.sort(images, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return file1.getName().compareToIgnoreCase(file2.getName());
            }

        });

        Collections.sort(images, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return file1.getName().compareToIgnoreCase(file2.getName());
            }

        });

        // Write images if found, else return error message
        if (images.size() > 0) {
            System.out.println("Found " + images.size() + " icons");
            writeXML();
            saveFiles(dir);
            System.out.print("Finished!");
        } else
            System.out.print("Could not find any .png image files. :(");
    }

    private static File[] Scan4Images(String dir) {
        File directory = new File(dir);

        return directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".png") || filename.endsWith(".PNG");
            }
        });
    }

    private static File[] Scan4Subfolders(String dir) {
        File directory = new File(dir);

        return directory.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
    }

    private static void writeXML() {
        // Initialize String
        icon = new StringBuilder();
        drawable = new StringBuilder();

        // Write headers
        icon.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        icon.append("<resources xmlns:tools=\"http://schemas.android.com/tools\" tools:ignore=\"MissingTranslation\">\n\n");
        drawable.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        drawable.append("<resources>\n\n");
        drawable.append("\t<version>1</version>\n\n");


        // Loop through all the subfolders that need to have their own string-array
        for (File[] subfolder : subfolderIndex) {
            if (subfolder.length > 0) {

                // Get name of subfolder
                String subfolderName = subfolder[0].getParentFile().getName();

                if (!subfolderName.equals("uncategorized")) {

                    // Starting writing this subfolder's array
                    icon.append("\t<string-array name=\"" + subfolderName + "\">\n");
                    drawable.append("\t<category title=\"" + subfolderName + "\">\n");

                    // Loop through all the images within this subfolder and assign a value for each
                    // Drawables only need to be written once, so we will not write them here
                    for (File img : subfolder) {
                        icon.append("\t\t<item>" + img.getName().replace(".png", "").replace(".PNG", "") + "</item>\n");
                        drawable.append("\t<item drawable=\"" + img.getName().replace(".png", "").replace(".PNG", "") + "\"/>\n");
                    }

                    // Close this subfolder's array
                    icon.append("\t</string-array>\n\n");
                    drawable.append("\t\n\n");

                }
            }
        }

        // Start writing the main array
        icon.append("\t<string-array name=\"icon_pack\">\n");

        // Loop through all files and assign a value for each
        for (File img : images) {
            icon.append("\t\t<item>" + img.getName().replace(".png", "").replace(".PNG", "") + "</item>\n");

        }

        // Close the main array
        icon.append("\t</string-array>\n\n");


        // Start writing the tab array (for IconShowcase)
        icon.append("\t<string-array name=\"icon_filters\">\n");
        // Loop through all the subfolders that need to have their own string-array
        for (File[] subfolder : subfolderIndex) {
            if (subfolder.length > 0) {

                // Get name of subfolder
                String subfolderName = subfolder[0].getParentFile().getName();

                //space to underscore

                subfolderName = subfolderName.replace("%20", "_");
                if (!subfolderName.equals("uncategorized")) {

                    // Starting writing this subfolder's array
                    icon.append("\t\t<item>" + subfolderName + "</item>\n");


                }
            }
        }

        // Close the tab array
        icon.append("\t</string-array>\n\n");
        drawable.append("\t\n\n");

        // Write footers
        icon.append("</resources>");
        drawable.append("\n</resources>");
    }

    private static void saveFiles(String dir) {
        // Save icon_pack.xml
        try {
            File iconFile = new File(dir, "icon_pack.xml");
            FileOutputStream f = new FileOutputStream(iconFile);
            f.write(icon.toString().getBytes());
            f.close();
        } catch (Exception e) {
            System.out.println("Oops, an error occured while writing icon_pack.xml");
        }
        // Save drawable.xml
        try
        {
            File drawableFile = new File(dir, "drawable.xml");
            FileOutputStream f = new FileOutputStream(drawableFile);
            f.write(drawable.toString().getBytes());
            f.close();
        }
        catch(Exception e)
        {
            System.out.println("Oops, an error occured while writing drawable.xml");
        }
    }
}

package bfst21;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

// Loads the file given from the Filechooser
public class Loader {

    private Creator creator;
    public void load(String filename) throws IOException, XMLStreamException, FactoryConfigurationError {
        long time = -System.nanoTime();
       if (filename.endsWith(".osm")) {
            loadOSM(filename);
        } else if (filename.endsWith(".zip")) {
            loadZIP(filename);
        }
        time += System.nanoTime();
        Logger.getGlobal().info(String.format("Load time: %dms", time / 1000000));
    }

    private void loadZIP(String filename) throws IOException, XMLStreamException, FactoryConfigurationError {
        var zip = new ZipInputStream(new FileInputStream(filename));
        zip.getNextEntry();
        loadOSM(zip);
    }

    private void loadOSM(String filename) throws IOException, XMLStreamException, FactoryConfigurationError {
        loadOSM(new FileInputStream(filename));
    }

    private void loadOSM(InputStream input) throws IOException, XMLStreamException, FactoryConfigurationError {
        creator.create(input);
    }
}


        //var waterTemp = new ArrayList<ArrayList<Way>>();

    // Load default .osm(binary) file

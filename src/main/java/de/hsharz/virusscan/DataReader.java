package de.hsharz.virusscan;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataReader {


    private final File file = new File("animals.csv");

    private static String[] replaceDecimal(String[] arr){
        arr[1] = arr[1].replace(',', '.');
        arr[2] = arr[2].replace(',', '.');
        return arr;
    }

    /*protected List<VirusHash> readCSV(){
        try {
            //lines
            Stream<String> lines = Files.lines(file.toPath());
            //Splitten
            Stream<String[]> parts = lines.map((s) -> s.split(";"));

            Stream<String[]> vparts = parts.map(DataReader::replaceDecimal);
            Stream<VirusHash> animals = vparts.map(VirusHash::fromString);


            return VirusHash.collect(Collectors.toList());
        } catch(Exception e){
            throw new RuntimeException(e);
        }

    }*/
}

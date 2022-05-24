package filesprocessing;
import filter.*;
import order.*;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;


public class DirectoryProcessor {

    public static void main(String[] args) {
        if (args.length != 2){
            System.err.println("ERROR: Invalid use of the program");
            return;
        }

        File src_dir = new File(args[0]);
        File command_file = new File(args[1]);

        if (!src_dir.exists() || !command_file.exists()){
            System.err.println("ERROR: given source path isn't valid");
            return;
        }

        ArrayList<File> files = getFiles(src_dir);

        Predicate<File> filter;
        Comparator<File> order;
        Parser parser;
        try {
            parser = new Parser(command_file);
        }catch (IOException e) {
            System.err.println("ERROR: an I/O problem was occurred.");
            return;
        }

        while (!parser.isFinished()){
            System.out.println("====== new SECTION =======");
            try{
                filter = parser.getNextFilter();
                if (filter == null) // EOF
                    return;
            }catch (IOException e){
                System.err.println("ERROR: an I/O problem was occurred.");
                return;
            }catch (IllegalFilterException e){
                filter = FilterFactory.defaultFilter();
                System.out.println("Warning in line " + parser.getCurLine() + " - " + e.getMessage());
            }catch (BadStructureException e){
                System.out.println("ERROR: " + e.getMessage());
                return;
            }

            try{
                order = parser.getNextOrder();
            }catch (IOException e){
                System.err.println("ERROR: an I/O problem was occurred.");
                return;
            }catch (IllegalOrderException e){
                order = OrderFactory.defaultOrder();
                System.out.println("Warning in line " + parser.getCurLine() + " - " + e.getMessage());
            }catch (BadStructureException e){
                System.out.println("ERROR: " + e.getMessage());
                return;
            }

            ArrayList<File> filteredFiles = getFilteredFiles(files, filter);
            filteredFiles.sort(order);
            for (File cur : filteredFiles) {
                System.out.println(cur.getName());
            }
        }
    }

    private static ArrayList<File> getFilteredFiles(ArrayList<File> files, Predicate<File> filter){
        ArrayList<File> filteredFiles = new ArrayList<File>();
        for (File cur : files) {
            if (filter.test(cur))
                filteredFiles.add(cur);
        }
        return filteredFiles;
    }

    private static ArrayList<File> getFiles(File src_dir){
        ArrayList<File> files = new ArrayList<File>();
        File[] allFiles = src_dir.listFiles();
        for (File cur : allFiles) {
            if (cur.isFile() && !cur.isDirectory())
                files.add(cur);
        }
        return files;
    }
}

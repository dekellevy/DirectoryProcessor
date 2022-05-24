package filesprocessing;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.function.Predicate;

import filter.FilterFactory;
import order.OrderFactory;

public class Parser {
    private BufferedReader br;
    private int curLine;
    private boolean finishedStatus;


    public Parser(File src_file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(src_file));
        curLine = 0;
        finishedStatus = false;
    }

    public boolean isFinished(){
        return finishedStatus;
    }

    public int getCurLine(){
        return curLine;
    }

    public Predicate<File> getNextFilter() throws IllegalUseException, BadStructureException, IOException{
        curLine += 1;
        String filterSectionLine = br.readLine();
        if (filterSectionLine == null){ // EOF
            finishedStatus = true;
            return null;
        }
        //validate "FILTER" section is exist
        if (! filterSectionLine.equals("FILTER"))
            throw new BadStructureException("FILTER subsection is missing");


        //get the next line which is the name of the filter
        curLine += 1;
        String filterNameLine = br.readLine();
        if (filterNameLine == null) // EOF
            throw new BadStructureException("missing name of desired filter");

        return FilterFactory.createFilter(filterNameLine);
    }

    public Comparator<File> getNextOrder() throws IllegalUseException, BadStructureException, IOException{
        curLine += 1;
        String orderSectionLine = br.readLine();
        if (orderSectionLine == null) { // EOF
            finishedStatus = true;
            throw new BadStructureException("ORDER subsection is missing");
        }

        
        //get the next line which is the name of the filter
        curLine += 1;
        String filterNameLine = br.readLine();
        if (filterNameLine == null) // EOF
            throw new BadStructureException("missing name of desired filter");

        return FilterFactory.createFilter(filterNameLine);

    }




}

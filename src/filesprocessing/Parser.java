package filesprocessing;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.function.Predicate;

import filter.*;
import order.*;

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

    public Predicate<File> getNextFilter() throws IllegalFilterException, BadStructureException, IOException{
        //validate "FILTER" section is exist
        String filterSectionLine = br.readLine();
        if (filterSectionLine == null){ // EOF
            finishedStatus = true;
            return null;
        }
        curLine += 1;
        if (! filterSectionLine.equals("FILTER"))
            throw new BadStructureException("FILTER subsection is missing");

        //get the next line which is the name of the filter
        String filterNameLine = br.readLine();
        if (filterNameLine == null){ // EOF
            finishedStatus = true;
            throw new BadStructureException("missing name of desired filter");
        }
        curLine += 1;

        return FilterFactory.createFilter(filterNameLine);
    }

    public Comparator<File> getNextOrder() throws IllegalOrderException, BadStructureException, IOException{
        //validate "ORDER" section is exist.
        String orderSectionLine = br.readLine();
        if (orderSectionLine == null){ // EOF
            finishedStatus = true;
            throw new BadStructureException("ORDER subsection is missing");
        }
        curLine += 1;

        if (! orderSectionLine.equals("ORDER"))
            throw new BadStructureException("ORDER subsection is missing");

        //get the next line which is the name of the order
        String orderNameLine = br.readLine();
        if (orderNameLine == null){ // EOF
            finishedStatus = true;
            throw new BadStructureException("missing name of desired order");
        }
        curLine += 1;
        return OrderFactory.createOrder(orderNameLine);
    }
}

package filter;

import filesprocessing.BadStructureException;
import filesprocessing.IllegalUseException;
import java.io.File;
import java.util.function.Predicate;

public class FilterFactory {

    public static Predicate<File> createFilter(String line) throws IllegalUseException {
        String[] parts = line.split("#");
        String filter_name = parts[0];
        int numParts = parts.length;

        Predicate<File> predicate = switch (filter_name){
            case "greater_than" -> greater_than(line);
            case "between" -> between(line);
            case "smaller_than" -> smaller_than(line);
            case "file" -> file(line);
            case "contains" -> contains(line);
            case "prefix" -> prefix(line);
            case "suffix" -> suffix(line);
            case "writable" -> writable(line);
            case "executable" -> executable(line);
            case "hidden" -> hidden(line);
            case "all", "default" -> all(line);
            default -> throw new IllegalUseException("illegal filter name");
        };

        if (parts[numParts-1].equals("NOT"))
            return predicate.negate();
        return predicate;
    }

    public static Predicate<File> defaultFilter(){
        return (File f) -> true;
    }

    private static Predicate<File> greater_than(String line) throws IllegalUseException{
        String[] parts = line.split("#");
        int numParts = parts.length;

        if (numParts == 1)
            throw new IllegalUseException("missing greater_than value");
        if (numParts >= 2 && Long.parseLong(parts[1]) < 0)
            throw new IllegalUseException("size value must be non-negative");
        if (numParts > 3 || (numParts == 3 && !parts[2].equals("NOT")))
            throw new IllegalUseException("bad structure of greater_than filter");

        return (File f) -> {return f.length()/1024 > Long.parseLong(parts[1]);};
    }

    private static Predicate<File> between(String line) throws IllegalUseException{
        String[] parts = line.split("#");
        int numParts = parts.length;

        if (numParts < 3)
            throw new IllegalUseException("missing between values");
        if (Long.parseLong(parts[1]) < 0 || Long.parseLong(parts[2]) < 0)
            throw new IllegalUseException("size value must be non-negative");
        if (numParts > 4 || (numParts == 4 && !parts[3].equals("NOT")))
            throw new IllegalUseException("bad structure of greater_than filter");

        return (File f) -> {
            long kb_size = f.length() / 1024;
            long size1 = Long.parseLong(parts[1]);
            long size2 = Long.parseLong(parts[2]);
            return kb_size >= size1 && kb_size <= size2;
        };
    }

    private static Predicate<File> smaller_than(String line) throws IllegalUseException{
        String[] parts = line.split("#");
        int numParts = parts.length;

        if (numParts == 1)
            throw new IllegalUseException("missing smaller_than value");
        if (numParts >= 2 && Long.parseLong(parts[1]) < 0)
            throw new IllegalUseException("size value must be non-negative");
        if (numParts > 3 || (numParts == 3 && !parts[2].equals("NOT")))
            throw new IllegalUseException("bad structure of smaller_than filter");

        return (File f) -> {return f.length()/1024 < Long.parseLong(parts[1]);};
    }

    private static Predicate<File> file(String line) throws IllegalUseException{
        String[] parts = line.split("#");
        if (! isValidPatternFilter(line))
            throw new IllegalUseException("bad format of file filter");
        return (File f) -> {return f.getName().equals(parts[1]);};
    }

    private static Predicate<File> contains(String line) throws IllegalUseException{
        String[] parts = line.split("#");
        if (! isValidPatternFilter(line))
            throw new IllegalUseException("bad format of contains filter");
        return (File f) -> {return f.getName().contains(parts[1]);};
    }

    private static Predicate<File> prefix(String line) throws IllegalUseException{
        String[] parts = line.split("#");
        if (! isValidPatternFilter(line))
            throw new IllegalUseException("bad format of prefix filter");
        return (File f) -> {return f.getName().startsWith(parts[1]);};
    }

    private static Predicate<File> suffix(String line) throws IllegalUseException{
        String[] parts = line.split("#");
        if (! isValidPatternFilter(line))
            throw new IllegalUseException("bad format of suffix filter");

        return (File f) -> {return f.getName().endsWith(parts[1]);};
    }

    private static Predicate<File> writable(String line) throws IllegalUseException{
        String[] parts = line.split("#");
        if (! isValidPatternFilter(line))
            throw new IllegalUseException("bad format of writable filter");

        boolean permission = getPermission(parts[1]);
        return (File f) -> {return f.canWrite() == permission;};
    }

    private static Predicate<File> executable(String line) throws IllegalUseException{
        String[] parts = line.split("#");
        if (! isValidPatternFilter(line))
            throw new IllegalUseException("bad format of executable filter");

        boolean permission = getPermission(parts[1]);
        return (File f) -> {return f.canExecute() == permission;};
    }

    private static Predicate<File> hidden(String line) throws IllegalUseException{
        String[] parts = line.split("#");
        if (! isValidPatternFilter(line))
            throw new IllegalUseException("bad format of hidden filter");

        boolean permission = getPermission(parts[1]);
        return (File f) -> {return f.canExecute() == permission;};
    }

    private static Predicate<File> all(String line) throws IllegalUseException{
        String[] parts = line.split("#");
        int numParts = parts.length;

        if (numParts > 2 || (numParts == 2 && !parts[1].equals("NOT")))
            throw new IllegalUseException("bad format of all filter");

        return (File f) -> true;
    }

    private static boolean getPermission(String value) throws IllegalUseException{
        if (value.equals("YES"))
            return true;
        else if (value.equals("NO"))
            return false;
        else
           throw new IllegalUseException("value must be \"YES\" or \"NO\" only\n");
    }

    private static boolean isValidPatternFilter(String line){
        String[] parts = line.split("#");
        int numParts = parts.length;

        if (numParts == 1 || (numParts == 3 && !parts[2].equals("NOT")) || numParts > 3)
            return false;
        return true;
    }
}

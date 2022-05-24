package filter;

import filesprocessing.IllegalUseException;
import java.io.File;
import java.util.function.Predicate;

public class FilterFactory {

    public static Predicate<File> createFilter(String line, boolean negate) throws IllegalUseException {
        String[] parts = line.split("#");
        String filter_name = parts[0];
        String value = parts[1];


        Predicate<File> predicate = switch (filter_name){
            case "greater_than" -> greater_than(value);
            case "between" -> between();
            case "smaller_than" -> smaller_than(value);
            case "file" -> file(value);
            case "contains" -> contains(value);
            case "prefix" -> prefix(value);
            case "suffix" -> suffix(value);
            case "writable" -> writable(value);
            case "executable" -> executable(value);
            case "hidden" -> hidden(value);
            case "all" -> all();
            default -> throw new IllegalUseException("illegal filter name");
        };

        if (negate)
            return predicate.negate();
        return predicate;
    }

    private static Predicate<File> greater_than(String value) throws IllegalUseException{
        long size = Long.parseLong(value);
        if (size < 0)
            throw new IllegalUseException("size value must be non-negative");
        return (File f) -> {return f.length()/1024 > size;};
    }

    private static Predicate<File> between(String value1, String value2) throws IllegalUseException{
        long size1 = Long.parseLong(value1);
        long size2 = Long.parseLong(value2);
        if (size1 < 0 || size2 < 0)
            throw new IllegalUseException("size value must be non-negative");
        if (size1 > size2)
            throw new IllegalUseException("left size's value must be smaller than right size's value");
        return (File f) -> {
            long kb_size = f.length() / 1024;
            return kb_size >= size1 && kb_size <= size2;
        };
    }

    private static Predicate<File> smaller_than(String value) throws IllegalUseException{
        long size = Long.parseLong(value);
        if (size < 0)
            throw new IllegalUseException("size value must be non-negative");;
        return (File f) -> {return f.length()/1024 < size;};
    }

    private static Predicate<File> file(String value){
        return (File f) -> {return f.getName().equals(value);};
    }

    private static Predicate<File> contains(String value){
        return (File f) -> {return f.getName().contains(value);};
    }

    private static Predicate<File> prefix(String value){
        return (File f) -> {return f.getName().startsWith(value);};
    }

    private static Predicate<File> suffix(String value){
        return (File f) -> {return f.getName().endsWith(value);};
    }

    private static Predicate<File> writable(String value) throws IllegalUseException{
        boolean permission = getPermission(value);
        return (File f) -> {return f.canWrite() == permission;};
    }

    private static Predicate<File> executable(String value) throws IllegalUseException{
        boolean permission = getPermission(value);
        return (File f) -> {return f.canExecute() == permission;};
    }

    private static Predicate<File> hidden(String value) throws IllegalUseException{
        boolean isHidden = getPermission(value);
        return (File f) -> {return f.isHidden() == isHidden;};
    }

    private static Predicate<File> all(){
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
}

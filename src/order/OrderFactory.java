package order;

import java.io.File;
import java.util.Comparator;

public class OrderFactory {
    public static Comparator<File> createOrder(String line) throws IllegalOrderException {
        if (!isValidPattern(line))
            throw new IllegalOrderException("bad format of order command");

        String[] parts = line.split("#");
        String order_name = parts[0];
        int numParts = parts.length;

        Comparator<File> comparator = switch (order_name) {
            case "abs" -> absComp();
            case "type" -> typeComp();
            case "size" -> sizeComp();
            default -> throw new IllegalOrderException("illegal order name");
        };

        if (parts[numParts-1].equals("REVERSE"))
            return comparator.reversed();

        return comparator;
    }

    public static Comparator<File> defaultOrder(){
        return absComp();
    }

    private static Comparator<File> absComp(){
        class absComparator implements Comparator<File>{
            public int compare(File file1, File file2){
                return file1.getAbsolutePath().compareTo(file2.getAbsolutePath());
            }
        }
        return new absComparator();
    }

    private static Comparator<File> typeComp(){
        class typeComparator implements Comparator<File>{
            public int compare(File file1, File file2){
                if (getType(file1).equals(getType(file2))) //if the same type -> order by abs
                    return file1.getAbsolutePath().compareTo(file2.getAbsolutePath());
                else
                    return getType(file1).compareTo(getType(file2));
            }

            private String getType(File file){
                String name = file.getName();
                int last_period_idx = name.lastIndexOf('.');
                if(last_period_idx == 0 || last_period_idx == -1) //if starts with "." or without "." at all - > type = ""
                    return "";
                else
                    return name.substring(last_period_idx+1);
            }
        }
        return new typeComparator();
    }

    private static Comparator<File> sizeComp(){
        class sizeComparator implements Comparator<File>{
            public int compare(File file1, File file2){
                if (file1.length() == file2.length()) //if the same length -> order by abs
                    return file1.getAbsolutePath().compareTo(file2.getAbsolutePath());
                else
                    return Long.compare(file1.length(), file2.length());
            }
        }
        return new sizeComparator();
    }

    private static boolean isValidPattern(String line){
        String[] parts = line.split("#");
        int numParts = parts.length;

        if((numParts == 2 && !parts[1].equals("REVERSE")) || numParts > 2)
            return false;
        return true;
    }
}

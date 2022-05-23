package filesprocessing;

import java.io.File;
import java.util.Comparator;

public class OrderFactory {
    public static Comparator<File> createOrder(String order, boolean reverse){
        Comparator<File> comparator = null;
        switch (order){
            case "abs":
                comparator = absComp();
                break;

            case "type":
                comparator = typeComp();
                break;

            case "size":
                comparator = sizeComp();
                break;

            case "deafult":
                comparator = absComp();
                break;
        }
        if (comparator == null){
            return null;
        }
        if (reverse)
            return comparator.reversed();
        return comparator;
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
                if (getType(file1).equals(getType(file2))){ //if the same type -> order by abs
                    return file1.getAbsolutePath().compareTo(file2.getAbsolutePath());
                }else{
                    return getType(file1).compareTo(getType(file2));
                }
            }

            private String getType(File file){
                String name = file.getName();
                int last_period_idx = name.lastIndexOf('.');
                if(last_period_idx == 0 || last_period_idx == -1){ //if starts with "." or without "." at all - > type = ""
                    return "";
                }else{
                    return name.substring(last_period_idx+1);
                }
            }
        }
        return new typeComparator();
    }

    private static Comparator<File> sizeComp(){
        class sizeComparator implements Comparator<File>{
            public int compare(File file1, File file2){
                if (file1.length() == file2.length()){ //if the same length -> order by abs
                    return file1.getAbsolutePath().compareTo(file2.getAbsolutePath());
                }else{
                    return Long.compare(file1.length(), file2.length());
                }
            }
        }
        return new sizeComparator();
    }
}

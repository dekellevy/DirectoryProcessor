package filesprocessing;
import java.io.File;
import java.util.ArrayList;


public class DirectoryProcessor {

    public static void main(String[] args) {
        String src_dir = args[0];
        String command_file = args[1];
        ArrayList<File> files = new ArrayList<File>();
        File dir_file = new File(src_dir);
        if (dir_file.exists()){
            System.out.println("file exists");
            String[] files_names = dir_file.list();
            for (String dir_name : files_names){
                String full_path_file = src_dir + "" + dir_name;
            }
        }


    }
}

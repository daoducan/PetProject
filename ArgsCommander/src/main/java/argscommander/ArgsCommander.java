package argscommander;

import com.objectmentor.utilities.Args;
import com.objectmentor.utilities.ArgsException;

public class ArgsCommander {
    
    public static void main(String[] args) {
        try {
            String[] argss = {"-l", "-d", "C:\\ProgramFile\\CommonLogging", "-p", "-3307"};
            Args arg = new Args("p#,l,d*", argss);
            boolean logging = arg.getBoolean('l');      
            int port = arg.getInt('p');      
            String directory = arg.getString('d');
            //executeApplication(logging, port, directory);
            System.out.printf("Argument 1 logging: %s\n", logging);
            System.out.printf("Argument 2 port: %s\n", port);
            System.out.printf("Argument 3 directory: %s\n", directory);
        } catch (ArgsException e) {
            System.out.printf("Argument error: %s\n", e.errorMessage());
        }
    }
    
}

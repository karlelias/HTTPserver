import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;

public class Logger {
    public static void write (String entry) throws Exception{
        File file = new File("log.txt");
        if (file.createNewFile()){
            PrintWriter writer = new PrintWriter("log.txt", "UTF-8");
            writer.println("///"+ LocalDate.now().toString()+ " "+LocalTime.now()+"/// P2P node created");
            writer.close();
        } else {
            FileWriter fileWritter = new FileWriter(file.getName(),true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write("\n///"+ LocalDate.now().toString()+ " "+LocalTime.now()+"/// "+entry);
            bufferWritter.close();
        }
    }
}
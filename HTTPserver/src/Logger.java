import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;

public class Logger {
    public static void write (String log) throws Exception{
        File file = new File("logifail.txt");
        if (file.createNewFile()){
            PrintWriter writer = new PrintWriter("logifail.txt", "UTF-8");
            writer.println("///"+ LocalDate.now().toString()+ " "+LocalTime.now()+"/// P2P node alustatud");
            writer.close();
        } else {
            FileWriter fileWriter = new FileWriter(file.getName(),true);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
            bufferWriter.write("\n///"+ LocalDate.now().toString()+ " "+LocalTime.now()+"/// "+log);
            bufferWriter.close();
        }
    }
}
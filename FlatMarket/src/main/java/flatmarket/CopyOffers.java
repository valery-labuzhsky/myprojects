package flatmarket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created on 10/05/19.
 *
 * @author ptasha
 */
public class CopyOffers {
    public static void main(String[] args) {
        File downloads = new File("/home/ptasha/Downloads");
        Pattern pattern = Pattern.compile("offer.*\\.xlsx");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmm");
        for (File file : downloads.listFiles((dir, name) -> pattern.matcher(name).matches())) {
            try {
                Date created = getCreationTime(file);
                Files.copy(file.toPath(), Paths.get("offers/"+format.format(created)+".xlsx"), StandardCopyOption.COPY_ATTRIBUTES);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Date getCreationTime(File file) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            return new Date(attributes.creationTime().toMillis());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package everlinkintl.com.myappwh.common;

import java.io.File;
import java.io.FileInputStream;

public class FileRead {
    public String loadFromSDFile( File file) {
        String result = null;
        try {
            int length = (int) file.length();
            byte[] buff = new byte[length];
            FileInputStream fin = new FileInputStream(file);
            fin.read(buff);
            fin.close();
            result = new String(buff, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}

package se.teamgejm.safesend.rsa;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import android.content.Context;

public class RsaUtils {
	
	public static String keyToString(byte[] key) {
		StringBuffer retString = new StringBuffer();
		for (int i = 0; i < key.length; ++i) {
            retString.append(Integer.toHexString(0x0100 + (key[i] & 0x00FF)).substring(1));
        }
		return retString.toString();
	}
	
	public static String fileToString(String fileName, Context context) throws IOException {
		File file = new File(context.getFilesDir(), fileName);
		InputStream in = context.openFileInput(fileName);
		byte[] b  = new byte[(int) file.length()];
		int len = b.length;
		int total = 0;

		while (total < len) {
		  int result = in.read(b, total, len - total);
		  if (result == -1) {
		    break;
		  }
		  total += result;
		}

		return new String(b , Charset.defaultCharset());
	}

}

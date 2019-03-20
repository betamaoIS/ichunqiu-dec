

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class DecryptoKey {
	private static final String BITMAPPATH = "dict.png";
	private static String AppVideoKey = "d83423af7d13d8d1695b565bb0f96a3e";
	private static byte[] byteArray;
    private static char[] charArray;

    static {
        byte v8 = 62;
        char v7 = '=';
        char v6 = '/';
        char v5 = '+';
        int v0 = 0;
        byteArray = new byte[0x80];
        charArray = new char[0x40];
        int i;
        for(i = 0; i < 0x80; ++i) {
            byteArray[i] = -1;
        }

        for(i = 90; i >= 65; --i) {
            byteArray[i] = ((byte)(i - 65));
        }

        for(i = 0x7A; i >= 97; --i) {
            byteArray[i] = ((byte)(i - 71));
        }

        for(i = 57; i >= 0x30; --i) {
            byteArray[i] = ((byte)(i + 4));
        }

        byteArray[v5] = v8;
        byteArray[v6] = 0x3F;
        for(i = 0; i <= 25; ++i) {
            charArray[i] = ((char)(i + 65));
        }

        int v2 = 26;
        for(i = 0; v2 <= 51; ++i) {
            charArray[v2] = ((char)(i + 97));
            ++v2;
        }

        i = 52;
        while(i <= v7) {
            charArray[i] = ((char)(v0 + 0x30));
            ++i;
            ++v0;
        }

        charArray[v8] = v5;
        charArray[0x3F] = v6;
    }

    public static String decrypt(String chpier) throws IOException {
		String text = rc4decrypto(chpier, AppVideoKey);
		String keypro = text.split("____")[0];
		return generaKey(keypro);
	}
	private static String generaKey(String s) throws IOException {
		String prefix;
		int hex = 16;
		String v0 = null;
		int v1 = 0;
		if (s != null && !s.equals("")) {
			System.currentTimeMillis();
			InputStream v2 = new FileInputStream(BITMAPPATH);
			BufferedImage bi = ImageIO.read(v2);
			int i = 4;
			try {
				prefix = s.substring(0, i);
			} catch (Exception v2_1) {
				v2_1.printStackTrace();
				prefix = v0;
			}

			if (prefix == null) {
				return v0;
			}

			int v5 = Integer.parseInt(prefix, hex);
			//String v3_1 = hls.int2Hex(v5);
			String v3_1 = Integer.toHexString(v5);
			if (!s.contains(((CharSequence) v3_1))) {
				return v0;
			}

			String v6 = s.replaceAll(prefix, "");

			int height = bi.getHeight();
			int width = bi.getWidth();
			int[][] intmap = new int[height][width];
			for (i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					int v9 = bi.getRGB(j, i);
					int[] rgb = new int[3];
					rgb[0] = (v9 & 0xff0000) >> 16; // r
					rgb[1] = (v9 & 0xff00) >> 8; // g
					rgb[2] = (v9 & 0xff); // b
					intmap[i][j] = rgb[2] / 85 + ((rgb[0] / 36 << 5) + (rgb[1] / 36 << 2));
				}

			}

			int[] v2_4 = intmap[v5];
			StringBuffer v3_2 = new StringBuffer("");
			for (int k = 0; k < v6.length(); k += 2) {
				String v4_1 = v6.substring(v1, v1 + 2);
				v1 += 2;
				String tmp = Integer.toHexString(v2_4[Integer.parseInt(v4_1, hex)]);
				System.err.println(tmp);
				v3_2.append(tmp.length()!=2?"0"+tmp:tmp);
			}

			System.currentTimeMillis();
			v0 = v3_2.toString();
		}

		return v0;
	}
	private static String rc4decrypto(String arg13, String passwd) {
		int v8;
		String v0;
		int IntArrLen = 0x100;
		int v12 = 8;
		if (arg13.isEmpty()) {
			v0 = "";
		} else {
			String md5S = MUtils.strToMd5(passwd);
			int len1 = md5S.length();
			byte[] v5 = MUtils.base64Decode(arg13);
			int len2 = v5.length;
			int[] v3 = new int[IntArrLen];
			int[] v7 = new int[IntArrLen];
			int i;
			for (i = 0; i <= 0xFF; ++i) {
				v8 = i % len1;
				v3[i] = md5S.substring(v8, v8 + 1).toCharArray()[0];
				v7[i] = i;
			}

			i = 0;
			len1 = 0;
			while (i < IntArrLen) {
				len1 = (len1 + v7[i] + v3[i]) % 0x100;
				v8 = v7[i];
				v7[i] = v7[len1];
				v7[len1] = v8;
				++i;
			}

			byte[] v8_1 = new byte[len2];
			i = 0;
			len1 = 0;
			int v3_1 = 0;
			while (i < len2) {
				len1 = (len1 + 1) % 0x100;
				v3_1 = (v3_1 + v7[len1]) % 0x100;
				int v9 = v7[len1];
				v7[len1] = v7[v3_1];
				v7[v3_1] = v9;
				v8_1[i] = ((byte) (((char) (v5[i] ^ v7[(v7[len1] + v7[v3_1]) % 0x100]))));
				++i;
			}

			v0 = new String(v8_1);
			v0 = v0.substring(0, v12)
					.equals(MUtils.strToMd5(v0.substring(v12, v0.length()).concat(md5S)).substring(0, v12))
							? v0.substring(v12)
							: "";
		}

		return v0;
	}

}

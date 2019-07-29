package com.woniu.core.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.widget.EditText;
import android.widget.Toast;

import com.woniu.core.utils.toast.BToast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 工具类
 *
 * @author yaoguangdong 2014-8-20
 */
public class Tools {
    public static int inte;

    /**
     * "0"-->"0.00" "12"-->"12.00"
     */
    public static String amountFormat2Point(String amount) {
        return String.format("%.2f", Float.parseFloat(amount));
    }


    /**
     * 系统跟踪号（流水号），格式化成6位，前补0
     */
    public static String formatSystemTrackingNo(int systemTrackingNO) {
        StringBuffer s = new StringBuffer();
        String stn = systemTrackingNO + "";
        for (int i = 0; i < 6 - stn.length(); i++) {
            s.append("0");
        }
        s.append(stn);
        return s.toString();
    }

    /**
     * 格式化金额
     *
     * @param amount : 0.01-->000000000001 112-->000000011200 13.0-->000000001300
     * @return:
     */
    public static final String formatAmount(String amount) {
        BigDecimal b1 = new BigDecimal(amount);
        BigDecimal b2 = new BigDecimal("100");
        BigDecimal bd = new BigDecimal(b1.multiply(b2).doubleValue());
        String formatAmount = ISO8583Utile.StringFillLeftZero(
                bd.toPlainString(), 12);
        return formatAmount;
    }

    /**
     * double保留2位小数
     */
    public static double get2Double(double a) {
        DecimalFormat df = new DecimalFormat("0.00");
        return new Double(df.format(a).toString());
    }

    /**
     * 提供精确加法计算的add方法
     *
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static double add(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确减法运算的sub方法
     *
     * @param value1 被减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static double sub(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确乘法运算的mul方法
     *
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static double mul(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供精确的除法运算方法div
     *
     * @param value1 被除数
     * @param value2 除数
     * @param scale  精确范围
     * @return 两个参数的商
     * @throws
     */
    public static double div(double value1, double value2, int scale) throws IllegalAccessException {
        //如果精确范围小于0，抛出异常信息
        if (scale < 0) {
            throw new IllegalAccessException("精确度不能小于0");
        }
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.divide(b2, scale).doubleValue();
    }


    /**
     * 金额表示的格式化,从个位起，每隔3位用一个逗号隔开，如：5000 → 5,000;50000 → 50,000
     *
     * @param amount
     * @return
     */
    public static String amountFormat(String amount) {
        int strLen = amount.length();
        String result = null;
        String remainderAmount = amount;
        String remainderStr = null;
        if (amount.contains(".")) {
            strLen = amount.substring(0, amount.indexOf(".")).length();
            remainderStr = amount.substring(amount.indexOf("."),
                    amount.length()).toString();
            amount = amount.substring(0, amount.indexOf(".")).toString();
        }
        StringBuffer sb = new StringBuffer();
        if (strLen < 4) {// 4位数以下
            return remainderAmount;
        } else {
            amount = new StringBuffer(amount).reverse().toString();
            int commaNum = strLen / 3;// 逗号数目
            int remainder = strLen % 3;
            if (remainder == 0) {
                commaNum = commaNum - 1;
            }
            for (int i = 0; i < commaNum; i++) {
                sb.append(amount.substring(3 * i, 3 * (i + 1)));
                sb.append(",");
            }
            sb.append(amount.substring(commaNum * 3, strLen));
        }
        if (remainderAmount.contains(".")) {
            result = sb.reverse().toString() + remainderStr;
        } else {
            result = sb.reverse().toString();
        }
        return result;
    }

    /**
     * String 转化为16进制的Ascii String
     *
     * @param value
     * @return
     */
    public static String stringToHexAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu.append(Integer.toString((int) chars[i], 16));
        }
        return sbu.toString();
    }

    /**
     * 通过传入参数，生成位图数据， 如传入参数 {1,4,8},返回 "10010001"
     *
     * @param args 参数
     * @return 返回位图数据
     */
    public static int getBitmap(byte... args) {
        byte[] bitmap = {0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < args.length; i++) {
            if (args[i] != 0) {
                bitmap[args[i] - 1] = 1;
            }
        }
        StringBuffer sb = new StringBuffer();
        for (byte b : bitmap) {
            sb.append(b);
        }
        String binaryString = sb.toString();
        return Integer.valueOf(binaryString, 2);
    }

    /**
     * 将int的num转换为数量为byteNum的bcd码byte数组
     *
     * @param num     需要被转换的数字
     * @param byteNum 转换为byte的数组
     * @return
     */
    public static byte[] int2bcd(int num, int byteNum) {

        String numStr = String.valueOf(num);
        int numCount = numStr.length();

        if (byteNum * 2 < numCount) {
            throw new ArithmeticException(byteNum
                    + " byte can't host int " + num);
        }
        byte[] b = new byte[byteNum];
        if (numCount % 2 == 0) {
            int needNum = numCount / 2;
            int zeroByteCount = byteNum - needNum;
            for (int i = 0; i < zeroByteCount; i++) {
                b[i] = 0;
            }
            for (int i = 0; i < numCount; i = i + 2) {

                byte high = (byte) ((byte) (numStr.charAt(i) - '1' + 1) << 4);
                byte low = (byte) (numStr.charAt(i + 1) - '1' + 1);
                b[zeroByteCount + i / 2] = (byte) (high | low);
            }
        } else {
            int needNum = numCount / 2 + 1;
            int zeroByteCount = byteNum - needNum;
            for (int i = 0; i < zeroByteCount; i++) {
                b[i] = 0;
            }
            b[zeroByteCount] = (byte) (numStr.charAt(0) - '1' + 1);
            for (int i = 1; i < numCount; i = i + 2) {
                byte high = (byte) ((byte) (numStr.charAt(i) - '1' + 1) << 4);
                byte low = (byte) (numStr.charAt(i + 1) - '1' + 1);
                b[zeroByteCount + 1 + i / 2] = (byte) (high | low);
            }
        }
        return b;
    }

    /**
     * 字符串转制成ASCII码，返回对应的字节数组
     *
     * @param input
     * @return
     */
    public static byte[] getAsciiBytes(String input) {
        char[] c = input.toCharArray();
        byte[] b = new byte[c.length];
        for (int i = 0; i < c.length; i++)
            b[i] = (byte) (c[i] & 0x007F);
        return b;
    }

    /**
     * 返回num字节的16进制
     *
     * @param decimal 10进制数
     * @param num     返回的字节数
     * @return
     */
    public static String getNumHex(int decimal, int num) {
        StringBuffer s = new StringBuffer();
        String hex = Integer.toString(decimal, 16);
        if (hex.length() < num + 1) {
            for (int i = 0; i < num - hex.length(); i++) {
                s.append("0");
            }
            s.append(hex);
        }
        return s.toString();
    }


    /**
     * 读取sdcard下的文件
     *
     * @param fileName
     * @return
     */
    public static byte[] readFileSdcard(String fileName) {
        byte[] buffer = null;
        try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            buffer = new byte[length];
            fin.read(buffer);
            fin.close();
        } catch (Exception e) {
        }
        return buffer;

    }

    /**
     * 基站信息报文63域要求，不足5位，前补0
     */
    private static String process(String param) {
        int len = param.length();
        if (len < 5) {
            for (int i = 0; i < 5 - len; i++) {
                param = "0" + param;
            }
        }
        return param;
    }

    /**
     * 验证URL
     *
     * @param url
     * @return
     */
    public static boolean checkUrl(String url) {
        return url.matches("^((https|http|ftp|rtsp|mms)?://)"
                + "+(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?"
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" + "|"
                + "([0-9a-z_!~*'()-]+\\.)*"
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." + "[a-z]{2,6})"
                + "(:[0-9]{1,4})?" + "((/?)|"
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$");
    }


    /**
     * 隐藏卡号，仅显示卡号的前6位，后4位，重点部分使用*号
     *
     * @param cardNo
     * @return
     */
    public static String hideCardNo(String cardNo) {
        if (!TextUtils.isEmpty(cardNo) && cardNo.length() >= 14) {
            StringBuilder sb = new StringBuilder();
            sb.append(cardNo.substring(0, 6));
            int hideLen = cardNo.length() - 6 - 4;
            if (hideLen > 0) {
                sb.append("***");
            }
            sb.append(cardNo.substring(cardNo.length() - 4, cardNo.length()));
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * 隐藏手机号，仅显示前三位后四位
     */
    public static String hidePhoneNo(String cardNo) {
        if (!TextUtils.isEmpty(cardNo)) {
            StringBuilder sb = new StringBuilder();
            sb.append(cardNo.substring(0, 3));
            int hideLen = cardNo.length() - 3 - 4;
            if (hideLen > 0) {
                sb.append("****");
            }
            sb.append(cardNo.substring(cardNo.length() - 4, cardNo.length()));
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * 去掉信用卡号的空格
     *
     * @param s
     * @return
     */
    private static String getDigitsOnly(String s) {
        StringBuffer digitsOnly = new StringBuffer();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (Character.isDigit(c)) {
                digitsOnly.append(c);
            }
        }
        return digitsOnly.toString();
    }

    /**
     * 校验过程：
     * 1、从卡号最后一位数字开始，逆向将奇数位(1、3、5等等)相加。
     * 2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以2（如果乘积为两位数，将个位十位数字相加，即将其减去9），再求和。
     * 3、将奇数位总和加上偶数位总和，结果应该可以被10整除。
     * 校验银行卡卡号
     */
    public static boolean checkBankCard(String bankCard) {
        if (bankCard.length() < 15 || bankCard.length() > 19) {
            return false;
        }
        char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return bankCard.charAt(bankCard.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhn 校验算法获得校验位
     */
    public static char getBankCardCheckCode(String nonCheckCodeBankCard) {
        if (nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim().length() == 0
                || !nonCheckCodeBankCard.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeBankCard.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * 联迪 将十六进制字符串转换成字节数组
     */
    public static byte[] toByteArray(String hex) {
        if (hex == null)
            return null;
        if (hex.length() % 2 != 0) {
            return null;
        }
        int length = hex.length() / 2;
        byte[] data = new byte[length];
        for (int i = 0, index = 0; i < length; i++) {
            index = i * 2;
            String oneByte = hex.substring(index, index + 2);
            data[i] = (byte) (Integer.valueOf(oneByte, 16) & 0xFF);
        }
        return data;
    }

    /**
     * 联迪 返回字符串的ASCII字节数组
     */
    public static final byte[] getBytes(String s) {
        try {
            return s.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            // 正常不会出现异常
            e.printStackTrace();
            return new byte[0];
        }
    }


    /**
     * 压缩图片的尺寸 path :图片路径 int reqWidth , 目标参数，想要压缩的宽的尺寸 int reqHeight
     * ,目标参数，想要压缩的长的尺寸
     */
    public static Bitmap compressImgResize(String srcPath, int reqWidth,
                                           int reqHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inInputShareable = true;
        opts.inPurgeable = true;
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, opts);
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);
        newOpts.inJustDecodeBounds = false;
        Bitmap destBm = BitmapFactory.decodeFile(srcPath, newOpts);

        return destBm;
    }

    /**
     * 压缩质量
     *
     * @param minSize  KB 压缩到最小质量，防止压缩过度导致的失真
     * @param maxSize  KB 压缩到的最大允许质量。
     * @param savePath
     */
    public static void compressBmpToFile(Bitmap bmp, int minSize, int maxSize,
                                         String savePath) {
        byte[] result;
        if (maxSize < minSize || bmp == null) {
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 默认按照80压缩一次
        bmp.compress(CompressFormat.JPEG, 80, baos);

        int size = baos.toByteArray().length / 1024;
        if (size < minSize) {// size<=60
            result = baos.toByteArray();
        } else if (size <= maxSize) {// 60<size<=90
            result = baos.toByteArray();
        } else if (size >= 90 && size < 130) {
            baos.reset();
            bmp.compress(CompressFormat.JPEG, 60, baos);
            result = baos.toByteArray();
        } else if (size >= 130 && size < 150) {
            baos.reset();
            bmp.compress(CompressFormat.JPEG, 45, baos);
            result = baos.toByteArray();
        } else if (size >= 150 && size < 170) {
            baos.reset();
            bmp.compress(CompressFormat.JPEG, 30, baos);
            result = baos.toByteArray();
        } else if (size >= 170 && size < 190) {
            baos.reset();
            bmp.compress(CompressFormat.JPEG, 25, baos);
            result = baos.toByteArray();
        } else {
            baos.reset();
            bmp.compress(CompressFormat.JPEG, 20, baos);
            result = baos.toByteArray();
        }
        // 回收bitmap资源
//		bmp.recycle();
        bmp = null;
        try {
            File file = new File(savePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * 判断是否有网络连接
     */
//    public static boolean isNetAvail(Context context) {
//        ConnectivityManager conn = (ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = conn.getActiveNetworkInfo();
//        return (info != null && info.isConnected());
//    }

    /**
     * 写/data/data/<应用程序名>目录上的文件
     *
     * @param fileName 写入的文件名
     * @param object   写入的文件内容
     */
    public static void writeFile(Context context, String fileName, Object object) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);

        } catch (Exception e) {
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 读/data/data/<应用程序名>目录上的文件
     *
     * @param fileName 读取的文件名
     * @return 返回文件内容
     */
    public static Object readFile(Context context, String fileName) {
        Object obj = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(fileName);
            ois = new ObjectInputStream(fis);
            obj = ois.readObject();
        } catch (Exception e) {
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return obj;
    }

    private static volatile String toastActName = "";
    private static volatile String toastMsg = "";

    /**
     * Toast的提示
     */
    public static void showToast(final Activity act, final String msg) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BToast.showText(act, msg);
            }

        });
    }

    /**
     * Toast的提示成功
     */
    public static void showToastorSucc(final Activity act, final String msg) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BToast.showText(act, msg, Toast.LENGTH_SHORT, true);
            }
        });
    }


    /**
     * Toast的提示
     */
    public static void showToastLong(final Activity act, final String msg) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BToast.showText(act, msg, Toast.LENGTH_LONG);
            }
        });
    }


    public static String md5(String res) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] strTemp = res.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;

            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            String dd = new String(str);
            return dd;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 取得指定日期格式的 字符串
     */
    public static String formatDate(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return dateFormat.format(date);
    }

    public static float dpToPx(Context context, float dp) {
        if (context == null) {
            return -1;
        }
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float pxToDp(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int dpToPxInt(Context context, float dp) {
        return (int) (dpToPx(context, dp) + 0.5f);
    }

    public static int pxToDpCeilInt(Context context, float px) {
        return (int) (pxToDp(context, px) + 0.5f);
    }

    /**
     * 判断手机号码
     */
    public static boolean isMobileNo(String mobileNum) {
        if (TextUtils.isEmpty(mobileNum)) {
            return false;
        }
        // Pattern pattern = Pattern.compile("^(\\+?\\d{2}-?)?(1[0-9])\\d{9}$");
        Pattern pattern = Pattern.compile("[1][34578]\\d{9}");
        Matcher matcher = pattern.matcher(mobileNum);
        return matcher.matches();
    }

    /**
     * stream to string
     */
    public static String readData(InputStream inputStream, String encodeType) {
        String result = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] byteBuffer = new byte[1024];
        int size = 0;
        byte[] partByteArray;
        try {
            while ((size = inputStream.read(byteBuffer)) != -1) {
                outputStream.write(byteBuffer, 0, size);
            }
            partByteArray = outputStream.toByteArray();
            outputStream.close();
            inputStream.close();
            result = new String(partByteArray, encodeType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除文件
     */
    public Boolean deleteFileOrFolder(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        String newpath = files[i].getAbsolutePath();
                        this.deleteFileOrFolder(newpath);
                    }
                } else if (file.isFile()) {
                    file.delete();
                } else {
                }
            } else {
                return false;
            }

        } else {
            return false;
        }
        return true;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful. If a
     * deletion fails, the method stops attempting to delete and returns
     * "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY,
                    0, file.length());
            messagedigest.update(byteBuffer);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bufferToHex(messagedigest.digest());
    }

    /**
     * 解压缩
     *
     * @param zipFile
     * @param destination
     */
    public static void decompress(String zipFile, String destination) {
        try {
            ZipFile zip = new ZipFile(zipFile);
            Enumeration en = zip.entries();
            ZipEntry entry = null;
            byte[] buffer = new byte[8192];
            int length = -1;
            InputStream input = null;
            BufferedOutputStream bos = null;
            File file = null;

            while (en.hasMoreElements()) {
                entry = (ZipEntry) en.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }

                input = zip.getInputStream(entry);
                file = new File(destination, entry.getName());
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                bos = new BufferedOutputStream(new FileOutputStream(file));

                while (true) {
                    length = input.read(buffer);
                    if (length == -1)
                        break;
                    bos.write(buffer, 0, length);
                }
                bos.close();
                input.close();

            }
            zip.close();
        } catch (Exception e) {
        }
    }

    protected static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a',

            'b', 'c', 'd', 'e', 'f'};
    protected static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
        }
    }

    public static String getFileMD5String(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = in.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
                file.length());
        messagedigest.update(byteBuffer);
        in.close();
        return bufferToHex(messagedigest.digest());
    }

    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    public static boolean checkPassword(String password, String md5PwdStr) {
        String s = getMD5String(password);
        return s.equals(md5PwdStr);
    }

    /**
     * 3DS加密
     */
    private static final String Algorithm = "DESede"; // 定义加密算法,可用
    // DES,DESede,Blowfish

    // keybyte为加密密钥，长度为24字节
    // src为被加密的数据缓冲区（源）
    public static byte[] encryptMode(byte[] keybyte, byte[] src) {
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            // 加密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);// 在单一方面的加密或解密
        } catch (NoSuchAlgorithmException e1) {
            // TODO: handle exception
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    // keybyte为加密密钥，长度为24字节
    // src为加密后的缓冲区
    public static byte[] decryptMode(byte[] keybyte, byte[] src) {
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            // 解密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (NoSuchAlgorithmException e1) {
            // TODO: handle exception
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    // 转换成十六进制字符串
    public static String byte2Hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < b.length - 1)
                hs = hs + ":";
        }
        return hs.toUpperCase();
    }

    /**
     * 对editext进行判断，小数点保留2位，并且第一位输入点时自动变为0.
     *
     * @param editText
     */
    public static void setPricePoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额
     *
     * @param amount
     * @return
     */
    public static String changeY2F(String amount) {
        String currency = amount.replaceAll("\\$|\\￥|\\,", ""); // 处理包含, ￥
        // 或者$的金额
        int index = currency.indexOf(".");
        int length = currency.length();
        Long amLong = 0l;
        if (index == -1) {
            amLong = Long.valueOf(currency + "00");
        } else if (length - index >= 3) {
            amLong = Long.valueOf((currency.substring(0, index + 3)).replace(
                    ".", ""));
        } else if (length - index == 2) {
            amLong = Long.valueOf((currency.substring(0, index + 2)).replace(
                    ".", "") + 0);
        } else {
            amLong = Long.valueOf((currency.substring(0, index + 1)).replace(
                    ".", "") + "00");
        }
        return amLong.toString();
    }

    /**
     * 将分为单位的转换为元 （除100）
     *
     * @param amount
     * @return
     * @throws Exception
     */
    /**
     * 金额为分的格式
     */
//    public static final String CURRENCY_FEN_REGEX = "\\-?[0-9]+";
//
//    public static String changeF2Y(String amount) throws Exception {
////        if (!amount.matches(CURRENCY_FEN_REGEX)) {
////            throw new Exception("金额格式有误");
////        }
//        BigDecimal bigDecimal = new BigDecimal(amount);
//
//        return String.valueOf(bigDecimal.divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP).doubleValue());
//    }

    /**
     * 将元为单位的转换为分 （乘100）
     *
     * @param amount
     * @return
     */
    public static String changerY2F(double amount) {
        return BigDecimal.valueOf(amount).multiply(new BigDecimal(100))
                .toString();
    }


    /**
     * 将ascii码转化为16进制
     */
    public static String convertHexToString(String hex) {

        try {
            StringBuilder sb = new StringBuilder();
            StringBuilder temp = new StringBuilder();

            for (int i = 0; i < hex.length() - 1; i += 2) {

                // grab the hex in pairs
                String output = hex.substring(i, (i + 2));
                // convert hex to decimal
                int decimal = Integer.parseInt(output, 16);
                // convert the decimal to character
                sb.append((char) decimal);

                temp.append(decimal);
            }

            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String printHexString(byte[] b) {
        String hex = "";
        for (int i = 0; i < b.length; i++) {
            hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print("8583=========" + hex.toUpperCase());

        }
        return hex.toUpperCase();
    }


    /**
     * 获取手机ip地址
     *
     * @return
     */
    public static String getPhoneIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 判断网络类型
     */
//    public static void getinte(Context cOnt) {
//        ConnectivityManager connectMgr = (ConnectivityManager) cOnt
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = connectMgr.getActiveNetworkInfo();
//        if (info != null) {
//            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
//                // banner网络类型
//                inte = 2;
//            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
//                inte = 1;
//            }
//        }
//    }

    /**
     * 对比版本号逻辑
     */
    //
    public static int BjVsersion(String version1, String version2) {
        String[] versionarry1 = version1.split("\\.");
        String[] versionarry2 = version2.split("\\.");
        int length1 = versionarry1.length;
        int length2 = versionarry2.length;
        if (length1 > length2) {
            for (int i = 0; i < length2; i++) {
                int parseInt1 = Integer.parseInt(versionarry1[i]);
                int parseInt2 = Integer.parseInt(versionarry2[i]);
                if (parseInt1 > parseInt2) {
                    return 1;
                } else if (parseInt1 < parseInt2) {
                    return 2;
                }
            }
            return 0;
        } else {
            for (int i = 0; i < length1; i++) {
                int parseInt1 = Integer.parseInt(versionarry1[i]);
                int parseInt2 = Integer.parseInt(versionarry2[i]);
                if (parseInt1 > parseInt2) {
                    return 1;
                } else if (parseInt1 < parseInt2) {
                    return 2;
                }
            }
            return 0;
        }

    }

    /**
     * 对密码解密
     */
    public static byte[] asciiToBcd(String src) {
        int inum = 0;

        String str = src.trim().replaceAll(" ", "");

        int numlen = str.length();
        if ((numlen % 2) > 0)
            return null;
        byte[] dst = new byte[numlen / 2];

        for (int i = 0; i < numlen; ) {
            // TODO: 过滤空格
            char hghch = ConvertHexChar(str.charAt(i));
            char lowch = ConvertHexChar(str.charAt(i + 1));

            dst[inum++] = (byte) (hghch * 16 + lowch);
            i += 2;
        }
        return dst;
    }

    public static String hex2DebugHexString(byte[] b, int len) {
        int[] x = new int[len];
        String[] y = new String[len];
        StringBuilder str = new StringBuilder();

        for (int j = 0; j < len; ++j) {
            x[j] = b[j] & 255;

            for (y[j] = Integer.toHexString(x[j]); y[j].length() < 2; y[j] = "0"
                    + y[j]) {
                ;
            }

            str.append(y[j]);
            str.append(" ");
        }

        return (new String(str)).toUpperCase();
    }

    // 如‘9’转成 9
    private static char ConvertHexChar(char ch) {
        if ((ch >= '0') && (ch <= '9'))
            return (char) (ch - 0x30);
        else if ((ch >= 'A') && (ch <= 'F'))
            return (char) (ch - 'A' + 10);
        else if ((ch >= 'a') && (ch <= 'f'))
            return (char) (ch - 'a' + 10);
        else
            return (char) (-1);
    }

    public static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();
    }

    public static String asciiToString(String value) {
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }

    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context
                    .getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String processsys(String str) {
        try {
            if (!TextUtils.isEmpty(str)) {
                String number = (Integer.parseInt(str) + 1) + "";
                int num = 6 - number.length();
                for (int i = 0; i < num; i++) {
                    number = "0" + number;
                }
                return number;
            } else {
                return "000001";
            }

        } catch (Exception e) {
            return "000001";
        }

    }


    /**
     * 文件保存本地 本应用私有 程序卸载 数据清除
     *
     * @param context
     * @param filename
     * @param text
     */
    public static void savefile(Context context, String filename, String text) {
        BufferedWriter out = null;
        try {
            File f = new File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    filename);
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(f, true)));
            if (f.length() > 0) {
                out.write("&&");
            }
            out.write(text);
            out.flush();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public static String getqrPath(Context context, String filename) {
        String path;
        File f = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), filename);
        path = f.getPath();

        return path;

    }


    public static String getqrPath1(Context context, String filename) {
        String path;
        File f = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), filename);
        path = f.getPath();
        return path;

    }

    public static String getData(String src) {
        int len = Integer.parseInt(src.substring(0, 1));
        return src.substring(1, src.length() - len);
    }

    public static String getproKey(String sn) {

        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sha1.update(sn.getBytes());
        byte[] digest = sha1.digest();

        byte[] resultBytes = new byte[8];

        System.arraycopy(digest, 0, resultBytes, 0, 8);
        String hexString = ISO8583Utile.bytesToHexString(resultBytes);
        return hexString;

    }

    /**
     * 获取当前系统时间
     */
    public static String getYear() {
        String date = "";
        try {
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
            date = dateFormat.format(now);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return date;
    }

    /**
     * 获取当前系统时间
     */
    public static String getDate() {
        String date = "";
        try {
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = dateFormat.format(now);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return date;
    }

    /**
     * 格式化时间
     */
    public static String GshDate(String time) {
        String date = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parse = dateFormat.parse(time);
            date = dateFormat.format(parse);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return date;
    }

    /**
     * 毫秒值转化成  年月日 时分秒
     */
    public static String millDate(String time) {
        try {
            long parseLong = Long.parseLong(time);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date data = new Date(parseLong);
            String format1 = format.format(data);
            return format1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return " ";
    }

    /**
     * 格式化时间（月日时分，为了交易）
     */
    public static String formatDate(String format) {
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
            Date date = sdf1.parse(format);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            return sdf2.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 图片保存本地 本应用私有 程序卸载 数据清除
     *
     * @param context
     * @param filename
     * @param bm
     */
    public static void saveBitmap(Context context, String filename, Bitmap bm) {

        try {
            File f = new File(context.getExternalFilesDir(
                    Environment.DIRECTORY_DOWNLOADS), filename);
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
        }
    }


    /**
     * 图片保存sd卡
     */
    public static void saveBitmapToSDCard(String filename, Bitmap bm) {
        File f = new File(Environment.getExternalStorageDirectory()
                + "/payments" + "/sign/");
        if (!f.exists()) {
            boolean r = f.mkdir();
        }
        File[] files = f.listFiles();
        if (files.length == 10) {
            List<File> fileList = Arrays.asList(files);
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return (int) (o1.lastModified() - o2.lastModified());
                }
            });
            boolean r = fileList.get(0).delete();
        }
        File f1 = new File(Environment.getExternalStorageDirectory()
                + "/payments" + "/sign/", filename);
        try {
            FileOutputStream out = new FileOutputStream(f1);
            bm.compress(CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
        }

    }


    /**
     * 去掉字符串里的特殊符号只保留字母和数字
     *
     * @param msg
     * @return
     */
    public static String filterNum(String msg) {

        String regEx = "[^a-zA-Z0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(msg);
        return m.replaceAll("").trim();
    }

    /**
     * 去掉二磁道里的F
     */
    public static String filterTrad2data(String msg) {
        String trad2 = "";
        if (TextUtils.isEmpty(msg)) {
            return trad2;
        } else {
            if (msg.contains("F") || msg.contains("f")) {
                trad2 = msg.replace("f", "");
                trad2 = msg.replace("F", "");
                return trad2;
            }
        }
        return msg;
    }


    public static void writeToFile(String fileName, String result, Context context)
            throws IOException {
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), fileName);

        if (file.exists()) {
            deleteDir(file);
        } else {
            file.mkdir();
        }

        if (!file.isFile()) {
            file.createNewFile();
            @SuppressWarnings("resource")
            DataOutputStream out = new DataOutputStream(new FileOutputStream(
                    file));
            out.writeBytes(result);
        }
    }

    // 读文件，返回字符串  
    public static String ReadFile(String fileName, Context context) {
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), fileName);
        BufferedReader reader = null;
        String laststr = "";
        try {
            // System.out.println("以行为单位读取文件内容，一次读一整行：");  
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束  
            while ((tempString = reader.readLine()) != null) {
                // 显示行号  
                System.out.println("line " + line + ": " + tempString);
                laststr = laststr + tempString;
                ++line;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return laststr;
    }

    /**
     * 将bitmap转化为String
     */
    public static String bitmapTobyte(Bitmap bmp) {
        String str = "";
        if (null != bmp) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            bmp.compress(CompressFormat.PNG, 100, output);
            byte[] result = output.toByteArray();//转换成功了
            try {
                output.close();
                str = ISO8583Utile.bytesToHexString(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return str;

    }

    /**
     * 将 string 转bitmap
     */
    public static Bitmap StringTobitmap(String str) {
        Bitmap bitmap = null;
        try {
            byte[] bit = ISO8583Utile.hexStringToByte(str);
            bitmap = BitmapFactory.decodeByteArray(bit, 0, bit.length);
        } catch (Exception e) {
        }
        return bitmap;
    }

    /**
     * MD5加密
     *
     * @param message 要进行MD5加密的字符串
     * @return 加密结果为32位字符串
     */
    public static String getMD5(String message) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(message.getBytes("UTF-8"));

            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                else
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return md5StrBuff.toString().toUpperCase();//字母大写
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dp2px(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }


    /**
     * 生成8位随机数
     */
    public static String getRandom() {
        StringBuilder str = new StringBuilder();//定义变长字符串
        Random random = new Random();
        //随机生成数字，并添加到字符串
        for (int i = 0; i < 20; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }


    /**
     * 金额为分的格式
     */
    public static final String CURRENCY_FEN_REGEX = "\\-?[0-9]+";

    /**
     * 将分为单位的转换为元并返回金额格式的字符串 （除100）
     *
     * @param amount
     * @return
     * @throws
     */
    public static String changeF2Y(String amount) {
        if (!amount.toString().matches(CURRENCY_FEN_REGEX)) {
            return "";
        }

        int flag = 0;
        String amString = amount.toString();
        if (amString.charAt(0) == '-') {
            flag = 1;
            amString = amString.substring(1);
        }
        StringBuffer result = new StringBuffer();
        if (amString.length() == 1) {
            result.append("0.0").append(amString);
        } else if (amString.length() == 2) {
            result.append("0.").append(amString);
        } else {
            String intString = amString.substring(0, amString.length() - 2);
            for (int i = 1; i <= intString.length(); i++) {
                if ((i - 1) % 3 == 0 && i != 1) {
                    result.append(",");
                }
                result.append(intString.substring(intString.length() - i, intString.length() - i + 1));
            }
            result.reverse().append(".").append(amString.substring(amString.length() - 2));
        }
        if (flag == 1) {
            return "-" + result.toString();
        } else {
            return result.toString();
        }
    }

}

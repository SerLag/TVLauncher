package com.droidlogic.tvlauncher;

import android.util.Log;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/* loaded from: classes.dex */
public class LedControl {
    public static boolean control_led(String str, int i) {
        Process process;
        DataOutputStream dataOutputStream = null;
        try {
            String str2 = "echo " + i + " > " + str;
            process = Runtime.getRuntime().exec("sh");
            try {
                try {
                    DataOutputStream dataOutputStream2 = new DataOutputStream(process.getOutputStream());
                    try {
                        dataOutputStream2.writeBytes(str2 + "\n");
                        dataOutputStream2.writeBytes("exit\n");
                        dataOutputStream2.flush();
                        process.waitFor();
                        try {
                            dataOutputStream2.close();
                            process.destroy();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    } catch (Exception e2) {
                        //    e = e2;
                        dataOutputStream = dataOutputStream2;
                        Log.d("runtime_exception:", e2.getMessage());
                        if (dataOutputStream != null) {
                            try {
                                dataOutputStream.close();
                            } catch (Exception e3) {
                                e3.printStackTrace();
                                return false;
                            }
                        }
                        process.destroy();
                        return false;
                    } catch (Throwable th) {
                        th = th;
                        dataOutputStream = dataOutputStream2;
                        if (dataOutputStream != null) {
                            try {
                                dataOutputStream.close();
                            } catch (Exception e4) {
                                e4.printStackTrace();
                                throw th;
                            }
                        }
                        process.destroy();
                        throw th;
                    }
                } catch (Throwable th2) {
                    //th = th2;
                }
            } catch (Exception e5) {
                //   e = e5;
            }
        } catch (Exception e6) {
            //   e = e6;
            process = null;
        } catch (Throwable th3) {
            //  th = th3;
            process = null;
        }
        return true;
    }

    public static String checkStatus(String str) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("cat " + str).getInputStream()));
            String str2 = "";
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    return str2;
                }
                str2 = str2 + readLine + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void control_led_status(String str, boolean z) {
        String checkStatus = checkStatus(str);
        if (checkStatus.equals("1")) {
            if (z) {
                return;
            }
            //control_led(str, 0);
        } else if (checkStatus.equals("0")) {
            if (z) {
            //    control_led(str, 1);
            }
        } else if (z) {
          //  control_led(str, 1);
        } else {
           // control_led(str, 0);
        }
    }
}
